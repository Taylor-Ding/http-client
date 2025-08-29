package com.example.httpclientdemo.builder;

import com.example.httpclientdemo.model.CompleteMessageModel;
import com.example.httpclientdemo.model.TxHeaderModel;
import com.example.httpclientdemo.model.TxEntityModel;
import com.example.httpclientdemo.model.TxComnModel;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * 报文构建器类
 * 提供流式API支持链式调用构建复杂报文
 * 支持默认值和验证机制
 */
public class MessageBuilder {
    
    private TxHeaderModel txHeader;
    private TxEntityModel txEntity;
    private TxComnModel txComn;
    
    // 私有构造函数，强制使用静态工厂方法
    private MessageBuilder() {
        this.txHeader = new TxHeaderModel();
        this.txEntity = new TxEntityModel();
        this.txComn = new TxComnModel();
    }
    
    /**
     * 创建MessageBuilder实例
     * @return MessageBuilder实例
     */
    public static MessageBuilder create() {
        return new MessageBuilder();
    }
    
    /**
     * 配置TxHeader
     * @param headerConfigurer TxHeader配置函数
     * @return MessageBuilder实例，支持链式调用
     */
    public MessageBuilder withTxHeader(Consumer<TxHeaderBuilder> headerConfigurer) {
        if (headerConfigurer != null) {
            TxHeaderBuilder headerBuilder = new TxHeaderBuilder(this.txHeader);
            headerConfigurer.accept(headerBuilder);
        }
        return this;
    }
    
    /**
     * 直接设置TxHeader
     * @param txHeader TxHeader实例
     * @return MessageBuilder实例，支持链式调用
     */
    public MessageBuilder withTxHeader(TxHeaderModel txHeader) {
        this.txHeader = txHeader != null ? txHeader : new TxHeaderModel();
        return this;
    }
    
    /**
     * 配置TxEntity
     * @param entityConfigurer TxEntity配置函数
     * @return MessageBuilder实例，支持链式调用
     */
    public MessageBuilder withTxEntity(Consumer<TxEntityBuilder> entityConfigurer) {
        if (entityConfigurer != null) {
            TxEntityBuilder entityBuilder = new TxEntityBuilder(this.txEntity);
            entityConfigurer.accept(entityBuilder);
        }
        return this;
    }
    
    /**
     * 直接设置TxEntity
     * @param txEntity TxEntity实例
     * @return MessageBuilder实例，支持链式调用
     */
    public MessageBuilder withTxEntity(TxEntityModel txEntity) {
        this.txEntity = txEntity != null ? txEntity : new TxEntityModel();
        return this;
    }
    
    /**
     * 配置TxComn
     * @param comnConfigurer TxComn配置函数
     * @return MessageBuilder实例，支持链式调用
     */
    public MessageBuilder withTxComn(Consumer<TxComnBuilder> comnConfigurer) {
        if (comnConfigurer != null) {
            TxComnBuilder comnBuilder = new TxComnBuilder(this.txComn);
            comnConfigurer.accept(comnBuilder);
        }
        return this;
    }
    
    /**
     * 直接设置TxComn
     * @param txComn TxComn实例
     * @return MessageBuilder实例，支持链式调用
     */
    public MessageBuilder withTxComn(TxComnModel txComn) {
        this.txComn = txComn != null ? txComn : new TxComnModel();
        return this;
    }
    
    /**
     * 应用默认值
     * @return MessageBuilder实例，支持链式调用
     */
    public MessageBuilder withDefaults() {
        applyDefaultValues();
        return this;
    }
    
    /**
     * 构建CompleteMessageModel实例
     * @return CompleteMessageModel实例
     * @throws IllegalStateException 当必填字段缺失时抛出异常
     */
    public CompleteMessageModel build() {
        validateRequiredFields();
        return new CompleteMessageModel(txHeader, txEntity, txComn);
    }
    
    /**
     * 构建并验证CompleteMessageModel实例
     * @return CompleteMessageModel实例
     * @throws IllegalStateException 当验证失败时抛出异常
     */
    public CompleteMessageModel buildAndValidate() {
        CompleteMessageModel message = build();
        if (!message.validate()) {
            throw new IllegalStateException("Message validation failed");
        }
        if (!message.validateFormat()) {
            throw new IllegalStateException("Message format validation failed");
        }
        return message;
    }
    
    /**
     * 应用默认值
     */
    private void applyDefaultValues() {
        // TxHeader默认值
        if (txHeader.getMsgGrptMac() == null) {
            txHeader.setMsgGrptMac("{{msgGrptMac}}");
        }
        if (txHeader.getGlobalBusiTrackNo() == null) {
            txHeader.setGlobalBusiTrackNo("{{globalBusiTrackNo}}");
        }
        if (txHeader.getSubtxNo() == null) {
            txHeader.setSubtxNo("{{subtxNo}}");
        }
        if (txHeader.getTxStartTime() == null) {
            txHeader.setTxStartTime("{{txStartTime}}");
        }
        if (txHeader.getTxSendTime() == null) {
            txHeader.setTxSendTime("{{txSendTime}}");
        }
        
        // TxEntity默认值
        if (txEntity.getCustNo() == null) {
            txEntity.setCustNo("040000037480013");
        }
        if (txEntity.getQryVchrTpCd() == null) {
            txEntity.setQryVchrTpCd("1");
        }
        if (txEntity.getTxSceneCd() == null) {
            txEntity.setTxSceneCd("C203");
        }
        
        // TxComn默认值
        if (txComn.getAccountingDate() == null) {
            txComn.setAccountingDate("00000000");
        }
    }
    
    /**
     * 验证必填字段
     * @throws IllegalStateException 当必填字段缺失时抛出异常
     */
    private void validateRequiredFields() {
        if (txHeader == null) {
            throw new IllegalStateException("TxHeader is required");
        }
        if (txEntity == null) {
            throw new IllegalStateException("TxEntity is required");
        }
        if (txComn == null) {
            throw new IllegalStateException("TxComn is required");
        }
    }
    
    // 内部构建器类
    
    /**
     * TxHeader构建器
     */
    public static class TxHeaderBuilder {
        private final TxHeaderModel header;
        
        public TxHeaderBuilder(TxHeaderModel header) {
            this.header = header;
        }
        
        public TxHeaderBuilder msgGrptMac(String msgGrptMac) {
            header.setMsgGrptMac(msgGrptMac);
            return this;
        }
        
        public TxHeaderBuilder globalBusiTrackNo(String globalBusiTrackNo) {
            header.setGlobalBusiTrackNo(globalBusiTrackNo);
            return this;
        }
        
        public TxHeaderBuilder subtxNo(String subtxNo) {
            header.setSubtxNo(subtxNo);
            return this;
        }
        
        public TxHeaderBuilder txStartTime(String txStartTime) {
            header.setTxStartTime(txStartTime);
            return this;
        }
        
        public TxHeaderBuilder txSendTime(String txSendTime) {
            header.setTxSendTime(txSendTime);
            return this;
        }
        
        public TxHeaderBuilder txCode(String txCode) {
            header.setTxCode(txCode);
            return this;
        }
        
        public TxHeaderBuilder channelNo(String channelNo) {
            header.setChannelNo(channelNo);
            return this;
        }
        
        public TxHeaderBuilder orgNo(String orgNo) {
            header.setOrgNo(orgNo);
            return this;
        }
        
        public TxHeaderBuilder tellerId(String tellerId) {
            header.setTellerId(tellerId);
            return this;
        }
        
        public TxHeaderBuilder authTellerId(String authTellerId) {
            header.setAuthTellerId(authTellerId);
            return this;
        }
        
        public TxHeaderBuilder custMgrId(String custMgrId) {
            header.setCustMgrId(custMgrId);
            return this;
        }
        
        public TxHeaderBuilder terminalId(String terminalId) {
            header.setTerminalId(terminalId);
            return this;
        }
        
        public TxHeaderBuilder terminalType(String terminalType) {
            header.setTerminalType(terminalType);
            return this;
        }
        
        public TxHeaderBuilder txBranchNo(String txBranchNo) {
            header.setTxBranchNo(txBranchNo);
            return this;
        }
        
        public TxHeaderBuilder authBranchNo(String authBranchNo) {
            header.setAuthBranchNo(authBranchNo);
            return this;
        }
        
        public TxHeaderBuilder clientIp(String clientIp) {
            header.setClientIp(clientIp);
            return this;
        }
        
        public TxHeaderBuilder macAddr(String macAddr) {
            header.setMacAddr(macAddr);
            return this;
        }
        
        public TxHeaderBuilder reqSysDate(String reqSysDate) {
            header.setReqSysDate(reqSysDate);
            return this;
        }
        
        public TxHeaderBuilder reqSysTime(String reqSysTime) {
            header.setReqSysTime(reqSysTime);
            return this;
        }
        
        public TxHeaderBuilder seqNo(String seqNo) {
            header.setSeqNo(seqNo);
            return this;
        }
        
        public TxHeaderBuilder remark(String remark) {
            header.setRemark(remark);
            return this;
        }
    }
    
    /**
     * TxEntity构建器
     */
    public static class TxEntityBuilder {
        private final TxEntityModel entity;
        
        public TxEntityBuilder(TxEntityModel entity) {
            this.entity = entity;
        }
        
        public TxEntityBuilder custNo(String custNo) {
            entity.setCustNo(custNo);
            return this;
        }
        
        public TxEntityBuilder qryVchrTpCd(String qryVchrTpCd) {
            entity.setQryVchrTpCd(qryVchrTpCd);
            return this;
        }
        
        public TxEntityBuilder txSceneCd(String txSceneCd) {
            entity.setTxSceneCd(txSceneCd);
            return this;
        }
        
        public TxEntityBuilder addField(String fieldName, Object fieldValue) {
            entity.addField(fieldName, fieldValue);
            return this;
        }
        
        public TxEntityBuilder withAdditionalFields(Map<String, Object> fields) {
            if (fields != null) {
                for (Map.Entry<String, Object> entry : fields.entrySet()) {
                    entity.addField(entry.getKey(), entry.getValue());
                }
            }
            return this;
        }
    }
    
    /**
     * TxComn构建器
     */
    public static class TxComnBuilder {
        private final TxComnModel comn;
        
        public TxComnBuilder(TxComnModel comn) {
            this.comn = comn;
        }
        
        public TxComnBuilder accountingDate(String accountingDate) {
            comn.setAccountingDate(accountingDate);
            return this;
        }
        
        public TxComnBuilder addtData(String key, Object value) {
            comn.addAddtDataField(key, value);
            return this;
        }
        
        public TxComnBuilder withAddtData(Map<String, Object> data) {
            if (data != null) {
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    comn.addAddtDataField(entry.getKey(), entry.getValue());
                }
            }
            return this;
        }
        
        public TxComnBuilder txComn1(String key, Object value) {
            comn.addTxComnField(1, key, value);
            return this;
        }
        
        public TxComnBuilder withTxComn1(Map<String, Object> data) {
            comn.setTxComn1(data);
            return this;
        }
        
        public TxComnBuilder txComn2(String key, Object value) {
            comn.addTxComnField(2, key, value);
            return this;
        }
        
        public TxComnBuilder withTxComn2(Map<String, Object> data) {
            comn.setTxComn2(data);
            return this;
        }
        
        public TxComnBuilder txComn3(String key, Object value) {
            comn.addTxComnField(3, key, value);
            return this;
        }
        
        public TxComnBuilder withTxComn3(Map<String, Object> data) {
            comn.setTxComn3(data);
            return this;
        }
        
        public TxComnBuilder txComn4(String key, Object value) {
            comn.addTxComnField(4, key, value);
            return this;
        }
        
        public TxComnBuilder withTxComn4(Map<String, Object> data) {
            comn.setTxComn4(data);
            return this;
        }
        
        public TxComnBuilder txComn5(String key, Object value) {
            comn.addTxComnField(5, key, value);
            return this;
        }
        
        public TxComnBuilder withTxComn5(Map<String, Object> data) {
            comn.setTxComn5(data);
            return this;
        }
        
        public TxComnBuilder txComn6(String key, Object value) {
            comn.addTxComnField(6, key, value);
            return this;
        }
        
        public TxComnBuilder withTxComn6(Map<String, Object> data) {
            comn.setTxComn6(data);
            return this;
        }
        
        public TxComnBuilder txComn7(String key, Object value) {
            comn.addTxComnField(7, key, value);
            return this;
        }
        
        public TxComnBuilder withTxComn7(Map<String, Object> data) {
            comn.setTxComn7(data);
            return this;
        }
        
        public TxComnBuilder txComn8(String key, Object value) {
            comn.addTxComnField(8, key, value);
            return this;
        }
        
        public TxComnBuilder withTxComn8(Map<String, Object> data) {
            comn.setTxComn8(data);
            return this;
        }
        
        // 便捷方法：设置常用的txComn1字段
        public TxComnBuilder curQryReqNum(String curQryReqNum) {
            comn.addTxComnField(1, "curQryReqNum", curQryReqNum);
            return this;
        }
        
        public TxComnBuilder bgnIndexNo(String bgnIndexNo) {
            comn.addTxComnField(1, "bgnIndexNo", bgnIndexNo);
            return this;
        }
        
        // 便捷方法：设置常用的txComn8字段
        public TxComnBuilder busiSendSysOrCmptNo(String busiSendSysOrCmptNo) {
            comn.addTxComnField(8, "busiSendSysOrCmptNo", busiSendSysOrCmptNo);
            return this;
        }
    }
}