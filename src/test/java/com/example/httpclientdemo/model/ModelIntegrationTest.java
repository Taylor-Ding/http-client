package com.example.httpclientdemo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 数据模型集成测试
 * 验证所有模型类协同工作的效果
 */
public class ModelIntegrationTest {
    
    @Test
    void testCompleteMessageCreationAndSerialization() {
        // 创建TxHeader
        TxHeaderModel txHeader = new TxHeaderModel();
        txHeader.setMsgGrptMac("{{msgGrptMac}}");
        txHeader.setGlobalBusiTrackNo("{{globalBusiTrackNo}}");
        txHeader.setSubtxNo("{{subtxNo}}");
        txHeader.setTxStartTime("{{txStartTime}}");
        txHeader.setTxSendTime("{{txSendTime}}");
        txHeader.setTxCode("C203");
        txHeader.setChannelNo("WEB");
        
        // 创建TxEntity
        TxEntityModel txEntity = new TxEntityModel();
        txEntity.setCustNo("040000037480013");
        txEntity.setQryVchrTpCd("1");
        txEntity.setTxSceneCd("C203");
        txEntity.addField("customField", "customValue");
        
        // 创建TxComn
        TxComnModel txComn = new TxComnModel();
        txComn.setAccountingDate("00000000");
        txComn.addTxComnField(1, "curQryReqNum", "0");
        txComn.addTxComnField(1, "bgnIndexNo", "0");
        txComn.addTxComnField(8, "busiSendSysOrCmptNo", "99710730008");
        txComn.addAddtDataField("additionalInfo", "test");
        
        // 创建完整报文
        CompleteMessageModel completeMessage = new CompleteMessageModel(txHeader, txEntity, txComn);
        
        // 验证数据完整性
        assertTrue(completeMessage.validate());
        assertTrue(completeMessage.hasRequiredData());
        
        // 序列化为JSON
        String json = completeMessage.toJson();
        assertNotNull(json);
        assertTrue(json.contains("txHeader"));
        assertTrue(json.contains("txBody"));
        assertTrue(json.contains("msgGrptMac"));
        assertTrue(json.contains("custNo"));
        assertTrue(json.contains("accountingDate"));
        assertTrue(json.contains("txComn1"));
        assertTrue(json.contains("txComn8"));
        
        // 反序列化
        CompleteMessageModel deserializedMessage = CompleteMessageModel.fromJson(json);
        assertNotNull(deserializedMessage);
        
        // 验证反序列化后的数据
        assertEquals("{{msgGrptMac}}", deserializedMessage.getTxHeader().getMsgGrptMac());
        assertEquals("040000037480013", deserializedMessage.getTxEntity().getCustNo());
        assertEquals("00000000", deserializedMessage.getTxComn().getAccountingDate());
        assertEquals("0", deserializedMessage.getTxComn().getTxComnField(1, "curQryReqNum"));
        assertEquals("99710730008", deserializedMessage.getTxComn().getTxComnField(8, "busiSendSysOrCmptNo"));
        
        // 验证深拷贝
        CompleteMessageModel copy = completeMessage.deepCopy();
        assertNotSame(completeMessage, copy);
        assertEquals(completeMessage.getTxHeader().getMsgGrptMac(), copy.getTxHeader().getMsgGrptMac());
        
        // 验证摘要信息
        String summary = completeMessage.getSummary();
        assertTrue(summary.contains("msgGrptMac={{msgGrptMac}}"));
        assertTrue(summary.contains("custNo=040000037480013"));
        assertTrue(summary.contains("txSceneCd=C203"));
    }
    
    @Test
    void testRealWorldJsonStructure() {
        // 基于用户提供的真实JSON结构创建报文
        String expectedJson = """
            {
              "txBody": {
                "txEntity": {
                  "custNo": "040000037480013",
                  "qryVchrTpCd": "1",
                  "txSceneCd": "C203"
                },
                "txComn": {
                  "accountingDate": "00000000"
                },
                "addtData": {},
                "txComn5": {},
                "txComn6": {},
                "txComn7": {},
                "txComn8": {
                  "busiSendSysOrCmptNo": "99710730008"
                },
                "txComn1": {
                  "curQryReqNum": "0",
                  "bgnIndexNo": "0"
                },
                "txComn2": {},
                "txComn3": {},
                "txComn4": {}
              },
              "txHeader": {
                "msgGrptMac": "{{msgGrptMac}}",
                "globalBusiTrackNo": "{{globalBusiTrackNo}}",
                "subtxNo": "{{subtxNo}}",
                "txStartTime": "{{txStartTime}}",
                "txSendTime": "{{txSendTime}}"
              }
            }
            """;
        
        // 使用模型类重建相同的结构
        CompleteMessageModel message = new CompleteMessageModel();
        
        // 设置txHeader
        TxHeaderModel header = new TxHeaderModel();
        header.setMsgGrptMac("{{msgGrptMac}}");
        header.setGlobalBusiTrackNo("{{globalBusiTrackNo}}");
        header.setSubtxNo("{{subtxNo}}");
        header.setTxStartTime("{{txStartTime}}");
        header.setTxSendTime("{{txSendTime}}");
        message.setTxHeader(header);
        
        // 设置txEntity
        TxEntityModel entity = new TxEntityModel();
        entity.setCustNo("040000037480013");
        entity.setQryVchrTpCd("1");
        entity.setTxSceneCd("C203");
        message.setTxEntity(entity);
        
        // 设置txComn
        TxComnModel comn = new TxComnModel();
        comn.setAccountingDate("00000000");
        comn.addTxComnField(1, "curQryReqNum", "0");
        comn.addTxComnField(1, "bgnIndexNo", "0");
        comn.addTxComnField(8, "busiSendSysOrCmptNo", "99710730008");
        message.setTxComn(comn);
        
        // 生成JSON并验证包含所有必要字段
        String generatedJson = message.toPrettyJson();
        
        // 验证关键字段存在
        assertTrue(generatedJson.contains("\"txHeader\""));
        assertTrue(generatedJson.contains("\"txBody\""));
        assertTrue(generatedJson.contains("\"txEntity\""));
        assertTrue(generatedJson.contains("\"custNo\":\"040000037480013\""));
        assertTrue(generatedJson.contains("\"qryVchrTpCd\":\"1\""));
        assertTrue(generatedJson.contains("\"txSceneCd\":\"C203\""));
        assertTrue(generatedJson.contains("\"accountingDate\":\"00000000\""));
        assertTrue(generatedJson.contains("\"msgGrptMac\":\"{{msgGrptMac}}\""));
        assertTrue(generatedJson.contains("\"txComn1\""));
        assertTrue(generatedJson.contains("\"txComn8\""));
        assertTrue(generatedJson.contains("\"curQryReqNum\":\"0\""));
        assertTrue(generatedJson.contains("\"busiSendSysOrCmptNo\":\"99710730008\""));
        
        // 验证可以成功反序列化
        CompleteMessageModel parsed = CompleteMessageModel.fromJson(generatedJson);
        assertNotNull(parsed);
        assertTrue(parsed.validate());
        assertTrue(parsed.hasRequiredData());
    }
}