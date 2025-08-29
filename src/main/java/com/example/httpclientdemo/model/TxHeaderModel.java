package com.example.httpclientdemo.model;

import com.alibaba.fastjson2.annotation.JSONField;

/**
 * TxHeader数据模型类
 * 包含所有txHeader固定字段名，支持值的修改
 */
public class TxHeaderModel {
    
    @JSONField(name = "msgGrptMac")
    private String msgGrptMac;
    
    @JSONField(name = "globalBusiTrackNo")
    private String globalBusiTrackNo;
    
    @JSONField(name = "subtxNo")
    private String subtxNo;
    
    @JSONField(name = "txStartTime")
    private String txStartTime;
    
    @JSONField(name = "txSendTime")
    private String txSendTime;
    
    @JSONField(name = "txCode")
    private String txCode;
    
    @JSONField(name = "channelNo")
    private String channelNo;
    
    @JSONField(name = "orgNo")
    private String orgNo;
    
    @JSONField(name = "tellerId")
    private String tellerId;
    
    @JSONField(name = "authTellerId")
    private String authTellerId;
    
    @JSONField(name = "custMgrId")
    private String custMgrId;
    
    @JSONField(name = "terminalId")
    private String terminalId;
    
    @JSONField(name = "terminalType")
    private String terminalType;
    
    @JSONField(name = "txBranchNo")
    private String txBranchNo;
    
    @JSONField(name = "authBranchNo")
    private String authBranchNo;
    
    @JSONField(name = "clientIp")
    private String clientIp;
    
    @JSONField(name = "macAddr")
    private String macAddr;
    
    @JSONField(name = "reqSysDate")
    private String reqSysDate;
    
    @JSONField(name = "reqSysTime")
    private String reqSysTime;
    
    @JSONField(name = "seqNo")
    private String seqNo;
    
    @JSONField(name = "remark")
    private String remark;
    
    // 默认构造函数
    public TxHeaderModel() {
    }
    
    // 带参数的构造函数，用于快速创建实例
    public TxHeaderModel(String msgGrptMac, String globalBusiTrackNo, String subtxNo) {
        this.msgGrptMac = msgGrptMac;
        this.globalBusiTrackNo = globalBusiTrackNo;
        this.subtxNo = subtxNo;
    }
    
    // Getter和Setter方法
    public String getMsgGrptMac() {
        return msgGrptMac;
    }
    
    public void setMsgGrptMac(String msgGrptMac) {
        this.msgGrptMac = msgGrptMac;
    }
    
    public String getGlobalBusiTrackNo() {
        return globalBusiTrackNo;
    }
    
    public void setGlobalBusiTrackNo(String globalBusiTrackNo) {
        this.globalBusiTrackNo = globalBusiTrackNo;
    }
    
    public String getSubtxNo() {
        return subtxNo;
    }
    
    public void setSubtxNo(String subtxNo) {
        this.subtxNo = subtxNo;
    }
    
    public String getTxStartTime() {
        return txStartTime;
    }
    
    public void setTxStartTime(String txStartTime) {
        this.txStartTime = txStartTime;
    }
    
    public String getTxSendTime() {
        return txSendTime;
    }
    
    public void setTxSendTime(String txSendTime) {
        this.txSendTime = txSendTime;
    }
    
    public String getTxCode() {
        return txCode;
    }
    
    public void setTxCode(String txCode) {
        this.txCode = txCode;
    }
    
    public String getChannelNo() {
        return channelNo;
    }
    
    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }
    
    public String getOrgNo() {
        return orgNo;
    }
    
    public void setOrgNo(String orgNo) {
        this.orgNo = orgNo;
    }
    
    public String getTellerId() {
        return tellerId;
    }
    
    public void setTellerId(String tellerId) {
        this.tellerId = tellerId;
    }
    
    public String getAuthTellerId() {
        return authTellerId;
    }
    
    public void setAuthTellerId(String authTellerId) {
        this.authTellerId = authTellerId;
    }
    
    public String getCustMgrId() {
        return custMgrId;
    }
    
    public void setCustMgrId(String custMgrId) {
        this.custMgrId = custMgrId;
    }
    
    public String getTerminalId() {
        return terminalId;
    }
    
    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }
    
    public String getTerminalType() {
        return terminalType;
    }
    
    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }
    
    public String getTxBranchNo() {
        return txBranchNo;
    }
    
    public void setTxBranchNo(String txBranchNo) {
        this.txBranchNo = txBranchNo;
    }
    
    public String getAuthBranchNo() {
        return authBranchNo;
    }
    
    public void setAuthBranchNo(String authBranchNo) {
        this.authBranchNo = authBranchNo;
    }
    
    public String getClientIp() {
        return clientIp;
    }
    
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }
    
    public String getMacAddr() {
        return macAddr;
    }
    
    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }
    
    public String getReqSysDate() {
        return reqSysDate;
    }
    
    public void setReqSysDate(String reqSysDate) {
        this.reqSysDate = reqSysDate;
    }
    
    public String getReqSysTime() {
        return reqSysTime;
    }
    
    public void setReqSysTime(String reqSysTime) {
        this.reqSysTime = reqSysTime;
    }
    
    public String getSeqNo() {
        return seqNo;
    }
    
    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    /**
     * 验证必填字段
     * @return 验证结果
     */
    public boolean validate() {
        return msgGrptMac != null && !msgGrptMac.trim().isEmpty() &&
               globalBusiTrackNo != null && !globalBusiTrackNo.trim().isEmpty() &&
               subtxNo != null && !subtxNo.trim().isEmpty();
    }
    
    /**
     * 验证字段长度限制
     * @return 验证结果
     */
    public boolean validateFieldLengths() {
        return (msgGrptMac == null || msgGrptMac.length() <= 50) &&
               (globalBusiTrackNo == null || globalBusiTrackNo.length() <= 50) &&
               (subtxNo == null || subtxNo.length() <= 20) &&
               (txCode == null || txCode.length() <= 10) &&
               (channelNo == null || channelNo.length() <= 10);
    }
}