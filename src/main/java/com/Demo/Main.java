package com.Demo;

import com.Demo.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        //Edge路径
        System.setProperty("webdriver.edge.driver", "D:\\ProgramsForJava\\msedgedriver.exe");
        //EdgeOptions 配置
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--headless=new");
        WebDriver driver = new EdgeDriver(options);

        try {
            //搜索bv号
            String keyword = "大数据模型";
            int targetNum = 300;
            BiliSearchAPI biliSearchAPI = new BiliSearchAPI(driver);
            List<String> bvList = biliSearchAPI.collectTopVideoByKeyword(keyword, targetNum);
            System.out.println("已获取 BV 数量: " + bvList.size());

            //爬取弹幕
            List<String> allDanmaku = new ArrayList<>();
            DanmakuFetcherAPI danmakuFetcherAPI = new DanmakuFetcherAPI(driver);
            int idx = 0;
            for(String bv : bvList){
                idx++;
                System.out.println("(" + idx + "/" + targetNum + ")抓取bv=" + bv + "的弹幕...");
                List<String> danmaku = danmakuFetcherAPI.fetchDanmaku(bv);
                System.out.println("弹幕数量：" + danmaku.size());
                allDanmaku.addAll(danmaku);
            }

            //数据过滤
            TextProcessor textProcessor = new TextProcessor();
            List<String> cleaned = textProcessor.filterNoise(allDanmaku);
            System.out.println("清洗后弹幕数量: " + cleaned.size());
            List<String> related = textProcessor.filterByKeyword(cleaned);
            System.out.println("包含关键词的弹幕数量: " + related.size());
            Map<String, Integer> freq = textProcessor.computeWordFrequency(related);
            List<Map.Entry<String, Integer>> top8 = textProcessor.topN(freq, 8);
            System.out.println("Top 8 词：");
            int i = 1;
            for (var e : top8) {
                System.out.println(i + "." + e.getKey() + ": " + e.getValue());
            }

            //excel导出
            ExcelExporter excelExporter = new ExcelExporter();
            String filePath1 = "D:\\danmaku_stats.xlsx";
            excelExporter.exportWordFreq(filePath1, freq, top8);

            //词云图制作
            WordCloudGenerator wordCloudGenerator = new WordCloudGenerator();
            String filePath2 = "D:\\wordcloud.png";
            wordCloudGenerator.generateWordCloud(freq, filePath2);

        } finally {
            //释放资源
            driver.quit();
        }
    }
}