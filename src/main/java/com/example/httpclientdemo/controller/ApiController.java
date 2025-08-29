package com.example.httpclientdemo.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.httpclientdemo.service.HttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @Autowired
    private HttpService httpService;

    @PostMapping("/api/send")
    public String sendApiRequest(@RequestBody String requestBody) {
        JSONObject requestJson = JSONObject.parseObject(requestBody);
        JSONObject txHeader = requestJson.getJSONObject("txHeader");
        JSONObject txBody = requestJson.getJSONObject("txBody");

        // For this example, we'll call a mock endpoint on the same server.
        String url = "http://localhost:8080/mock-service";

        return httpService.sendRequest(url, txHeader, txBody);
    }

    @PostMapping("/mock-service")
    public String mockService(@RequestBody String requestBody) {
        System.out.println("Mock service received request: " + requestBody);
        JSONObject response = new JSONObject();
        response.put("status", "success");
        response.put("message", "Request received successfully by mock service");
        return response.toJSONString();
    }
}
