package com.shitwish.paymentService.PaymentServiceApp.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentServiceController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "HELLO BOI";
    }

    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    public String initiatePayment(@RequestParam("hello") String hello) {
        return hello;
    }


}
