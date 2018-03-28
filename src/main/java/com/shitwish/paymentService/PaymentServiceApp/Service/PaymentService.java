package com.shitwish.paymentService.PaymentServiceApp.Service;

import com.google.gson.Gson;
import com.shitwish.paymentService.PaymentServiceApp.Model.Payment;
import com.shitwish.paymentService.PaymentServiceApp.Repository.PaymentRepository;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {

    private Gson gson = new Gson();

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    PaymentRepository repository;

    public List<Payment> getPaymentsByBuyer(long id) {
        return repository.getAllByBuyer(id);
    }

    public List<Payment> getPaymentsBySeller(long id) {
        return repository.getAllBySeller(id);
    }

    public void savePayment(Payment payment) { repository.save(payment); }

    public Payment payUser(long userId, Map<String, String> seller) throws IOException {
        String postUrl = "https://shitwish-user.herokuapp.com/user/" + seller.get("userId");
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(postUrl);
        StringEntity postingString = new StringEntity(gson.toJson(seller));
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        HttpResponse response = httpClient.execute(post);
        System.out.println("User " + seller.get("userId") + " paid for " + seller.get("value"));
        System.out.println(EntityUtils.toString(response.getEntity()));
        return new Payment(userId, Long.valueOf(seller.get("userId")), Integer.valueOf(seller.get("value")));
    }

    public double getSellersData(double totalPrice, List<Map<String, String>> sellers, int productId) {
        String product = restTemplate.getForObject("http://shitwish-product.herokuapp.com/products/" + productId, String.class);
        Map<String, String> sellerInfo = new HashMap<>();
        JSONObject productObject = new JSONObject(product);
        int currentPrice = (int) productObject.get("price");
        int currentUserId = (int) productObject.get("userId");
        sellerInfo.put("userId", String.valueOf(currentUserId));
        sellerInfo.put("value", String.valueOf(currentPrice));
        sellers.add(sellerInfo);
        totalPrice += currentPrice;
        System.out.println("Total price : " + (int) totalPrice);
        System.out.println("Seller Info: " + sellerInfo);
        return totalPrice;
    }

    public void withdrawUser(long userId, int totalPrice) throws IOException {
        Map<String, String> buyer = new HashMap<>();
        buyer.put("userId", String.valueOf(userId));
        buyer.put("value", String.valueOf(totalPrice * -1));
        String postUrl = "https://shitwish-user.herokuapp.com/user/" + buyer.get("userId");
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(postUrl);
        StringEntity postingString = new StringEntity(gson.toJson(buyer));
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        HttpResponse response = httpClient.execute(post);
        System.out.println("User " + buyer.get("userId") + " withdraw for " + buyer.get("value"));
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    public void responseSuccess(JSONObject response) {
        response.put("success", true);
        response.put("message", "Payment successful!");
    }

    public void responseError(JSONObject response, String errorString) {
        response.put("success", false);
        response.put("message", errorString);
    }
}
