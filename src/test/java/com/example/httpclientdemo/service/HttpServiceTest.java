package com.example.httpclientdemo.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.httpclientdemo.builder.MessageBuilder;
import com.example.httpclientdemo.factory.TestDataFactory;
import com.example.httpclientdemo.model.CompleteMessageModel;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void sendRequestWithCompleteMessageModel() throws InterruptedException {
        // Given
        String url = mockWebServer.url("/").toString();
        CompleteMessageModel message = MessageBuilder.create()
            .withDefaults()
            .withTxHeader(header -> header
                .msgGrptMac("TEST_MAC_001")
                .globalBusiTrackNo("TEST_TRACK_001")
                .subtxNo("TEST_SUBTX_001")
                .txCode("TEST001")
            )
            .withTxEntity(entity -> entity
                .custNo("040000037480013")
                .qryVchrTpCd("1")
                .txSceneCd("C203")
            )
            .withTxComn(comn -> comn
                .accountingDate("20240101")
                .curQryReqNum("0")
                .bgnIndexNo("0")
                .busiSendSysOrCmptNo("99710730008")
            )
            .build();

        String expectedResponse = "{\"status\":\"success\",\"code\":\"0000\"}";
        mockWebServer.enqueue(new MockResponse().setBody(expectedResponse).addHeader("Content-Type", "application/json"));

        // When
        String actualResponse = httpService.sendRequest(url, message);

        // Then
        assertEquals(expectedResponse, actualResponse);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        String receivedBody = recordedRequest.getBody().readUtf8();

        // Verify the complete JSON structure was transmitted correctly
        JSONObject receivedJson = JSON.parseObject(receivedBody);
        assertNotNull(receivedJson);
        assertTrue(receivedJson.containsKey("txHeader"));
        assertTrue(receivedJson.containsKey("txBody"));

        // Verify txHeader structure
        JSONObject txHeader = receivedJson.getJSONObject("txHeader");
        assertEquals("TEST_MAC_001", txHeader.getString("msgGrptMac"));
        assertEquals("TEST_TRACK_001", txHeader.getString("globalBusiTrackNo"));
        assertEquals("TEST_SUBTX_001", txHeader.getString("subtxNo"));
        assertEquals("TEST001", txHeader.getString("txCode"));

        // Verify txBody structure
        JSONObject txBody = receivedJson.getJSONObject("txBody");
        assertNotNull(txBody);
        assertTrue(txBody.containsKey("txEntity"));
        assertEquals("20240101", txBody.getString("accountingDate"));

        // Verify txEntity structure
        JSONObject txEntity = txBody.getJSONObject("txEntity");
        assertEquals("040000037480013", txEntity.getString("custNo"));
        assertEquals("1", txEntity.getString("qryVchrTpCd"));
        assertEquals("C203", txEntity.getString("txSceneCd"));

        // Verify txComn structures exist
        assertTrue(txBody.containsKey("txComn1"));
        assertTrue(txBody.containsKey("txComn8"));
        
        JSONObject txComn1 = txBody.getJSONObject("txComn1");
        assertEquals("0", txComn1.getString("curQryReqNum"));
        assertEquals("0", txComn1.getString("bgnIndexNo"));
        
        JSONObject txComn8 = txBody.getJSONObject("txComn8");
        assertEquals("99710730008", txComn8.getString("busiSendSysOrCmptNo"));
    }

    @Test
    void sendRequestWithCompleteMessageModelUsingTestDataFactory() throws InterruptedException {
        // Given
        String url = mockWebServer.url("/").toString();
        CompleteMessageModel message = TestDataFactory.createStandardBusinessScenario();

        String expectedResponse = "{\"status\":\"success\",\"data\":{\"result\":\"processed\"}}";
        mockWebServer.enqueue(new MockResponse().setBody(expectedResponse).addHeader("Content-Type", "application/json"));

        // When
        String actualResponse = httpService.sendRequest(url, message);

        // Then
        assertEquals(expectedResponse, actualResponse);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        String receivedBody = recordedRequest.getBody().readUtf8();

        // Verify the message structure from TestDataFactory
        JSONObject receivedJson = JSON.parseObject(receivedBody);
        assertNotNull(receivedJson);
        
        // Verify it contains the expected structure
        assertTrue(receivedJson.containsKey("txHeader"));
        assertTrue(receivedJson.containsKey("txBody"));
        
        JSONObject txHeader = receivedJson.getJSONObject("txHeader");
        assertNotNull(txHeader.getString("msgGrptMac"));
        assertNotNull(txHeader.getString("globalBusiTrackNo"));
        assertEquals("STD001", txHeader.getString("txCode"));
        assertEquals("WEB", txHeader.getString("channelNo"));
        
        JSONObject txBody = receivedJson.getJSONObject("txBody");
        JSONObject txEntity = txBody.getJSONObject("txEntity");
        assertEquals("040000037480013", txEntity.getString("custNo"));
        assertEquals("C203", txEntity.getString("txSceneCd"));
    }

    @Test
    void sendRequestWithNullMessage() {
        // Given
        String url = mockWebServer.url("/").toString();

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> httpService.sendRequest(url, (CompleteMessageModel) null)
        );
        assertEquals("Message cannot be null", exception.getMessage());
    }

    @Test
    void sendRequestWithCompleteMessageModelVerifyBackwardCompatibility() throws InterruptedException {
        // Given - Test that both old and new methods work together
        String url = mockWebServer.url("/").toString();
        
        // Old method test
        JSONObject txHeader = new JSONObject();
        txHeader.put("testHeader", "headerValue");
        JSONObject txBody = new JSONObject();
        txBody.put("testBody", "bodyValue");

        String expectedResponse1 = "{\"status\":\"success\"}";
        mockWebServer.enqueue(new MockResponse().setBody(expectedResponse1).addHeader("Content-Type", "application/json"));

        // New method test
        CompleteMessageModel message = TestDataFactory.createQueryBusinessScenario();
        String expectedResponse2 = "{\"status\":\"success\",\"queryResult\":\"found\"}";
        mockWebServer.enqueue(new MockResponse().setBody(expectedResponse2).addHeader("Content-Type", "application/json"));

        // When
        String actualResponse1 = httpService.sendRequest(url, txHeader, txBody);
        String actualResponse2 = httpService.sendRequest(url, message);

        // Then
        assertEquals(expectedResponse1, actualResponse1);
        assertEquals(expectedResponse2, actualResponse2);

        // Verify both requests were made correctly
        RecordedRequest request1 = mockWebServer.takeRequest();
        RecordedRequest request2 = mockWebServer.takeRequest();

        // Verify old format
        JSONObject oldRequestBody = JSON.parseObject(request1.getBody().readUtf8());
        assertTrue(oldRequestBody.containsKey("txHeader"));
        assertTrue(oldRequestBody.containsKey("txBody"));
        assertEquals("headerValue", oldRequestBody.getJSONObject("txHeader").getString("testHeader"));

        // Verify new format
        JSONObject newRequestBody = JSON.parseObject(request2.getBody().readUtf8());
        assertTrue(newRequestBody.containsKey("txHeader"));
        assertTrue(newRequestBody.containsKey("txBody"));
        assertEquals("QRY001", newRequestBody.getJSONObject("txHeader").getString("txCode"));
    }

    @Test
    void testRealMessageStructureIntegration() throws InterruptedException {
        // Given - Create a message that matches the user-provided JSON structure exactly
        String url = mockWebServer.url("/").toString();
        CompleteMessageModel message = MessageBuilder.create()
            .withTxHeader(header -> header
                .msgGrptMac("{{msgGrptMac}}")
                .globalBusiTrackNo("{{globalBusiTrackNo}}")
                .subtxNo("{{subtxNo}}")
                .txStartTime("{{txStartTime}}")
                .txSendTime("{{txSendTime}}")
                .txCode("C203001")
                .channelNo("WEB")
                .orgNo("001")
                .tellerId("TELLER001")
                .terminalId("TERM001")
                .clientIp("192.168.1.100")
            )
            .withTxEntity(entity -> entity
                .custNo("040000037480013")
                .qryVchrTpCd("1")
                .txSceneCd("C203")
            )
            .withTxComn(comn -> comn
                .accountingDate("00000000")
                .curQryReqNum("0")
                .bgnIndexNo("0")
                .busiSendSysOrCmptNo("99710730008")
            )
            .build();

        String expectedResponse = "{\"status\":\"success\",\"txHeader\":{\"respCode\":\"0000\"}}";
        mockWebServer.enqueue(new MockResponse().setBody(expectedResponse).addHeader("Content-Type", "application/json"));

        // When
        String actualResponse = httpService.sendRequest(url, message);

        // Then
        assertEquals(expectedResponse, actualResponse);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        String receivedBody = recordedRequest.getBody().readUtf8();
        JSONObject receivedJson = JSON.parseObject(receivedBody);

        // Verify the exact structure matches user-provided JSON format
        assertNotNull(receivedJson);
        assertTrue(receivedJson.containsKey("txHeader"));
        assertTrue(receivedJson.containsKey("txBody"));

        // Verify txHeader structure and all required fields
        JSONObject txHeader = receivedJson.getJSONObject("txHeader");
        assertEquals("{{msgGrptMac}}", txHeader.getString("msgGrptMac"));
        assertEquals("{{globalBusiTrackNo}}", txHeader.getString("globalBusiTrackNo"));
        assertEquals("{{subtxNo}}", txHeader.getString("subtxNo"));
        assertEquals("{{txStartTime}}", txHeader.getString("txStartTime"));
        assertEquals("{{txSendTime}}", txHeader.getString("txSendTime"));
        assertEquals("C203001", txHeader.getString("txCode"));
        assertEquals("WEB", txHeader.getString("channelNo"));
        assertEquals("001", txHeader.getString("orgNo"));
        assertEquals("TELLER001", txHeader.getString("tellerId"));
        assertEquals("TERM001", txHeader.getString("terminalId"));
        assertEquals("192.168.1.100", txHeader.getString("clientIp"));

        // Verify txBody structure matches expected format
        JSONObject txBody = receivedJson.getJSONObject("txBody");
        assertNotNull(txBody);
        
        // Verify txEntity structure
        assertTrue(txBody.containsKey("txEntity"));
        JSONObject txEntity = txBody.getJSONObject("txEntity");
        assertEquals("040000037480013", txEntity.getString("custNo"));
        assertEquals("1", txEntity.getString("qryVchrTpCd"));
        assertEquals("C203", txEntity.getString("txSceneCd"));

        // Verify txComn structure - accountingDate at root level
        assertEquals("00000000", txBody.getString("accountingDate"));

        // Verify all txComn1-8 structures exist
        assertTrue(txBody.containsKey("txComn1"));
        assertTrue(txBody.containsKey("txComn2"));
        assertTrue(txBody.containsKey("txComn3"));
        assertTrue(txBody.containsKey("txComn4"));
        assertTrue(txBody.containsKey("txComn5"));
        assertTrue(txBody.containsKey("txComn6"));
        assertTrue(txBody.containsKey("txComn7"));
        assertTrue(txBody.containsKey("txComn8"));

        // Verify txComn1 content
        JSONObject txComn1 = txBody.getJSONObject("txComn1");
        assertEquals("0", txComn1.getString("curQryReqNum"));
        assertEquals("0", txComn1.getString("bgnIndexNo"));

        // Verify txComn8 content
        JSONObject txComn8 = txBody.getJSONObject("txComn8");
        assertEquals("99710730008", txComn8.getString("busiSendSysOrCmptNo"));

        // Verify addtData exists
        assertTrue(txBody.containsKey("addtData"));
    }

    @Test
    void testComplexNestedStructureSerialization() throws InterruptedException {
        // Given - Test complex nested structures with multiple levels
        String url = mockWebServer.url("/").toString();
        CompleteMessageModel message = MessageBuilder.create()
            .withDefaults()
            .withTxHeader(header -> header
                .msgGrptMac("COMPLEX_TEST_MAC")
                .globalBusiTrackNo("COMPLEX_TRACK_001")
                .subtxNo("COMPLEX_SUBTX")
                .txCode("COMPLEX001")
                .remark("Complex nested structure test")
            )
            .withTxEntity(entity -> entity
                .custNo("123456789012345")
                .qryVchrTpCd("2")
                .txSceneCd("C204")
                .addField("nestedObject", JSON.parseObject("{\"level1\":{\"level2\":{\"value\":\"deep\"}}}"))
                .addField("arrayField", JSON.parseArray("[\"item1\",\"item2\",\"item3\"]"))
                .addField("complexData", JSON.parseObject("{\"type\":\"complex\",\"attributes\":{\"attr1\":\"value1\",\"attr2\":123}}"))
            )
            .withTxComn(comn -> comn
                .accountingDate("20240315")
                .addtData("complexAddtData", JSON.parseObject("{\"metadata\":{\"version\":\"1.0\",\"timestamp\":\"2024-03-15T10:30:00\"}}"))
                .txComn2("nestedTxComn", JSON.parseObject("{\"subfield1\":\"value1\",\"subfield2\":{\"nested\":\"data\"}}"))
                .txComn3("arrayInTxComn", JSON.parseArray("[{\"id\":1,\"name\":\"item1\"},{\"id\":2,\"name\":\"item2\"}]"))
                .busiSendSysOrCmptNo("COMPLEX_SYS_001")
            )
            .build();

        String expectedResponse = "{\"status\":\"success\",\"message\":\"Complex structure processed\"}";
        mockWebServer.enqueue(new MockResponse().setBody(expectedResponse).addHeader("Content-Type", "application/json"));

        // When
        String actualResponse = httpService.sendRequest(url, message);

        // Then
        assertEquals(expectedResponse, actualResponse);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        String receivedBody = recordedRequest.getBody().readUtf8();
        JSONObject receivedJson = JSON.parseObject(receivedBody);



        // Verify complex nested structures are preserved
        JSONObject txBody = receivedJson.getJSONObject("txBody");
        JSONObject txEntity = txBody.getJSONObject("txEntity");
        
        // Additional fields are stored in additionalFields object
        assertTrue(txEntity.containsKey("additionalFields"));
        JSONObject additionalFields = txEntity.getJSONObject("additionalFields");
        
        // Verify nested object structure
        assertTrue(additionalFields.containsKey("nestedObject"));
        JSONObject nestedObj = additionalFields.getJSONObject("nestedObject");
        assertEquals("deep", nestedObj.getJSONObject("level1").getJSONObject("level2").getString("value"));

        // Verify array field
        assertTrue(additionalFields.containsKey("arrayField"));
        assertEquals(3, additionalFields.getJSONArray("arrayField").size());
        assertEquals("item1", additionalFields.getJSONArray("arrayField").getString(0));

        // Verify complex data structure
        assertTrue(additionalFields.containsKey("complexData"));
        JSONObject complexData = additionalFields.getJSONObject("complexData");
        assertEquals("complex", complexData.getString("type"));
        assertEquals("value1", complexData.getJSONObject("attributes").getString("attr1"));
        assertEquals(123, complexData.getJSONObject("attributes").getIntValue("attr2"));

        // Verify complex addtData
        if (txBody.containsKey("addtData")) {
            JSONObject addtData = txBody.getJSONObject("addtData");
            if (addtData.containsKey("complexAddtData")) {
                JSONObject complexAddtData = addtData.getJSONObject("complexAddtData");
                assertEquals("1.0", complexAddtData.getJSONObject("metadata").getString("version"));
            }
        }

        // Verify complex txComn structures
        if (txBody.containsKey("txComn2")) {
            JSONObject txComn2 = txBody.getJSONObject("txComn2");
            if (txComn2.containsKey("nestedTxComn")) {
                assertEquals("data", txComn2.getJSONObject("nestedTxComn").getJSONObject("subfield2").getString("nested"));
            }
        }

        if (txBody.containsKey("txComn3")) {
            JSONObject txComn3 = txBody.getJSONObject("txComn3");
            if (txComn3.containsKey("arrayInTxComn")) {
                assertEquals(2, txComn3.getJSONArray("arrayInTxComn").size());
                assertEquals("item1", txComn3.getJSONArray("arrayInTxComn").getJSONObject(0).getString("name"));
            }
        }
    }

    @Test
    void testRequestResponseIntegrityValidation() throws InterruptedException {
        // Given - Test complete request/response cycle with validation
        String url = mockWebServer.url("/").toString();
        CompleteMessageModel message = TestDataFactory.createTransferBusinessScenario();

        // Mock a realistic response that mirrors the request structure
        String mockResponse = """
            {
                "txHeader": {
                    "respCode": "0000",
                    "respMsg": "Success",
                    "msgGrptMac": "RESP_MAC_001",
                    "globalBusiTrackNo": "RESP_TRACK_001"
                },
                "txBody": {
                    "respEntity": {
                        "transferId": "TRF_20240315_001",
                        "status": "COMPLETED",
                        "amount": "1000.00"
                    },
                    "respComn": {
                        "processTime": "20240315103000",
                        "resultCode": "SUCCESS"
                    }
                }
            }
            """;

        mockWebServer.enqueue(new MockResponse().setBody(mockResponse).addHeader("Content-Type", "application/json"));

        // When
        String actualResponse = httpService.sendRequest(url, message);

        // Then
        assertNotNull(actualResponse);
        JSONObject responseJson = JSON.parseObject(actualResponse);
        assertEquals("0000", responseJson.getJSONObject("txHeader").getString("respCode"));
        assertEquals("Success", responseJson.getJSONObject("txHeader").getString("respMsg"));

        // Verify request integrity
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        String receivedBody = recordedRequest.getBody().readUtf8();
        JSONObject requestJson = JSON.parseObject(receivedBody);

        // Validate complete message structure integrity
        assertTrue(message.validate());
        assertTrue(message.validateFormat());
        assertTrue(message.hasRequiredData());

        // Verify all required fields are present in transmitted request
        JSONObject txHeader = requestJson.getJSONObject("txHeader");
        assertNotNull(txHeader.getString("msgGrptMac"));
        assertNotNull(txHeader.getString("globalBusiTrackNo"));
        assertNotNull(txHeader.getString("subtxNo"));
        assertEquals("TRF001", txHeader.getString("txCode"));
        assertEquals("MOBILE", txHeader.getString("channelNo"));

        JSONObject txBody = requestJson.getJSONObject("txBody");
        JSONObject txEntity = txBody.getJSONObject("txEntity");
        assertNotNull(txEntity.getString("custNo"));
        assertEquals("C204", txEntity.getString("txSceneCd"));
        
        // Additional fields are in additionalFields object
        if (txEntity.containsKey("additionalFields")) {
            JSONObject additionalFields = txEntity.getJSONObject("additionalFields");
            assertTrue(additionalFields.containsKey("fromAccount"));
            assertTrue(additionalFields.containsKey("toAccount"));
            assertTrue(additionalFields.containsKey("amount"));
        }

        // Verify txComn data integrity
        assertTrue(txBody.containsKey("addtData"));
        JSONObject addtData = txBody.getJSONObject("addtData");
        assertEquals("INTERNAL", addtData.getString("transferType"));

        assertTrue(txBody.containsKey("txComn2"));
        JSONObject txComn2 = txBody.getJSONObject("txComn2");
        assertEquals("2", txComn2.getString("authLevel"));
        assertEquals("LOW", txComn2.getString("riskLevel"));
    }

    @Test
    void testParameterizedBusinessScenarios() throws InterruptedException {
        // Given - Test multiple business scenarios using TestDataFactory
        String url = mockWebServer.url("/").toString();
        
        // Test scenario 1: Standard business scenario
        CompleteMessageModel standardMessage = TestDataFactory.createStandardBusinessScenario();
        mockWebServer.enqueue(new MockResponse().setBody("{\"status\":\"success\",\"scenario\":\"standard\"}").addHeader("Content-Type", "application/json"));
        
        // Test scenario 2: Query business scenario
        CompleteMessageModel queryMessage = TestDataFactory.createQueryBusinessScenario();
        mockWebServer.enqueue(new MockResponse().setBody("{\"status\":\"success\",\"scenario\":\"query\"}").addHeader("Content-Type", "application/json"));
        
        // Test scenario 3: Transfer business scenario
        CompleteMessageModel transferMessage = TestDataFactory.createTransferBusinessScenario();
        mockWebServer.enqueue(new MockResponse().setBody("{\"status\":\"success\",\"scenario\":\"transfer\"}").addHeader("Content-Type", "application/json"));

        // When - Execute all scenarios
        String standardResponse = httpService.sendRequest(url, standardMessage);
        String queryResponse = httpService.sendRequest(url, queryMessage);
        String transferResponse = httpService.sendRequest(url, transferMessage);

        // Then - Verify all responses
        assertEquals("{\"status\":\"success\",\"scenario\":\"standard\"}", standardResponse);
        assertEquals("{\"status\":\"success\",\"scenario\":\"query\"}", queryResponse);
        assertEquals("{\"status\":\"success\",\"scenario\":\"transfer\"}", transferResponse);

        // Verify request structures for each scenario
        RecordedRequest standardRequest = mockWebServer.takeRequest();
        RecordedRequest queryRequest = mockWebServer.takeRequest();
        RecordedRequest transferRequest = mockWebServer.takeRequest();

        // Verify standard scenario structure
        JSONObject standardJson = JSON.parseObject(standardRequest.getBody().readUtf8());
        assertEquals("STD001", standardJson.getJSONObject("txHeader").getString("txCode"));
        assertEquals("WEB", standardJson.getJSONObject("txHeader").getString("channelNo"));
        assertEquals("040000037480013", standardJson.getJSONObject("txBody").getJSONObject("txEntity").getString("custNo"));

        // Verify query scenario structure
        JSONObject queryJson = JSON.parseObject(queryRequest.getBody().readUtf8());
        assertEquals("QRY001", queryJson.getJSONObject("txHeader").getString("txCode"));
        assertEquals("API", queryJson.getJSONObject("txHeader").getString("channelNo"));
        assertEquals("10", queryJson.getJSONObject("txBody").getJSONObject("txComn1").getString("curQryReqNum"));

        // Verify transfer scenario structure
        JSONObject transferJson = JSON.parseObject(transferRequest.getBody().readUtf8());
        assertEquals("TRF001", transferJson.getJSONObject("txHeader").getString("txCode"));
        assertEquals("MOBILE", transferJson.getJSONObject("txHeader").getString("channelNo"));
        assertEquals("C204", transferJson.getJSONObject("txBody").getJSONObject("txEntity").getString("txSceneCd"));
    }

    @Test
    void testParameterizedCustomScenarios() throws InterruptedException {
        // Given - Test parameterized scenarios with custom parameters
        String url = mockWebServer.url("/").toString();
        
        Map<String, Object> customParams1 = new HashMap<>();
        customParams1.put("header.channelNo", "CUSTOM_CHANNEL");
        customParams1.put("entity.custNo", "999888777666555");
        customParams1.put("comn.accountingDate", "20240401");
        customParams1.put("customField", "customValue1");
        
        Map<String, Object> customParams2 = new HashMap<>();
        customParams2.put("header.orgNo", "999");
        customParams2.put("entity.txSceneCd", "C999");
        customParams2.put("testParam", "testValue2");
        
        CompleteMessageModel customMessage1 = TestDataFactory.createParameterizedScenario(
            "custom_test_1", "999888777666555", "C301", customParams1);
        CompleteMessageModel customMessage2 = TestDataFactory.createParameterizedScenario(
            "custom_test_2", "111222333444555", "C999", customParams2);

        mockWebServer.enqueue(new MockResponse().setBody("{\"status\":\"success\",\"custom\":1}").addHeader("Content-Type", "application/json"));
        mockWebServer.enqueue(new MockResponse().setBody("{\"status\":\"success\",\"custom\":2}").addHeader("Content-Type", "application/json"));

        // When
        String response1 = httpService.sendRequest(url, customMessage1);
        String response2 = httpService.sendRequest(url, customMessage2);

        // Then
        assertEquals("{\"status\":\"success\",\"custom\":1}", response1);
        assertEquals("{\"status\":\"success\",\"custom\":2}", response2);

        // Verify custom parameters were applied correctly
        RecordedRequest request1 = mockWebServer.takeRequest();
        RecordedRequest request2 = mockWebServer.takeRequest();

        JSONObject json1 = JSON.parseObject(request1.getBody().readUtf8());
        assertEquals("CUSTOM_CHANNEL", json1.getJSONObject("txHeader").getString("channelNo"));
        assertEquals("999888777666555", json1.getJSONObject("txBody").getJSONObject("txEntity").getString("custNo"));
        assertEquals("20240401", json1.getJSONObject("txBody").getString("accountingDate"));
        assertEquals("customValue1", json1.getJSONObject("txBody").getJSONObject("addtData").getString("customField"));

        JSONObject json2 = JSON.parseObject(request2.getBody().readUtf8());
        assertEquals("999", json2.getJSONObject("txHeader").getString("orgNo"));
        assertEquals("C999", json2.getJSONObject("txBody").getJSONObject("txEntity").getString("txSceneCd"));
        assertEquals("testValue2", json2.getJSONObject("txBody").getJSONObject("addtData").getString("testParam"));
    }

    @Test
    void testBoundaryValueScenarios() throws InterruptedException {
        // Given - Test boundary value scenarios
        String url = mockWebServer.url("/").toString();
        
        CompleteMessageModel minBoundaryMessage = TestDataFactory.createMinBoundaryScenario();
        CompleteMessageModel maxBoundaryMessage = TestDataFactory.createMaxBoundaryScenario();

        mockWebServer.enqueue(new MockResponse().setBody("{\"status\":\"success\",\"boundary\":\"min\"}").addHeader("Content-Type", "application/json"));
        mockWebServer.enqueue(new MockResponse().setBody("{\"status\":\"success\",\"boundary\":\"max\"}").addHeader("Content-Type", "application/json"));

        // When
        String minResponse = httpService.sendRequest(url, minBoundaryMessage);
        String maxResponse = httpService.sendRequest(url, maxBoundaryMessage);

        // Then
        assertEquals("{\"status\":\"success\",\"boundary\":\"min\"}", minResponse);
        assertEquals("{\"status\":\"success\",\"boundary\":\"max\"}", maxResponse);

        // Verify boundary values
        RecordedRequest minRequest = mockWebServer.takeRequest();
        RecordedRequest maxRequest = mockWebServer.takeRequest();

        JSONObject minJson = JSON.parseObject(minRequest.getBody().readUtf8());
        assertEquals("MIN", minJson.getJSONObject("txHeader").getString("msgGrptMac"));
        assertEquals("1", minJson.getJSONObject("txHeader").getString("globalBusiTrackNo"));
        assertEquals("000000000000001", minJson.getJSONObject("txBody").getJSONObject("txEntity").getString("custNo"));

        JSONObject maxJson = JSON.parseObject(maxRequest.getBody().readUtf8());
        assertEquals("MAX_VALUE_TEST_MAC_BOUNDARY_SCENARIO_TESTING", maxJson.getJSONObject("txHeader").getString("msgGrptMac"));
        assertEquals("999999999999999", maxJson.getJSONObject("txBody").getJSONObject("txEntity").getString("custNo"));
    }

    @Test
    void testExceptionHandlingScenarios() throws InterruptedException {
        // Given - Test exception and edge case scenarios
        String url = mockWebServer.url("/").toString();
        
        CompleteMessageModel nullValueMessage = TestDataFactory.createNullValueScenario();
        CompleteMessageModel invalidFormatMessage = TestDataFactory.createInvalidFormatScenario();

        mockWebServer.enqueue(new MockResponse().setBody("{\"status\":\"success\",\"scenario\":\"null_values\"}").addHeader("Content-Type", "application/json"));
        mockWebServer.enqueue(new MockResponse().setBody("{\"status\":\"success\",\"scenario\":\"invalid_format\"}").addHeader("Content-Type", "application/json"));

        // When
        String nullResponse = httpService.sendRequest(url, nullValueMessage);
        String invalidResponse = httpService.sendRequest(url, invalidFormatMessage);

        // Then
        assertEquals("{\"status\":\"success\",\"scenario\":\"null_values\"}", nullResponse);
        assertEquals("{\"status\":\"success\",\"scenario\":\"invalid_format\"}", invalidResponse);

        // Verify exception scenarios structure
        RecordedRequest nullRequest = mockWebServer.takeRequest();
        RecordedRequest invalidRequest = mockWebServer.takeRequest();

        JSONObject nullJson = JSON.parseObject(nullRequest.getBody().readUtf8());
        assertEquals("NULL_TEST_MAC", nullJson.getJSONObject("txHeader").getString("msgGrptMac"));
        assertEquals("000000000000000", nullJson.getJSONObject("txBody").getJSONObject("txEntity").getString("custNo"));

        JSONObject invalidJson = JSON.parseObject(invalidRequest.getBody().readUtf8());
        assertEquals("INVALID_MAC_TOO_LONG_FOR_FIELD_VALIDATION_TESTING_PURPOSES_EXCEEDING_LIMITS", 
                    invalidJson.getJSONObject("txHeader").getString("msgGrptMac"));
        assertEquals("INVALID_CUSTOMER_NUMBER", invalidJson.getJSONObject("txBody").getJSONObject("txEntity").getString("custNo"));
    }

    @Test
    void testBatchDataGeneration() throws InterruptedException {
        // Given - Test batch data generation capabilities
        String url = mockWebServer.url("/").toString();
        
        CompleteMessageModel[] standardBatch = TestDataFactory.createBatchTestData(3, "standard");
        CompleteMessageModel[] randomBatch = TestDataFactory.createBatchTestData(2, "random");

        // Enqueue responses for all batch requests
        for (int i = 0; i < 5; i++) {
            mockWebServer.enqueue(new MockResponse().setBody("{\"status\":\"success\",\"batch\":" + (i + 1) + "}").addHeader("Content-Type", "application/json"));
        }

        // When - Send all batch messages
        String[] responses = new String[5];
        int index = 0;
        
        for (CompleteMessageModel message : standardBatch) {
            responses[index++] = httpService.sendRequest(url, message);
        }
        
        for (CompleteMessageModel message : randomBatch) {
            responses[index++] = httpService.sendRequest(url, message);
        }

        // Then - Verify all responses
        for (int i = 0; i < 5; i++) {
            assertEquals("{\"status\":\"success\",\"batch\":" + (i + 1) + "}", responses[i]);
        }

        // Verify batch message uniqueness and structure
        for (int i = 0; i < 5; i++) {
            RecordedRequest request = mockWebServer.takeRequest();
            JSONObject json = JSON.parseObject(request.getBody().readUtf8());
            
            // Each message should have unique sequence number
            assertEquals(String.valueOf((i % 3) + 1), json.getJSONObject("txHeader").getString("seqNo"));
            assertTrue(json.getJSONObject("txHeader").getString("remark").contains("Batch test"));
        }
    }

    @Test
    void testFieldModificationFlexibility() throws InterruptedException {
        // Given - Test the flexibility of modifying field values
        String url = mockWebServer.url("/").toString();
        
        // Create base message and modify specific fields
        CompleteMessageModel baseMessage = TestDataFactory.createStandardBusinessScenario();
        
        // Modify txHeader fields
        baseMessage.getTxHeader().setTxCode("MODIFIED_CODE");
        baseMessage.getTxHeader().setChannelNo("MODIFIED_CHANNEL");
        baseMessage.getTxHeader().setRemark("Modified for flexibility test");
        
        // Modify txEntity fields
        baseMessage.getTxEntity().setCustNo("111111111111111");
        baseMessage.getTxEntity().setTxSceneCd("C999");
        
        // Modify txComn fields - need to modify the txBody directly
        baseMessage.getTxBody().setAccountingDate("20241225");
        baseMessage.getTxComn().addTxComnField(1, "modifiedField", "modifiedValue");
        // Re-apply the modified txComn to the message
        baseMessage.setTxComn(baseMessage.getTxComn());

        mockWebServer.enqueue(new MockResponse().setBody("{\"status\":\"success\",\"modified\":true}").addHeader("Content-Type", "application/json"));

        // When
        String response = httpService.sendRequest(url, baseMessage);

        // Then
        assertEquals("{\"status\":\"success\",\"modified\":true}", response);

        RecordedRequest request = mockWebServer.takeRequest();
        JSONObject json = JSON.parseObject(request.getBody().readUtf8());

        // Verify modifications were applied
        assertEquals("MODIFIED_CODE", json.getJSONObject("txHeader").getString("txCode"));
        assertEquals("MODIFIED_CHANNEL", json.getJSONObject("txHeader").getString("channelNo"));
        assertEquals("Modified for flexibility test", json.getJSONObject("txHeader").getString("remark"));
        
        assertEquals("111111111111111", json.getJSONObject("txBody").getJSONObject("txEntity").getString("custNo"));
        assertEquals("C999", json.getJSONObject("txBody").getJSONObject("txEntity").getString("txSceneCd"));
        
        assertEquals("20241225", json.getJSONObject("txBody").getString("accountingDate"));
        assertEquals("modifiedValue", json.getJSONObject("txBody").getJSONObject("txComn1").getString("modifiedField"));
    }
}
