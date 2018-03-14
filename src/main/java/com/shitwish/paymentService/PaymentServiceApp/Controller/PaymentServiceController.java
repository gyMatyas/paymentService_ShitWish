package com.shitwish.paymentService.PaymentServiceApp.Controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class PaymentServiceController {
    private final RestTemplate restTemplate = new RestTemplate();

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "HELLO BOI";
    }

    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    public String initiatePayment(@RequestParam("data") String json) {
        JSONObject request = new JSONObject(json);
        int userId = (int) request.get("userId");

        // TODO: Here comes the request for UserService (getting the buyer)

        /*String buyerResponse = restTemplate.getForObject("userservice/user/" + userId, String.class);
        JSONObject buyerJSON = new JSONObject(buyerResponse);
        float buyerWallet = (float) buyerJSON.get("wallet");
        */
        // TODO: Here comes the request for OrderService (getting the order with products) with fake status

        //String orderResponse = restTemplate.getForObject("orderservice/getForUser/" + userId, String.class);
        JSONObject orderJSON = new JSONObject("{ 'orders': [ { 'field1' : 'hello' }, { 'field1' : 'world' } ]} ");
        JSONArray orders = (JSONArray) orderJSON.get("orders");

                //TODO: Getting the seller's id from products and price

                for (int i = 0; i < orders.length(); i++){
                    System.out.println(orders.getJSONObject(i).get("field1"));
                }

        // TODO: Here comes process of payment

        // TODO: Here comes the status change request for OrderService

        // TODO: Here comes the request for Product service (change quantity)


        JSONObject response = new JSONObject();

        return String.valueOf(response.put("success", true));
    }


}
