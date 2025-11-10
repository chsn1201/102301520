package com.Demo;

import com.Demo.api.BiliSearchAPI;
import com.Demo.api.DanmakuFetcherAPI;
import com.Demo.api.TextProcessor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

/**
 * JUnit 单元测试：测试 B站 API
 * 1. 搜索视频 BV
 * 2. 获取 cid
 * 3. 下载部分弹幕
 */
public class ApiTest {

    private static BiliSearchAPI biliSearchAPI;
    private static DanmakuFetcherAPI danmakuFetcherAPI;
    private static TextProcessor textProcessor;

    @BeforeAll
    public static void setup() {
        // 初始化 Selenium 搜索器和弹幕抓取器
        biliSearchAPI = new BiliSearchAPI();
        danmakuFetcherAPI = new DanmakuFetcherAPI();
        textProcessor = new TextProcessor();
    }

    @AfterAll
    public static void cleanup() {
        // 关闭浏览器
        if (biliSearchAPI != null) biliSearchAPI.close();
        if (danmakuFetcherAPI != null) danmakuFetcherAPI.close();
    }

    @Test
    public void testSearchBV() {
        String keyword = "大语言模型";
        int targetCount = 100;
        //获取bv列表
        List<String> list = biliSearchAPI.collectTopVideoByKeyword(keyword, targetCount);
        //输出bv列表
        list.stream().forEach(System.out::println);
    }

    @Test
    public void testDanmakuFetcher() {
        String bv = "BV1uNk1YxEJQ";
        List<String> danmaku = danmakuFetcherAPI.fetchDanmaku(bv);
        System.out.println("抓取到弹幕数：" + danmaku.size());
        //打印部分弹幕
        danmaku.stream().limit(20).forEach(System.out::println);
    }

    @Test
    public void testTextProcessor() {
        String bv = "BV12N411x7FL";
        List<String> danmaku = danmakuFetcherAPI.fetchDanmaku(bv);

        // 1. 过滤噪声
        List<String> cleaned = textProcessor.filterNoise(danmaku);
        System.out.println("清洗后: " + cleaned);

        // 2. 筛选关键词弹幕
        List<String> related = textProcessor.filterByKeyword(cleaned);
        System.out.println("关键词相关弹幕: " + related);

        // 3. 词频统计
        Map<String, Integer> freq = textProcessor.computeWordFrequency(related);
        System.out.println("词频统计: " + freq);

        // 4. TopN
        var top = textProcessor.topN(freq, 3);
        System.out.println("Top3: " + top);
    }
}
