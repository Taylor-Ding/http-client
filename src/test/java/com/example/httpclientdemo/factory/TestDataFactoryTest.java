package com.example.httpclientdemo.factory;

import com.example.httpclientdemo.model.CompleteMessageModel;
import com.example.httpclientdemo.model.TxHeaderModel;
import com.example.httpclientdemo.model.TxEntityModel;
import com.example.httpclientdemo.model.TxComnModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;

/**
 * TestDataFactory单元测试类
 */
@DisplayName("TestDataFactory Tests")
class TestDataFactoryTest {
    
    @Nested
    @DisplayName("Standard Business Scenarios")
    class StandardBusinessScenarios {
        
        @Test
        @DisplayName("Should create standard business scenario")
        void shouldCreateStandardBusinessScenario() {
            CompleteMessageModel message = TestDataFactory.createStandardBusinessScenario();
            
            assertNotNull(message);
            assertTrue(message.validate());
            
            // 验证TxHeader
            TxHeaderModel header = message.getTxHeader();
            assertNotNull(header.getMsgGrptMac());
            assertNotNull(header.getGlobalBusiTrackNo());
            assertNotNull(header.getSubtxNo());
            assertEquals("STD001", header.getTxCode());
            assertEquals("WEB", header.getChannelNo());
            assertEquals("001", header.getOrgNo());
            assertEquals("STD_TELLER", header.getTellerId());
            
            // 验证TxEntity
            TxEntityModel entity = message.getTxEntity();
            assertEquals("040000037480013", entity.getCustNo());
            assertEquals("1", entity.getQryVchrTpCd());
            assertEquals("C203", entity.getTxSceneCd());
            
            // 验证TxComn
            TxComnModel comn = message.getTxComn();
            assertNotNull(comn.getAccountingDate());
            assertEquals("0", comn.getTxComnField(1, "curQryReqNum"));
            assertEquals("0", comn.getTxComnField(1, "bgnIndexNo"));
            assertEquals("99710730008", comn.getTxComnField(8, "busiSendSysOrCmptNo"));
        }
        
        @Test
        @DisplayName("Should create query business scenario")
        void shouldCreateQueryBusinessScenario() {
            CompleteMessageModel message = TestDataFactory.createQueryBusinessScenario();
            
            assertNotNull(message);
            assertTrue(message.validate());
            
            TxHeaderModel header = message.getTxHeader();
            assertEquals("QRY001", header.getTxCode());
            assertEquals("API", header.getChannelNo());
            assertEquals("002", header.getOrgNo());
            
            TxEntityModel entity = message.getTxEntity();
            assertNotNull(entity.getCustNo());
            assertEquals("1", entity.getQryVchrTpCd());
            assertEquals("C203", entity.getTxSceneCd());
            assertEquals("BALANCE", entity.getField("queryType"));
            assertEquals("CURRENT", entity.getField("queryRange"));
            
            TxComnModel comn = message.getTxComn();
            assertEquals("10", comn.getTxComnField(1, "curQryReqNum"));
            assertEquals("1", comn.getTxComnField(1, "bgnIndexNo"));
            assertEquals("balance,history", comn.getAddtDataField("queryParams"));
        }
        
        @Test
        @DisplayName("Should create transfer business scenario")
        void shouldCreateTransferBusinessScenario() {
            CompleteMessageModel message = TestDataFactory.createTransferBusinessScenario();
            
            assertNotNull(message);
            assertTrue(message.validate());
            
            TxHeaderModel header = message.getTxHeader();
            assertEquals("TRF001", header.getTxCode());
            assertEquals("MOBILE", header.getChannelNo());
            assertEquals("003", header.getOrgNo());
            assertEquals("TRF_TELLER", header.getTellerId());
            assertEquals("AUTH_TELLER", header.getAuthTellerId());
            
            TxEntityModel entity = message.getTxEntity();
            assertNotNull(entity.getCustNo());
            assertEquals("2", entity.getQryVchrTpCd());
            assertEquals("C204", entity.getTxSceneCd());
            assertEquals("1234567890", entity.getField("fromAccount"));
            assertEquals("0987654321", entity.getField("toAccount"));
            assertEquals("1000.00", entity.getField("amount"));
            assertEquals("CNY", entity.getField("currency"));
            
            TxComnModel comn = message.getTxComn();
            assertEquals("INTERNAL", comn.getAddtDataField("transferType"));
            assertEquals("Transfer test", comn.getAddtDataField("memo"));
            assertEquals("2", comn.getTxComnField(2, "authLevel"));
            assertEquals("LOW", comn.getTxComnField(2, "riskLevel"));
        }
    }
    
    @Nested
    @DisplayName("Boundary Value Scenarios")
    class BoundaryValueScenarios {
        
        @Test
        @DisplayName("Should create minimum boundary scenario")
        void shouldCreateMinBoundaryScenario() {
            CompleteMessageModel message = TestDataFactory.createMinBoundaryScenario();
            
            assertNotNull(message);
            
            TxHeaderModel header = message.getTxHeader();
            assertEquals("MIN", header.getMsgGrptMac());
            assertEquals("1", header.getGlobalBusiTrackNo());
            assertEquals("1", header.getSubtxNo());
            assertEquals("20230101000000", header.getTxStartTime());
            assertEquals("20230101000001", header.getTxSendTime());
            assertEquals("MIN", header.getTxCode());
            assertEquals("M", header.getChannelNo());
            assertEquals("1", header.getOrgNo());
            
            TxEntityModel entity = message.getTxEntity();
            assertEquals("000000000000001", entity.getCustNo());
            assertEquals("1", entity.getQryVchrTpCd());
            assertEquals("C001", entity.getTxSceneCd());
            
            TxComnModel comn = message.getTxComn();
            assertEquals("20230101", comn.getAccountingDate());
            assertEquals("0", comn.getTxComnField(1, "curQryReqNum"));
            assertEquals("0", comn.getTxComnField(1, "bgnIndexNo"));
        }
        
        @Test
        @DisplayName("Should create maximum boundary scenario")
        void shouldCreateMaxBoundaryScenario() {
            CompleteMessageModel message = TestDataFactory.createMaxBoundaryScenario();
            
            assertNotNull(message);
            
            TxHeaderModel header = message.getTxHeader();
            assertTrue(header.getMsgGrptMac().startsWith("MAX_VALUE_TEST"));
            assertTrue(header.getGlobalBusiTrackNo().startsWith("MAX_GLOBAL_BUSI"));
            assertEquals("MAX_SUBTX_NO_BOUNDARY", header.getSubtxNo());
            assertEquals("99991231235959", header.getTxStartTime());
            assertEquals("99991231235959", header.getTxSendTime());
            assertEquals("MAXCODE999", header.getTxCode());
            assertEquals("255.255.255.255", header.getClientIp());
            
            TxEntityModel entity = message.getTxEntity();
            assertEquals("999999999999999", entity.getCustNo());
            assertEquals("9", entity.getQryVchrTpCd());
            assertEquals("Z999", entity.getTxSceneCd());
            assertTrue(entity.getField("maxField").toString().startsWith("MAX_VALUE_FIELD"));
            
            TxComnModel comn = message.getTxComn();
            assertEquals("99991231", comn.getAccountingDate());
            assertEquals("999999", comn.getTxComnField(1, "curQryReqNum"));
            assertEquals("999999", comn.getTxComnField(1, "bgnIndexNo"));
            assertTrue(comn.getAddtDataField("maxKey").toString().startsWith("MAX_ADDITIONAL"));
        }
    }
    
    @Nested
    @DisplayName("Exception Scenarios")
    class ExceptionScenarios {
        
        @Test
        @DisplayName("Should create null value scenario")
        void shouldCreateNullValueScenario() {
            CompleteMessageModel message = TestDataFactory.createNullValueScenario();
            
            assertNotNull(message);
            
            TxHeaderModel header = message.getTxHeader();
            assertEquals("NULL_TEST_MAC", header.getMsgGrptMac());
            assertEquals("NULL_TEST_TRACK", header.getGlobalBusiTrackNo());
            assertEquals("NULL_TEST", header.getSubtxNo());
            // 其他字段应该为null或默认值
            
            TxEntityModel entity = message.getTxEntity();
            assertEquals("000000000000000", entity.getCustNo());
            assertEquals("0", entity.getQryVchrTpCd());
            assertEquals("C000", entity.getTxSceneCd());
            
            TxComnModel comn = message.getTxComn();
            assertEquals("00000000", comn.getAccountingDate());
        }
        
        @Test
        @DisplayName("Should create invalid format scenario")
        void shouldCreateInvalidFormatScenario() {
            CompleteMessageModel message = TestDataFactory.createInvalidFormatScenario();
            
            assertNotNull(message);
            
            // 这个消息应该包含格式错误，用于测试验证逻辑
            TxHeaderModel header = message.getTxHeader();
            assertTrue(header.getMsgGrptMac().length() > 50); // 超长字段
            assertEquals("", header.getGlobalBusiTrackNo()); // 空字符串
            
            TxEntityModel entity = message.getTxEntity();
            assertEquals("INVALID_CUSTOMER_NUMBER", entity.getCustNo()); // 不符合数字格式
            assertEquals("INVALID", entity.getQryVchrTpCd()); // 不符合格式
            
            TxComnModel comn = message.getTxComn();
            assertEquals("INVALID_DATE", comn.getAccountingDate()); // 不符合日期格式
            
            // 验证这个消息确实无法通过格式验证
            assertFalse(message.validateFormat());
        }
    }
    
    @Nested
    @DisplayName("Parameterized Scenarios")
    class ParameterizedScenarios {
        
        @Test
        @DisplayName("Should create parameterized scenario with basic parameters")
        void shouldCreateParameterizedScenarioWithBasicParameters() {
            Map<String, Object> params = new HashMap<>();
            params.put("customParam", "customValue");
            params.put("numericParam", 123);
            params.put("booleanParam", true);
            
            CompleteMessageModel message = TestDataFactory.createParameterizedScenario(
                "testScenario", 
                "123456789012345", 
                "C205", 
                params
            );
            
            assertNotNull(message);
            assertTrue(message.validate());
            
            TxHeaderModel header = message.getTxHeader();
            assertEquals("PARAM_TESTSCENARIO", header.getTxCode());
            assertEquals("Parameterized test: testScenario", header.getRemark());
            
            TxEntityModel entity = message.getTxEntity();
            assertEquals("123456789012345", entity.getCustNo());
            assertEquals("C205", entity.getTxSceneCd());
            
            TxComnModel comn = message.getTxComn();
            assertEquals("testScenario", comn.getAddtDataField("scenario"));
            assertEquals("customValue", comn.getAddtDataField("customParam"));
            assertEquals(123, comn.getAddtDataField("numericParam"));
            assertEquals(true, comn.getAddtDataField("booleanParam"));
        }
        
        @Test
        @DisplayName("Should create parameterized scenario with header parameters")
        void shouldCreateParameterizedScenarioWithHeaderParameters() {
            Map<String, Object> params = new HashMap<>();
            params.put("header.channelNo", "CUSTOM_CHANNEL");
            params.put("header.orgNo", "999");
            params.put("header.tellerId", "CUSTOM_TELLER");
            
            CompleteMessageModel message = TestDataFactory.createParameterizedScenario(
                "headerTest", 
                "987654321098765", 
                "C206", 
                params
            );
            
            assertNotNull(message);
            
            TxHeaderModel header = message.getTxHeader();
            assertEquals("CUSTOM_CHANNEL", header.getChannelNo());
            assertEquals("999", header.getOrgNo());
            assertEquals("CUSTOM_TELLER", header.getTellerId());
        }
        
        @Test
        @DisplayName("Should create parameterized scenario with entity parameters")
        void shouldCreateParameterizedScenarioWithEntityParameters() {
            Map<String, Object> params = new HashMap<>();
            params.put("entity.customEntityField", "entityValue");
            params.put("entity.numericEntityField", 456);
            
            CompleteMessageModel message = TestDataFactory.createParameterizedScenario(
                "entityTest", 
                "555666777888999", 
                "C207", 
                params
            );
            
            assertNotNull(message);
            
            TxEntityModel entity = message.getTxEntity();
            assertEquals("entityValue", entity.getField("customEntityField"));
            assertEquals(456, entity.getField("numericEntityField"));
        }
        
        @Test
        @DisplayName("Should create parameterized scenario with comn parameters")
        void shouldCreateParameterizedScenarioWithComnParameters() {
            Map<String, Object> params = new HashMap<>();
            params.put("comn.curQryReqNum", "50");
            params.put("comn.bgnIndexNo", "25");
            params.put("comn.customComnField", "comnValue");
            
            CompleteMessageModel message = TestDataFactory.createParameterizedScenario(
                "comnTest", 
                "111222333444555", 
                "C208", 
                params
            );
            
            assertNotNull(message);
            
            TxComnModel comn = message.getTxComn();
            assertEquals("50", comn.getTxComnField(1, "curQryReqNum"));
            assertEquals("25", comn.getTxComnField(1, "bgnIndexNo"));
            assertEquals("comnValue", comn.getAddtDataField("customComnField"));
        }
        
        @Test
        @DisplayName("Should handle null parameters gracefully")
        void shouldHandleNullParametersGracefully() {
            CompleteMessageModel message = TestDataFactory.createParameterizedScenario(
                "nullTest", 
                "040000037480013", 
                "C203", 
                null
            );
            
            assertNotNull(message);
            assertTrue(message.validate());
            
            TxEntityModel entity = message.getTxEntity();
            assertEquals("040000037480013", entity.getCustNo());
            assertEquals("C203", entity.getTxSceneCd());
        }
    }
    
    @Nested
    @DisplayName("Random and Batch Scenarios")
    class RandomAndBatchScenarios {
        
        @Test
        @DisplayName("Should create random scenario")
        void shouldCreateRandomScenario() {
            CompleteMessageModel message = TestDataFactory.createRandomScenario();
            
            assertNotNull(message);
            assertTrue(message.validate());
            
            TxHeaderModel header = message.getTxHeader();
            assertNotNull(header.getMsgGrptMac());
            assertNotNull(header.getGlobalBusiTrackNo());
            assertNotNull(header.getSubtxNo());
            assertTrue(header.getTxCode().startsWith("RND"));
            assertNotNull(header.getChannelNo());
            assertNotNull(header.getOrgNo());
            assertTrue(header.getTellerId().startsWith("TELLER_"));
            
            TxEntityModel entity = message.getTxEntity();
            assertNotNull(entity.getCustNo());
            assertNotNull(entity.getQryVchrTpCd());
            assertNotNull(entity.getTxSceneCd());
            assertNotNull(entity.getField("randomField1"));
            assertNotNull(entity.getField("randomField2"));
            
            TxComnModel comn = message.getTxComn();
            assertNotNull(comn.getAccountingDate());
            assertNotNull(comn.getTxComnField(1, "curQryReqNum"));
            assertNotNull(comn.getTxComnField(1, "bgnIndexNo"));
            assertNotNull(comn.getAddtDataField("randomKey"));
            assertTrue(comn.getTxComnField(8, "busiSendSysOrCmptNo").toString().startsWith("SYS"));
        }
        
        @Test
        @DisplayName("Should create multiple random scenarios with different values")
        void shouldCreateMultipleRandomScenariosWithDifferentValues() {
            CompleteMessageModel message1 = TestDataFactory.createRandomScenario();
            CompleteMessageModel message2 = TestDataFactory.createRandomScenario();
            
            assertNotNull(message1);
            assertNotNull(message2);
            
            // 验证两个随机消息不完全相同
            assertNotEquals(message1.getTxHeader().getMsgGrptMac(), 
                           message2.getTxHeader().getMsgGrptMac());
            assertNotEquals(message1.getTxHeader().getGlobalBusiTrackNo(), 
                           message2.getTxHeader().getGlobalBusiTrackNo());
        }
        
        @ParameterizedTest
        @ValueSource(strings = {"standard", "query", "transfer", "random"})
        @DisplayName("Should create batch test data for different scenarios")
        void shouldCreateBatchTestDataForDifferentScenarios(String scenario) {
            int batchSize = 5;
            CompleteMessageModel[] messages = TestDataFactory.createBatchTestData(batchSize, scenario);
            
            assertNotNull(messages);
            assertEquals(batchSize, messages.length);
            
            for (int i = 0; i < batchSize; i++) {
                assertNotNull(messages[i]);
                assertTrue(messages[i].validate());
                assertEquals(String.valueOf(i + 1), messages[i].getTxHeader().getSeqNo());
                assertEquals("Batch test #" + (i + 1), messages[i].getTxHeader().getRemark());
            }
        }
        
        @Test
        @DisplayName("Should create batch test data with unique identifiers")
        void shouldCreateBatchTestDataWithUniqueIdentifiers() {
            CompleteMessageModel[] messages = TestDataFactory.createBatchTestData(3, "standard");
            
            assertEquals("1", messages[0].getTxHeader().getSeqNo());
            assertEquals("2", messages[1].getTxHeader().getSeqNo());
            assertEquals("3", messages[2].getTxHeader().getSeqNo());
            
            assertEquals("Batch test #1", messages[0].getTxHeader().getRemark());
            assertEquals("Batch test #2", messages[1].getTxHeader().getRemark());
            assertEquals("Batch test #3", messages[2].getTxHeader().getRemark());
        }
        
        @Test
        @DisplayName("Should handle unknown scenario in batch creation")
        void shouldHandleUnknownScenarioInBatchCreation() {
            CompleteMessageModel[] messages = TestDataFactory.createBatchTestData(2, "unknown");
            
            assertNotNull(messages);
            assertEquals(2, messages.length);
            
            // 应该默认创建标准场景
            for (CompleteMessageModel message : messages) {
                assertNotNull(message);
                assertTrue(message.validate());
            }
        }
    }
    
    @Nested
    @DisplayName("Data Validation")
    class DataValidation {
        
        @Test
        @DisplayName("Should generate valid JSON for all scenario types")
        void shouldGenerateValidJSONForAllScenarioTypes() {
            CompleteMessageModel[] scenarios = {
                TestDataFactory.createStandardBusinessScenario(),
                TestDataFactory.createQueryBusinessScenario(),
                TestDataFactory.createTransferBusinessScenario(),
                TestDataFactory.createMinBoundaryScenario(),
                TestDataFactory.createMaxBoundaryScenario(),
                TestDataFactory.createNullValueScenario(),
                TestDataFactory.createRandomScenario()
            };
            
            for (CompleteMessageModel scenario : scenarios) {
                String json = scenario.toJson();
                assertNotNull(json);
                assertFalse(json.isEmpty());
                
                // 验证可以从JSON反序列化
                CompleteMessageModel deserializedMessage = CompleteMessageModel.fromJson(json);
                assertNotNull(deserializedMessage);
            }
        }
        
        @Test
        @DisplayName("Should create messages with required data")
        void shouldCreateMessagesWithRequiredData() {
            CompleteMessageModel[] scenarios = {
                TestDataFactory.createStandardBusinessScenario(),
                TestDataFactory.createQueryBusinessScenario(),
                TestDataFactory.createTransferBusinessScenario(),
                TestDataFactory.createRandomScenario()
            };
            
            for (CompleteMessageModel scenario : scenarios) {
                assertTrue(scenario.hasRequiredData());
                assertNotNull(scenario.getTxHeader().getMsgGrptMac());
                assertNotNull(scenario.getTxHeader().getGlobalBusiTrackNo());
                assertNotNull(scenario.getTxHeader().getSubtxNo());
                assertNotNull(scenario.getTxEntity().getCustNo());
            }
        }
        
        @Test
        @DisplayName("Should create messages with proper field formats")
        void shouldCreateMessagesWithProperFieldFormats() {
            CompleteMessageModel message = TestDataFactory.createStandardBusinessScenario();
            
            // 验证日期时间格式
            TxHeaderModel header = message.getTxHeader();
            assertTrue(header.getTxStartTime().matches("\\d{14}")); // YYYYMMDDHHMMSS
            assertTrue(header.getTxSendTime().matches("\\d{14}"));
            
            // 验证日期格式
            TxComnModel comn = message.getTxComn();
            assertTrue(comn.getAccountingDate().matches("\\d{8}")); // YYYYMMDD
            
            // 验证客户号格式
            TxEntityModel entity = message.getTxEntity();
            assertTrue(entity.getCustNo().matches("\\d{15}")); // 15位数字
        }
    }
}