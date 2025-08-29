package com.example.httpclientdemo.exception;

import com.example.httpclientdemo.model.*;
import com.example.httpclientdemo.factory.TestDataFactory;
import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 异常场景测试类
 * 测试缺失关键字段、格式错误数据、超长字段值等场景的处理
 */
public class ExceptionScenarioTest {
    
    private TestDataFactory testDataFactory;
    
    @BeforeEach
    void setUp() {
        testDataFactory = new TestDataFactory();
    }
    
    // ========== 缺失关键字段测试 ==========
    
    @Test
    @DisplayName("缺失关键字段 - 缺少txHeader必填字段时验证失败")
    void testMissingCriticalFields_TxHeaderRequired_ShouldFail() {
        // 创建缺少必填字段的txHeader
        TxHeaderModel incompleteHeader = new TxHeaderModel();
        // 只设置部分字段，缺少msgGrptMac, globalBusiTrackNo, subtxNo
        incompleteHeader.setTxCode("TEST001");
        
        TxEntityModel validEntity = new TxEntityModel("040000037480013", "1", "C203");
        TxComnModel validComn = new TxComnModel("20231201");
        
        CompleteMessageModel incompleteMessage = new CompleteMessageModel();
        incompleteMessage.setTxHeader(incompleteHeader);
        incompleteMessage.setTxEntity(validEntity);
        incompleteMessage.setTxComn(validComn);
        
        // 验证不完整的消息
        assertFalse(incompleteMessage.validate(), "缺少必填字段的消息应该验证失败");
        assertFalse(incompleteMessage.hasRequiredData(), "缺少必填字段的消息应该被识别为不完整");
        
        // 验证JSON序列化不会抛出异常
        assertDoesNotThrow(() -> {
            String json = incompleteMessage.toJson();
            assertNotNull(json, "即使数据不完整，JSON序列化也应该成功");
            assertTrue(json.contains("txHeader"), "JSON应该包含txHeader");
        }, "JSON序列化不应该因为数据不完整而抛出异常");
    }
    
    @Test
    @DisplayName("缺失关键字段 - 缺少txEntity必填字段时验证失败")
    void testMissingCriticalFields_TxEntityRequired_ShouldFail() {
        TxHeaderModel validHeader = new TxHeaderModel("{{msgGrptMac}}", "{{globalBusiTrackNo}}", "{{subtxNo}}");
        
        // 创建缺少必填字段的txEntity
        TxEntityModel incompleteEntity = new TxEntityModel();
        // 只设置部分字段，缺少custNo, qryVchrTpCd, txSceneCd
        incompleteEntity.addField("extraField", "extraValue");
        
        TxComnModel validComn = new TxComnModel("20231201");
        
        CompleteMessageModel incompleteMessage = new CompleteMessageModel();
        incompleteMessage.setTxHeader(validHeader);
        incompleteMessage.setTxEntity(incompleteEntity);
        incompleteMessage.setTxComn(validComn);
        
        // 验证不完整的消息
        assertFalse(incompleteMessage.validate(), "缺少txEntity必填字段的消息应该验证失败");
        
        // 验证JSON序列化处理
        assertDoesNotThrow(() -> {
            String json = incompleteMessage.toJson();
            assertNotNull(json, "JSON序列化应该成功");
            assertTrue(json.contains("extraField"), "JSON应该包含额外字段");
        }, "JSON序列化应该能够处理不完整的txEntity");
    }
    
    @Test
    @DisplayName("缺失关键字段 - 完全空的消息体处理")
    void testMissingCriticalFields_EmptyMessage_ShouldFail() {
        CompleteMessageModel emptyMessage = new CompleteMessageModel();
        // 不设置任何字段，使用默认的空对象
        
        assertFalse(emptyMessage.validate(), "空消息应该验证失败");
        assertFalse(emptyMessage.hasRequiredData(), "空消息应该被识别为缺少必要数据");
        
        // 验证空消息的JSON序列化
        assertDoesNotThrow(() -> {
            String json = emptyMessage.toJson();
            assertNotNull(json, "空消息的JSON序列化应该成功");
            assertTrue(json.contains("txHeader"), "JSON应该包含空的txHeader");
            assertTrue(json.contains("txBody"), "JSON应该包含空的txBody");
        }, "空消息的JSON序列化不应该抛出异常");
    }
    
    // ========== 格式错误数据处理测试 ==========
    
    @Test
    @DisplayName("格式错误数据 - JSON反序列化异常处理")
    void testFormatErrorData_JsonDeserializationException_ShouldHandleGracefully() {
        String invalidJson = "{invalid json format without closing brace";
        
        // 测试JSON反序列化异常处理
        assertThrows(RuntimeException.class, () -> {
            CompleteMessageModel.fromJson(invalidJson);
        }, "无效JSON应该抛出RuntimeException");
        
        // 测试null和空字符串处理
        assertNull(CompleteMessageModel.fromJson(null), "null JSON应该返回null");
        assertNull(CompleteMessageModel.fromJson(""), "空字符串JSON应该返回null");
        assertNull(CompleteMessageModel.fromJson("   "), "空白字符串JSON应该返回null");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "abc123456789012",  // 包含字母
        "04000003748001",   // 14位
        "0400000374800134", // 16位
        ""                  // 空字符串
    })
    @DisplayName("格式错误数据 - 无效custNo格式验证失败")
    void testFormatErrorData_InvalidCustNoFormat_ShouldFail(String invalidCustNo) {
        TxHeaderModel validHeader = new TxHeaderModel("{{msgGrptMac}}", "{{globalBusiTrackNo}}", "{{subtxNo}}");
        
        TxEntityModel invalidEntity = new TxEntityModel();
        invalidEntity.setCustNo(invalidCustNo);
        invalidEntity.setQryVchrTpCd("1");
        invalidEntity.setTxSceneCd("C203");
        
        TxComnModel validComn = new TxComnModel("20231201");
        
        CompleteMessageModel messageWithInvalidCustNo = new CompleteMessageModel();
        messageWithInvalidCustNo.setTxHeader(validHeader);
        messageWithInvalidCustNo.setTxEntity(invalidEntity);
        messageWithInvalidCustNo.setTxComn(validComn);
        
        // 验证格式错误
        assertFalse(messageWithInvalidCustNo.validateFormat(), 
            "无效custNo格式应该验证失败: " + invalidCustNo);
        
        // 验证JSON序列化仍然可以工作
        assertDoesNotThrow(() -> {
            String json = messageWithInvalidCustNo.toJson();
            assertNotNull(json, "即使格式错误，JSON序列化也应该成功");
        }, "JSON序列化不应该因为格式错误而抛出异常: " + invalidCustNo);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"12", "a", ""})
    @DisplayName("格式错误数据 - 无效qryVchrTpCd格式验证失败")
    void testFormatErrorData_InvalidQryVchrTpCdFormat_ShouldFail(String invalidQryVchrTpCd) {
        TxHeaderModel validHeader = new TxHeaderModel("{{msgGrptMac}}", "{{globalBusiTrackNo}}", "{{subtxNo}}");
        
        TxEntityModel invalidEntity = new TxEntityModel();
        invalidEntity.setCustNo("040000037480013");
        invalidEntity.setQryVchrTpCd(invalidQryVchrTpCd);
        invalidEntity.setTxSceneCd("C203");
        
        TxComnModel validComn = new TxComnModel("20231201");
        
        CompleteMessageModel messageWithInvalidQryVchrTpCd = new CompleteMessageModel();
        messageWithInvalidQryVchrTpCd.setTxHeader(validHeader);
        messageWithInvalidQryVchrTpCd.setTxEntity(invalidEntity);
        messageWithInvalidQryVchrTpCd.setTxComn(validComn);
        
        // 验证格式错误
        assertFalse(messageWithInvalidQryVchrTpCd.validateFormat(), 
            "无效qryVchrTpCd格式应该验证失败: " + invalidQryVchrTpCd);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"1203", "C20", "C2034", "203", "ABCD"})
    @DisplayName("格式错误数据 - 无效txSceneCd格式验证失败")
    void testFormatErrorData_InvalidTxSceneCdFormat_ShouldFail(String invalidTxSceneCd) {
        TxHeaderModel validHeader = new TxHeaderModel("{{msgGrptMac}}", "{{globalBusiTrackNo}}", "{{subtxNo}}");
        
        TxEntityModel invalidEntity = new TxEntityModel();
        invalidEntity.setCustNo("040000037480013");
        invalidEntity.setQryVchrTpCd("1");
        invalidEntity.setTxSceneCd(invalidTxSceneCd);
        
        TxComnModel validComn = new TxComnModel("20231201");
        
        CompleteMessageModel messageWithInvalidTxSceneCd = new CompleteMessageModel();
        messageWithInvalidTxSceneCd.setTxHeader(validHeader);
        messageWithInvalidTxSceneCd.setTxEntity(invalidEntity);
        messageWithInvalidTxSceneCd.setTxComn(validComn);
        
        // 验证格式错误
        assertFalse(messageWithInvalidTxSceneCd.validateFormat(), 
            "无效txSceneCd格式应该验证失败: " + invalidTxSceneCd);
    }
    
    // ========== 超长字段值异常处理测试 ==========
    
    @Test
    @DisplayName("超长字段值 - msgGrptMac超长验证失败")
    void testOversizedFieldValue_MsgGrptMacTooLong_ShouldFail() {
        String oversizedMsgGrptMac = "a".repeat(100); // 超过50字符限制
        
        TxHeaderModel oversizedHeader = new TxHeaderModel();
        oversizedHeader.setMsgGrptMac(oversizedMsgGrptMac);
        oversizedHeader.setGlobalBusiTrackNo("{{globalBusiTrackNo}}");
        oversizedHeader.setSubtxNo("{{subtxNo}}");
        
        TxEntityModel validEntity = new TxEntityModel("040000037480013", "1", "C203");
        TxComnModel validComn = new TxComnModel("20231201");
        
        CompleteMessageModel messageWithOversizedField = new CompleteMessageModel();
        messageWithOversizedField.setTxHeader(oversizedHeader);
        messageWithOversizedField.setTxEntity(validEntity);
        messageWithOversizedField.setTxComn(validComn);
        
        // 验证字段长度错误
        assertFalse(messageWithOversizedField.validateFormat(), "超长msgGrptMac应该验证失败");
        
        // 验证JSON序列化仍然可以处理超长字段
        assertDoesNotThrow(() -> {
            String json = messageWithOversizedField.toJson();
            assertNotNull(json, "JSON序列化应该能够处理超长字段");
            assertTrue(json.contains(oversizedMsgGrptMac), "JSON应该包含超长字段值");
        }, "JSON序列化不应该因为字段超长而抛出异常");
    }
    
    @Test
    @DisplayName("超长字段值 - 多个字段同时超长验证失败")
    void testOversizedFieldValue_MultipleFieldsTooLong_ShouldFail() {
        TxHeaderModel oversizedHeader = new TxHeaderModel();
        oversizedHeader.setMsgGrptMac("a".repeat(100));           // 超过50字符
        oversizedHeader.setGlobalBusiTrackNo("b".repeat(100));    // 超过50字符
        oversizedHeader.setSubtxNo("c".repeat(50));               // 超过20字符
        oversizedHeader.setTxCode("d".repeat(20));                // 超过10字符
        oversizedHeader.setChannelNo("e".repeat(20));             // 超过10字符
        
        TxEntityModel oversizedEntity = new TxEntityModel();
        oversizedEntity.setCustNo("f".repeat(50));                // 超过20字符
        oversizedEntity.setQryVchrTpCd("g".repeat(10));           // 超过5字符
        oversizedEntity.setTxSceneCd("h".repeat(20));             // 超过10字符
        
        TxComnModel validComn = new TxComnModel("20231201");
        
        CompleteMessageModel messageWithMultipleOversizedFields = new CompleteMessageModel();
        messageWithMultipleOversizedFields.setTxHeader(oversizedHeader);
        messageWithMultipleOversizedFields.setTxEntity(oversizedEntity);
        messageWithMultipleOversizedFields.setTxComn(validComn);
        
        // 验证多个字段长度错误
        assertFalse(messageWithMultipleOversizedFields.validateFormat(), "多个超长字段应该验证失败");
        
        // 验证JSON序列化处理多个超长字段
        assertDoesNotThrow(() -> {
            String json = messageWithMultipleOversizedFields.toJson();
            assertNotNull(json, "JSON序列化应该能够处理多个超长字段");
            assertTrue(json.length() > 1000, "包含多个超长字段的JSON应该很长");
        }, "JSON序列化不应该因为多个超长字段而抛出异常");
    }
    
    @Test
    @DisplayName("超长字段值 - 极大JSON数据处理")
    void testOversizedFieldValue_ExtremelyLargeJson_ShouldHandleGracefully() {
        TxHeaderModel validHeader = new TxHeaderModel("{{msgGrptMac}}", "{{globalBusiTrackNo}}", "{{subtxNo}}");
        TxEntityModel validEntity = new TxEntityModel("040000037480013", "1", "C203");
        TxComnModel largeComn = new TxComnModel();
        largeComn.setAccountingDate("20231201");
        
        // 添加大量数据到txComn字段
        for (int i = 1; i <= 8; i++) {
            for (int j = 0; j < 100; j++) {
                largeComn.addTxComnField(i, "field_" + j, "value_" + "x".repeat(50));
            }
        }
        
        // 添加大量addtData
        for (int i = 0; i < 100; i++) {
            largeComn.addAddtDataField("addtField_" + i, "addtValue_" + "y".repeat(50));
        }
        
        CompleteMessageModel messageWithLargeData = new CompleteMessageModel();
        messageWithLargeData.setTxHeader(validHeader);
        messageWithLargeData.setTxEntity(validEntity);
        messageWithLargeData.setTxComn(largeComn);
        
        // 验证大数据处理
        assertDoesNotThrow(() -> {
            String json = messageWithLargeData.toJson();
            assertTrue(json.length() > 50000, "JSON应该非常大");
            
            // 测试深拷贝功能
            CompleteMessageModel copy = messageWithLargeData.deepCopy();
            assertNotNull(copy, "深拷贝应该成功");
            assertNotSame(messageWithLargeData, copy, "深拷贝应该创建新对象");
        }, "应该能够处理极大的JSON数据");
    }
    
    // ========== 综合异常场景测试 ==========
    
    @Test
    @DisplayName("综合异常场景 - 多种错误同时发生")
    void testCombinedExceptionScenarios_MultipleErrors_ShouldHandleGracefully() {
        // 创建包含多种错误的消息
        TxHeaderModel errorHeader = new TxHeaderModel();
        errorHeader.setMsgGrptMac("a".repeat(100));  // 超长
        // 缺少globalBusiTrackNo和subtxNo
        
        TxEntityModel errorEntity = new TxEntityModel();
        errorEntity.setCustNo("invalid_cust_no");    // 格式错误
        errorEntity.setQryVchrTpCd("invalid");       // 格式错误
        // 缺少txSceneCd
        
        TxComnModel errorComn = new TxComnModel();
        errorComn.setAccountingDate("invalid_date"); // 格式错误
        
        CompleteMessageModel errorMessage = new CompleteMessageModel();
        errorMessage.setTxHeader(errorHeader);
        errorMessage.setTxEntity(errorEntity);
        errorMessage.setTxComn(errorComn);
        
        // 验证多种错误
        assertFalse(errorMessage.validate(), "包含多种错误的消息应该验证失败");
        assertFalse(errorMessage.validateFormat(), "包含多种格式错误的消息应该格式验证失败");
        assertFalse(errorMessage.hasRequiredData(), "包含多种错误的消息应该被识别为不完整");
        
        // 验证即使有多种错误，JSON序列化仍然可以工作
        assertDoesNotThrow(() -> {
            String json = errorMessage.toJson();
            assertNotNull(json, "即使有多种错误，JSON序列化也应该成功");
            assertTrue(json.contains("invalid_cust_no"), "JSON应该包含错误的字段值");
        }, "JSON序列化不应该因为多种错误而抛出异常");
    }
    
    @Test
    @DisplayName("综合异常场景 - 异常恢复测试")
    void testCombinedExceptionScenarios_ErrorRecovery_ShouldWork() {
        // 创建一个有错误的消息
        TxHeaderModel errorHeader = new TxHeaderModel();
        errorHeader.setMsgGrptMac("a".repeat(100));  // 超长
        
        TxEntityModel errorEntity = new TxEntityModel();
        errorEntity.setCustNo("invalid_cust_no");    // 格式错误
        errorEntity.setQryVchrTpCd("1");
        errorEntity.setTxSceneCd("C203");
        
        CompleteMessageModel errorMessage = new CompleteMessageModel();
        errorMessage.setTxHeader(errorHeader);
        errorMessage.setTxEntity(errorEntity);
        
        // 验证错误状态
        assertFalse(errorMessage.validateFormat(), "错误消息应该验证失败");
        
        // 修复错误
        errorHeader.setMsgGrptMac("valid_mac");      // 修复超长问题
        errorHeader.setGlobalBusiTrackNo("{{globalBusiTrackNo}}");
        errorHeader.setSubtxNo("{{subtxNo}}");
        
        errorEntity.setCustNo("040000037480013");    // 修复格式错误
        
        // 验证恢复后的状态
        assertTrue(errorMessage.validate(), "修复后的消息应该验证通过");
        assertTrue(errorMessage.validateFormat(), "修复后的消息格式应该验证通过");
        assertTrue(errorMessage.hasRequiredData(), "修复后的消息应该包含必要数据");
        
        // 验证修复后的JSON序列化
        assertDoesNotThrow(() -> {
            String json = errorMessage.toJson();
            assertNotNull(json, "修复后的JSON序列化应该成功");
            assertTrue(json.contains("040000037480013"), "JSON应该包含修复后的字段值");
        }, "修复后的JSON序列化不应该抛出异常");
    }
}