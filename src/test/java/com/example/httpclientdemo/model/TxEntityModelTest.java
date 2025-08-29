package com.example.httpclientdemo.model;

import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

/**
 * TxEntityModel单元测试类
 */
public class TxEntityModelTest {
    
    private TxEntityModel txEntityModel;
    
    @BeforeEach
    void setUp() {
        txEntityModel = new TxEntityModel();
    }
    
    @Test
    void testDefaultConstructor() {
        TxEntityModel model = new TxEntityModel();
        assertNull(model.getCustNo());
        assertNull(model.getQryVchrTpCd());
        assertNull(model.getTxSceneCd());
        assertNotNull(model.getAdditionalFields());
        assertTrue(model.getAdditionalFields().isEmpty());
    }
    
    @Test
    void testParameterizedConstructor() {
        String custNo = "040000037480013";
        String qryVchrTpCd = "1";
        String txSceneCd = "C203";
        
        TxEntityModel model = new TxEntityModel(custNo, qryVchrTpCd, txSceneCd);
        
        assertEquals(custNo, model.getCustNo());
        assertEquals(qryVchrTpCd, model.getQryVchrTpCd());
        assertEquals(txSceneCd, model.getTxSceneCd());
        assertNotNull(model.getAdditionalFields());
    }
    
    @Test
    void testGettersAndSetters() {
        String custNo = "040000037480013";
        String qryVchrTpCd = "1";
        String txSceneCd = "C203";
        
        txEntityModel.setCustNo(custNo);
        assertEquals(custNo, txEntityModel.getCustNo());
        
        txEntityModel.setQryVchrTpCd(qryVchrTpCd);
        assertEquals(qryVchrTpCd, txEntityModel.getQryVchrTpCd());
        
        txEntityModel.setTxSceneCd(txSceneCd);
        assertEquals(txSceneCd, txEntityModel.getTxSceneCd());
    }
    
    @Test
    void testAdditionalFieldsOperations() {
        // 测试添加字段
        txEntityModel.addField("customField1", "value1");
        txEntityModel.addField("customField2", 123);
        
        assertEquals("value1", txEntityModel.getField("customField1"));
        assertEquals(123, txEntityModel.getField("customField2"));
        assertTrue(txEntityModel.hasField("customField1"));
        assertTrue(txEntityModel.hasField("customField2"));
        assertFalse(txEntityModel.hasField("nonExistentField"));
        
        // 测试移除字段
        Object removedValue = txEntityModel.removeField("customField1");
        assertEquals("value1", removedValue);
        assertFalse(txEntityModel.hasField("customField1"));
        assertNull(txEntityModel.getField("customField1"));
    }
    
    @Test
    void testAdditionalFieldsWithNullMap() {
        txEntityModel.setAdditionalFields(null);
        assertNotNull(txEntityModel.getAdditionalFields());
        assertTrue(txEntityModel.getAdditionalFields().isEmpty());
        
        // 测试在null map上的操作
        txEntityModel.setAdditionalFields(null);
        assertNull(txEntityModel.getField("anyField"));
        assertNull(txEntityModel.removeField("anyField"));
        assertFalse(txEntityModel.hasField("anyField"));
        
        // 添加字段应该创建新的map
        txEntityModel.addField("newField", "newValue");
        assertTrue(txEntityModel.hasField("newField"));
    }
    
    @Test
    void testValidateWithValidData() {
        txEntityModel.setCustNo("040000037480013");
        txEntityModel.setQryVchrTpCd("1");
        txEntityModel.setTxSceneCd("C203");
        
        assertTrue(txEntityModel.validate());
    }
    
    @Test
    void testValidateWithMissingCustNo() {
        txEntityModel.setQryVchrTpCd("1");
        txEntityModel.setTxSceneCd("C203");
        
        assertFalse(txEntityModel.validate());
    }
    
    @Test
    void testValidateWithEmptyCustNo() {
        txEntityModel.setCustNo("");
        txEntityModel.setQryVchrTpCd("1");
        txEntityModel.setTxSceneCd("C203");
        
        assertFalse(txEntityModel.validate());
    }
    
    @Test
    void testValidateWithWhitespaceCustNo() {
        txEntityModel.setCustNo("   ");
        txEntityModel.setQryVchrTpCd("1");
        txEntityModel.setTxSceneCd("C203");
        
        assertFalse(txEntityModel.validate());
    }
    
    @Test
    void testValidateWithMissingQryVchrTpCd() {
        txEntityModel.setCustNo("040000037480013");
        txEntityModel.setTxSceneCd("C203");
        
        assertFalse(txEntityModel.validate());
    }
    
    @Test
    void testValidateWithMissingTxSceneCd() {
        txEntityModel.setCustNo("040000037480013");
        txEntityModel.setQryVchrTpCd("1");
        
        assertFalse(txEntityModel.validate());
    }
    
    @Test
    void testValidateFormatWithValidData() {
        txEntityModel.setCustNo("040000037480013");
        txEntityModel.setQryVchrTpCd("1");
        txEntityModel.setTxSceneCd("C203");
        
        assertTrue(txEntityModel.validateFormat());
    }
    
    @Test
    void testValidateFormatWithInvalidCustNo() {
        txEntityModel.setCustNo("04000003748001"); // 14位，不是15位
        txEntityModel.setQryVchrTpCd("1");
        txEntityModel.setTxSceneCd("C203");
        
        assertFalse(txEntityModel.validateFormat());
    }
    
    @Test
    void testValidateFormatWithInvalidQryVchrTpCd() {
        txEntityModel.setCustNo("040000037480013");
        txEntityModel.setQryVchrTpCd("12"); // 2位，不是1位
        txEntityModel.setTxSceneCd("C203");
        
        assertFalse(txEntityModel.validateFormat());
    }
    
    @Test
    void testValidateFormatWithInvalidTxSceneCd() {
        txEntityModel.setCustNo("040000037480013");
        txEntityModel.setQryVchrTpCd("1");
        txEntityModel.setTxSceneCd("1203"); // 不符合字母+3位数字格式
        
        assertFalse(txEntityModel.validateFormat());
    }
    
    @Test
    void testValidateFormatWithNullValues() {
        // null值应该通过格式验证
        assertTrue(txEntityModel.validateFormat());
    }
    
    @Test
    void testValidateFieldLengthsWithValidData() {
        txEntityModel.setCustNo("040000037480013");
        txEntityModel.setQryVchrTpCd("1");
        txEntityModel.setTxSceneCd("C203");
        
        assertTrue(txEntityModel.validateFieldLengths());
    }
    
    @Test
    void testValidateFieldLengthsWithTooLongCustNo() {
        String longCustNo = "a".repeat(21); // 超过20字符限制
        txEntityModel.setCustNo(longCustNo);
        
        assertFalse(txEntityModel.validateFieldLengths());
    }
    
    @Test
    void testValidateFieldLengthsWithTooLongQryVchrTpCd() {
        String longQryVchrTpCd = "a".repeat(6); // 超过5字符限制
        txEntityModel.setQryVchrTpCd(longQryVchrTpCd);
        
        assertFalse(txEntityModel.validateFieldLengths());
    }
    
    @Test
    void testValidateFieldLengthsWithTooLongTxSceneCd() {
        String longTxSceneCd = "a".repeat(11); // 超过10字符限制
        txEntityModel.setTxSceneCd(longTxSceneCd);
        
        assertFalse(txEntityModel.validateFieldLengths());
    }
    
    @Test
    void testToMap() {
        txEntityModel.setCustNo("040000037480013");
        txEntityModel.setQryVchrTpCd("1");
        txEntityModel.setTxSceneCd("C203");
        txEntityModel.addField("customField", "customValue");
        
        Map<String, Object> map = txEntityModel.toMap();
        
        assertEquals("040000037480013", map.get("custNo"));
        assertEquals("1", map.get("qryVchrTpCd"));
        assertEquals("C203", map.get("txSceneCd"));
        assertEquals("customValue", map.get("customField"));
    }
    
    @Test
    void testToMapWithNullValues() {
        txEntityModel.addField("customField", "customValue");
        
        Map<String, Object> map = txEntityModel.toMap();
        
        assertFalse(map.containsKey("custNo"));
        assertFalse(map.containsKey("qryVchrTpCd"));
        assertFalse(map.containsKey("txSceneCd"));
        assertEquals("customValue", map.get("customField"));
    }
    
    @Test
    void testFromMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("custNo", "040000037480013");
        map.put("qryVchrTpCd", "1");
        map.put("txSceneCd", "C203");
        map.put("customField", "customValue");
        
        TxEntityModel model = TxEntityModel.fromMap(map);
        
        assertEquals("040000037480013", model.getCustNo());
        assertEquals("1", model.getQryVchrTpCd());
        assertEquals("C203", model.getTxSceneCd());
        assertEquals("customValue", model.getField("customField"));
    }
    
    @Test
    void testFromMapWithNullMap() {
        TxEntityModel model = TxEntityModel.fromMap(null);
        
        assertNull(model.getCustNo());
        assertNull(model.getQryVchrTpCd());
        assertNull(model.getTxSceneCd());
        assertNotNull(model.getAdditionalFields());
        assertTrue(model.getAdditionalFields().isEmpty());
    }
    
    @Test
    void testJsonSerialization() {
        txEntityModel.setCustNo("040000037480013");
        txEntityModel.setQryVchrTpCd("1");
        txEntityModel.setTxSceneCd("C203");
        
        String json = JSON.toJSONString(txEntityModel);
        
        assertNotNull(json);
        assertTrue(json.contains("custNo"));
        assertTrue(json.contains("040000037480013"));
        assertTrue(json.contains("qryVchrTpCd"));
        assertTrue(json.contains("txSceneCd"));
    }
    
    @Test
    void testJsonDeserialization() {
        String json = "{\"custNo\":\"040000037480013\",\"qryVchrTpCd\":\"1\",\"txSceneCd\":\"C203\"}";
        
        TxEntityModel model = JSON.parseObject(json, TxEntityModel.class);
        
        assertNotNull(model);
        assertEquals("040000037480013", model.getCustNo());
        assertEquals("1", model.getQryVchrTpCd());
        assertEquals("C203", model.getTxSceneCd());
    }
}