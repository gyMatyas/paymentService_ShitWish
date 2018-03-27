package com.shitwish.paymentService.PaymentServiceApp.Controller;

import com.google.gson.Gson;
import com.shitwish.paymentService.PaymentServiceApp.Model.Payment;
import com.shitwish.paymentService.PaymentServiceApp.Service.PaymentService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
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
    public String initiatePayment(@RequestParam("userId") String json) {
        int userId = Integer.valueOf(json);

        // TODO: Here comes the request for UserService (getting the buyer)

        String buyerResponse = restTemplate.getForObject("https://shitwish-user.herokuapp.com/user/" + userId, String.class);
        JSONObject buyerJSON = new JSONObject(buyerResponse);
        int buyerBalance = (int) buyerJSON.get("balance");

        // TODO: Here comes the request for OrderService (getting the order with products) with fake status

        //String orderResponse = restTemplate.getForObject("orderservice/getForUser/" + userId, String.class);
        JSONObject orderJSON = new JSONObject("{ 'orders': [ { 'field1' : 'hello' }, { 'field1' : 'world' } ]} ");
        JSONArray orders = (JSONArray) orderJSON.get("orders");
                //TODO: Getting the seller's id from products and price
                for (int i = 0; i < orders.length(); i++){
                    System.out.println(orders.getJSONObject(i).get("field1"));
                }

        // TODO: Here comes process of payment

        boolean succeeded = true;
        // TODO: POST REQUESTS TO User Service to change balance


        // TODO: Here comes the status change request for OrderService
        // TODO: POST REQUEST to Order Service

        // TODO: Here comes the request for Product service (change quantity)
        // TODO: POST REQUEST TO Product Service


        JSONObject response = new JSONObject();

        if (succeeded) {
            Payment payment = new Payment( (long) userId, (long) userId, buyerBalance);
            service.savePayment(payment);
            response.put("success", true);
            response.put("message", "Payment successful!");
        } else {
            response.put("success", false);
            response.put("message", "Insufficient wallet balance.");
        }
        return String.valueOf(response);
    }


    @RequestMapping(value = "/payment/{id}", method = RequestMethod.GET)
    public String getPayment(@PathVariable("id") String id) {
        Map<String, Object> responseObject = new HashMap<>();

        /** DIS IS HOW YOU POST SOMETHING TY JAVA //
        try {
            Request.Post("http://localhost:8080/pay")
                    .bodyForm(Form.form().add("userId", "1").build())
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
         **/
        try {
            long userId = Long.parseLong(id);
            responseObject.put("bought", service.getPaymentsByBuyer(userId));
            responseObject.put("sold", service.getPaymentsBySeller(userId));
        } catch (NumberFormatException e) {
            responseObject.put("message", "Wrong user ID.");
        }
        return String.valueOf(gson.toJson(responseObject));
    }

}
