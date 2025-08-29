package com.example.httpclientdemo.model;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import java.util.HashMap;
import java.util.Map;

/**
 * 完整报文模型类
 * 整合txHeader、txEntity、txComn形成完整的JSON报文结构
 */
public class CompleteMessageModel {
    
    @JSONField(name = "txHeader")
    private TxHeaderModel txHeader;
    
    @JSONField(name = "txBody")
    private TxBodyModel txBody;
    
    // 内部类：TxBody模型
    public static class TxBodyModel {
        @JSONField(name = "txEntity")
        private TxEntityModel txEntity;
        
        @JSONField(name = "accountingDate")
        private String accountingDate;
        
        @JSONField(name = "addtData")
        private Map<String, Object> addtData;
        
        @JSONField(name = "txComn1")
        private Map<String, Object> txComn1;
        
        @JSONField(name = "txComn2")
        private Map<String, Object> txComn2;
        
        @JSONField(name = "txComn3")
        private Map<String, Object> txComn3;
        
        @JSONField(name = "txComn4")
        private Map<String, Object> txComn4;
        
        @JSONField(name = "txComn5")
        private Map<String, Object> txComn5;
        
        @JSONField(name = "txComn6")
        private Map<String, Object> txComn6;
        
        @JSONField(name = "txComn7")
        private Map<String, Object> txComn7;
        
        @JSONField(name = "txComn8")
        private Map<String, Object> txComn8;
        
        public TxBodyModel() {
            this.addtData = new HashMap<>();
            this.txComn1 = new HashMap<>();
            this.txComn2 = new HashMap<>();
            this.txComn3 = new HashMap<>();
            this.txComn4 = new HashMap<>();
            this.txComn5 = new HashMap<>();
            this.txComn6 = new HashMap<>();
            this.txComn7 = new HashMap<>();
            this.txComn8 = new HashMap<>();
        }
        
        // Getters and Setters
        public TxEntityModel getTxEntity() {
            return txEntity;
        }
        
        public void setTxEntity(TxEntityModel txEntity) {
            this.txEntity = txEntity;
        }
        
        public String getAccountingDate() {
            return accountingDate;
        }
        
        public void setAccountingDate(String accountingDate) {
            this.accountingDate = accountingDate;
        }
        
        public Map<String, Object> getAddtData() {
            return addtData;
        }
        
        public void setAddtData(Map<String, Object> addtData) {
            this.addtData = addtData != null ? addtData : new HashMap<>();
        }
        
        public Map<String, Object> getTxComn1() {
            return txComn1;
        }
        
        public void setTxComn1(Map<String, Object> txComn1) {
            this.txComn1 = txComn1 != null ? txComn1 : new HashMap<>();
        }
        
        public Map<String, Object> getTxComn2() {
            return txComn2;
        }
        
        public void setTxComn2(Map<String, Object> txComn2) {
            this.txComn2 = txComn2 != null ? txComn2 : new HashMap<>();
        }
        
        public Map<String, Object> getTxComn3() {
            return txComn3;
        }
        
        public void setTxComn3(Map<String, Object> txComn3) {
            this.txComn3 = txComn3 != null ? txComn3 : new HashMap<>();
        }
        
        public Map<String, Object> getTxComn4() {
            return txComn4;
        }
        
        public void setTxComn4(Map<String, Object> txComn4) {
            this.txComn4 = txComn4 != null ? txComn4 : new HashMap<>();
        }
        
        public Map<String, Object> getTxComn5() {
            return txComn5;
        }
        
        public void setTxComn5(Map<String, Object> txComn5) {
            this.txComn5 = txComn5 != null ? txComn5 : new HashMap<>();
        }
        
        public Map<String, Object> getTxComn6() {
            return txComn6;
        }
        
        public void setTxComn6(Map<String, Object> txComn6) {
            this.txComn6 = txComn6 != null ? txComn6 : new HashMap<>();
        }
        
        public Map<String, Object> getTxComn7() {
            return txComn7;
        }
        
        public void setTxComn7(Map<String, Object> txComn7) {
            this.txComn7 = txComn7 != null ? txComn7 : new HashMap<>();
        }
        
        public Map<String, Object> getTxComn8() {
            return txComn8;
        }
        
        public void setTxComn8(Map<String, Object> txComn8) {
            this.txComn8 = txComn8 != null ? txComn8 : new HashMap<>();
        }
    }
    
    // 默认构造函数
    public CompleteMessageModel() {
        this.txHeader = new TxHeaderModel();
        this.txBody = new TxBodyModel();
    }
    
    // 带参数的构造函数
    public CompleteMessageModel(TxHeaderModel txHeader, TxEntityModel txEntity, TxComnModel txComn) {
        this();
        this.txHeader = txHeader != null ? txHeader : new TxHeaderModel();
        setTxEntity(txEntity);
        setTxComn(txComn);
    }
    
    // Getters and Setters
    public TxHeaderModel getTxHeader() {
        return txHeader;
    }
    
    public void setTxHeader(TxHeaderModel txHeader) {
        this.txHeader = txHeader != null ? txHeader : new TxHeaderModel();
    }
    
    public TxBodyModel getTxBody() {
        return txBody;
    }
    
    public void setTxBody(TxBodyModel txBody) {
        this.txBody = txBody != null ? txBody : new TxBodyModel();
    }
    
    // 便捷方法：设置TxEntity
    public void setTxEntity(TxEntityModel txEntity) {
        if (txBody == null) {
            txBody = new TxBodyModel();
        }
        txBody.setTxEntity(txEntity);
    }
    
    public TxEntityModel getTxEntity() {
        return txBody != null ? txBody.getTxEntity() : null;
    }
    
    // 便捷方法：设置TxComn数据
    public void setTxComn(TxComnModel txComn) {
        if (txBody == null) {
            txBody = new TxBodyModel();
        }
        
        if (txComn != null) {
            txBody.setAccountingDate(txComn.getAccountingDate());
            txBody.setAddtData(txComn.getAddtData());
            txBody.setTxComn1(txComn.getTxComn1());
            txBody.setTxComn2(txComn.getTxComn2());
            txBody.setTxComn3(txComn.getTxComn3());
            txBody.setTxComn4(txComn.getTxComn4());
            txBody.setTxComn5(txComn.getTxComn5());
            txBody.setTxComn6(txComn.getTxComn6());
            txBody.setTxComn7(txComn.getTxComn7());
            txBody.setTxComn8(txComn.getTxComn8());
        }
    }
    
    // 便捷方法：获取TxComn数据
    public TxComnModel getTxComn() {
        if (txBody == null) {
            return null;
        }
        
        TxComnModel txComn = new TxComnModel();
        txComn.setAccountingDate(txBody.getAccountingDate());
        txComn.setAddtData(txBody.getAddtData());
        txComn.setTxComn1(txBody.getTxComn1());
        txComn.setTxComn2(txBody.getTxComn2());
        txComn.setTxComn3(txBody.getTxComn3());
        txComn.setTxComn4(txBody.getTxComn4());
        txComn.setTxComn5(txBody.getTxComn5());
        txComn.setTxComn6(txBody.getTxComn6());
        txComn.setTxComn7(txBody.getTxComn7());
        txComn.setTxComn8(txBody.getTxComn8());
        
        return txComn;
    }
    
    /**
     * 验证完整报文的有效性
     * @return 验证结果
     */
    public boolean validate() {
        // 验证txHeader
        if (txHeader == null || !txHeader.validate()) {
            return false;
        }
        
        // 验证txBody
        if (txBody == null) {
            return false;
        }
        
        // 验证txEntity
        TxEntityModel txEntity = txBody.getTxEntity();
        if (txEntity == null || !txEntity.validate()) {
            return false;
        }
        
        // 验证accountingDate格式
        String accountingDate = txBody.getAccountingDate();
        if (accountingDate != null && !accountingDate.matches("\\d{8}")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 验证字段格式
     * @return 验证结果
     */
    public boolean validateFormat() {
        // 验证txHeader字段长度
        if (txHeader != null && !txHeader.validateFieldLengths()) {
            return false;
        }
        
        // 验证txEntity格式
        TxEntityModel txEntity = getTxEntity();
        if (txEntity != null && !txEntity.validateFormat()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 序列化为JSON字符串
     * @return JSON字符串
     */
    public String toJson() {
        return JSON.toJSONString(this);
    }
    
    /**
     * 序列化为格式化的JSON字符串
     * @return 格式化的JSON字符串
     */
    public String toPrettyJson() {
        return JSON.toJSONString(this, com.alibaba.fastjson2.JSONWriter.Feature.PrettyFormat);
    }
    
    /**
     * 从JSON字符串反序列化
     * @param json JSON字符串
     * @return CompleteMessageModel实例
     */
    public static CompleteMessageModel fromJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        
        try {
            return JSON.parseObject(json, CompleteMessageModel.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON: " + e.getMessage(), e);
        }
    }
    
    /**
     * 创建一个深拷贝
     * @return 深拷贝的实例
     */
    public CompleteMessageModel deepCopy() {
        String json = toJson();
        return fromJson(json);
    }
    
    /**
     * 检查报文是否包含必要的数据
     * @return 是否包含必要数据
     */
    public boolean hasRequiredData() {
        return txHeader != null && 
               txBody != null && 
               txBody.getTxEntity() != null &&
               txHeader.getMsgGrptMac() != null &&
               txHeader.getGlobalBusiTrackNo() != null &&
               txHeader.getSubtxNo() != null;
    }
    
    /**
     * 获取报文的摘要信息
     * @return 摘要信息
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("CompleteMessage[");
        
        if (txHeader != null) {
            summary.append("msgGrptMac=").append(txHeader.getMsgGrptMac()).append(", ");
            summary.append("globalBusiTrackNo=").append(txHeader.getGlobalBusiTrackNo()).append(", ");
        }
        
        TxEntityModel txEntity = getTxEntity();
        if (txEntity != null) {
            summary.append("custNo=").append(txEntity.getCustNo()).append(", ");
            summary.append("txSceneCd=").append(txEntity.getTxSceneCd());
        }
        
        summary.append("]");
        return summary.toString();
    }
}