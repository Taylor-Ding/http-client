package com.example.httpclientdemo.model;

import com.alibaba.fastjson2.annotation.JSONField;
import java.util.HashMap;
import java.util.Map;

/**
 * TxComn数据模型类
 * 支持accountingDate和各种txComn子字段，实现addtData的动态字段支持
 */
public class TxComnModel {
    
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
    
    // 默认构造函数
    public TxComnModel() {
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
    
    // 带参数的构造函数
    public TxComnModel(String accountingDate) {
        this();
        this.accountingDate = accountingDate;
    }
    
    // Getter和Setter方法
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
    
    // addtData操作方法
    /**
     * 向addtData添加字段
     * @param key 字段名
     * @param value 字段值
     */
    public void addAddtDataField(String key, Object value) {
        if (addtData == null) {
            addtData = new HashMap<>();
        }
        addtData.put(key, value);
    }
    
    /**
     * 获取addtData中的字段值
     * @param key 字段名
     * @return 字段值
     */
    public Object getAddtDataField(String key) {
        return addtData != null ? addtData.get(key) : null;
    }
    
    /**
     * 移除addtData中的字段
     * @param key 字段名
     * @return 被移除的字段值
     */
    public Object removeAddtDataField(String key) {
        return addtData != null ? addtData.remove(key) : null;
    }
    
    // txComn字段操作的通用方法
    /**
     * 向指定的txComn字段添加数据
     * @param comnNumber txComn编号（1-8）
     * @param key 字段名
     * @param value 字段值
     */
    public void addTxComnField(int comnNumber, String key, Object value) {
        Map<String, Object> targetMap = getTxComnMap(comnNumber);
        if (targetMap != null) {
            targetMap.put(key, value);
        }
    }
    
    /**
     * 获取指定txComn字段中的值
     * @param comnNumber txComn编号（1-8）
     * @param key 字段名
     * @return 字段值
     */
    public Object getTxComnField(int comnNumber, String key) {
        Map<String, Object> targetMap = getTxComnMap(comnNumber);
        return targetMap != null ? targetMap.get(key) : null;
    }
    
    /**
     * 移除指定txComn字段中的值
     * @param comnNumber txComn编号（1-8）
     * @param key 字段名
     * @return 被移除的字段值
     */
    public Object removeTxComnField(int comnNumber, String key) {
        Map<String, Object> targetMap = getTxComnMap(comnNumber);
        return targetMap != null ? targetMap.remove(key) : null;
    }
    
    /**
     * 获取指定编号的txComn Map
     * @param comnNumber txComn编号（1-8）
     * @return 对应的Map对象
     */
    private Map<String, Object> getTxComnMap(int comnNumber) {
        switch (comnNumber) {
            case 1: return txComn1;
            case 2: return txComn2;
            case 3: return txComn3;
            case 4: return txComn4;
            case 5: return txComn5;
            case 6: return txComn6;
            case 7: return txComn7;
            case 8: return txComn8;
            default: return null;
        }
    }
    
    /**
     * 设置指定编号的txComn Map
     * @param comnNumber txComn编号（1-8）
     * @param data 要设置的数据
     */
    public void setTxComnMap(int comnNumber, Map<String, Object> data) {
        Map<String, Object> safeData = data != null ? data : new HashMap<>();
        switch (comnNumber) {
            case 1: setTxComn1(safeData); break;
            case 2: setTxComn2(safeData); break;
            case 3: setTxComn3(safeData); break;
            case 4: setTxComn4(safeData); break;
            case 5: setTxComn5(safeData); break;
            case 6: setTxComn6(safeData); break;
            case 7: setTxComn7(safeData); break;
            case 8: setTxComn8(safeData); break;
        }
    }
    
    /**
     * 验证accountingDate格式
     * @return 验证结果
     */
    public boolean validateAccountingDate() {
        if (accountingDate == null) {
            return true; // null值被认为是有效的
        }
        // 验证日期格式：8位数字（YYYYMMDD）或者"00000000"
        return accountingDate.matches("\\d{8}");
    }
    
    /**
     * 验证所有字段
     * @return 验证结果
     */
    public boolean validate() {
        return validateAccountingDate();
    }
    
    /**
     * 检查是否有任何txComn字段包含数据
     * @return 是否有数据
     */
    public boolean hasAnyTxComnData() {
        return (txComn1 != null && !txComn1.isEmpty()) ||
               (txComn2 != null && !txComn2.isEmpty()) ||
               (txComn3 != null && !txComn3.isEmpty()) ||
               (txComn4 != null && !txComn4.isEmpty()) ||
               (txComn5 != null && !txComn5.isEmpty()) ||
               (txComn6 != null && !txComn6.isEmpty()) ||
               (txComn7 != null && !txComn7.isEmpty()) ||
               (txComn8 != null && !txComn8.isEmpty());
    }
    
    /**
     * 检查addtData是否包含数据
     * @return 是否有数据
     */
    public boolean hasAddtData() {
        return addtData != null && !addtData.isEmpty();
    }
    
    /**
     * 清空所有txComn字段
     */
    public void clearAllTxComn() {
        if (txComn1 != null) txComn1.clear();
        if (txComn2 != null) txComn2.clear();
        if (txComn3 != null) txComn3.clear();
        if (txComn4 != null) txComn4.clear();
        if (txComn5 != null) txComn5.clear();
        if (txComn6 != null) txComn6.clear();
        if (txComn7 != null) txComn7.clear();
        if (txComn8 != null) txComn8.clear();
    }
    
    /**
     * 清空addtData
     */
    public void clearAddtData() {
        if (addtData != null) {
            addtData.clear();
        }
    }
}