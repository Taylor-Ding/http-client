package com.example.httpclientdemo.integration;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.httpclientdemo.builder.MessageBuilder;
import com.example.httpclientdemo.factory.TestDataFactory;
import com.example.httpclientdemo.model.CompleteMessageModel;
import com.example.httpclientdemo.service.HttpService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 端到端集成测试
 * 验证从报文构建到HTTP传输的完整链路
 * 测试真实业务场景下的数据处理
 * 确保所有组件协同工作正常
 */
@DisplayName("End-to-End Integration Tests")
class EndToEndIntegrationTest {
    
    private MockWebServer mockWebServer;
    private HttpService httpService;
    private String baseUrl;
    
    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        baseUrl = mockWebServer.url("/api/").toString();
        
        WebClient.Builder webClientBuilder = WebClient.builder();
        httpService = new HttpService(webClientBuilder);
    }
    
    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
    
    @Test
    @DisplayName("Should complete full business workflow - Standard Query Scenario")
    void shouldCompleteFullBusinessWorkflowStandardQueryScenario() throws InterruptedException {
        // Given - 完整的标准查询业务流程
        CompleteMessageModel queryMessage = MessageBuilder.create()
            .withDefaults()
            .withTxHeader(header -> header
                .msgGrptMac("E2E_QUERY_MAC_001")
                .globalBusiTrackNo("E2E_QUERY_TRACK_001")
                .subtxNo("E2E_QUERY_SUBTX_001")
                .txCode("QUERY001")
                .channelNo("WEB")
                .orgNo("001")
                .tellerId("QUERY_TELLER")
                .terminalId("WEB_TERMINAL_001")
                .clientIp("192.168.1.100")
                .remark("End-to-end query test")
            )
            .withTxEntity(entity -> entity
                .custNo("040000037480013")
                .qryVchrTpCd("1")
                .txSceneCd("C203")
                .addField("queryType", "ACCOUNT_BALANCE")
                .addField("accountNo", "1234567890123456")
                .addField("queryRange", "CURRENT_MONTH")
            )
            .withTxComn(comn -> comn
                .accountingDate("20240315")
                .curQryReqNum("1")
                .bgnIndexNo("0")
                .busiSendSysOrCmptNo("99710730008")
                .addtData("requestId", "REQ_20240315_001")
                .addtData("sessionId", "SESSION_12345")
                .txComn2("authLevel", "1")
                .txComn3("riskLevel", "LOW")
            )
            .build();
        
        // Mock realistic business response
        String mockResponse = """
            {
                "txHeader": {
                    "respCode": "0000",
                    "respMsg": "查询成功",
                    "msgGrptMac": "E2E_RESP_MAC_001",
                    "globalBusiTrackNo": "E2E_QUERY_TRACK_001",
                    "respTime": "20240315103000"
                },
                "txBody": {
                    "respEntity": {
                        "custNo": "040000037480013",
                        "accountNo": "1234567890123456",
                        "balance": "50000.00",
                        "currency": "CNY",
                        "status": "ACTIVE"
                    },
                    "respComn": {
                        "recordCount": "1",
                        "hasMore": "false",
                        "processTime": "20240315103000"
                    }
                }
            }
            """;
        
        mockWebServer.enqueue(new MockResponse()
            .setBody(mockResponse)
            .addHeader("Content-Type", "application/json")
            .setResponseCode(200));
        
        // When - 执行完整的业务流程
        String actualResponse = httpService.sendRequest(baseUrl + "query", queryMessage);
        
        // Then - 验证业务流程结果
        assertNotNull(actualResponse);
        JSONObject responseJson = JSON.parseObject(actualResponse);
        
        // 验证响应结构
        assertTrue(responseJson.containsKey("txHeader"));
        assertTrue(responseJson.containsKey("txBody"));
        
        JSONObject respHeader = responseJson.getJSONObject("txHeader");
        assertEquals("0000", respHeader.getString("respCode"));
        assertEquals("查询成功", respHeader.getString("respMsg"));
        assertEquals("E2E_QUERY_TRACK_001", respHeader.getString("globalBusiTrackNo"));
        
        JSONObject respBody = responseJson.getJSONObject("txBody");
        JSONObject respEntity = respBody.getJSONObject("respEntity");
        assertEquals("040000037480013", respEntity.getString("custNo"));
        assertEquals("50000.00", respEntity.getString("balance"));
        assertEquals("ACTIVE", respEntity.getString("status"));
        
        // 验证请求完整性
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("/api/query", recordedRequest.getPath());
        
        String requestBody = recordedRequest.getBody().readUtf8();
        JSONObject requestJson = JSON.parseObject(requestBody);
        
        // 验证完整的请求结构
        validateCompleteRequestStructure(requestJson, queryMessage);
        
        System.out.println("=== End-to-End Query Test Completed Successfully ===");
        System.out.println("Request: " + queryMessage.toPrettyJson());
        System.out.println("Response: " + JSON.toJSONString(responseJson, com.alibaba.fastjson2.JSONWriter.Feature.PrettyFormat));
    }
    
    @Test
    @DisplayName("Should complete full business workflow - Transfer Transaction Scenario")
    void shouldCompleteFullBusinessWorkflowTransferTransactionScenario() throws InterruptedException {
        // Given - 完整的转账交易业务流程
        CompleteMessageModel transferMessage = MessageBuilder.create()
            .withDefaults()
            .withTxHeader(header -> header
                .msgGrptMac("E2E_TRANSFER_MAC_001")
                .globalBusiTrackNo("E2E_TRANSFER_TRACK_001")
                .subtxNo("E2E_TRANSFER_SUBTX_001")
                .txCode("TRANSFER001")
                .channelNo("MOBILE")
                .orgNo("002")
                .tellerId("MOBILE_TELLER")
                .authTellerId("AUTH_TELLER_001")
                .terminalId("MOBILE_APP_001")
                .clientIp("10.0.0.100")
                .remark("End-to-end transfer test")
            )
            .withTxEntity(entity -> entity
                .custNo("123456789012345")
                .qryVchrTpCd("2")
                .txSceneCd("C204")
                .addField("fromAccount", "1111222233334444")
                .addField("toAccount", "5555666677778888")
                .addField("amount", "1000.00")
                .addField("currency", "CNY")
                .addField("transferType", "INTERNAL")
                .addField("memo", "End-to-end test transfer")
            )
            .withTxComn(comn -> comn
                .accountingDate("20240315")
                .curQryReqNum("0")
                .bgnIndexNo("0")
                .busiSendSysOrCmptNo("99710730008")
                .addtData("requestId", "TRF_REQ_20240315_001")
                .addtData("sessionId", "TRF_SESSION_67890")
                .addtData("deviceId", "MOBILE_DEVICE_001")
                .txComn2("authLevel", "2")
                .txComn2("authMethod", "SMS_OTP")
                .txComn3("riskLevel", "MEDIUM")
                .txComn3("riskScore", "75")
                .txComn4("feeAmount", "2.00")
                .txComn4("feeType", "TRANSFER_FEE")
            )
            .build();
        
        // Mock realistic transfer response
        String mockResponse = """
            {
                "txHeader": {
                    "respCode": "0000",
                    "respMsg": "转账成功",
                    "msgGrptMac": "E2E_TRANSFER_RESP_MAC",
                    "globalBusiTrackNo": "E2E_TRANSFER_TRACK_001",
                    "respTime": "20240315104500"
                },
                "txBody": {
                    "respEntity": {
                        "transferId": "TRF_20240315_001",
                        "fromAccount": "1111222233334444",
                        "toAccount": "5555666677778888",
                        "amount": "1000.00",
                        "fee": "2.00",
                        "status": "SUCCESS",
                        "transactionTime": "20240315104500"
                    },
                    "respComn": {
                        "balanceAfter": "49000.00",
                        "referenceNo": "REF_20240315_001",
                        "processTime": "20240315104500"
                    }
                }
            }
            """;
        
        mockWebServer.enqueue(new MockResponse()
            .setBody(mockResponse)
            .addHeader("Content-Type", "application/json")
            .setResponseCode(200));
        
        // When - 执行转账业务流程
        String actualResponse = httpService.sendRequest(baseUrl + "transfer", transferMessage);
        
        // Then - 验证转账流程结果
        assertNotNull(actualResponse);
        JSONObject responseJson = JSON.parseObject(actualResponse);
        
        // 验证转账响应
        JSONObject respHeader = responseJson.getJSONObject("txHeader");
        assertEquals("0000", respHeader.getString("respCode"));
        assertEquals("转账成功", respHeader.getString("respMsg"));
        
        JSONObject respEntity = responseJson.getJSONObject("txBody").getJSONObject("respEntity");
        assertEquals("TRF_20240315_001", respEntity.getString("transferId"));
        assertEquals("1000.00", respEntity.getString("amount"));
        assertEquals("SUCCESS", respEntity.getString("status"));
        
        // 验证请求完整性
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        String requestBody = recordedRequest.getBody().readUtf8();
        JSONObject requestJson = JSON.parseObject(requestBody);
        
        validateCompleteRequestStructure(requestJson, transferMessage);
        
        // 验证转账特定字段
        JSONObject txEntity = requestJson.getJSONObject("txBody").getJSONObject("txEntity");
        if (txEntity.containsKey("additionalFields")) {
            JSONObject additionalFields = txEntity.getJSONObject("additionalFields");
            assertEquals("1111222233334444", additionalFields.getString("fromAccount"));
            assertEquals("5555666677778888", additionalFields.getString("toAccount"));
            assertEquals("1000.00", additionalFields.getString("amount"));
        }
        
        System.out.println("=== End-to-End Transfer Test Completed Successfully ===");
    }
    
    @Test
    @DisplayName("Should handle complete business workflow with TestDataFactory scenarios")
    void shouldHandleCompleteBusinessWorkflowWithTestDataFactoryScenarios() throws InterruptedException {
        // Given - 使用TestDataFactory创建的各种业务场景
        CompleteMessageModel[] scenarios = {
            TestDataFactory.createStandardBusinessScenario(),
            TestDataFactory.createQueryBusinessScenario(),
            TestDataFactory.createTransferBusinessScenario()
        };
        
        String[] expectedResponses = {
            "{\"status\":\"success\",\"scenario\":\"standard\",\"code\":\"0000\"}",
            "{\"status\":\"success\",\"scenario\":\"query\",\"code\":\"0000\"}",
            "{\"status\":\"success\",\"scenario\":\"transfer\",\"code\":\"0000\"}"
        };
        
        // Mock responses for each scenario
        for (String response : expectedResponses) {
            mockWebServer.enqueue(new MockResponse()
                .setBody(response)
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200));
        }
        
        // When & Then - 执行所有业务场景
        for (int i = 0; i < scenarios.length; i++) {
            CompleteMessageModel scenario = scenarios[i];
            String expectedResponse = expectedResponses[i];
            
            // 验证消息有效性
            assertTrue(scenario.validate(), "Scenario " + i + " should be valid");
            assertTrue(scenario.validateFormat(), "Scenario " + i + " should have valid format");
            assertTrue(scenario.hasRequiredData(), "Scenario " + i + " should have required data");
            
            // 执行HTTP请求
            String actualResponse = httpService.sendRequest(baseUrl + "business", scenario);
            assertEquals(expectedResponse, actualResponse);
            
            // 验证请求结构
            RecordedRequest recordedRequest = mockWebServer.takeRequest();
            String requestBody = recordedRequest.getBody().readUtf8();
            JSONObject requestJson = JSON.parseObject(requestBody);
            
            validateCompleteRequestStructure(requestJson, scenario);
            
            System.out.println("=== Scenario " + i + " completed successfully ===");
        }
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"standard", "query", "transfer", "random"})
    @DisplayName("Should handle parameterized business scenarios end-to-end")
    void shouldHandleParameterizedBusinessScenariosEndToEnd(String scenarioType) throws InterruptedException {
        // Given - 参数化业务场景测试
        CompleteMessageModel message;
        switch (scenarioType) {
            case "standard":
                message = TestDataFactory.createStandardBusinessScenario();
                break;
            case "query":
                message = TestDataFactory.createQueryBusinessScenario();
                break;
            case "transfer":
                message = TestDataFactory.createTransferBusinessScenario();
                break;
            case "random":
                message = TestDataFactory.createRandomScenario();
                break;
            default:
                message = TestDataFactory.createStandardBusinessScenario();
        }
        
        String mockResponse = String.format(
            "{\"status\":\"success\",\"scenario\":\"%s\",\"code\":\"0000\",\"timestamp\":\"%d\"}",
            scenarioType, System.currentTimeMillis()
        );
        
        mockWebServer.enqueue(new MockResponse()
            .setBody(mockResponse)
            .addHeader("Content-Type", "application/json")
            .setResponseCode(200));
        
        // When - 执行参数化场景
        String actualResponse = httpService.sendRequest(baseUrl + "parameterized/" + scenarioType, message);
        
        // Then - 验证结果
        assertNotNull(actualResponse);
        JSONObject responseJson = JSON.parseObject(actualResponse);
        assertEquals("success", responseJson.getString("status"));
        assertEquals(scenarioType, responseJson.getString("scenario"));
        assertEquals("0000", responseJson.getString("code"));
        
        // 验证请求完整性
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("/api/parameterized/" + scenarioType, recordedRequest.getPath());
        
        String requestBody = recordedRequest.getBody().readUtf8();
        JSONObject requestJson = JSON.parseObject(requestBody);
        validateCompleteRequestStructure(requestJson, message);
        
        System.out.println("=== Parameterized scenario '" + scenarioType + "' completed ===");
    }
    
    @Test
    @DisplayName("Should handle complex nested data structures end-to-end")
    void shouldHandleComplexNestedDataStructuresEndToEnd() throws InterruptedException {
        // Given - 复杂嵌套数据结构的端到端测试
        Map<String, Object> complexParams = new HashMap<>();
        complexParams.put("header.channelNo", "COMPLEX_CHANNEL");
        complexParams.put("entity.nestedData", JSON.parseObject("{\"level1\":{\"level2\":{\"value\":\"deep_nested\"}}}"));
        complexParams.put("entity.arrayData", JSON.parseArray("[{\"id\":1,\"name\":\"item1\"},{\"id\":2,\"name\":\"item2\"}]"));
        complexParams.put("comn.complexMetadata", JSON.parseObject("{\"version\":\"2.0\",\"features\":[\"feature1\",\"feature2\"]}"));
        
        CompleteMessageModel complexMessage = TestDataFactory.createParameterizedScenario(
            "complex_nested", "999888777666555", "C999", complexParams);
        
        String mockResponse = """
            {
                "txHeader": {
                    "respCode": "0000",
                    "respMsg": "复杂结构处理成功"
                },
                "txBody": {
                    "respEntity": {
                        "processedData": {
                            "nestedResult": "processed",
                            "arrayCount": 2
                        }
                    }
                }
            }
            """;
        
        mockWebServer.enqueue(new MockResponse()
            .setBody(mockResponse)
            .addHeader("Content-Type", "application/json")
            .setResponseCode(200));
        
        // When - 执行复杂结构处理
        String actualResponse = httpService.sendRequest(baseUrl + "complex", complexMessage);
        
        // Then - 验证复杂结构处理结果
        assertNotNull(actualResponse);
        JSONObject responseJson = JSON.parseObject(actualResponse);
        assertEquals("0000", responseJson.getJSONObject("txHeader").getString("respCode"));
        assertEquals("复杂结构处理成功", responseJson.getJSONObject("txHeader").getString("respMsg"));
        
        // 验证请求中的复杂结构
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        String requestBody = recordedRequest.getBody().readUtf8();
        JSONObject requestJson = JSON.parseObject(requestBody);
        
        validateCompleteRequestStructure(requestJson, complexMessage);
        
        // 验证复杂嵌套数据
        JSONObject txEntity = requestJson.getJSONObject("txBody").getJSONObject("txEntity");
        if (txEntity.containsKey("additionalFields")) {
            JSONObject additionalFields = txEntity.getJSONObject("additionalFields");
            
            // 验证嵌套对象
            if (additionalFields.containsKey("nestedData")) {
                JSONObject nestedData = additionalFields.getJSONObject("nestedData");
                assertEquals("deep_nested", 
                    nestedData.getJSONObject("level1").getJSONObject("level2").getString("value"));
            }
            
            // 验证数组数据
            if (additionalFields.containsKey("arrayData")) {
                assertEquals(2, additionalFields.getJSONArray("arrayData").size());
            }
        }
        
        System.out.println("=== Complex nested structure test completed ===");
    }
    
    @Test
    @DisplayName("Should handle error scenarios and recovery end-to-end")
    void shouldHandleErrorScenariosAndRecoveryEndToEnd() throws InterruptedException {
        // Given - 错误场景和恢复测试
        CompleteMessageModel errorMessage = TestDataFactory.createStandardBusinessScenario();
        
        // First request fails
        mockWebServer.enqueue(new MockResponse()
            .setBody("{\"txHeader\":{\"respCode\":\"9999\",\"respMsg\":\"系统繁忙，请稍后重试\"}}")
            .addHeader("Content-Type", "application/json")
            .setResponseCode(500));
        
        // Retry request succeeds
        mockWebServer.enqueue(new MockResponse()
            .setBody("{\"txHeader\":{\"respCode\":\"0000\",\"respMsg\":\"重试成功\"}}")
            .addHeader("Content-Type", "application/json")
            .setResponseCode(200));
        
        // When - 执行错误场景和重试
        try {
            // First request should fail with 500 error
            assertThrows(Exception.class, () -> {
                httpService.sendRequest(baseUrl + "error-test", errorMessage);
            }, "First request should fail with 500 error");
            
            // Simulate retry logic - second request succeeds
            String retryResponse = httpService.sendRequest(baseUrl + "error-test", errorMessage);
            JSONObject retryJson = JSON.parseObject(retryResponse);
            assertEquals("0000", retryJson.getJSONObject("txHeader").getString("respCode"));
            assertEquals("重试成功", retryJson.getJSONObject("txHeader").getString("respMsg"));
            
        } catch (Exception e) {
            fail("Should handle error scenarios gracefully: " + e.getMessage());
        }
        
        // Then - 验证两次请求都正确发送
        assertEquals(2, mockWebServer.getRequestCount());
        
        RecordedRequest firstRequest = mockWebServer.takeRequest();
        RecordedRequest retryRequest = mockWebServer.takeRequest();
        
        // 验证两次请求结构一致
        String firstRequestBody = firstRequest.getBody().readUtf8();
        String retryRequestBody = retryRequest.getBody().readUtf8();
        
        JSONObject firstRequestJson = JSON.parseObject(firstRequestBody);
        JSONObject retryRequestJson = JSON.parseObject(retryRequestBody);
        
        validateCompleteRequestStructure(firstRequestJson, errorMessage);
        validateCompleteRequestStructure(retryRequestJson, errorMessage);
        
        System.out.println("=== Error handling and recovery test completed ===");
    }
    
    @Test
    @DisplayName("Should validate complete message lifecycle end-to-end")
    void shouldValidateCompleteMessageLifecycleEndToEnd() throws InterruptedException {
        // Given - 完整的消息生命周期测试
        CompleteMessageModel originalMessage = MessageBuilder.create()
            .withDefaults()
            .withTxHeader(header -> header
                .msgGrptMac("LIFECYCLE_MAC_001")
                .globalBusiTrackNo("LIFECYCLE_TRACK_001")
                .subtxNo("LIFECYCLE_SUBTX_001")
                .txCode("LIFECYCLE001")
                .remark("Complete lifecycle test")
            )
            .withTxEntity(entity -> entity
                .custNo("111222333444555")
                .qryVchrTpCd("3")
                .txSceneCd("C301")
                .addField("lifecycleStage", "INITIAL")
                .addField("timestamp", System.currentTimeMillis())
            )
            .withTxComn(comn -> comn
                .accountingDate("20240315")
                .addtData("lifecycleId", "LC_001")
                .addtData("stage", "START")
                .busiSendSysOrCmptNo("99710730008")
            )
            .build();
        
        // Mock response that includes request tracking
        String mockResponse = String.format("""
            {
                "txHeader": {
                    "respCode": "0000",
                    "respMsg": "生命周期测试成功",
                    "msgGrptMac": "LIFECYCLE_MAC_001",
                    "globalBusiTrackNo": "LIFECYCLE_TRACK_001",
                    "respTime": "%d"
                },
                "txBody": {
                    "respEntity": {
                        "lifecycleId": "LC_001",
                        "stage": "COMPLETED",
                        "originalStage": "INITIAL",
                        "processedAt": "%d"
                    }
                }
            }
            """, System.currentTimeMillis(), System.currentTimeMillis());
        
        mockWebServer.enqueue(new MockResponse()
            .setBody(mockResponse)
            .addHeader("Content-Type", "application/json")
            .setResponseCode(200));
        
        // When - 执行完整生命周期
        long startTime = System.currentTimeMillis();
        String actualResponse = httpService.sendRequest(baseUrl + "lifecycle", originalMessage);
        long endTime = System.currentTimeMillis();
        
        // Then - 验证完整生命周期
        assertNotNull(actualResponse);
        JSONObject responseJson = JSON.parseObject(actualResponse);
        
        // 验证响应追踪信息
        JSONObject respHeader = responseJson.getJSONObject("txHeader");
        assertEquals("LIFECYCLE_MAC_001", respHeader.getString("msgGrptMac"));
        assertEquals("LIFECYCLE_TRACK_001", respHeader.getString("globalBusiTrackNo"));
        
        JSONObject respEntity = responseJson.getJSONObject("txBody").getJSONObject("respEntity");
        assertEquals("LC_001", respEntity.getString("lifecycleId"));
        assertEquals("COMPLETED", respEntity.getString("stage"));
        assertEquals("INITIAL", respEntity.getString("originalStage"));
        
        // 验证请求完整性和性能
        RecordedRequest recordedRequest = mockWebServer.takeRequest(5, TimeUnit.SECONDS);
        assertNotNull(recordedRequest, "Request should be recorded within timeout");
        
        long requestTime = endTime - startTime;
        assertTrue(requestTime < 5000, "Request should complete within 5 seconds, actual: " + requestTime + "ms");
        
        String requestBody = recordedRequest.getBody().readUtf8();
        JSONObject requestJson = JSON.parseObject(requestBody);
        
        validateCompleteRequestStructure(requestJson, originalMessage);
        
        // 验证生命周期数据完整性
        JSONObject txEntity = requestJson.getJSONObject("txBody").getJSONObject("txEntity");
        if (txEntity.containsKey("additionalFields")) {
            JSONObject additionalFields = txEntity.getJSONObject("additionalFields");
            assertEquals("INITIAL", additionalFields.getString("lifecycleStage"));
            assertTrue(additionalFields.containsKey("timestamp"));
        }
        
        JSONObject addtData = requestJson.getJSONObject("txBody").getJSONObject("addtData");
        assertEquals("LC_001", addtData.getString("lifecycleId"));
        assertEquals("START", addtData.getString("stage"));
        
        System.out.println("=== Complete message lifecycle test completed ===");
        System.out.println("Request processing time: " + requestTime + "ms");
    }
    
    /**
     * 验证完整的请求结构
     */
    private void validateCompleteRequestStructure(JSONObject requestJson, CompleteMessageModel originalMessage) {
        // 验证顶级结构
        assertTrue(requestJson.containsKey("txHeader"), "Request should contain txHeader");
        assertTrue(requestJson.containsKey("txBody"), "Request should contain txBody");
        
        // 验证txHeader结构
        JSONObject txHeader = requestJson.getJSONObject("txHeader");
        assertNotNull(txHeader.getString("msgGrptMac"), "msgGrptMac should not be null");
        assertNotNull(txHeader.getString("globalBusiTrackNo"), "globalBusiTrackNo should not be null");
        assertNotNull(txHeader.getString("subtxNo"), "subtxNo should not be null");
        
        // 验证txBody结构
        JSONObject txBody = requestJson.getJSONObject("txBody");
        assertTrue(txBody.containsKey("txEntity"), "txBody should contain txEntity");
        assertNotNull(txBody.getString("accountingDate"), "accountingDate should not be null");
        
        // 验证txEntity结构
        JSONObject txEntity = txBody.getJSONObject("txEntity");
        assertNotNull(txEntity.getString("custNo"), "custNo should not be null");
        assertNotNull(txEntity.getString("qryVchrTpCd"), "qryVchrTpCd should not be null");
        assertNotNull(txEntity.getString("txSceneCd"), "txSceneCd should not be null");
        
        // 验证txComn结构存在
        assertTrue(txBody.containsKey("txComn1"), "txBody should contain txComn1");
        assertTrue(txBody.containsKey("txComn8"), "txBody should contain txComn8");
        assertTrue(txBody.containsKey("addtData"), "txBody should contain addtData");
        
        // 验证原始消息的有效性
        assertTrue(originalMessage.validate(), "Original message should be valid");
        assertTrue(originalMessage.hasRequiredData(), "Original message should have required data");
    }
}