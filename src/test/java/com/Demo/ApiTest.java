package com.Demo;

import com.Demo.api.BiliSearchAPI;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * JUnit 单元测试：测试 B站 API
 * 1. 搜索视频 BV
 * 2. 获取 cid
 * 3. 下载部分弹幕
 */
public class ApiTest {

    private static BiliSearchAPI biliSearchAPI;

    @BeforeAll
    public static void setup() {
        // 初始化 Selenium 搜索器和弹幕抓取器
        biliSearchAPI = new BiliSearchAPI();
    }

    @AfterAll
    public static void cleanup() {
        // 关闭浏览器
        if (biliSearchAPI != null) biliSearchAPI.close();
    }

    @Test
    public void testSearchBV() {
        String keyword = "大语言模型";
        int targetCount = 100;
        //获取bv列表
        List<String> list = biliSearchAPI.collectTopVideoByKeyword(keyword, targetCount);
        //输出bv列表
        System.out.println(list);
    }
}
