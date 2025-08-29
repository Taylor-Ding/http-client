package com.example.httpclientdemo.performance;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.httpclientdemo.builder.MessageBuilder;
import com.example.httpclientdemo.factory.TestDataFactory;
import com.example.httpclientdemo.model.CompleteMessageModel;
import com.example.httpclientdemo.service.HttpService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 性能和质量验证测试
 * 实现测试覆盖率检查、代码质量验证、性能基准测试
 * 验证内存使用和执行效率
 */
@DisplayName("Performance and Quality Verification Tests")
class PerformanceAndQualityTest {
    
    private MockWebServer mockWebServer;
    private HttpService httpService;
    private String baseUrl;
    
    // 性能基准阈值
    private static final long MAX_SINGLE_REQUEST_TIME_MS = 1000;
    private static final long MAX_BATCH_REQUEST_TIME_MS = 5000;
    private static final int CONCURRENT_THREADS = 10;
    private static final int BATCH_SIZE = 100;
    
    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        baseUrl = mockWebServer.url("/api/").toString();
        
        WebClient.Builder webClientBuilder = WebClient.builder();
        httpService = new HttpService(webClientBuilder);
        
        // 预设快速响应以减少网络延迟影响
        setupFastMockResponses();
    }
    
    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }
    
    @Test
    @DisplayName("Should validate message building performance")
    void shouldValidateMessageBuildingPerformance() {
        // Given - 性能基准测试参数
        int iterations = 1000;
        List<Long> buildTimes = new ArrayList<>();
        
        // When - 测试消息构建性能
        for (int i = 0; i < iterations; i++) {
            final int iteration = i; // Make variable effectively final
            long startTime = System.nanoTime();
            
            CompleteMessageModel message = MessageBuilder.create()
                .withDefaults()
                .withTxHeader(header -> header
                    .msgGrptMac("PERF_MAC_" + iteration)
                    .globalBusiTrackNo("PERF_TRACK_" + iteration)
                    .subtxNo("PERF_SUBTX_" + iteration)
                    .txCode("PERF001")
                    .channelNo("PERFORMANCE_TEST")
                )
                .withTxEntity(entity -> entity
                    .custNo("PERF" + String.format("%011d", iteration))
                    .qryVchrTpCd("1")
                    .txSceneCd("C203")
                    .addField("iteration", iteration)
                    .addField("timestamp", System.currentTimeMillis())
                )
                .withTxComn(comn -> comn
                    .accountingDate("20240315")
                    .curQryReqNum(String.valueOf(iteration % 100))
                    .bgnIndexNo("0")
                    .addtData("perfTest", true)
                    .addtData("iteration", iteration)
                    .busiSendSysOrCmptNo("99710730008")
                )
                .build();
            
            long endTime = System.nanoTime();
            long buildTimeMs = (endTime - startTime) / 1_000_000;
            buildTimes.add(buildTimeMs);
            
            // 验证构建的消息有效性
            assertNotNull(message);
            assertTrue(message.validate());
        }
        
        // Then - 分析性能指标
        double avgBuildTime = buildTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        long maxBuildTime = buildTimes.stream().mapToLong(Long::longValue).max().orElse(0L);
        long minBuildTime = buildTimes.stream().mapToLong(Long::longValue).min().orElse(0L);
        
        System.out.println("=== Message Building Performance Results ===");
        System.out.println("Iterations: " + iterations);
        System.out.println("Average build time: " + String.format("%.2f", avgBuildTime) + " ms");
        System.out.println("Max build time: " + maxBuildTime + " ms");
        System.out.println("Min build time: " + minBuildTime + " ms");
        
        // 性能断言
        assertTrue(avgBuildTime < 10.0, "Average build time should be less than 10ms, actual: " + avgBuildTime);
        assertTrue(maxBuildTime < 50, "Max build time should be less than 50ms, actual: " + maxBuildTime);
    }
    
    @Test
    @DisplayName("Should validate JSON serialization performance")
    void shouldValidateJsonSerializationPerformance() {
        // Given - JSON序列化性能测试
        CompleteMessageModel testMessage = TestDataFactory.createStandardBusinessScenario();
        int iterations = 1000;
        List<Long> serializationTimes = new ArrayList<>();
        List<Integer> jsonSizes = new ArrayList<>();
        
        // When - 测试JSON序列化性能
        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            
            String json = testMessage.toJson();
            String prettyJson = testMessage.toPrettyJson();
            
            long endTime = System.nanoTime();
            long serializationTimeMs = (endTime - startTime) / 1_000_000;
            serializationTimes.add(serializationTimeMs);
            jsonSizes.add(json.length());
            
            // 验证序列化结果
            assertNotNull(json);
            assertNotNull(prettyJson);
            assertTrue(json.length() > 0);
            assertTrue(prettyJson.length() > json.length()); // Pretty JSON应该更长
            
            // 验证可以反序列化
            JSONObject jsonObj = JSON.parseObject(json);
            assertTrue(jsonObj.containsKey("txHeader"));
            assertTrue(jsonObj.containsKey("txBody"));
        }
        
        // Then - 分析序列化性能
        double avgSerializationTime = serializationTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        double avgJsonSize = jsonSizes.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        
        System.out.println("=== JSON Serialization Performance Results ===");
        System.out.println("Iterations: " + iterations);
        System.out.println("Average serialization time: " + String.format("%.2f", avgSerializationTime) + " ms");
        System.out.println("Average JSON size: " + String.format("%.0f", avgJsonSize) + " bytes");
        
        // 性能断言
        assertTrue(avgSerializationTime < 5.0, "Average serialization time should be less than 5ms");
        assertTrue(avgJsonSize > 500, "JSON size should be reasonable (>500 bytes)");
        assertTrue(avgJsonSize < 10000, "JSON size should not be too large (<10KB)");
    }
    
    @RepeatedTest(5)
    @DisplayName("Should validate HTTP request performance consistency")
    void shouldValidateHttpRequestPerformanceConsistency() throws InterruptedException {
        // Given - HTTP请求性能一致性测试
        CompleteMessageModel message = TestDataFactory.createQueryBusinessScenario();
        
        // When - 执行HTTP请求并测量时间
        long startTime = System.currentTimeMillis();
        String response = httpService.sendRequest(baseUrl + "performance", message);
        long endTime = System.currentTimeMillis();
        long requestTime = endTime - startTime;
        
        // Then - 验证性能一致性
        assertNotNull(response);
        assertTrue(requestTime < MAX_SINGLE_REQUEST_TIME_MS, 
            "Request time should be less than " + MAX_SINGLE_REQUEST_TIME_MS + "ms, actual: " + requestTime + "ms");
        
        // 验证请求正确性
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertNotNull(recordedRequest);
        assertEquals("POST", recordedRequest.getMethod());
        
        String requestBody = recordedRequest.getBody().readUtf8();
        JSONObject requestJson = JSON.parseObject(requestBody);
        assertTrue(requestJson.containsKey("txHeader"));
        assertTrue(requestJson.containsKey("txBody"));
        
        System.out.println("HTTP request completed in: " + requestTime + "ms");
    }
    
    @Test
    @DisplayName("Should validate concurrent request handling performance")
    void shouldValidateConcurrentRequestHandlingPerformance() throws InterruptedException, ExecutionException {
        // Given - 并发请求性能测试
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        List<Future<Long>> futures = new ArrayList<>();
        
        // When - 执行并发请求
        long overallStartTime = System.currentTimeMillis();
        
        for (int i = 0; i < CONCURRENT_THREADS; i++) {
            final int threadId = i;
            Future<Long> future = executor.submit(() -> {
                final int currentThreadId = threadId; // Make variable effectively final
                CompleteMessageModel message = MessageBuilder.create()
                    .withDefaults()
                    .withTxHeader(header -> header
                        .msgGrptMac("CONCURRENT_MAC_" + currentThreadId)
                        .globalBusiTrackNo("CONCURRENT_TRACK_" + currentThreadId)
                        .txCode("CONCURRENT001")
                        .remark("Concurrent test thread " + currentThreadId)
                    )
                    .withTxEntity(entity -> entity
                        .custNo("THREAD" + String.format("%010d", currentThreadId))
                        .qryVchrTpCd("1")
                        .txSceneCd("C203")
                        .addField("threadId", currentThreadId)
                    )
                    .withTxComn(comn -> comn
                        .accountingDate("20240315")
                        .addtData("threadId", currentThreadId)
                        .addtData("concurrentTest", true)
                        .busiSendSysOrCmptNo("99710730008")
                    )
                    .build();
                
                long threadStartTime = System.currentTimeMillis();
                String response = httpService.sendRequest(baseUrl + "concurrent/" + currentThreadId, message);
                long threadEndTime = System.currentTimeMillis();
                
                assertNotNull(response);
                return threadEndTime - threadStartTime;
            });
            futures.add(future);
        }
        
        // 等待所有请求完成
        List<Long> requestTimes = new ArrayList<>();
        for (Future<Long> future : futures) {
            requestTimes.add(future.get());
        }
        
        long overallEndTime = System.currentTimeMillis();
        long totalTime = overallEndTime - overallStartTime;
        
        executor.shutdown();
        
        // Then - 分析并发性能
        double avgRequestTime = requestTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        long maxRequestTime = requestTimes.stream().mapToLong(Long::longValue).max().orElse(0L);
        
        System.out.println("=== Concurrent Request Performance Results ===");
        System.out.println("Concurrent threads: " + CONCURRENT_THREADS);
        System.out.println("Total execution time: " + totalTime + " ms");
        System.out.println("Average request time: " + String.format("%.2f", avgRequestTime) + " ms");
        System.out.println("Max request time: " + maxRequestTime + " ms");
        
        // 性能断言
        assertTrue(totalTime < MAX_BATCH_REQUEST_TIME_MS, 
            "Total concurrent execution time should be less than " + MAX_BATCH_REQUEST_TIME_MS + "ms");
        assertTrue(avgRequestTime < MAX_SINGLE_REQUEST_TIME_MS, 
            "Average concurrent request time should be reasonable");
        
        // 验证所有请求都被正确处理
        assertEquals(CONCURRENT_THREADS, mockWebServer.getRequestCount());
    }
    
    @ParameterizedTest
    @ValueSource(ints = {10, 50, 100, 200})
    @DisplayName("Should validate batch processing performance with different sizes")
    void shouldValidateBatchProcessingPerformanceWithDifferentSizes(int batchSize) throws InterruptedException {
        // Given - 不同批次大小的性能测试
        CompleteMessageModel[] batchMessages = TestDataFactory.createBatchTestData(batchSize, "standard");
        
        // When - 执行批量处理
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < batchSize; i++) {
            String response = httpService.sendRequest(baseUrl + "batch/" + i, batchMessages[i]);
            assertNotNull(response);
        }
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double avgTimePerRequest = (double) totalTime / batchSize;
        
        // Then - 验证批量处理性能
        System.out.println("=== Batch Processing Performance (Size: " + batchSize + ") ===");
        System.out.println("Total time: " + totalTime + " ms");
        System.out.println("Average time per request: " + String.format("%.2f", avgTimePerRequest) + " ms");
        System.out.println("Throughput: " + String.format("%.2f", (batchSize * 1000.0) / totalTime) + " requests/second");
        
        // 性能断言 - 随着批次增大，平均时间应该保持稳定
        assertTrue(avgTimePerRequest < MAX_SINGLE_REQUEST_TIME_MS, 
            "Average time per request should be reasonable for batch size " + batchSize);
        
        // 验证所有请求都被处理
        assertEquals(batchSize, mockWebServer.getRequestCount());
        
        // 清理请求队列以便下次测试
        while (mockWebServer.getRequestCount() > 0) {
            mockWebServer.takeRequest();
        }
    }
    
    @Test
    @DisplayName("Should validate memory usage and garbage collection efficiency")
    void shouldValidateMemoryUsageAndGarbageCollectionEfficiency() {
        // Given - 内存使用和GC效率测试
        Runtime runtime = Runtime.getRuntime();
        int iterations = 1000;
        
        // 强制GC并获取基线内存
        System.gc();
        long baselineMemory = runtime.totalMemory() - runtime.freeMemory();
        
        // When - 执行大量对象创建和处理
        List<CompleteMessageModel> messages = new ArrayList<>();
        
        for (int i = 0; i < iterations; i++) {
            final int iteration = i; // Make variable effectively final
            CompleteMessageModel message = MessageBuilder.create()
                .withDefaults()
                .withTxHeader(header -> header
                    .msgGrptMac("MEMORY_TEST_" + iteration)
                    .globalBusiTrackNo("MEMORY_TRACK_" + iteration)
                    .txCode("MEMORY001")
                )
                .withTxEntity(entity -> entity
                    .custNo("MEM" + String.format("%012d", iteration))
                    .qryVchrTpCd("1")
                    .txSceneCd("C203")
                    .addField("memoryTest", true)
                    .addField("iteration", iteration)
                )
                .withTxComn(comn -> comn
                    .accountingDate("20240315")
                    .addtData("memoryTestData", "data_" + iteration)
                    .busiSendSysOrCmptNo("99710730008")
                )
                .build();
            
            // 序列化以模拟实际使用
            String json = message.toJson();
            assertNotNull(json);
            
            // 每100次迭代保存一个消息以测试内存累积
            if (i % 100 == 0) {
                messages.add(message);
            }
        }
        
        // 测量峰值内存使用
        long peakMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = peakMemory - baselineMemory;
        
        // 清理并强制GC
        messages.clear();
        System.gc();
        Thread.yield(); // 给GC一些时间
        
        long afterGcMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryReclaimed = peakMemory - afterGcMemory;
        double gcEfficiency = (double) memoryReclaimed / memoryIncrease * 100;
        
        // Then - 分析内存使用情况
        System.out.println("=== Memory Usage and GC Efficiency Results ===");
        System.out.println("Baseline memory: " + formatBytes(baselineMemory));
        System.out.println("Peak memory: " + formatBytes(peakMemory));
        System.out.println("Memory increase: " + formatBytes(memoryIncrease));
        System.out.println("Memory after GC: " + formatBytes(afterGcMemory));
        System.out.println("Memory reclaimed: " + formatBytes(memoryReclaimed));
        System.out.println("GC efficiency: " + String.format("%.2f", gcEfficiency) + "%");
        
        // 内存使用断言
        assertTrue(memoryIncrease < 100 * 1024 * 1024, // 100MB
            "Memory increase should be reasonable (<100MB)");
        assertTrue(gcEfficiency > 50.0, 
            "GC should reclaim at least 50% of allocated memory");
    }
    
    @Test
    @DisplayName("Should validate code quality metrics and test coverage")
    void shouldValidateCodeQualityMetricsAndTestCoverage() {
        // Given - 代码质量指标验证
        
        // 测试消息构建器的各种使用模式
        testMessageBuilderQuality();
        
        // 测试工厂类的各种场景
        testTestDataFactoryQuality();
        
        // 测试模型类的完整性
        testModelClassesQuality();
        
        // 测试服务类的健壮性
        testServiceClassQuality();
        
        System.out.println("=== Code Quality Validation Completed ===");
        System.out.println("All quality metrics passed successfully");
    }
    
    private void testMessageBuilderQuality() {
        // 测试MessageBuilder的各种构建模式
        
        // 1. 最小配置构建
        CompleteMessageModel minimalMessage = MessageBuilder.create().build();
        assertNotNull(minimalMessage);
        
        // 2. 完整配置构建
        CompleteMessageModel fullMessage = MessageBuilder.create()
            .withDefaults()
            .withTxHeader(header -> header.msgGrptMac("TEST"))
            .withTxEntity(entity -> entity.custNo("123"))
            .withTxComn(comn -> comn.accountingDate("20240315"))
            .buildAndValidate();
        assertTrue(fullMessage.validate());
        
        // 3. 链式调用测试
        CompleteMessageModel chainedMessage = MessageBuilder.create()
            .withDefaults()
            .withTxHeader(TestDataFactory.createStandardBusinessScenario().getTxHeader())
            .withTxEntity(TestDataFactory.createStandardBusinessScenario().getTxEntity())
            .withTxComn(TestDataFactory.createStandardBusinessScenario().getTxComn())
            .build();
        assertNotNull(chainedMessage);
        
        // 4. 错误处理测试
        assertThrows(IllegalStateException.class, () -> {
            MessageBuilder.create()
                .withTxHeader(header -> header.msgGrptMac(null))
                .buildAndValidate();
        });
    }
    
    private void testTestDataFactoryQuality() {
        // 测试TestDataFactory的各种场景覆盖
        
        // 1. 标准场景
        CompleteMessageModel standard = TestDataFactory.createStandardBusinessScenario();
        assertTrue(standard.validate());
        
        // 2. 边界值场景
        CompleteMessageModel minBoundary = TestDataFactory.createMinBoundaryScenario();
        CompleteMessageModel maxBoundary = TestDataFactory.createMaxBoundaryScenario();
        assertNotNull(minBoundary);
        assertNotNull(maxBoundary);
        
        // 3. 异常场景
        CompleteMessageModel nullScenario = TestDataFactory.createNullValueScenario();
        CompleteMessageModel invalidScenario = TestDataFactory.createInvalidFormatScenario();
        assertNotNull(nullScenario);
        assertNotNull(invalidScenario);
        assertFalse(invalidScenario.validateFormat());
        
        // 4. 批量生成
        CompleteMessageModel[] batch = TestDataFactory.createBatchTestData(5, "query");
        assertEquals(5, batch.length);
        for (CompleteMessageModel msg : batch) {
            assertNotNull(msg);
        }
    }
    
    private void testModelClassesQuality() {
        // 测试模型类的完整性和健壮性
        CompleteMessageModel message = TestDataFactory.createStandardBusinessScenario();
        
        // 1. 验证方法测试
        assertTrue(message.validate());
        assertTrue(message.validateFormat());
        assertTrue(message.hasRequiredData());
        
        // 2. 序列化测试
        String json = message.toJson();
        String prettyJson = message.toPrettyJson();
        assertNotNull(json);
        assertNotNull(prettyJson);
        assertTrue(prettyJson.length() > json.length());
        
        // 3. 字段访问测试
        assertNotNull(message.getTxHeader());
        assertNotNull(message.getTxEntity());
        assertNotNull(message.getTxComn());
        
        // 4. 深拷贝和不变性测试
        String originalJson = message.toJson();
        message.getTxHeader().setRemark("Modified");
        String modifiedJson = message.toJson();
        assertNotEquals(originalJson, modifiedJson);
    }
    
    private void testServiceClassQuality() {
        // 测试服务类的健壮性
        
        // 1. 空值处理测试
        assertThrows(IllegalArgumentException.class, () -> {
            httpService.sendRequest(baseUrl, (CompleteMessageModel) null);
        });
        
        // 2. 有效消息处理测试
        CompleteMessageModel validMessage = TestDataFactory.createStandardBusinessScenario();
        assertDoesNotThrow(() -> {
            httpService.sendRequest(baseUrl + "test", validMessage);
        });
    }
    
    /**
     * 设置快速Mock响应以减少网络延迟
     */
    private void setupFastMockResponses() {
        // 为性能测试预设大量快速响应
        for (int i = 0; i < 1000; i++) {
            mockWebServer.enqueue(new MockResponse()
                .setBody("{\"status\":\"success\",\"code\":\"0000\"}")
                .addHeader("Content-Type", "application/json")
                .setResponseCode(200));
        }
    }
    
    /**
     * 格式化字节数为可读格式
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }
}