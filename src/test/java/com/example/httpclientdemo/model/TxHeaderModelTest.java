package com.example.httpclientdemo.model;

import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TxHeaderModel单元测试类
 */
public class TxHeaderModelTest {
    
    private TxHeaderModel txHeaderModel;
    
    @BeforeEach
    void setUp() {
        txHeaderModel = new TxHeaderModel();
    }
    
    @Test
    void testDefaultConstructor() {
        TxHeaderModel model = new TxHeaderModel();
        assertNull(model.getMsgGrptMac());
        assertNull(model.getGlobalBusiTrackNo());
        assertNull(model.getSubtxNo());
    }
    
    @Test
    void testParameterizedConstructor() {
        String msgGrptMac = "test_mac";
        String globalBusiTrackNo = "test_track_no";
        String subtxNo = "test_subtx";
        
        TxHeaderModel model = new TxHeaderModel(msgGrptMac, globalBusiTrackNo, subtxNo);
        
        assertEquals(msgGrptMac, model.getMsgGrptMac());
        assertEquals(globalBusiTrackNo, model.getGlobalBusiTrackNo());
        assertEquals(subtxNo, model.getSubtxNo());
    }
    
    @Test
    void testGettersAndSetters() {
        String testValue = "test_value";
        
        txHeaderModel.setMsgGrptMac(testValue);
        assertEquals(testValue, txHeaderModel.getMsgGrptMac());
        
        txHeaderModel.setGlobalBusiTrackNo(testValue);
        assertEquals(testValue, txHeaderModel.getGlobalBusiTrackNo());
        
        txHeaderModel.setSubtxNo(testValue);
        assertEquals(testValue, txHeaderModel.getSubtxNo());
        
        txHeaderModel.setTxStartTime(testValue);
        assertEquals(testValue, txHeaderModel.getTxStartTime());
        
        txHeaderModel.setTxSendTime(testValue);
        assertEquals(testValue, txHeaderModel.getTxSendTime());
        
        txHeaderModel.setTxCode(testValue);
        assertEquals(testValue, txHeaderModel.getTxCode());
        
        txHeaderModel.setChannelNo(testValue);
        assertEquals(testValue, txHeaderModel.getChannelNo());
        
        txHeaderModel.setOrgNo(testValue);
        assertEquals(testValue, txHeaderModel.getOrgNo());
        
        txHeaderModel.setTellerId(testValue);
        assertEquals(testValue, txHeaderModel.getTellerId());
        
        txHeaderModel.setAuthTellerId(testValue);
        assertEquals(testValue, txHeaderModel.getAuthTellerId());
        
        txHeaderModel.setCustMgrId(testValue);
        assertEquals(testValue, txHeaderModel.getCustMgrId());
        
        txHeaderModel.setTerminalId(testValue);
        assertEquals(testValue, txHeaderModel.getTerminalId());
        
        txHeaderModel.setTerminalType(testValue);
        assertEquals(testValue, txHeaderModel.getTerminalType());
        
        txHeaderModel.setTxBranchNo(testValue);
        assertEquals(testValue, txHeaderModel.getTxBranchNo());
        
        txHeaderModel.setAuthBranchNo(testValue);
        assertEquals(testValue, txHeaderModel.getAuthBranchNo());
        
        txHeaderModel.setClientIp(testValue);
        assertEquals(testValue, txHeaderModel.getClientIp());
        
        txHeaderModel.setMacAddr(testValue);
        assertEquals(testValue, txHeaderModel.getMacAddr());
        
        txHeaderModel.setReqSysDate(testValue);
        assertEquals(testValue, txHeaderModel.getReqSysDate());
        
        txHeaderModel.setReqSysTime(testValue);
        assertEquals(testValue, txHeaderModel.getReqSysTime());
        
        txHeaderModel.setSeqNo(testValue);
        assertEquals(testValue, txHeaderModel.getSeqNo());
        
        txHeaderModel.setRemark(testValue);
        assertEquals(testValue, txHeaderModel.getRemark());
    }
    
    @Test
    void testValidateWithValidData() {
        txHeaderModel.setMsgGrptMac("valid_mac");
        txHeaderModel.setGlobalBusiTrackNo("valid_track_no");
        txHeaderModel.setSubtxNo("valid_subtx");
        
        assertTrue(txHeaderModel.validate());
    }
    
    @Test
    void testValidateWithMissingMsgGrptMac() {
        txHeaderModel.setGlobalBusiTrackNo("valid_track_no");
        txHeaderModel.setSubtxNo("valid_subtx");
        
        assertFalse(txHeaderModel.validate());
    }
    
    @Test
    void testValidateWithEmptyMsgGrptMac() {
        txHeaderModel.setMsgGrptMac("");
        txHeaderModel.setGlobalBusiTrackNo("valid_track_no");
        txHeaderModel.setSubtxNo("valid_subtx");
        
        assertFalse(txHeaderModel.validate());
    }
    
    @Test
    void testValidateWithWhitespaceMsgGrptMac() {
        txHeaderModel.setMsgGrptMac("   ");
        txHeaderModel.setGlobalBusiTrackNo("valid_track_no");
        txHeaderModel.setSubtxNo("valid_subtx");
        
        assertFalse(txHeaderModel.validate());
    }
    
    @Test
    void testValidateWithMissingGlobalBusiTrackNo() {
        txHeaderModel.setMsgGrptMac("valid_mac");
        txHeaderModel.setSubtxNo("valid_subtx");
        
        assertFalse(txHeaderModel.validate());
    }
    
    @Test
    void testValidateWithMissingSubtxNo() {
        txHeaderModel.setMsgGrptMac("valid_mac");
        txHeaderModel.setGlobalBusiTrackNo("valid_track_no");
        
        assertFalse(txHeaderModel.validate());
    }
    
    @Test
    void testValidateFieldLengthsWithValidData() {
        txHeaderModel.setMsgGrptMac("valid_mac");
        txHeaderModel.setGlobalBusiTrackNo("valid_track_no");
        txHeaderModel.setSubtxNo("valid_subtx");
        txHeaderModel.setTxCode("TX001");
        txHeaderModel.setChannelNo("CH001");
        
        assertTrue(txHeaderModel.validateFieldLengths());
    }
    
    @Test
    void testValidateFieldLengthsWithTooLongMsgGrptMac() {
        String longValue = "a".repeat(51); // 超过50字符限制
        txHeaderModel.setMsgGrptMac(longValue);
        
        assertFalse(txHeaderModel.validateFieldLengths());
    }
    
    @Test
    void testValidateFieldLengthsWithTooLongGlobalBusiTrackNo() {
        String longValue = "a".repeat(51); // 超过50字符限制
        txHeaderModel.setGlobalBusiTrackNo(longValue);
        
        assertFalse(txHeaderModel.validateFieldLengths());
    }
    
    @Test
    void testValidateFieldLengthsWithTooLongSubtxNo() {
        String longValue = "a".repeat(21); // 超过20字符限制
        txHeaderModel.setSubtxNo(longValue);
        
        assertFalse(txHeaderModel.validateFieldLengths());
    }
    
    @Test
    void testValidateFieldLengthsWithNullValues() {
        // null值应该通过长度验证
        assertTrue(txHeaderModel.validateFieldLengths());
    }
    
    @Test
    void testJsonSerialization() {
        txHeaderModel.setMsgGrptMac("{{msgGrptMac}}");
        txHeaderModel.setGlobalBusiTrackNo("{{globalBusiTrackNo}}");
        txHeaderModel.setSubtxNo("{{subtxNo}}");
        txHeaderModel.setTxStartTime("{{txStartTime}}");
        txHeaderModel.setTxSendTime("{{txSendTime}}");
        
        String json = JSON.toJSONString(txHeaderModel);
        
        assertNotNull(json);
        assertTrue(json.contains("msgGrptMac"));
        assertTrue(json.contains("{{msgGrptMac}}"));
        assertTrue(json.contains("globalBusiTrackNo"));
        assertTrue(json.contains("{{globalBusiTrackNo}}"));
    }
    
    @Test
    void testJsonDeserialization() {
        String json = "{\"msgGrptMac\":\"{{msgGrptMac}}\",\"globalBusiTrackNo\":\"{{globalBusiTrackNo}}\",\"subtxNo\":\"{{subtxNo}}\"}";
        
        TxHeaderModel model = JSON.parseObject(json, TxHeaderModel.class);
        
        assertNotNull(model);
        assertEquals("{{msgGrptMac}}", model.getMsgGrptMac());
        assertEquals("{{globalBusiTrackNo}}", model.getGlobalBusiTrackNo());
        assertEquals("{{subtxNo}}", model.getSubtxNo());
    }
}