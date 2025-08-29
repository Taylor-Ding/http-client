package com.example.httpclientdemo.service;

import com.alibaba.fastjson2.JSONObject;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpServiceTest {

    private MockWebServer mockWebServer;
    private HttpService httpService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        WebClient.Builder webClientBuilder = WebClient.builder();
        httpService = new HttpService(webClientBuilder);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void sendRequest() throws InterruptedException {
        // Given
        String url = mockWebServer.url("/").toString();
        JSONObject txHeader = new JSONObject();
        txHeader.put("testHeader", "headerValue");
        JSONObject txBody = new JSONObject();
        txBody.put("testBody", "bodyValue");

        String expectedResponse = "{\"status\":\"success\"}";
        mockWebServer.enqueue(new MockResponse().setBody(expectedResponse).addHeader("Content-Type", "application/json"));

        // When
        String actualResponse = httpService.sendRequest(url, txHeader, txBody);

        // Then
        assertEquals(expectedResponse, actualResponse);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        String receivedBody = recordedRequest.getBody().readUtf8();

        JSONObject expectedRequestBody = new JSONObject();
        expectedRequestBody.put("txHeader", txHeader);
        expectedRequestBody.put("txBody", txBody);

        assertEquals(expectedRequestBody.toJSONString(), receivedBody);
    }
}
