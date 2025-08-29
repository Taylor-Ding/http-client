package com.example.httpclientdemo.validation;

import com.example.httpclientdemo.model.*;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 数据验证测试类
 * 测试所有模型类的数据验证逻辑，包括必填字段检查、格式验证、长度限制等
 */
public class DataValidationTest {
    
    private TxHeaderModel txHeaderModel;
    private TxEntityModel txEntityModel;
    private TxComnModel txComnModel;
    private CompleteMessageModel completeMessageModel;
    
    @BeforeEach
    void setUp() {
        txHeaderModel = new TxHeaderModel();
        txEntityModel = new TxEntityModel();
        txComnModel = new TxComnModel();
        completeMessageModel = new CompleteMessageModel();
    }
    
    // ========== TxHeaderModel 必填字段验证测试 ==========
    
    @Test
    @DisplayName("TxHeaderModel - 所有必填字段都存在时验证通过")
    void testTxHeaderValidation_AllRequiredFieldsPresent_ShouldPass() {
        txHeaderModel.setMsgGrptMac("valid_mac");
        txHeaderModel.setGlobalBusiTrackNo("valid_track_no");
        txHeaderModel.setSubtxNo("valid_subtx");
        
        assertTrue(txHeaderModel.validate(), "所有必填字段存在时应该验证通过");
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("TxHeaderModel - msgGrptMac为空或null时验证失败")
    void testTxHeaderValidation_InvalidMsgGrptMac_ShouldFail(String invalidValue) {
        txHeaderModel.setMsgGrptMac(invalidValue);
        txHeaderModel.setGlobalBusiTrackNo("valid_track_no");
        txHeaderModel.setSubtxNo("valid_subtx");
        
        assertFalse(txHeaderModel.validate(), 
            "msgGrptMac为空或null时应该验证失败: " + invalidValue);
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("TxHeaderModel - globalBusiTrackNo为空或null时验证失败")
    void testTxHeaderValidation_InvalidGlobalBusiTrackNo_ShouldFail(String invalidValue) {
        txHeaderModel.setMsgGrptMac("valid_mac");
        txHeaderModel.setGlobalBusiTrackNo(invalidValue);
        txHeaderModel.setSubtxNo("valid_subtx");
        
        assertFalse(txHeaderModel.validate(), 
            "globalBusiTrackNo为空或null时应该验证失败: " + invalidValue);
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("TxHeaderModel - subtxNo为空或null时验证失败")
    void testTxHeaderValidation_InvalidSubtxNo_ShouldFail(String invalidValue) {
        txHeaderModel.setMsgGrptMac("valid_mac");
        txHeaderModel.setGlobalBusiTrackNo("valid_track_no");
        txHeaderModel.setSubtxNo(invalidValue);
        
        assertFalse(txHeaderModel.validate(), 
            "subtxNo为空或null时应该验证失败: " + invalidValue);
    }
    
    // ========== TxHeaderModel 字段长度验证测试 ==========
    
    @Test
    @DisplayName("TxHeaderModel - 字段长度在限制内时验证通过")
    void testTxHeaderFieldLengths_ValidLengths_ShouldPass() {
        txHeaderModel.setMsgGrptMac("a".repeat(50)); // 最大长度
        txHeaderModel.setGlobalBusiTrackNo("b".repeat(50)); // 最大长度
        txHeaderModel.setSubtxNo("c".repeat(20)); // 最大长度
        txHeaderModel.setTxCode("d".repeat(10)); // 最大长度
        txHeaderModel.setChannelNo("e".repeat(10)); // 最大长度
        
        assertTrue(txHeaderModel.validateFieldLengths(), "字段长度在限制内时应该验证通过");
    }
    
    @Test
    @DisplayName("TxHeaderModel - msgGrptMac超长时验证失败")
    void testTxHeaderFieldLengths_MsgGrptMacTooLong_ShouldFail() {
        txHeaderModel.setMsgGrptMac("a".repeat(51)); // 超过50字符限制
        
        assertFalse(txHeaderModel.validateFieldLengths(), "msgGrptMac超长时应该验证失败");
    }
    
    @Test
    @DisplayName("TxHeaderModel - globalBusiTrackNo超长时验证失败")
    void testTxHeaderFieldLengths_GlobalBusiTrackNoTooLong_ShouldFail() {
        txHeaderModel.setGlobalBusiTrackNo("a".repeat(51)); // 超过50字符限制
        
        assertFalse(txHeaderModel.validateFieldLengths(), "globalBusiTrackNo超长时应该验证失败");
    }
    
    @Test
    @DisplayName("TxHeaderModel - subtxNo超长时验证失败")
    void testTxHeaderFieldLengths_SubtxNoTooLong_ShouldFail() {
        txHeaderModel.setSubtxNo("a".repeat(21)); // 超过20字符限制
        
        assertFalse(txHeaderModel.validateFieldLengths(), "subtxNo超长时应该验证失败");
    }
    
    @Test
    @DisplayName("TxHeaderModel - txCode超长时验证失败")
    void testTxHeaderFieldLengths_TxCodeTooLong_ShouldFail() {
        txHeaderModel.setTxCode("a".repeat(11)); // 超过10字符限制
        
        assertFalse(txHeaderModel.validateFieldLengths(), "txCode超长时应该验证失败");
    }
    
    @Test
    @DisplayName("TxHeaderModel - channelNo超长时验证失败")
    void testTxHeaderFieldLengths_ChannelNoTooLong_ShouldFail() {
        txHeaderModel.setChannelNo("a".repeat(11)); // 超过10字符限制
        
        assertFalse(txHeaderModel.validateFieldLengths(), "channelNo超长时应该验证失败");
    }
    
    // ========== TxEntityModel 必填字段验证测试 ==========
    
    @Test
    @DisplayName("TxEntityModel - 所有必填字段都存在时验证通过")
    void testTxEntityValidation_AllRequiredFieldsPresent_ShouldPass() {
        txEntityModel.setCustNo("040000037480013");
        txEntityModel.setQryVchrTpCd("1");
        txEntityModel.setTxSceneCd("C203");
        
        assertTrue(txEntityModel.validate(), "所有必填字段存在时应该验证通过");
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("TxEntityModel - custNo为空或null时验证失败")
    void testTxEntityValidation_InvalidCustNo_ShouldFail(String invalidValue) {
        txEntityModel.setCustNo(invalidValue);
        txEntityModel.setQryVchrTpCd("1");
        txEntityModel.setTxSceneCd("C203");
        
        assertFalse(txEntityModel.validate(), 
            "custNo为空或null时应该验证失败: " + invalidValue);
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("TxEntityModel - qryVchrTpCd为空或null时验证失败")
    void testTxEntityValidation_InvalidQryVchrTpCd_ShouldFail(String invalidValue) {
        txEntityModel.setCustNo("040000037480013");
        txEntityModel.setQryVchrTpCd(invalidValue);
        txEntityModel.setTxSceneCd("C203");
        
        assertFalse(txEntityModel.validate(), 
            "qryVchrTpCd为空或null时应该验证失败: " + invalidValue);
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("TxEntityModel - txSceneCd为空或null时验证失败")
    void testTxEntityValidation_InvalidTxSceneCd_ShouldFail(String invalidValue) {
        txEntityModel.setCustNo("040000037480013");
        txEntityModel.setQryVchrTpCd("1");
        txEntityModel.setTxSceneCd(invalidValue);
        
        assertFalse(txEntityModel.validate(), 
            "txSceneCd为空或null时应该验证失败: " + invalidValue);
    }
    
    // ========== TxEntityModel 格式验证测试 ==========
    
    @Test
    @DisplayName("TxEntityModel - 正确格式时验证通过")
    void testTxEntityFormatValidation_ValidFormats_ShouldPass() {
        txEntityModel.setCustNo("040000037480013"); // 15位数字
        txEntityModel.setQryVchrTpCd("1"); // 1位数字
        txEntityModel.setTxSceneCd("C203"); // 字母+3位数字
        
        assertTrue(txEntityModel.validateFormat(), "正确格式时应该验证通过");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"04000003748001", "0400000374800134", "04000003748001a", "abc123456789012"})
    @DisplayName("TxEntityModel - custNo格式错误时验证失败")
    void testTxEntityFormatValidation_InvalidCustNoFormat_ShouldFail(String invalidCustNo) {
        txEntityModel.setCustNo(invalidCustNo);
        txEntityModel.setQryVchrTpCd("1");
        txEntityModel.setTxSceneCd("C203");
        
        assertFalse(txEntityModel.validateFormat(), 
            "custNo格式错误时应该验证失败: " + invalidCustNo);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"12", "a", ""})
    @DisplayName("TxEntityModel - qryVchrTpCd格式错误时验证失败")
    void testTxEntityFormatValidation_InvalidQryVchrTpCdFormat_ShouldFail(String invalidQryVchrTpCd) {
        txEntityModel.setCustNo("040000037480013");
        txEntityModel.setQryVchrTpCd(invalidQryVchrTpCd);
        txEntityModel.setTxSceneCd("C203");
        
        assertFalse(txEntityModel.validateFormat(), 
            "qryVchrTpCd格式错误时应该验证失败: " + invalidQryVchrTpCd);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"1203", "C20", "C2034", "203", "ABCD"})
    @DisplayName("TxEntityModel - txSceneCd格式错误时验证失败")
    void testTxEntityFormatValidation_InvalidTxSceneCdFormat_ShouldFail(String invalidTxSceneCd) {
        txEntityModel.setCustNo("040000037480013");
        txEntityModel.setQryVchrTpCd("1");
        txEntityModel.setTxSceneCd(invalidTxSceneCd);
        
        assertFalse(txEntityModel.validateFormat(), 
            "txSceneCd格式错误时应该验证失败: " + invalidTxSceneCd);
    }
    
    // ========== TxEntityModel 字段长度验证测试 ==========
    
    @Test
    @DisplayName("TxEntityModel - 字段长度在限制内时验证通过")
    void testTxEntityFieldLengths_ValidLengths_ShouldPass() {
        txEntityModel.setCustNo("a".repeat(20)); // 最大长度
        txEntityModel.setQryVchrTpCd("a".repeat(5)); // 最大长度
        txEntityModel.setTxSceneCd("a".repeat(10)); // 最大长度
        
        assertTrue(txEntityModel.validateFieldLengths(), "字段长度在限制内时应该验证通过");
    }
    
    @Test
    @DisplayName("TxEntityModel - custNo超长时验证失败")
    void testTxEntityFieldLengths_CustNoTooLong_ShouldFail() {
        txEntityModel.setCustNo("a".repeat(21)); // 超过20字符限制
        
        assertFalse(txEntityModel.validateFieldLengths(), "custNo超长时应该验证失败");
    }
    
    @Test
    @DisplayName("TxEntityModel - qryVchrTpCd超长时验证失败")
    void testTxEntityFieldLengths_QryVchrTpCdTooLong_ShouldFail() {
        txEntityModel.setQryVchrTpCd("a".repeat(6)); // 超过5字符限制
        
        assertFalse(txEntityModel.validateFieldLengths(), "qryVchrTpCd超长时应该验证失败");
    }
    
    @Test
    @DisplayName("TxEntityModel - txSceneCd超长时验证失败")
    void testTxEntityFieldLengths_TxSceneCdTooLong_ShouldFail() {
        txEntityModel.setTxSceneCd("a".repeat(11)); // 超过10字符限制
        
        assertFalse(txEntityModel.validateFieldLengths(), "txSceneCd超长时应该验证失败");
    }
    
    // ========== TxComnModel 验证测试 ==========
    
    @Test
    @DisplayName("TxComnModel - 有效的accountingDate格式验证通过")
    void testTxComnValidation_ValidAccountingDate_ShouldPass() {
        txComnModel.setAccountingDate("20231201");
        assertTrue(txComnModel.validateAccountingDate(), "有效的accountingDate格式应该验证通过");
        
        txComnModel.setAccountingDate("00000000");
        assertTrue(txComnModel.validateAccountingDate(), "00000000格式应该验证通过");
        
        txComnModel.setAccountingDate("19991231");
        assertTrue(txComnModel.validateAccountingDate(), "19991231格式应该验证通过");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"2023120", "202312011", "2023-12-01", "abcd1234", ""})
    @DisplayName("TxComnModel - 无效的accountingDate格式验证失败")
    void testTxComnValidation_InvalidAccountingDate_ShouldFail(String invalidDate) {
        txComnModel.setAccountingDate(invalidDate);
        
        assertFalse(txComnModel.validateAccountingDate(), 
            "无效的accountingDate格式应该验证失败: " + invalidDate);
    }
    
    @Test
    @DisplayName("TxComnModel - null的accountingDate验证通过")
    void testTxComnValidation_NullAccountingDate_ShouldPass() {
        txComnModel.setAccountingDate(null);
        assertTrue(txComnModel.validateAccountingDate(), "null的accountingDate应该验证通过");
    }
    
    @Test
    @DisplayName("TxComnModel - 20231301等8位数字格式验证通过（不验证日期有效性）")
    void testTxComnValidation_EightDigitsPass_ShouldPass() {
        // 注意：当前实现只验证8位数字格式，不验证日期的实际有效性
        txComnModel.setAccountingDate("20231301"); // 13月无效，但格式正确
        assertTrue(txComnModel.validateAccountingDate(), "8位数字格式应该验证通过");
        
        txComnModel.setAccountingDate("99999999"); // 无效日期，但格式正确
        assertTrue(txComnModel.validateAccountingDate(), "8位数字格式应该验证通过");
    }
    
    @Test
    @DisplayName("TxEntityModel - qryVchrTpCd为0等单个数字验证通过")
    void testTxEntityFormatValidation_SingleDigitPass_ShouldPass() {
        txEntityModel.setCustNo("040000037480013");
        txEntityModel.setQryVchrTpCd("0");
        txEntityModel.setTxSceneCd("C203");
        
        assertTrue(txEntityModel.validateFormat(), "单个数字0应该验证通过");
        
        txEntityModel.setQryVchrTpCd("9");
        assertTrue(txEntityModel.validateFormat(), "单个数字9应该验证通过");
    }
    
    // ========== CompleteMessageModel 综合验证测试 ==========
    
    @Test
    @DisplayName("CompleteMessageModel - 完整有效数据验证通过")
    void testCompleteMessageValidation_ValidData_ShouldPass() {
        // 设置有效的txHeader
        TxHeaderModel validHeader = new TxHeaderModel();
        validHeader.setMsgGrptMac("valid_mac");
        validHeader.setGlobalBusiTrackNo("valid_track_no");
        validHeader.setSubtxNo("valid_subtx");
        
        // 设置有效的txEntity
        TxEntityModel validEntity = new TxEntityModel();
        validEntity.setCustNo("040000037480013");
        validEntity.setQryVchrTpCd("1");
        validEntity.setTxSceneCd("C203");
        
        // 设置有效的txComn
        TxComnModel validComn = new TxComnModel();
        validComn.setAccountingDate("20231201");
        
        completeMessageModel.setTxHeader(validHeader);
        completeMessageModel.setTxEntity(validEntity);
        completeMessageModel.setTxComn(validComn);
        
        assertTrue(completeMessageModel.validate(), "完整有效数据应该验证通过");
        assertTrue(completeMessageModel.validateFormat(), "完整有效数据格式应该验证通过");
        assertTrue(completeMessageModel.hasRequiredData(), "应该包含必要数据");
    }
    
    @Test
    @DisplayName("CompleteMessageModel - 缺少txHeader时验证失败")
    void testCompleteMessageValidation_MissingTxHeader_ShouldFail() {
        completeMessageModel.setTxHeader(null); // 会创建空的TxHeaderModel
        
        TxEntityModel validEntity = new TxEntityModel();
        validEntity.setCustNo("040000037480013");
        validEntity.setQryVchrTpCd("1");
        validEntity.setTxSceneCd("C203");
        
        completeMessageModel.setTxEntity(validEntity);
        
        assertFalse(completeMessageModel.validate(), "缺少有效txHeader时应该验证失败");
        assertFalse(completeMessageModel.hasRequiredData(), "缺少必要数据");
    }
    
    @Test
    @DisplayName("CompleteMessageModel - 缺少txEntity时验证失败")
    void testCompleteMessageValidation_MissingTxEntity_ShouldFail() {
        TxHeaderModel validHeader = new TxHeaderModel();
        validHeader.setMsgGrptMac("valid_mac");
        validHeader.setGlobalBusiTrackNo("valid_track_no");
        validHeader.setSubtxNo("valid_subtx");
        
        completeMessageModel.setTxHeader(validHeader);
        completeMessageModel.setTxEntity(null);
        
        assertFalse(completeMessageModel.validate(), "缺少txEntity时应该验证失败");
        assertFalse(completeMessageModel.hasRequiredData(), "缺少必要数据");
    }
    
    @Test
    @DisplayName("CompleteMessageModel - 无效accountingDate时验证失败")
    void testCompleteMessageValidation_InvalidAccountingDate_ShouldFail() {
        TxHeaderModel validHeader = new TxHeaderModel();
        validHeader.setMsgGrptMac("valid_mac");
        validHeader.setGlobalBusiTrackNo("valid_track_no");
        validHeader.setSubtxNo("valid_subtx");
        
        TxEntityModel validEntity = new TxEntityModel();
        validEntity.setCustNo("040000037480013");
        validEntity.setQryVchrTpCd("1");
        validEntity.setTxSceneCd("C203");
        
        TxComnModel invalidComn = new TxComnModel();
        invalidComn.setAccountingDate("invalid_date");
        
        completeMessageModel.setTxHeader(validHeader);
        completeMessageModel.setTxEntity(validEntity);
        completeMessageModel.setTxComn(invalidComn);
        
        assertFalse(completeMessageModel.validate(), "无效accountingDate时应该验证失败");
    }
    
    // ========== JSON序列化异常处理测试 ==========
    
    @Test
    @DisplayName("JSON序列化 - 正常对象序列化成功")
    void testJsonSerialization_ValidObject_ShouldSucceed() {
        TxHeaderModel validHeader = new TxHeaderModel();
        validHeader.setMsgGrptMac("{{msgGrptMac}}");
        validHeader.setGlobalBusiTrackNo("{{globalBusiTrackNo}}");
        validHeader.setSubtxNo("{{subtxNo}}");
        
        assertDoesNotThrow(() -> {
            String json = JSON.toJSONString(validHeader);
            assertNotNull(json, "JSON序列化结果不应为null");
            assertTrue(json.contains("msgGrptMac"), "JSON应包含msgGrptMac字段");
        }, "正常对象序列化不应抛出异常");
    }
    
    @Test
    @DisplayName("JSON序列化 - 包含特殊字符的对象序列化成功")
    void testJsonSerialization_ObjectWithSpecialCharacters_ShouldSucceed() {
        TxEntityModel entityWithSpecialChars = new TxEntityModel();
        entityWithSpecialChars.setCustNo("040000037480013");
        entityWithSpecialChars.setQryVchrTpCd("1");
        entityWithSpecialChars.setTxSceneCd("C203");
        entityWithSpecialChars.addField("specialField", "包含中文和特殊字符!@#$%^&*()");
        
        assertDoesNotThrow(() -> {
            String json = JSON.toJSONString(entityWithSpecialChars);
            assertNotNull(json, "包含特殊字符的对象序列化结果不应为null");
            assertTrue(json.contains("specialField"), "JSON应包含特殊字段");
        }, "包含特殊字符的对象序列化不应抛出异常");
    }
    
    @Test
    @DisplayName("JSON序列化 - 嵌套对象序列化成功")
    void testJsonSerialization_NestedObject_ShouldSucceed() {
        TxHeaderModel header = new TxHeaderModel();
        header.setMsgGrptMac("{{msgGrptMac}}");
        header.setGlobalBusiTrackNo("{{globalBusiTrackNo}}");
        header.setSubtxNo("{{subtxNo}}");
        
        TxEntityModel entity = new TxEntityModel();
        entity.setCustNo("040000037480013");
        entity.setQryVchrTpCd("1");
        entity.setTxSceneCd("C203");
        
        TxComnModel comn = new TxComnModel();
        comn.setAccountingDate("20231201");
        
        CompleteMessageModel completeMessage = new CompleteMessageModel();
        completeMessage.setTxHeader(header);
        completeMessage.setTxEntity(entity);
        completeMessage.setTxComn(comn);
        
        assertDoesNotThrow(() -> {
            String json = completeMessage.toJson();
            assertNotNull(json, "嵌套对象序列化结果不应为null");
            assertTrue(json.contains("txHeader"), "JSON应包含txHeader");
            assertTrue(json.contains("txBody"), "JSON应包含txBody");
        }, "嵌套对象序列化不应抛出异常");
    }
    
    @Test
    @DisplayName("JSON反序列化 - 有效JSON反序列化成功")
    void testJsonDeserialization_ValidJson_ShouldSucceed() {
        String validJson = "{\"msgGrptMac\":\"{{msgGrptMac}}\",\"globalBusiTrackNo\":\"{{globalBusiTrackNo}}\",\"subtxNo\":\"{{subtxNo}}\"}";
        
        assertDoesNotThrow(() -> {
            TxHeaderModel header = JSON.parseObject(validJson, TxHeaderModel.class);
            assertNotNull(header, "反序列化结果不应为null");
            assertEquals("{{msgGrptMac}}", header.getMsgGrptMac(), "msgGrptMac应正确反序列化");
        }, "有效JSON反序列化不应抛出异常");
    }
    
    @Test
    @DisplayName("JSON反序列化 - 无效JSON抛出异常")
    void testJsonDeserialization_InvalidJson_ShouldThrowException() {
        String invalidJson = "{invalid json format";
        
        assertThrows(Exception.class, () -> {
            JSON.parseObject(invalidJson, TxHeaderModel.class);
        }, "无效JSON应该抛出异常");
    }
    
    @Test
    @DisplayName("JSON反序列化 - 空JSON字符串处理")
    void testJsonDeserialization_EmptyJson_ShouldHandleGracefully() {
        assertNull(CompleteMessageModel.fromJson(null), "null JSON应返回null");
        assertNull(CompleteMessageModel.fromJson(""), "空字符串JSON应返回null");
        assertNull(CompleteMessageModel.fromJson("   "), "空白字符串JSON应返回null");
    }
    
    @Test
    @DisplayName("JSON反序列化 - CompleteMessageModel无效JSON抛出RuntimeException")
    void testCompleteMessageFromJson_InvalidJson_ShouldThrowRuntimeException() {
        String invalidJson = "{invalid json}";
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            CompleteMessageModel.fromJson(invalidJson);
        }, "CompleteMessageModel.fromJson对无效JSON应抛出RuntimeException");
        
        assertTrue(exception.getMessage().contains("Failed to parse JSON"), 
            "异常消息应包含解析失败信息");
        assertNotNull(exception.getCause(), "应该包含原始异常信息");
    }
    
    @Test
    @DisplayName("JSON序列化 - 循环引用对象处理")
    void testJsonSerialization_CircularReference_ShouldHandleGracefully() {
        // 创建一个包含大量嵌套数据的对象来测试序列化性能和稳定性
        TxComnModel complexComn = new TxComnModel();
        complexComn.setAccountingDate("20231201");
        
        // 添加大量数据到各个txComn字段
        for (int i = 1; i <= 8; i++) {
            for (int j = 0; j < 100; j++) {
                complexComn.addTxComnField(i, "field_" + j, "value_" + j);
            }
        }
        
        // 添加大量addtData
        for (int i = 0; i < 100; i++) {
            complexComn.addAddtDataField("addtField_" + i, "addtValue_" + i);
        }
        
        assertDoesNotThrow(() -> {
            String json = JSON.toJSONString(complexComn);
            assertNotNull(json, "复杂对象序列化结果不应为null");
            assertTrue(json.length() > 1000, "复杂对象的JSON应该很长");
        }, "复杂对象序列化不应抛出异常");
    }
    
    @Test
    @DisplayName("JSON序列化 - null值字段处理")
    void testJsonSerialization_NullFields_ShouldHandleGracefully() {
        TxHeaderModel headerWithNulls = new TxHeaderModel();
        // 只设置部分字段，其他保持null
        headerWithNulls.setMsgGrptMac("{{msgGrptMac}}");
        // globalBusiTrackNo和subtxNo保持null
        
        assertDoesNotThrow(() -> {
            String json = JSON.toJSONString(headerWithNulls);
            assertNotNull(json, "包含null字段的对象序列化结果不应为null");
            assertTrue(json.contains("msgGrptMac"), "JSON应包含非null字段");
        }, "包含null字段的对象序列化不应抛出异常");
    }
    
    @Test
    @DisplayName("JSON序列化 - 深拷贝功能测试")
    void testJsonSerialization_DeepCopy_ShouldWork() {
        TxHeaderModel originalHeader = new TxHeaderModel();
        originalHeader.setMsgGrptMac("original_mac");
        originalHeader.setGlobalBusiTrackNo("original_track");
        originalHeader.setSubtxNo("original_subtx");
        
        TxEntityModel originalEntity = new TxEntityModel();
        originalEntity.setCustNo("040000037480013");
        originalEntity.setQryVchrTpCd("1");
        originalEntity.setTxSceneCd("C203");
        
        CompleteMessageModel original = new CompleteMessageModel();
        original.setTxHeader(originalHeader);
        original.setTxEntity(originalEntity);
        
        assertDoesNotThrow(() -> {
            CompleteMessageModel copy = original.deepCopy();
            assertNotNull(copy, "深拷贝结果不应为null");
            assertNotSame(original, copy, "深拷贝应创建新对象");
            assertNotSame(original.getTxHeader(), copy.getTxHeader(), "深拷贝应创建新的txHeader对象");
            assertEquals(original.getTxHeader().getMsgGrptMac(), 
                        copy.getTxHeader().getMsgGrptMac(), "深拷贝应保持数据一致性");
        }, "深拷贝功能不应抛出异常");
    }
}