package com.shitwish.paymentService.PaymentServiceApp.Controller;

import com.google.gson.Gson;
import com.shitwish.paymentService.PaymentServiceApp.Model.Payment;
import com.shitwish.paymentService.PaymentServiceApp.Service.PaymentService;
import org.apache.http.client.fluent.Request;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
public class PaymentServiceController {

    Gson gson = new Gson();

    @Autowired
    PaymentService service;

    private final RestTemplate restTemplate = new RestTemplate();

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "dummy";
    }

    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    public String initiatePayment(@RequestBody Map<String, String> data) {
        boolean succeeded;

        JSONObject response = new JSONObject();
        int userId = Integer.valueOf(data.get("userId"));

        // Request for UserService (getting the buyer)

        String buyerResponse = restTemplate.getForObject("https://shitwish-user.herokuapp.com/user/" + userId, String.class);
        JSONObject buyerJSON = new JSONObject(buyerResponse);
        int buyerBalance = (int) buyerJSON.get("balance");

        // Request for OrderService (getting the order with products)

        String orderResponse = restTemplate.getForObject("https://shitwish-order-service.herokuapp.com/get-active-order/" + userId, String.class);
        System.out.println(orderResponse);
        JSONObject order = new JSONObject(orderResponse);
        // Getting the seller's id from products and price
        if (order.has("productIds")) {
            double totalPrice = 0;
            List<Map<String, String>> sellers = new LinkedList<>();
            JSONArray productIds = order.getJSONArray("productIds");

            for (Object productId : productIds) {
                totalPrice = service.getSellersData(totalPrice, sellers, (int) productId);
            }
            succeeded = totalPrice <= buyerBalance;

            // Process of payment

            if (succeeded) {
                for (Map<String, String> seller : sellers) {
                    try {
                        Payment payment = service.payUser(userId, seller);
                        service.savePayment(payment);
                    } catch (IOException e) {
                        service.responseError(response, "Payment has encountered an error!");
                        return String.valueOf(response);
                    }
                }
                try {
                    service.withdrawUser(userId, (int) totalPrice);
                } catch (IOException e) {
                    service.responseError(response, "Withdrawing money encountered an error!");
                    return String.valueOf(response);
                }

                for (Object productId : productIds) {
                    try {
                        Request.Post("http://shitwish-product.herokuapp.com/products/sell-product?id=" + (int) productId )
                                .execute();
                        System.out.println("Changed the status for product : " + (int) productId);
                    } catch (IOException e) {
                        service.responseError(response, "Changing the product status encountered an error!");
                        return String.valueOf(response);
                    }
                }

                service.responseSuccess(response);
                return String.valueOf(response);
            }
            // TODO: Here comes the status change request for OrderService
            // TODO: POST REQUEST to Order Service

            // TODO: Here comes the request for Product service (change quantity)
            // TODO: POST REQUEST TO Product Service
        }
        service.responseError(response, "Insufficient wallet balance.");
        return String.valueOf(response);
    }

    @RequestMapping(value = "/payment/{id}", method = RequestMethod.GET)
    public String getPayment(@PathVariable("id") String id) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            long userId = Long.parseLong(id);
            responseMap.put("bought", service.getPaymentsByBuyer(userId));
            responseMap.put("sold", service.getPaymentsBySeller(userId));
        } catch (NumberFormatException e) {
            responseMap.put("message", "Wrong user ID.");
        }
        return String.valueOf(gson.toJson(responseMap));
    }

}
