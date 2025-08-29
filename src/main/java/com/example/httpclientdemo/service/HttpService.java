package com.example.httpclientdemo.service;

import com.alibaba.fastjson2.JSONObject;
import com.example.httpclientdemo.model.CompleteMessageModel;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class HttpService {

    private final WebClient webClient;

    public HttpService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String sendRequest(String url, JSONObject txHeader, JSONObject txBody) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("txHeader", txHeader);
        requestBody.put("txBody", txBody);

        return webClient.post()
                .uri(url)
                .body(Mono.just(requestBody.toJSONString()), String.class)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String sendRequest(String url, CompleteMessageModel message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }

        return webClient.post()
                .uri(url)
                .body(Mono.just(message.toJson()), String.class)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
