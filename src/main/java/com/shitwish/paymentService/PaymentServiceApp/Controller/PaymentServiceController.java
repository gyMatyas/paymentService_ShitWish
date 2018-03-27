package com.shitwish.paymentService.PaymentServiceApp.Controller;

import com.shitwish.paymentService.PaymentServiceApp.Model.Payment;
import com.shitwish.paymentService.PaymentServiceApp.Service.PaymentService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class PaymentServiceController {

    @Autowired
    PaymentService service;

    private final RestTemplate restTemplate = new RestTemplate();

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "Payment service";
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
        // TODO: Here comes the status change request for OrderService

        // TODO: Here comes the request for Product service (change quantity)


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
        JSONObject response = new JSONObject();

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
            response.put("bought", service.getPaymentsByBuyer(userId));
            response.put("sold", service.getPaymentsBySeller(userId));
        } catch (NumberFormatException e) {
            response.put("message", "Wrong user ID.");
        }
        return String.valueOf(response);
    }

}
