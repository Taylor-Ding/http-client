package com.example.httpclientdemo.model;

import com.alibaba.fastjson2.annotation.JSONField;
import java.util.HashMap;
import java.util.Map;

/**
 * TxEntity数据模型类
 * 包含custNo、qryVchrTpCd、txSceneCd等核心字段，支持灵活的字段扩展
 */
public class TxEntityModel {
    
    @JSONField(name = "custNo")
    private String custNo;
    
    @JSONField(name = "qryVchrTpCd")
    private String qryVchrTpCd;
    
    @JSONField(name = "txSceneCd")
    private String txSceneCd;
    
    // 扩展字段，用于支持动态添加其他字段
    private Map<String, Object> additionalFields;
    
    // 默认构造函数
    public TxEntityModel() {
        this.additionalFields = new HashMap<>();
    }
    
    // 带参数的构造函数
    public TxEntityModel(String custNo, String qryVchrTpCd, String txSceneCd) {
        this();
        this.custNo = custNo;
        this.qryVchrTpCd = qryVchrTpCd;
        this.txSceneCd = txSceneCd;
    }
    
    // Getter和Setter方法
    public String getCustNo() {
        return custNo;
    }
    
    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }
    
    public String getQryVchrTpCd() {
        return qryVchrTpCd;
    }
    
    public void setQryVchrTpCd(String qryVchrTpCd) {
        this.qryVchrTpCd = qryVchrTpCd;
    }
    
    public String getTxSceneCd() {
        return txSceneCd;
    }
    
    public void setTxSceneCd(String txSceneCd) {
        this.txSceneCd = txSceneCd;
    }
    
    public Map<String, Object> getAdditionalFields() {
        return additionalFields;
    }
    
    public void setAdditionalFields(Map<String, Object> additionalFields) {
        this.additionalFields = additionalFields != null ? additionalFields : new HashMap<>();
    }
    
    /**
     * 添加扩展字段
     * @param fieldName 字段名
     * @param fieldValue 字段值
     */
    public void addField(String fieldName, Object fieldValue) {
        if (additionalFields == null) {
            additionalFields = new HashMap<>();
        }
        additionalFields.put(fieldName, fieldValue);
    }
    
    /**
     * 获取扩展字段值
     * @param fieldName 字段名
     * @return 字段值
     */
    public Object getField(String fieldName) {
        return additionalFields != null ? additionalFields.get(fieldName) : null;
    }
    
    /**
     * 移除扩展字段
     * @param fieldName 字段名
     * @return 被移除的字段值
     */
    public Object removeField(String fieldName) {
        return additionalFields != null ? additionalFields.remove(fieldName) : null;
    }
    
    /**
     * 检查是否包含指定的扩展字段
     * @param fieldName 字段名
     * @return 是否包含该字段
     */
    public boolean hasField(String fieldName) {
        return additionalFields != null && additionalFields.containsKey(fieldName);
    }
    
    /**
     * 验证必填字段
     * @return 验证结果
     */
    public boolean validate() {
        return custNo != null && !custNo.trim().isEmpty() &&
               qryVchrTpCd != null && !qryVchrTpCd.trim().isEmpty() &&
               txSceneCd != null && !txSceneCd.trim().isEmpty();
    }
    
    /**
     * 验证字段格式
     * @return 验证结果
     */
    public boolean validateFormat() {
        // 客户号格式验证（假设为15位数字）
        if (custNo != null && !custNo.matches("\\d{15}")) {
            return false;
        }
        
        // 查询凭证类型代码格式验证（假设为1位数字）
        if (qryVchrTpCd != null && !qryVchrTpCd.matches("\\d{1}")) {
            return false;
        }
        
        // 交易场景代码格式验证（假设为4位字符）
        if (txSceneCd != null && !txSceneCd.matches("[A-Z]\\d{3}")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 验证字段长度限制
     * @return 验证结果
     */
    public boolean validateFieldLengths() {
        return (custNo == null || custNo.length() <= 20) &&
               (qryVchrTpCd == null || qryVchrTpCd.length() <= 5) &&
               (txSceneCd == null || txSceneCd.length() <= 10);
    }
    
    /**
     * 获取所有字段的Map表示（包括核心字段和扩展字段）
     * @return 包含所有字段的Map
     */
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        
        if (custNo != null) {
            result.put("custNo", custNo);
        }
        if (qryVchrTpCd != null) {
            result.put("qryVchrTpCd", qryVchrTpCd);
        }
        if (txSceneCd != null) {
            result.put("txSceneCd", txSceneCd);
        }
        
        // 添加扩展字段
        if (additionalFields != null) {
            result.putAll(additionalFields);
        }
        
        return result;
    }
    
    /**
     * 从Map创建TxEntityModel实例
     * @param map 包含字段数据的Map
     * @return TxEntityModel实例
     */
    public static TxEntityModel fromMap(Map<String, Object> map) {
        TxEntityModel model = new TxEntityModel();
        
        if (map != null) {
            model.setCustNo((String) map.get("custNo"));
            model.setQryVchrTpCd((String) map.get("qryVchrTpCd"));
            model.setTxSceneCd((String) map.get("txSceneCd"));
            
            // 添加其他字段作为扩展字段
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                if (!"custNo".equals(key) && !"qryVchrTpCd".equals(key) && !"txSceneCd".equals(key)) {
                    model.addField(key, entry.getValue());
                }
            }
        }
        
        return model;
    }
}