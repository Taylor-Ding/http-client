package com.example.httpclientdemo.integration;

import com.example.httpclientdemo.builder.MessageBuilder;
import com.example.httpclientdemo.factory.TestDataFactory;
import com.example.httpclientdemo.model.CompleteMessageModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;

/**
 * MessageBuilder和TestDataFactory集成测试
 * 演示如何使用这两个组件协同工作
 */
@DisplayName("MessageBuilder and TestDataFactory Integration Tests")
class MessageBuilderIntegrationTest {
    
    @Test
    @DisplayName("Should demonstrate fluent API usage with MessageBuilder")
    void shouldDemonstrateFluentAPIUsageWithMessageBuilder() {
        // 使用MessageBuilder的流式API构建复杂报文
        CompleteMessageModel message = MessageBuilder.create()
            .withDefaults()
            .withTxHeader(header -> header
                .msgGrptMac("DEMO_MAC_12345")
                .globalBusiTrackNo("DEMO_TRACK_67890")
                .subtxNo("DEMO_SUBTX_001")
                .txCode("DEMO001")
                .channelNo("WEB")
                .orgNo("001")
                .tellerId("DEMO_TELLER")
                .clientIp("192.168.1.100")
                .remark("Integration test demonstration")
            )
            .withTxEntity(entity -> entity
                .custNo("040000037480013")
                .qryVchrTpCd("1")
                .txSceneCd("C203")
                .addField("demoField1", "demoValue1")
                .addField("demoField2", 12345)
                .addField("demoField3", true)
            )
            .withTxComn(comn -> comn
                .accountingDate("20231201")
                .curQryReqNum("10")
                .bgnIndexNo("5")
                .busiSendSysOrCmptNo("99710730008")
                .addtData("demoKey1", "demoValue1")
                .addtData("demoKey2", "demoValue2")
                .txComn2("level", "HIGH")
                .txComn3("priority", "URGENT")
            )
            .buildAndValidate();
        
        // 验证构建的消息
        assertNotNull(message);
        assertTrue(message.validate());
        assertTrue(message.hasRequiredData());
        
        // 验证可以序列化为JSON
        String json = message.toJson();
        assertNotNull(json);
        assertTrue(json.contains("DEMO_MAC_12345"));
        assertTrue(json.contains("040000037480013"));
        assertTrue(json.contains("demoValue1"));
        
        System.out.println("=== MessageBuilder Demo JSON ===");
        System.out.println(message.toPrettyJson());
    }
    
    @Test
    @DisplayName("Should demonstrate TestDataFactory usage for different scenarios")
    void shouldDemonstrateTestDataFactoryUsageForDifferentScenarios() {
        // 标准业务场景
        CompleteMessageModel standardMessage = TestDataFactory.createStandardBusinessScenario();
        assertNotNull(standardMessage);
        assertTrue(standardMessage.validate());
        assertEquals("STD001", standardMessage.getTxHeader().getTxCode());
        
        // 查询业务场景
        CompleteMessageModel queryMessage = TestDataFactory.createQueryBusinessScenario();
        assertNotNull(queryMessage);
        assertTrue(queryMessage.validate());
        assertEquals("QRY001", queryMessage.getTxHeader().getTxCode());
        assertEquals("BALANCE", queryMessage.getTxEntity().getField("queryType"));
        
        // 转账业务场景
        CompleteMessageModel transferMessage = TestDataFactory.createTransferBusinessScenario();
        assertNotNull(transferMessage);
        assertTrue(transferMessage.validate());
        assertEquals("TRF001", transferMessage.getTxHeader().getTxCode());
        assertEquals("1000.00", transferMessage.getTxEntity().getField("amount"));
        
        // 随机场景
        CompleteMessageModel randomMessage = TestDataFactory.createRandomScenario();
        assertNotNull(randomMessage);
        assertTrue(randomMessage.validate());
        assertTrue(randomMessage.getTxHeader().getTxCode().startsWith("RND"));
        
        System.out.println("=== TestDataFactory Demo - Standard Scenario ===");
        System.out.println(standardMessage.toPrettyJson());
        
        System.out.println("\n=== TestDataFactory Demo - Transfer Scenario ===");
        System.out.println(transferMessage.toPrettyJson());
    }
    
    @Test
    @DisplayName("Should demonstrate parameterized test data creation")
    void shouldDemonstrateParameterizedTestDataCreation() {
        // 创建参数化测试数据
        Map<String, Object> params = new HashMap<>();
        params.put("header.channelNo", "MOBILE_APP");
        params.put("header.orgNo", "999");
        params.put("header.tellerId", "MOBILE_TELLER");
        params.put("entity.customField", "mobileValue");
        params.put("entity.deviceId", "DEVICE_12345");
        params.put("comn.curQryReqNum", "20");
        params.put("comn.sessionId", "SESSION_67890");
        params.put("businessType", "MOBILE_BANKING");
        
        CompleteMessageModel paramMessage = TestDataFactory.createParameterizedScenario(
            "mobileDemo", 
            "555666777888999", 
            "C206", 
            params
        );
        
        assertNotNull(paramMessage);
        assertTrue(paramMessage.validate());
        
        // 验证参数化设置
        assertEquals("PARAM_MOBILEDEMO", paramMessage.getTxHeader().getTxCode());
        assertEquals("MOBILE_APP", paramMessage.getTxHeader().getChannelNo());
        assertEquals("999", paramMessage.getTxHeader().getOrgNo());
        assertEquals("MOBILE_TELLER", paramMessage.getTxHeader().getTellerId());
        assertEquals("555666777888999", paramMessage.getTxEntity().getCustNo());
        assertEquals("C206", paramMessage.getTxEntity().getTxSceneCd());
        assertEquals("mobileValue", paramMessage.getTxEntity().getField("customField"));
        assertEquals("DEVICE_12345", paramMessage.getTxEntity().getField("deviceId"));
        assertEquals("20", paramMessage.getTxComn().getTxComnField(1, "curQryReqNum"));
        assertEquals("SESSION_67890", paramMessage.getTxComn().getAddtDataField("sessionId"));
        assertEquals("MOBILE_BANKING", paramMessage.getTxComn().getAddtDataField("businessType"));
        
        System.out.println("=== Parameterized Test Data Demo ===");
        System.out.println(paramMessage.toPrettyJson());
    }
    
    @Test
    @DisplayName("Should demonstrate batch test data creation")
    void shouldDemonstrateBatchTestDataCreation() {
        // 批量创建测试数据
        CompleteMessageModel[] batchMessages = TestDataFactory.createBatchTestData(3, "query");
        
        assertEquals(3, batchMessages.length);
        
        for (int i = 0; i < batchMessages.length; i++) {
            CompleteMessageModel message = batchMessages[i];
            assertNotNull(message);
            assertTrue(message.validate());
            assertEquals("QRY001", message.getTxHeader().getTxCode());
            assertEquals(String.valueOf(i + 1), message.getTxHeader().getSeqNo());
            assertEquals("Batch test #" + (i + 1), message.getTxHeader().getRemark());
        }
        
        System.out.println("=== Batch Test Data Demo ===");
        for (int i = 0; i < batchMessages.length; i++) {
            System.out.println("Batch Message #" + (i + 1) + ":");
            System.out.println("  SeqNo: " + batchMessages[i].getTxHeader().getSeqNo());
            System.out.println("  Remark: " + batchMessages[i].getTxHeader().getRemark());
            System.out.println("  CustNo: " + batchMessages[i].getTxEntity().getCustNo());
        }
    }
    
    @Test
    @DisplayName("Should demonstrate boundary and exception scenarios")
    void shouldDemonstrateBoundaryAndExceptionScenarios() {
        // 最小边界值场景
        CompleteMessageModel minMessage = TestDataFactory.createMinBoundaryScenario();
        assertNotNull(minMessage);
        assertEquals("MIN", minMessage.getTxHeader().getMsgGrptMac());
        assertEquals("1", minMessage.getTxHeader().getGlobalBusiTrackNo());
        
        // 最大边界值场景
        CompleteMessageModel maxMessage = TestDataFactory.createMaxBoundaryScenario();
        assertNotNull(maxMessage);
        assertTrue(maxMessage.getTxHeader().getMsgGrptMac().length() > 30);
        assertEquals("999999999999999", maxMessage.getTxEntity().getCustNo());
        
        // 空值场景
        CompleteMessageModel nullMessage = TestDataFactory.createNullValueScenario();
        assertNotNull(nullMessage);
        assertEquals("NULL_TEST_MAC", nullMessage.getTxHeader().getMsgGrptMac());
        assertEquals("000000000000000", nullMessage.getTxEntity().getCustNo());
        
        // 格式错误场景
        CompleteMessageModel invalidMessage = TestDataFactory.createInvalidFormatScenario();
        assertNotNull(invalidMessage);
        assertFalse(invalidMessage.validateFormat()); // 应该验证失败
        
        System.out.println("=== Boundary Scenarios Demo ===");
        System.out.println("Min Boundary - MAC: " + minMessage.getTxHeader().getMsgGrptMac());
        System.out.println("Max Boundary - MAC Length: " + maxMessage.getTxHeader().getMsgGrptMac().length());
        System.out.println("Null Scenario - CustNo: " + nullMessage.getTxEntity().getCustNo());
        System.out.println("Invalid Format - Validation Result: " + invalidMessage.validateFormat());
    }
    
    @Test
    @DisplayName("Should demonstrate MessageBuilder and TestDataFactory combination")
    void shouldDemonstrateMessageBuilderAndTestDataFactoryCombination() {
        // 使用TestDataFactory创建基础数据，然后用MessageBuilder进行定制
        CompleteMessageModel baseMessage = TestDataFactory.createStandardBusinessScenario();
        
        // 使用MessageBuilder对基础数据进行定制
        CompleteMessageModel customizedMessage = MessageBuilder.create()
            .withTxHeader(baseMessage.getTxHeader())
            .withTxEntity(baseMessage.getTxEntity())
            .withTxComn(baseMessage.getTxComn())
            .withTxHeader(header -> header
                .txCode("CUSTOM001")
                .remark("Customized from factory data")
            )
            .withTxEntity(entity -> entity
                .addField("customization", "enhanced")
                .addField("timestamp", System.currentTimeMillis())
            )
            .withTxComn(comn -> comn
                .addtData("source", "factory+builder")
                .addtData("enhanced", true)
            )
            .build();
        
        assertNotNull(customizedMessage);
        assertTrue(customizedMessage.validate());
        assertEquals("CUSTOM001", customizedMessage.getTxHeader().getTxCode());
        assertEquals("Customized from factory data", customizedMessage.getTxHeader().getRemark());
        assertEquals("enhanced", customizedMessage.getTxEntity().getField("customization"));
        assertEquals("factory+builder", customizedMessage.getTxComn().getAddtDataField("source"));
        assertEquals(true, customizedMessage.getTxComn().getAddtDataField("enhanced"));
        
        System.out.println("=== Factory + Builder Combination Demo ===");
        System.out.println(customizedMessage.toPrettyJson());
    }
}