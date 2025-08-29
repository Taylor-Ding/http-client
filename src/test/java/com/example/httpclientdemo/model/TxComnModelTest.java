package com.example.httpclientdemo.model;

import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

/**
 * TxComnModel单元测试类
 */
public class TxComnModelTest {
    
    private TxComnModel txComnModel;
    
    @BeforeEach
    void setUp() {
        txComnModel = new TxComnModel();
    }
    
    @Test
    void testDefaultConstructor() {
        TxComnModel model = new TxComnModel();
        assertNull(model.getAccountingDate());
        assertNotNull(model.getAddtData());
        assertTrue(model.getAddtData().isEmpty());
        
        // 验证所有txComn字段都被初始化
        assertNotNull(model.getTxComn1());
        assertTrue(model.getTxComn1().isEmpty());
        assertNotNull(model.getTxComn2());
        assertTrue(model.getTxComn2().isEmpty());
        assertNotNull(model.getTxComn3());
        assertTrue(model.getTxComn3().isEmpty());
        assertNotNull(model.getTxComn4());
        assertTrue(model.getTxComn4().isEmpty());
        assertNotNull(model.getTxComn5());
        assertTrue(model.getTxComn5().isEmpty());
        assertNotNull(model.getTxComn6());
        assertTrue(model.getTxComn6().isEmpty());
        assertNotNull(model.getTxComn7());
        assertTrue(model.getTxComn7().isEmpty());
        assertNotNull(model.getTxComn8());
        assertTrue(model.getTxComn8().isEmpty());
    }
    
    @Test
    void testParameterizedConstructor() {
        String accountingDate = "20231201";
        TxComnModel model = new TxComnModel(accountingDate);
        
        assertEquals(accountingDate, model.getAccountingDate());
        assertNotNull(model.getAddtData());
    }
    
    @Test
    void testAccountingDateGetterAndSetter() {
        String accountingDate = "20231201";
        txComnModel.setAccountingDate(accountingDate);
        assertEquals(accountingDate, txComnModel.getAccountingDate());
    }
    
    @Test
    void testAddtDataOperations() {
        // 测试添加字段
        txComnModel.addAddtDataField("key1", "value1");
        txComnModel.addAddtDataField("key2", 123);
        
        assertEquals("value1", txComnModel.getAddtDataField("key1"));
        assertEquals(123, txComnModel.getAddtDataField("key2"));
        assertTrue(txComnModel.hasAddtData());
        
        // 测试移除字段
        Object removedValue = txComnModel.removeAddtDataField("key1");
        assertEquals("value1", removedValue);
        assertNull(txComnModel.getAddtDataField("key1"));
        
        // 测试清空
        txComnModel.clearAddtData();
        assertFalse(txComnModel.hasAddtData());
    }
    
    @Test
    void testAddtDataWithNullMap() {
        txComnModel.setAddtData(null);
        assertNotNull(txComnModel.getAddtData());
        assertTrue(txComnModel.getAddtData().isEmpty());
        
        // 测试在null map上的操作
        txComnModel.setAddtData(null);
        assertNull(txComnModel.getAddtDataField("anyKey"));
        assertNull(txComnModel.removeAddtDataField("anyKey"));
        
        // 添加字段应该创建新的map
        txComnModel.addAddtDataField("newKey", "newValue");
        assertEquals("newValue", txComnModel.getAddtDataField("newKey"));
    }
    
    @Test
    void testTxComnOperations() {
        // 测试添加字段到txComn1
        txComnModel.addTxComnField(1, "curQryReqNum", "0");
        txComnModel.addTxComnField(1, "bgnIndexNo", "0");
        
        assertEquals("0", txComnModel.getTxComnField(1, "curQryReqNum"));
        assertEquals("0", txComnModel.getTxComnField(1, "bgnIndexNo"));
        
        // 测试添加字段到txComn8
        txComnModel.addTxComnField(8, "busiSendSysOrCmptNo", "99710730008");
        assertEquals("99710730008", txComnModel.getTxComnField(8, "busiSendSysOrCmptNo"));
        
        assertTrue(txComnModel.hasAnyTxComnData());
        
        // 测试移除字段
        Object removedValue = txComnModel.removeTxComnField(1, "curQryReqNum");
        assertEquals("0", removedValue);
        assertNull(txComnModel.getTxComnField(1, "curQryReqNum"));
    }
    
    @Test
    void testTxComnOperationsWithInvalidNumber() {
        // 测试无效的txComn编号
        txComnModel.addTxComnField(0, "key", "value"); // 无效编号
        txComnModel.addTxComnField(9, "key", "value"); // 无效编号
        
        assertNull(txComnModel.getTxComnField(0, "key"));
        assertNull(txComnModel.getTxComnField(9, "key"));
        assertNull(txComnModel.removeTxComnField(0, "key"));
        assertNull(txComnModel.removeTxComnField(9, "key"));
    }
    
    @Test
    void testTxComnGettersAndSetters() {
        Map<String, Object> testData = new HashMap<>();
        testData.put("testKey", "testValue");
        
        // 测试所有txComn字段的getter和setter
        txComnModel.setTxComn1(testData);
        assertEquals(testData, txComnModel.getTxComn1());
        
        txComnModel.setTxComn2(testData);
        assertEquals(testData, txComnModel.getTxComn2());
        
        txComnModel.setTxComn3(testData);
        assertEquals(testData, txComnModel.getTxComn3());
        
        txComnModel.setTxComn4(testData);
        assertEquals(testData, txComnModel.getTxComn4());
        
        txComnModel.setTxComn5(testData);
        assertEquals(testData, txComnModel.getTxComn5());
        
        txComnModel.setTxComn6(testData);
        assertEquals(testData, txComnModel.getTxComn6());
        
        txComnModel.setTxComn7(testData);
        assertEquals(testData, txComnModel.getTxComn7());
        
        txComnModel.setTxComn8(testData);
        assertEquals(testData, txComnModel.getTxComn8());
    }
    
    @Test
    void testTxComnSettersWithNull() {
        // 测试设置null值
        txComnModel.setTxComn1(null);
        assertNotNull(txComnModel.getTxComn1());
        assertTrue(txComnModel.getTxComn1().isEmpty());
        
        txComnModel.setTxComn8(null);
        assertNotNull(txComnModel.getTxComn8());
        assertTrue(txComnModel.getTxComn8().isEmpty());
    }
    
    @Test
    void testSetTxComnMapMethod() {
        Map<String, Object> testData = new HashMap<>();
        testData.put("testKey", "testValue");
        
        // 测试通过编号设置txComn
        txComnModel.setTxComnMap(1, testData);
        assertEquals(testData, txComnModel.getTxComn1());
        txComnModel.setTxComnMap(2, testData);
        assertEquals(testData, txComnModel.getTxComn2());
        txComnModel.setTxComnMap(3, testData);
        assertEquals(testData, txComnModel.getTxComn3());
        txComnModel.setTxComnMap(4, testData);
        assertEquals(testData, txComnModel.getTxComn4());
        txComnModel.setTxComnMap(5, testData);
        assertEquals(testData, txComnModel.getTxComn5());
        txComnModel.setTxComnMap(6, testData);
        assertEquals(testData, txComnModel.getTxComn6());
        txComnModel.setTxComnMap(7, testData);
        assertEquals(testData, txComnModel.getTxComn7());
        txComnModel.setTxComnMap(8, testData);
        assertEquals(testData, txComnModel.getTxComn8());
        
        // 测试无效编号
        txComnModel.setTxComnMap(0, testData); // 应该不会抛出异常
        txComnModel.setTxComnMap(9, testData); // 应该不会抛出异常
    }
    
    @Test
    void testValidateAccountingDateWithValidData() {
        txComnModel.setAccountingDate("20231201");
        assertTrue(txComnModel.validateAccountingDate());
        
        txComnModel.setAccountingDate("00000000");
        assertTrue(txComnModel.validateAccountingDate());
        
        txComnModel.setAccountingDate("19991231");
        assertTrue(txComnModel.validateAccountingDate());
    }
    
    @Test
    void testValidateAccountingDateWithInvalidData() {
        txComnModel.setAccountingDate("2023120"); // 7位数字
        assertFalse(txComnModel.validateAccountingDate());
        
        txComnModel.setAccountingDate("202312011"); // 9位数字
        assertFalse(txComnModel.validateAccountingDate());
        
        txComnModel.setAccountingDate("2023-12-01"); // 包含非数字字符
        assertFalse(txComnModel.validateAccountingDate());
        
        txComnModel.setAccountingDate("abcd1234"); // 包含字母
        assertFalse(txComnModel.validateAccountingDate());
    }
    
    @Test
    void testValidateAccountingDateWithNull() {
        txComnModel.setAccountingDate(null);
        assertTrue(txComnModel.validateAccountingDate()); // null应该被认为是有效的
    }
    
    @Test
    void testValidate() {
        txComnModel.setAccountingDate("20231201");
        assertTrue(txComnModel.validate());
        
        txComnModel.setAccountingDate("invalid");
        assertFalse(txComnModel.validate());
    }
    
    @Test
    void testHasAnyTxComnData() {
        assertFalse(txComnModel.hasAnyTxComnData());
        
        txComnModel.addTxComnField(1, "key", "value");
        assertTrue(txComnModel.hasAnyTxComnData());
        
        txComnModel.clearAllTxComn();
        assertFalse(txComnModel.hasAnyTxComnData());
        
        txComnModel.addTxComnField(8, "key", "value");
        assertTrue(txComnModel.hasAnyTxComnData());
    }
    
    @Test
    void testClearAllTxComn() {
        // 添加数据到多个txComn字段
        for (int i = 1; i <= 8; i++) {
            txComnModel.addTxComnField(i, "key" + i, "value" + i);
        }
        
        assertTrue(txComnModel.hasAnyTxComnData());
        
        txComnModel.clearAllTxComn();
        assertFalse(txComnModel.hasAnyTxComnData());
        
        // 验证所有字段都被清空
        assertTrue(txComnModel.getTxComn1().isEmpty());
        assertTrue(txComnModel.getTxComn2().isEmpty());
        assertTrue(txComnModel.getTxComn3().isEmpty());
        assertTrue(txComnModel.getTxComn4().isEmpty());
        assertTrue(txComnModel.getTxComn5().isEmpty());
        assertTrue(txComnModel.getTxComn6().isEmpty());
        assertTrue(txComnModel.getTxComn7().isEmpty());
        assertTrue(txComnModel.getTxComn8().isEmpty());
    }
    
    @Test
    void testJsonSerialization() {
        txComnModel.setAccountingDate("00000000");
        txComnModel.addTxComnField(1, "curQryReqNum", "0");
        txComnModel.addTxComnField(1, "bgnIndexNo", "0");
        txComnModel.addTxComnField(8, "busiSendSysOrCmptNo", "99710730008");
        
        String json = JSON.toJSONString(txComnModel);
        
        assertNotNull(json);
        assertTrue(json.contains("accountingDate"));
        assertTrue(json.contains("00000000"));
        assertTrue(json.contains("txComn1"));
        assertTrue(json.contains("txComn8"));
        assertTrue(json.contains("curQryReqNum"));
        assertTrue(json.contains("busiSendSysOrCmptNo"));
    }
    
    @Test
    void testJsonDeserialization() {
        String json = "{\"accountingDate\":\"00000000\",\"txComn1\":{\"curQryReqNum\":\"0\",\"bgnIndexNo\":\"0\"},\"txComn8\":{\"busiSendSysOrCmptNo\":\"99710730008\"}}";
        
        TxComnModel model = JSON.parseObject(json, TxComnModel.class);
        
        assertNotNull(model);
        assertEquals("00000000", model.getAccountingDate());
        assertEquals("0", model.getTxComnField(1, "curQryReqNum"));
        assertEquals("0", model.getTxComnField(1, "bgnIndexNo"));
        assertEquals("99710730008", model.getTxComnField(8, "busiSendSysOrCmptNo"));
    }
}