package com.example.httpclientdemo.model;

import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * CompleteMessageModel单元测试类
 */
public class CompleteMessageModelTest {
    
    private CompleteMessageModel completeMessage;
    private TxHeaderModel txHeader;
    private TxEntityModel txEntity;
    private TxComnModel txComn;
    
    @BeforeEach
    void setUp() {
        completeMessage = new CompleteMessageModel();
        
        // 创建测试用的txHeader
        txHeader = new TxHeaderModel();
        txHeader.setMsgGrptMac("{{msgGrptMac}}");
        txHeader.setGlobalBusiTrackNo("{{globalBusiTrackNo}}");
        txHeader.setSubtxNo("{{subtxNo}}");
        txHeader.setTxStartTime("{{txStartTime}}");
        txHeader.setTxSendTime("{{txSendTime}}");
        
        // 创建测试用的txEntity
        txEntity = new TxEntityModel();
        txEntity.setCustNo("040000037480013");
        txEntity.setQryVchrTpCd("1");
        txEntity.setTxSceneCd("C203");
        
        // 创建测试用的txComn
        txComn = new TxComnModel();
        txComn.setAccountingDate("00000000");
        txComn.addTxComnField(1, "curQryReqNum", "0");
        txComn.addTxComnField(1, "bgnIndexNo", "0");
        txComn.addTxComnField(8, "busiSendSysOrCmptNo", "99710730008");
    }
    
    @Test
    void testDefaultConstructor() {
        CompleteMessageModel model = new CompleteMessageModel();
        assertNotNull(model.getTxHeader());
        assertNotNull(model.getTxBody());
    }
    
    @Test
    void testParameterizedConstructor() {
        CompleteMessageModel model = new CompleteMessageModel(txHeader, txEntity, txComn);
        
        assertEquals(txHeader, model.getTxHeader());
        assertEquals(txEntity, model.getTxEntity());
        assertNotNull(model.getTxComn());
        assertEquals("00000000", model.getTxComn().getAccountingDate());
    }
    
    @Test
    void testParameterizedConstructorWithNullValues() {
        CompleteMessageModel model = new CompleteMessageModel(null, null, null);
        
        assertNotNull(model.getTxHeader());
        assertNotNull(model.getTxBody());
        assertNull(model.getTxEntity());
    }
    
    @Test
    void testTxHeaderGetterAndSetter() {
        completeMessage.setTxHeader(txHeader);
        assertEquals(txHeader, completeMessage.getTxHeader());
        
        // 测试设置null
        completeMessage.setTxHeader(null);
        assertNotNull(completeMessage.getTxHeader());
    }
    
    @Test
    void testTxBodyGetterAndSetter() {
        CompleteMessageModel.TxBodyModel txBody = new CompleteMessageModel.TxBodyModel();
        txBody.setTxEntity(txEntity);
        
        completeMessage.setTxBody(txBody);
        assertEquals(txBody, completeMessage.getTxBody());
        
        // 测试设置null
        completeMessage.setTxBody(null);
        assertNotNull(completeMessage.getTxBody());
    }
    
    @Test
    void testTxEntityConvenienceMethods() {
        completeMessage.setTxEntity(txEntity);
        assertEquals(txEntity, completeMessage.getTxEntity());
        
        // 测试在txBody为null时设置txEntity
        completeMessage.setTxBody(null);
        completeMessage.setTxEntity(txEntity);
        assertEquals(txEntity, completeMessage.getTxEntity());
    }
    
    @Test
    void testTxComnConvenienceMethods() {
        completeMessage.setTxComn(txComn);
        
        TxComnModel retrievedTxComn = completeMessage.getTxComn();
        assertNotNull(retrievedTxComn);
        assertEquals("00000000", retrievedTxComn.getAccountingDate());
        assertEquals("0", retrievedTxComn.getTxComnField(1, "curQryReqNum"));
        assertEquals("99710730008", retrievedTxComn.getTxComnField(8, "busiSendSysOrCmptNo"));
        
        // 测试在txBody为null时设置txComn
        completeMessage.setTxBody(null);
        completeMessage.setTxComn(txComn);
        assertNotNull(completeMessage.getTxComn());
    }
    
    @Test
    void testTxComnWithNullValue() {
        completeMessage.setTxComn(null);
        // 应该不会抛出异常
        
        // 测试setTxBody(null)会创建新的TxBodyModel
        completeMessage.setTxBody(null);
        assertNotNull(completeMessage.getTxBody());
        assertNotNull(completeMessage.getTxComn()); // setTxBody(null)创建了新的TxBodyModel，所以getTxComn()不会返回null
    }
    
    @Test
    void testValidateWithValidData() {
        completeMessage.setTxHeader(txHeader);
        completeMessage.setTxEntity(txEntity);
        completeMessage.setTxComn(txComn);
        
        assertTrue(completeMessage.validate());
    }
    
    @Test
    void testValidateWithInvalidTxHeader() {
        TxHeaderModel invalidHeader = new TxHeaderModel();
        // 缺少必填字段
        
        completeMessage.setTxHeader(invalidHeader);
        completeMessage.setTxEntity(txEntity);
        completeMessage.setTxComn(txComn);
        
        assertFalse(completeMessage.validate());
    }
    
    @Test
    void testValidateWithNullTxHeader() {
        completeMessage.setTxHeader(null);
        completeMessage.setTxEntity(txEntity);
        completeMessage.setTxComn(txComn);
        
        // setTxHeader(null)会创建新的TxHeaderModel，但没有必填字段
        assertFalse(completeMessage.validate());
    }
    
    @Test
    void testValidateWithInvalidTxEntity() {
        TxEntityModel invalidEntity = new TxEntityModel();
        // 缺少必填字段
        
        completeMessage.setTxHeader(txHeader);
        completeMessage.setTxEntity(invalidEntity);
        completeMessage.setTxComn(txComn);
        
        assertFalse(completeMessage.validate());
    }
    
    @Test
    void testValidateWithNullTxEntity() {
        completeMessage.setTxHeader(txHeader);
        completeMessage.setTxEntity(null);
        completeMessage.setTxComn(txComn);
        
        assertFalse(completeMessage.validate());
    }
    
    @Test
    void testValidateWithInvalidAccountingDate() {
        TxComnModel invalidTxComn = new TxComnModel();
        invalidTxComn.setAccountingDate("invalid_date");
        
        completeMessage.setTxHeader(txHeader);
        completeMessage.setTxEntity(txEntity);
        completeMessage.setTxComn(invalidTxComn);
        
        assertFalse(completeMessage.validate());
    }
    
    @Test
    void testValidateFormatWithValidData() {
        completeMessage.setTxHeader(txHeader);
        completeMessage.setTxEntity(txEntity);
        completeMessage.setTxComn(txComn);
        
        assertTrue(completeMessage.validateFormat());
    }
    
    @Test
    void testValidateFormatWithInvalidTxHeaderLength() {
        TxHeaderModel invalidHeader = new TxHeaderModel();
        invalidHeader.setMsgGrptMac("a".repeat(51)); // 超过长度限制
        
        completeMessage.setTxHeader(invalidHeader);
        completeMessage.setTxEntity(txEntity);
        
        assertFalse(completeMessage.validateFormat());
    }
    
    @Test
    void testValidateFormatWithInvalidTxEntityFormat() {
        TxEntityModel invalidEntity = new TxEntityModel();
        invalidEntity.setCustNo("invalid_cust_no"); // 不符合格式
        invalidEntity.setQryVchrTpCd("1");
        invalidEntity.setTxSceneCd("C203");
        
        completeMessage.setTxHeader(txHeader);
        completeMessage.setTxEntity(invalidEntity);
        
        assertFalse(completeMessage.validateFormat());
    }
    
    @Test
    void testJsonSerialization() {
        completeMessage.setTxHeader(txHeader);
        completeMessage.setTxEntity(txEntity);
        completeMessage.setTxComn(txComn);
        
        String json = completeMessage.toJson();
        
        assertNotNull(json);
        assertTrue(json.contains("txHeader"));
        assertTrue(json.contains("txBody"));
        assertTrue(json.contains("msgGrptMac"));
        assertTrue(json.contains("custNo"));
        assertTrue(json.contains("accountingDate"));
    }
    
    @Test
    void testPrettyJsonSerialization() {
        completeMessage.setTxHeader(txHeader);
        completeMessage.setTxEntity(txEntity);
        completeMessage.setTxComn(txComn);
        
        String prettyJson = completeMessage.toPrettyJson();
        
        assertNotNull(prettyJson);
        assertTrue(prettyJson.contains("\n")); // 格式化的JSON应该包含换行符
        assertTrue(prettyJson.contains("txHeader"));
        assertTrue(prettyJson.contains("txBody"));
    }
    
    @Test
    void testJsonDeserialization() {
        completeMessage.setTxHeader(txHeader);
        completeMessage.setTxEntity(txEntity);
        completeMessage.setTxComn(txComn);
        
        String json = completeMessage.toJson();
        CompleteMessageModel deserializedModel = CompleteMessageModel.fromJson(json);
        
        assertNotNull(deserializedModel);
        assertNotNull(deserializedModel.getTxHeader());
        assertNotNull(deserializedModel.getTxEntity());
        assertEquals("{{msgGrptMac}}", deserializedModel.getTxHeader().getMsgGrptMac());
        assertEquals("040000037480013", deserializedModel.getTxEntity().getCustNo());
    }
    
    @Test
    void testJsonDeserializationWithNullOrEmptyJson() {
        assertNull(CompleteMessageModel.fromJson(null));
        assertNull(CompleteMessageModel.fromJson(""));
        assertNull(CompleteMessageModel.fromJson("   "));
    }
    
    @Test
    void testJsonDeserializationWithInvalidJson() {
        assertThrows(RuntimeException.class, () -> {
            CompleteMessageModel.fromJson("invalid json");
        });
    }
    
    @Test
    void testDeepCopy() {
        completeMessage.setTxHeader(txHeader);
        completeMessage.setTxEntity(txEntity);
        completeMessage.setTxComn(txComn);
        
        CompleteMessageModel copy = completeMessage.deepCopy();
        
        assertNotNull(copy);
        assertNotSame(completeMessage, copy);
        assertNotSame(completeMessage.getTxHeader(), copy.getTxHeader());
        assertNotSame(completeMessage.getTxEntity(), copy.getTxEntity());
        
        // 验证数据相同
        assertEquals(completeMessage.getTxHeader().getMsgGrptMac(), copy.getTxHeader().getMsgGrptMac());
        assertEquals(completeMessage.getTxEntity().getCustNo(), copy.getTxEntity().getCustNo());
    }
    
    @Test
    void testHasRequiredDataWithValidData() {
        completeMessage.setTxHeader(txHeader);
        completeMessage.setTxEntity(txEntity);
        
        assertTrue(completeMessage.hasRequiredData());
    }
    
    @Test
    void testHasRequiredDataWithMissingTxHeader() {
        completeMessage.setTxHeader(null);
        completeMessage.setTxEntity(txEntity);
        
        // setTxHeader(null)会创建新的TxHeaderModel，但缺少必填字段
        assertFalse(completeMessage.hasRequiredData());
    }
    
    @Test
    void testHasRequiredDataWithMissingTxEntity() {
        completeMessage.setTxHeader(txHeader);
        completeMessage.setTxEntity(null);
        
        assertFalse(completeMessage.hasRequiredData());
    }
    
    @Test
    void testHasRequiredDataWithMissingHeaderFields() {
        TxHeaderModel incompleteHeader = new TxHeaderModel();
        incompleteHeader.setMsgGrptMac("{{msgGrptMac}}");
        // 缺少globalBusiTrackNo和subtxNo
        
        completeMessage.setTxHeader(incompleteHeader);
        completeMessage.setTxEntity(txEntity);
        
        assertFalse(completeMessage.hasRequiredData());
    }
    
    @Test
    void testGetSummary() {
        completeMessage.setTxHeader(txHeader);
        completeMessage.setTxEntity(txEntity);
        
        String summary = completeMessage.getSummary();
        
        assertNotNull(summary);
        assertTrue(summary.contains("CompleteMessage["));
        assertTrue(summary.contains("msgGrptMac={{msgGrptMac}}"));
        assertTrue(summary.contains("globalBusiTrackNo={{globalBusiTrackNo}}"));
        assertTrue(summary.contains("custNo=040000037480013"));
        assertTrue(summary.contains("txSceneCd=C203"));
        assertTrue(summary.contains("]"));
    }
    
    @Test
    void testGetSummaryWithNullData() {
        CompleteMessageModel emptyMessage = new CompleteMessageModel();
        String summary = emptyMessage.getSummary();
        
        assertNotNull(summary);
        assertTrue(summary.contains("CompleteMessage["));
        assertTrue(summary.contains("]"));
    }
    
    @Test
    void testTxBodyModelConstructor() {
        CompleteMessageModel.TxBodyModel txBody = new CompleteMessageModel.TxBodyModel();
        
        assertNotNull(txBody.getAddtData());
        assertTrue(txBody.getAddtData().isEmpty());
        assertNotNull(txBody.getTxComn1());
        assertTrue(txBody.getTxComn1().isEmpty());
        assertNotNull(txBody.getTxComn8());
        assertTrue(txBody.getTxComn8().isEmpty());
    }
    
    @Test
    void testTxBodyModelSettersWithNull() {
        CompleteMessageModel.TxBodyModel txBody = new CompleteMessageModel.TxBodyModel();
        
        txBody.setAddtData(null);
        assertNotNull(txBody.getAddtData());
        assertTrue(txBody.getAddtData().isEmpty());
        
        txBody.setTxComn1(null);
        assertNotNull(txBody.getTxComn1());
        assertTrue(txBody.getTxComn1().isEmpty());
    }
}