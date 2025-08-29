package com.example.httpclientdemo.builder;

import com.example.httpclientdemo.model.CompleteMessageModel;
import com.example.httpclientdemo.model.TxHeaderModel;
import com.example.httpclientdemo.model.TxEntityModel;
import com.example.httpclientdemo.model.TxComnModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * MessageBuilder单元测试类
 */
@DisplayName("MessageBuilder Tests")
class MessageBuilderTest {
    
    private MessageBuilder builder;
    
    @BeforeEach
    void setUp() {
        builder = MessageBuilder.create();
    }
    
    @Nested
    @DisplayName("Basic Builder Operations")
    class BasicBuilderOperations {
        
        @Test
        @DisplayName("Should create MessageBuilder instance")
        void shouldCreateMessageBuilderInstance() {
            assertNotNull(builder);
        }
        
        @Test
        @DisplayName("Should build basic message with defaults")
        void shouldBuildBasicMessageWithDefaults() {
            CompleteMessageModel message = builder
                .withDefaults()
                .build();
            
            assertNotNull(message);
            assertNotNull(message.getTxHeader());
            assertNotNull(message.getTxEntity());
            assertNotNull(message.getTxComn());
        }
        
        @Test
        @DisplayName("Should build message without defaults")
        void shouldBuildMessageWithoutDefaults() {
            CompleteMessageModel message = builder.build();
            
            assertNotNull(message);
            assertNotNull(message.getTxHeader());
            assertNotNull(message.getTxEntity());
            assertNotNull(message.getTxComn());
        }
    }
    
    @Nested
    @DisplayName("TxHeader Configuration")
    class TxHeaderConfiguration {
        
        @Test
        @DisplayName("Should configure TxHeader using fluent API")
        void shouldConfigureTxHeaderUsingFluentAPI() {
            CompleteMessageModel message = builder
                .withTxHeader(header -> header
                    .msgGrptMac("test-mac")
                    .globalBusiTrackNo("test-track-no")
                    .subtxNo("test-subtx")
                    .txStartTime("20231201120000")
                    .txSendTime("20231201120001")
                    .txCode("TEST001")
                    .channelNo("WEB")
                    .orgNo("001")
                    .tellerId("teller001")
                )
                .build();
            
            TxHeaderModel header = message.getTxHeader();
            assertEquals("test-mac", header.getMsgGrptMac());
            assertEquals("test-track-no", header.getGlobalBusiTrackNo());
            assertEquals("test-subtx", header.getSubtxNo());
            assertEquals("20231201120000", header.getTxStartTime());
            assertEquals("20231201120001", header.getTxSendTime());
            assertEquals("TEST001", header.getTxCode());
            assertEquals("WEB", header.getChannelNo());
            assertEquals("001", header.getOrgNo());
            assertEquals("teller001", header.getTellerId());
        }
        
        @Test
        @DisplayName("Should configure TxHeader with direct model")
        void shouldConfigureTxHeaderWithDirectModel() {
            TxHeaderModel headerModel = new TxHeaderModel();
            headerModel.setMsgGrptMac("direct-mac");
            headerModel.setGlobalBusiTrackNo("direct-track");
            headerModel.setSubtxNo("direct-subtx");
            
            CompleteMessageModel message = builder
                .withTxHeader(headerModel)
                .build();
            
            TxHeaderModel header = message.getTxHeader();
            assertEquals("direct-mac", header.getMsgGrptMac());
            assertEquals("direct-track", header.getGlobalBusiTrackNo());
            assertEquals("direct-subtx", header.getSubtxNo());
        }
        
        @Test
        @DisplayName("Should handle null TxHeader gracefully")
        void shouldHandleNullTxHeaderGracefully() {
            CompleteMessageModel message = builder
                .withTxHeader((TxHeaderModel) null)
                .build();
            
            assertNotNull(message.getTxHeader());
        }
        
        @Test
        @DisplayName("Should apply TxHeader defaults")
        void shouldApplyTxHeaderDefaults() {
            CompleteMessageModel message = builder
                .withDefaults()
                .build();
            
            TxHeaderModel header = message.getTxHeader();
            assertEquals("{{msgGrptMac}}", header.getMsgGrptMac());
            assertEquals("{{globalBusiTrackNo}}", header.getGlobalBusiTrackNo());
            assertEquals("{{subtxNo}}", header.getSubtxNo());
            assertEquals("{{txStartTime}}", header.getTxStartTime());
            assertEquals("{{txSendTime}}", header.getTxSendTime());
        }
    }
    
    @Nested
    @DisplayName("TxEntity Configuration")
    class TxEntityConfiguration {
        
        @Test
        @DisplayName("Should configure TxEntity using fluent API")
        void shouldConfigureTxEntityUsingFluentAPI() {
            CompleteMessageModel message = builder
                .withTxEntity(entity -> entity
                    .custNo("123456789012345")
                    .qryVchrTpCd("2")
                    .txSceneCd("C204")
                    .addField("customField", "customValue")
                )
                .build();
            
            TxEntityModel entity = message.getTxEntity();
            assertEquals("123456789012345", entity.getCustNo());
            assertEquals("2", entity.getQryVchrTpCd());
            assertEquals("C204", entity.getTxSceneCd());
            assertEquals("customValue", entity.getField("customField"));
        }
        
        @Test
        @DisplayName("Should configure TxEntity with additional fields")
        void shouldConfigureTxEntityWithAdditionalFields() {
            Map<String, Object> additionalFields = new HashMap<>();
            additionalFields.put("field1", "value1");
            additionalFields.put("field2", 123);
            additionalFields.put("field3", true);
            
            CompleteMessageModel message = builder
                .withTxEntity(entity -> entity
                    .custNo("123456789012345")
                    .qryVchrTpCd("1")
                    .txSceneCd("C203")
                    .withAdditionalFields(additionalFields)
                )
                .build();
            
            TxEntityModel entity = message.getTxEntity();
            assertEquals("value1", entity.getField("field1"));
            assertEquals(123, entity.getField("field2"));
            assertEquals(true, entity.getField("field3"));
        }
        
        @Test
        @DisplayName("Should configure TxEntity with direct model")
        void shouldConfigureTxEntityWithDirectModel() {
            TxEntityModel entityModel = new TxEntityModel();
            entityModel.setCustNo("987654321098765");
            entityModel.setQryVchrTpCd("3");
            entityModel.setTxSceneCd("C205");
            
            CompleteMessageModel message = builder
                .withTxEntity(entityModel)
                .build();
            
            TxEntityModel entity = message.getTxEntity();
            assertEquals("987654321098765", entity.getCustNo());
            assertEquals("3", entity.getQryVchrTpCd());
            assertEquals("C205", entity.getTxSceneCd());
        }
        
        @Test
        @DisplayName("Should apply TxEntity defaults")
        void shouldApplyTxEntityDefaults() {
            CompleteMessageModel message = builder
                .withDefaults()
                .build();
            
            TxEntityModel entity = message.getTxEntity();
            assertEquals("040000037480013", entity.getCustNo());
            assertEquals("1", entity.getQryVchrTpCd());
            assertEquals("C203", entity.getTxSceneCd());
        }
    }
    
    @Nested
    @DisplayName("TxComn Configuration")
    class TxComnConfiguration {
        
        @Test
        @DisplayName("Should configure TxComn using fluent API")
        void shouldConfigureTxComnUsingFluentAPI() {
            CompleteMessageModel message = builder
                .withTxComn(comn -> comn
                    .accountingDate("20231201")
                    .addtData("key1", "value1")
                    .txComn1("field1", "value1")
                    .txComn8("field8", "value8")
                    .curQryReqNum("10")
                    .bgnIndexNo("0")
                    .busiSendSysOrCmptNo("99710730008")
                )
                .build();
            
            TxComnModel comn = message.getTxComn();
            assertEquals("20231201", comn.getAccountingDate());
            assertEquals("value1", comn.getAddtDataField("key1"));
            assertEquals("value1", comn.getTxComnField(1, "field1"));
            assertEquals("value8", comn.getTxComnField(8, "field8"));
            assertEquals("10", comn.getTxComnField(1, "curQryReqNum"));
            assertEquals("0", comn.getTxComnField(1, "bgnIndexNo"));
            assertEquals("99710730008", comn.getTxComnField(8, "busiSendSysOrCmptNo"));
        }
        
        @Test
        @DisplayName("Should configure TxComn with Map data")
        void shouldConfigureTxComnWithMapData() {
            Map<String, Object> addtData = new HashMap<>();
            addtData.put("addtKey1", "addtValue1");
            addtData.put("addtKey2", 456);
            
            Map<String, Object> txComn1Data = new HashMap<>();
            txComn1Data.put("curQryReqNum", "5");
            txComn1Data.put("bgnIndexNo", "1");
            
            CompleteMessageModel message = builder
                .withTxComn(comn -> comn
                    .accountingDate("20231202")
                    .withAddtData(addtData)
                    .withTxComn1(txComn1Data)
                )
                .build();
            
            TxComnModel comn = message.getTxComn();
            assertEquals("20231202", comn.getAccountingDate());
            assertEquals("addtValue1", comn.getAddtDataField("addtKey1"));
            assertEquals(456, comn.getAddtDataField("addtKey2"));
            assertEquals("5", comn.getTxComnField(1, "curQryReqNum"));
            assertEquals("1", comn.getTxComnField(1, "bgnIndexNo"));
        }
        
        @Test
        @DisplayName("Should configure TxComn with direct model")
        void shouldConfigureTxComnWithDirectModel() {
            TxComnModel comnModel = new TxComnModel();
            comnModel.setAccountingDate("20231203");
            comnModel.addAddtDataField("directKey", "directValue");
            comnModel.addTxComnField(1, "directField", "directFieldValue");
            
            CompleteMessageModel message = builder
                .withTxComn(comnModel)
                .build();
            
            TxComnModel comn = message.getTxComn();
            assertEquals("20231203", comn.getAccountingDate());
            assertEquals("directValue", comn.getAddtDataField("directKey"));
            assertEquals("directFieldValue", comn.getTxComnField(1, "directField"));
        }
        
        @Test
        @DisplayName("Should apply TxComn defaults")
        void shouldApplyTxComnDefaults() {
            CompleteMessageModel message = builder
                .withDefaults()
                .build();
            
            TxComnModel comn = message.getTxComn();
            assertEquals("00000000", comn.getAccountingDate());
        }
    }
    
    @Nested
    @DisplayName("Validation and Error Handling")
    class ValidationAndErrorHandling {
        
        @Test
        @DisplayName("Should validate and build successfully with valid data")
        void shouldValidateAndBuildSuccessfullyWithValidData() {
            CompleteMessageModel message = builder
                .withTxHeader(header -> header
                    .msgGrptMac("valid-mac")
                    .globalBusiTrackNo("valid-track")
                    .subtxNo("valid-subtx")
                )
                .withTxEntity(entity -> entity
                    .custNo("123456789012345")
                    .qryVchrTpCd("1")
                    .txSceneCd("C203")
                )
                .withTxComn(comn -> comn
                    .accountingDate("20231201")
                )
                .buildAndValidate();
            
            assertNotNull(message);
            assertTrue(message.validate());
            assertTrue(message.hasRequiredData());
        }
        
        @Test
        @DisplayName("Should throw exception when validation fails")
        void shouldThrowExceptionWhenValidationFails() {
            // 创建一个会导致验证失败的消息（缺少必填字段）
            assertThrows(IllegalStateException.class, () -> {
                builder
                    .withTxHeader(header -> header
                        // 缺少必填的msgGrptMac
                        .globalBusiTrackNo("valid-track")
                        .subtxNo("valid-subtx")
                    )
                    .withTxEntity(entity -> entity
                        .custNo("123456789012345")
                        .qryVchrTpCd("1")
                        .txSceneCd("C203")
                    )
                    .buildAndValidate();
            });
        }
        
        @Test
        @DisplayName("Should handle null configurers gracefully")
        void shouldHandleNullConfigurersGracefully() {
            CompleteMessageModel message = builder
                .withTxHeader((Consumer<MessageBuilder.TxHeaderBuilder>) null)
                .withTxEntity((Consumer<MessageBuilder.TxEntityBuilder>) null)
                .withTxComn((Consumer<MessageBuilder.TxComnBuilder>) null)
                .build();
            
            assertNotNull(message);
            assertNotNull(message.getTxHeader());
            assertNotNull(message.getTxEntity());
            assertNotNull(message.getTxComn());
        }
    }
    
    @Nested
    @DisplayName("Complex Message Building")
    class ComplexMessageBuilding {
        
        @Test
        @DisplayName("Should build complex message with all components")
        void shouldBuildComplexMessageWithAllComponents() {
            Map<String, Object> entityFields = new HashMap<>();
            entityFields.put("extraField1", "extraValue1");
            entityFields.put("extraField2", 789);
            
            Map<String, Object> addtData = new HashMap<>();
            addtData.put("addtKey", "addtValue");
            
            CompleteMessageModel message = builder
                .withTxHeader(header -> header
                    .msgGrptMac("complex-mac")
                    .globalBusiTrackNo("complex-track")
                    .subtxNo("complex-subtx")
                    .txStartTime("20231201120000")
                    .txSendTime("20231201120001")
                    .txCode("COMPLEX")
                    .channelNo("API")
                    .orgNo("999")
                    .tellerId("complex-teller")
                    .terminalId("terminal-001")
                    .clientIp("192.168.1.100")
                )
                .withTxEntity(entity -> entity
                    .custNo("999888777666555")
                    .qryVchrTpCd("9")
                    .txSceneCd("C999")
                    .withAdditionalFields(entityFields)
                )
                .withTxComn(comn -> comn
                    .accountingDate("20231201")
                    .withAddtData(addtData)
                    .curQryReqNum("100")
                    .bgnIndexNo("50")
                    .busiSendSysOrCmptNo("complex-sys-001")
                    .txComn2("comn2Field", "comn2Value")
                    .txComn3("comn3Field", "comn3Value")
                )
                .build();
            
            // 验证TxHeader
            TxHeaderModel header = message.getTxHeader();
            assertEquals("complex-mac", header.getMsgGrptMac());
            assertEquals("complex-track", header.getGlobalBusiTrackNo());
            assertEquals("COMPLEX", header.getTxCode());
            assertEquals("API", header.getChannelNo());
            assertEquals("192.168.1.100", header.getClientIp());
            
            // 验证TxEntity
            TxEntityModel entity = message.getTxEntity();
            assertEquals("999888777666555", entity.getCustNo());
            assertEquals("9", entity.getQryVchrTpCd());
            assertEquals("C999", entity.getTxSceneCd());
            assertEquals("extraValue1", entity.getField("extraField1"));
            assertEquals(789, entity.getField("extraField2"));
            
            // 验证TxComn
            TxComnModel comn = message.getTxComn();
            assertEquals("20231201", comn.getAccountingDate());
            assertEquals("addtValue", comn.getAddtDataField("addtKey"));
            assertEquals("100", comn.getTxComnField(1, "curQryReqNum"));
            assertEquals("50", comn.getTxComnField(1, "bgnIndexNo"));
            assertEquals("complex-sys-001", comn.getTxComnField(8, "busiSendSysOrCmptNo"));
            assertEquals("comn2Value", comn.getTxComnField(2, "comn2Field"));
            assertEquals("comn3Value", comn.getTxComnField(3, "comn3Field"));
        }
        
        @Test
        @DisplayName("Should build message with chained operations")
        void shouldBuildMessageWithChainedOperations() {
            CompleteMessageModel message = MessageBuilder.create()
                .withDefaults()
                .withTxHeader(header -> header
                    .msgGrptMac("chained-mac")
                    .globalBusiTrackNo("chained-track")
                )
                .withTxEntity(entity -> entity
                    .custNo("111222333444555")
                    .addField("chainedField", "chainedValue")
                )
                .withTxComn(comn -> comn
                    .accountingDate("20231205")
                    .addtData("chainedKey", "chainedValue")
                )
                .build();
            
            assertNotNull(message);
            assertEquals("chained-mac", message.getTxHeader().getMsgGrptMac());
            assertEquals("111222333444555", message.getTxEntity().getCustNo());
            assertEquals("20231205", message.getTxComn().getAccountingDate());
            assertEquals("chainedValue", message.getTxEntity().getField("chainedField"));
            assertEquals("chainedValue", message.getTxComn().getAddtDataField("chainedKey"));
        }
        
        @Test
        @DisplayName("Should generate valid JSON from built message")
        void shouldGenerateValidJSONFromBuiltMessage() {
            CompleteMessageModel message = builder
                .withDefaults()
                .withTxHeader(header -> header
                    .msgGrptMac("json-test-mac")
                    .globalBusiTrackNo("json-test-track")
                )
                .withTxEntity(entity -> entity
                    .custNo("040000037480013")
                    .qryVchrTpCd("1")
                    .txSceneCd("C203")
                )
                .withTxComn(comn -> comn
                    .accountingDate("20231201")
                    .curQryReqNum("0")
                    .bgnIndexNo("0")
                    .busiSendSysOrCmptNo("99710730008")
                )
                .build();
            
            String json = message.toJson();
            assertNotNull(json);
            assertFalse(json.isEmpty());
            assertTrue(json.contains("json-test-mac"));
            assertTrue(json.contains("040000037480013"));
            assertTrue(json.contains("20231201"));
            
            // 验证可以从JSON反序列化
            CompleteMessageModel deserializedMessage = CompleteMessageModel.fromJson(json);
            assertNotNull(deserializedMessage);
            assertEquals("json-test-mac", deserializedMessage.getTxHeader().getMsgGrptMac());
        }
    }
}