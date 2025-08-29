package com.example.httpclientdemo.factory;

import com.example.httpclientdemo.builder.MessageBuilder;
import com.example.httpclientdemo.model.CompleteMessageModel;
import com.example.httpclientdemo.model.TxHeaderModel;
import com.example.httpclientdemo.model.TxEntityModel;
import com.example.httpclientdemo.model.TxComnModel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * 测试数据工厂类
 * 提供预定义测试数据模板，支持标准业务场景、边界值、异常情况的测试数据
 * 实现参数化测试数据生成方法
 */
public class TestDataFactory {
    
    private static final Random random = new Random();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    // 预定义的客户号池
    private static final String[] CUSTOMER_NUMBERS = {
        "040000037480013",
        "040000037480014", 
        "040000037480015",
        "123456789012345",
        "987654321098765",
        "555666777888999"
    };
    
    // 预定义的交易场景代码池
    private static final String[] TX_SCENE_CODES = {
        "C203", "C204", "C205", "C206", "C207", "C208"
    };
    
    // 预定义的查询凭证类型代码池
    private static final String[] QRY_VCHR_TP_CODES = {
        "1", "2", "3", "4", "5"
    };
    
    /**
     * 创建标准业务场景的测试数据
     * @return 标准业务场景的CompleteMessageModel
     */
    public static CompleteMessageModel createStandardBusinessScenario() {
        return MessageBuilder.create()
            .withDefaults()
            .withTxHeader(header -> header
                .msgGrptMac(generateMsgGrptMac())
                .globalBusiTrackNo(generateGlobalBusiTrackNo())
                .subtxNo(generateSubtxNo())
                .txStartTime(getCurrentDateTime())
                .txSendTime(getCurrentDateTime())
                .txCode("STD001")
                .channelNo("WEB")
                .orgNo("001")
                .tellerId("STD_TELLER")
                .terminalId("TERMINAL_001")
                .clientIp("192.168.1.100")
            )
            .withTxEntity(entity -> entity
                .custNo("040000037480013")
                .qryVchrTpCd("1")
                .txSceneCd("C203")
            )
            .withTxComn(comn -> comn
                .accountingDate(getCurrentDate())
                .curQryReqNum("0")
                .bgnIndexNo("0")
                .busiSendSysOrCmptNo("99710730008")
            )
            .build();
    }
    
    /**
     * 创建查询业务场景的测试数据
     * @return 查询业务场景的CompleteMessageModel
     */
    public static CompleteMessageModel createQueryBusinessScenario() {
        return MessageBuilder.create()
            .withDefaults()
            .withTxHeader(header -> header
                .msgGrptMac(generateMsgGrptMac())
                .globalBusiTrackNo(generateGlobalBusiTrackNo())
                .subtxNo(generateSubtxNo())
                .txStartTime(getCurrentDateTime())
                .txSendTime(getCurrentDateTime())
                .txCode("QRY001")
                .channelNo("API")
                .orgNo("002")
            )
            .withTxEntity(entity -> entity
                .custNo(getRandomCustomerNumber())
                .qryVchrTpCd("1")
                .txSceneCd("C203")
                .addField("queryType", "BALANCE")
                .addField("queryRange", "CURRENT")
            )
            .withTxComn(comn -> comn
                .accountingDate(getCurrentDate())
                .curQryReqNum("10")
                .bgnIndexNo("1")
                .addtData("queryParams", "balance,history")
                .busiSendSysOrCmptNo("99710730008")
            )
            .build();
    }
    
    /**
     * 创建转账业务场景的测试数据
     * @return 转账业务场景的CompleteMessageModel
     */
    public static CompleteMessageModel createTransferBusinessScenario() {
        return MessageBuilder.create()
            .withDefaults()
            .withTxHeader(header -> header
                .msgGrptMac(generateMsgGrptMac())
                .globalBusiTrackNo(generateGlobalBusiTrackNo())
                .subtxNo(generateSubtxNo())
                .txStartTime(getCurrentDateTime())
                .txSendTime(getCurrentDateTime())
                .txCode("TRF001")
                .channelNo("MOBILE")
                .orgNo("003")
                .tellerId("TRF_TELLER")
                .authTellerId("AUTH_TELLER")
            )
            .withTxEntity(entity -> entity
                .custNo(getRandomCustomerNumber())
                .qryVchrTpCd("2")
                .txSceneCd("C204")
                .addField("fromAccount", "1234567890")
                .addField("toAccount", "0987654321")
                .addField("amount", "1000.00")
                .addField("currency", "CNY")
            )
            .withTxComn(comn -> comn
                .accountingDate(getCurrentDate())
                .addtData("transferType", "INTERNAL")
                .addtData("memo", "Transfer test")
                .txComn2("authLevel", "2")
                .txComn2("riskLevel", "LOW")
                .busiSendSysOrCmptNo("99710730008")
            )
            .build();
    }
    
    /**
     * 创建边界值测试数据 - 最小值场景
     * @return 最小值边界测试的CompleteMessageModel
     */
    public static CompleteMessageModel createMinBoundaryScenario() {
        return MessageBuilder.create()
            .withTxHeader(header -> header
                .msgGrptMac("MIN")
                .globalBusiTrackNo("1")
                .subtxNo("1")
                .txStartTime("20230101000000")
                .txSendTime("20230101000001")
                .txCode("MIN")
                .channelNo("M")
                .orgNo("1")
            )
            .withTxEntity(entity -> entity
                .custNo("000000000000001")
                .qryVchrTpCd("1")
                .txSceneCd("C001")
            )
            .withTxComn(comn -> comn
                .accountingDate("20230101")
                .curQryReqNum("0")
                .bgnIndexNo("0")
            )
            .build();
    }
    
    /**
     * 创建边界值测试数据 - 最大值场景
     * @return 最大值边界测试的CompleteMessageModel
     */
    public static CompleteMessageModel createMaxBoundaryScenario() {
        return MessageBuilder.create()
            .withTxHeader(header -> header
                .msgGrptMac("MAX_VALUE_TEST_MAC_BOUNDARY_SCENARIO_TESTING")
                .globalBusiTrackNo("MAX_GLOBAL_BUSI_TRACK_NO_BOUNDARY_TEST_SCENARIO")
                .subtxNo("MAX_SUBTX_NO_BOUNDARY")
                .txStartTime("99991231235959")
                .txSendTime("99991231235959")
                .txCode("MAXCODE999")
                .channelNo("MAXCHANNEL")
                .orgNo("999999999")
                .tellerId("MAX_TELLER_ID_BOUNDARY_TEST")
                .terminalId("MAX_TERMINAL_ID_BOUNDARY")
                .clientIp("255.255.255.255")
            )
            .withTxEntity(entity -> entity
                .custNo("999999999999999")
                .qryVchrTpCd("9")
                .txSceneCd("Z999")
                .addField("maxField", "MAX_VALUE_FIELD_CONTENT_FOR_BOUNDARY_TESTING")
            )
            .withTxComn(comn -> comn
                .accountingDate("99991231")
                .curQryReqNum("999999")
                .bgnIndexNo("999999")
                .addtData("maxKey", "MAX_ADDITIONAL_DATA_VALUE_FOR_BOUNDARY_TESTING")
                .busiSendSysOrCmptNo("MAX_BUSI_SEND_SYS_BOUNDARY")
            )
            .build();
    }
    
    /**
     * 创建异常情况测试数据 - 空值场景
     * @return 包含空值的CompleteMessageModel
     */
    public static CompleteMessageModel createNullValueScenario() {
        return MessageBuilder.create()
            .withTxHeader(header -> header
                .msgGrptMac("NULL_TEST_MAC")
                .globalBusiTrackNo("NULL_TEST_TRACK")
                .subtxNo("NULL_TEST")
                // 故意留空一些字段来测试空值处理
            )
            .withTxEntity(entity -> entity
                .custNo("000000000000000")
                .qryVchrTpCd("0")
                .txSceneCd("C000")
                // 不添加额外字段
            )
            .withTxComn(comn -> comn
                .accountingDate("00000000")
                // 不设置其他字段
            )
            .build();
    }
    
    /**
     * 创建异常情况测试数据 - 格式错误场景
     * @return 包含格式错误的CompleteMessageModel（用于测试验证逻辑）
     */
    public static CompleteMessageModel createInvalidFormatScenario() {
        CompleteMessageModel message = new CompleteMessageModel();
        
        // 创建包含格式错误的TxHeader
        TxHeaderModel header = new TxHeaderModel();
        header.setMsgGrptMac("INVALID_MAC_TOO_LONG_FOR_FIELD_VALIDATION_TESTING_PURPOSES_EXCEEDING_LIMITS");
        header.setGlobalBusiTrackNo(""); // 空字符串
        header.setSubtxNo("INVALID_SUBTX_NO_TOO_LONG_FOR_VALIDATION");
        message.setTxHeader(header);
        
        // 创建包含格式错误的TxEntity
        TxEntityModel entity = new TxEntityModel();
        entity.setCustNo("INVALID_CUSTOMER_NUMBER"); // 不符合15位数字格式
        entity.setQryVchrTpCd("INVALID"); // 不符合1位数字格式
        entity.setTxSceneCd("INVALID_SCENE_CODE"); // 不符合格式
        message.setTxEntity(entity);
        
        // 创建包含格式错误的TxComn
        TxComnModel comn = new TxComnModel();
        comn.setAccountingDate("INVALID_DATE"); // 不符合8位数字格式
        message.setTxComn(comn);
        
        return message;
    }
    
    /**
     * 创建参数化测试数据
     * @param scenario 场景名称
     * @param custNo 客户号
     * @param txSceneCd 交易场景代码
     * @param additionalParams 额外参数
     * @return 参数化的CompleteMessageModel
     */
    public static CompleteMessageModel createParameterizedScenario(
            String scenario, 
            String custNo, 
            String txSceneCd, 
            Map<String, Object> additionalParams) {
        
        MessageBuilder builder = MessageBuilder.create()
            .withDefaults()
            .withTxHeader(header -> header
                .msgGrptMac(generateMsgGrptMac())
                .globalBusiTrackNo(generateGlobalBusiTrackNo())
                .subtxNo(generateSubtxNo())
                .txStartTime(getCurrentDateTime())
                .txSendTime(getCurrentDateTime())
                .txCode("PARAM_" + scenario.toUpperCase())
                .remark("Parameterized test: " + scenario)
            )
            .withTxEntity(entity -> entity
                .custNo(custNo)
                .qryVchrTpCd(getRandomQryVchrTpCode())
                .txSceneCd(txSceneCd)
            )
            .withTxComn(comn -> comn
                .accountingDate(getCurrentDate())
                .addtData("scenario", scenario)
                .busiSendSysOrCmptNo("99710730008")
            );
        
        // 添加额外参数
        if (additionalParams != null) {
            for (Map.Entry<String, Object> entry : additionalParams.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                
                if (key.startsWith("header.")) {
                    // TxHeader参数
                    String headerField = key.substring(7);
                    builder.withTxHeader(header -> setHeaderField(header, headerField, value));
                } else if (key.startsWith("entity.")) {
                    // TxEntity参数
                    String entityField = key.substring(7);
                    builder.withTxEntity(entity -> setEntityField(entity, entityField, value));
                } else if (key.startsWith("comn.")) {
                    // TxComn参数
                    String comnField = key.substring(5);
                    builder.withTxComn(comn -> setComnField(comn, comnField, value));
                } else {
                    // 默认添加到addtData
                    builder.withTxComn(comn -> comn.addtData(key, value));
                }
            }
        }
        
        return builder.build();
    }
    
    /**
     * 创建随机测试数据
     * @return 随机生成的CompleteMessageModel
     */
    public static CompleteMessageModel createRandomScenario() {
        return MessageBuilder.create()
            .withDefaults()
            .withTxHeader(header -> header
                .msgGrptMac(generateMsgGrptMac())
                .globalBusiTrackNo(generateGlobalBusiTrackNo())
                .subtxNo(generateSubtxNo())
                .txStartTime(getCurrentDateTime())
                .txSendTime(getCurrentDateTime())
                .txCode("RND" + String.format("%03d", random.nextInt(1000)))
                .channelNo(getRandomChannelNo())
                .orgNo(String.format("%03d", random.nextInt(1000)))
                .tellerId("TELLER_" + random.nextInt(1000))
            )
            .withTxEntity(entity -> entity
                .custNo(getRandomCustomerNumber())
                .qryVchrTpCd(getRandomQryVchrTpCode())
                .txSceneCd(getRandomTxSceneCode())
                .addField("randomField1", "randomValue" + random.nextInt(100))
                .addField("randomField2", random.nextInt(1000))
            )
            .withTxComn(comn -> comn
                .accountingDate(getCurrentDate())
                .curQryReqNum(String.valueOf(random.nextInt(100)))
                .bgnIndexNo(String.valueOf(random.nextInt(100)))
                .addtData("randomKey", "randomValue" + random.nextInt(100))
                .busiSendSysOrCmptNo("SYS" + String.format("%08d", random.nextInt(100000000)))
            )
            .build();
    }
    
    /**
     * 批量创建测试数据
     * @param count 数量
     * @param scenario 场景类型
     * @return 测试数据数组
     */
    public static CompleteMessageModel[] createBatchTestData(int count, String scenario) {
        CompleteMessageModel[] messages = new CompleteMessageModel[count];
        
        for (int i = 0; i < count; i++) {
            switch (scenario.toLowerCase()) {
                case "standard":
                    messages[i] = createStandardBusinessScenario();
                    break;
                case "query":
                    messages[i] = createQueryBusinessScenario();
                    break;
                case "transfer":
                    messages[i] = createTransferBusinessScenario();
                    break;
                case "random":
                    messages[i] = createRandomScenario();
                    break;
                default:
                    messages[i] = createStandardBusinessScenario();
            }
            
            // 为每个消息添加唯一标识
            messages[i].getTxHeader().setSeqNo(String.valueOf(i + 1));
            messages[i].getTxHeader().setRemark("Batch test #" + (i + 1));
        }
        
        return messages;
    }
    
    // 工具方法
    
    /**
     * 生成消息组MAC
     */
    private static String generateMsgGrptMac() {
        return "MAC_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
    
    /**
     * 生成全局业务跟踪号
     */
    private static String generateGlobalBusiTrackNo() {
        return "TRACK_" + System.currentTimeMillis() + "_" + random.nextInt(10000);
    }
    
    /**
     * 生成子交易号
     */
    private static String generateSubtxNo() {
        return "SUBTX_" + String.format("%06d", random.nextInt(1000000));
    }
    
    /**
     * 获取当前日期时间
     */
    private static String getCurrentDateTime() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }
    
    /**
     * 获取当前日期
     */
    private static String getCurrentDate() {
        return LocalDateTime.now().format(DATE_FORMATTER);
    }
    
    /**
     * 获取随机客户号
     */
    private static String getRandomCustomerNumber() {
        return CUSTOMER_NUMBERS[random.nextInt(CUSTOMER_NUMBERS.length)];
    }
    
    /**
     * 获取随机交易场景代码
     */
    private static String getRandomTxSceneCode() {
        return TX_SCENE_CODES[random.nextInt(TX_SCENE_CODES.length)];
    }
    
    /**
     * 获取随机查询凭证类型代码
     */
    private static String getRandomQryVchrTpCode() {
        return QRY_VCHR_TP_CODES[random.nextInt(QRY_VCHR_TP_CODES.length)];
    }
    
    /**
     * 获取随机渠道号
     */
    private static String getRandomChannelNo() {
        String[] channels = {"WEB", "MOBILE", "API", "ATM", "COUNTER"};
        return channels[random.nextInt(channels.length)];
    }
    
    // 参数设置辅助方法
    
    private static void setHeaderField(MessageBuilder.TxHeaderBuilder header, String field, Object value) {
        String strValue = value != null ? value.toString() : null;
        switch (field) {
            case "msgGrptMac": header.msgGrptMac(strValue); break;
            case "globalBusiTrackNo": header.globalBusiTrackNo(strValue); break;
            case "subtxNo": header.subtxNo(strValue); break;
            case "txStartTime": header.txStartTime(strValue); break;
            case "txSendTime": header.txSendTime(strValue); break;
            case "txCode": header.txCode(strValue); break;
            case "channelNo": header.channelNo(strValue); break;
            case "orgNo": header.orgNo(strValue); break;
            case "tellerId": header.tellerId(strValue); break;
            case "terminalId": header.terminalId(strValue); break;
            case "clientIp": header.clientIp(strValue); break;
            case "remark": header.remark(strValue); break;
        }
    }
    
    private static void setEntityField(MessageBuilder.TxEntityBuilder entity, String field, Object value) {
        switch (field) {
            case "custNo": entity.custNo(value != null ? value.toString() : null); break;
            case "qryVchrTpCd": entity.qryVchrTpCd(value != null ? value.toString() : null); break;
            case "txSceneCd": entity.txSceneCd(value != null ? value.toString() : null); break;
            default: entity.addField(field, value); break;
        }
    }
    
    private static void setComnField(MessageBuilder.TxComnBuilder comn, String field, Object value) {
        switch (field) {
            case "accountingDate": comn.accountingDate(value != null ? value.toString() : null); break;
            case "curQryReqNum": comn.curQryReqNum(value != null ? value.toString() : null); break;
            case "bgnIndexNo": comn.bgnIndexNo(value != null ? value.toString() : null); break;
            case "busiSendSysOrCmptNo": comn.busiSendSysOrCmptNo(value != null ? value.toString() : null); break;
            default: comn.addtData(field, value); break;
        }
    }
}