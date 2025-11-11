package com.Demo.api;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用 Selenium 自动化抓取视频弹幕
 */
public class DanmakuFetcherAPI {
    private WebDriver driver;

    public DanmakuFetcherAPI() {
        // 设置本地 EdgeDriver 路径
        System.setProperty("webdriver.edge.driver", "D:\\ProgramsForJava\\msedgedriver.exe");
        EdgeOptions options = new EdgeOptions();
        //options.addArguments("--headless=new");
        driver = new EdgeDriver(options);
    }

    public DanmakuFetcherAPI(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * 根据bv获取cid
     * @param bv bv号
     * @return cid
     */
    public String getCidByBV(String bv) {
        try {
            String videoUrl = "https://www.bilibili.com/video/" + bv;
            driver.get(videoUrl);
            //等待页面完全加载
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until((ExpectedCondition<Boolean>) wd ->
                    ((JavascriptExecutor) wd)
                            .executeScript("return document.readyState")
                            .equals("complete"));

            // 直接执行 JS 获取 cid
            Object result = ((JavascriptExecutor) driver).executeScript(
                    "return (window.__INITIAL_STATE__ && window.__INITIAL_STATE__.videoData) ? " +
                            "window.__INITIAL_STATE__.videoData.cid : " +
                            "(window.__playinfo__ && window.__playinfo__.videoData) ? " +
                            "window.__playinfo__.videoData.cid : null;"
            );

            if (result != null) {
                return result.toString();
            } else {
                System.out.println("JS 未能获取 cid");
            }
        } catch (Exception e) {
            System.out.println("解析 CID 时出现异常：" + e.getMessage());
        }
        return null;
    }

    /**
     * 根据bv获取弹幕文本
     */
    public List<String> fetchDanmaku(String bv){
        List<String> list = new ArrayList<>();
        try {
            String cid = getCidByBV(bv);
            if(cid == null){
                System.out.println("无法从" + bv + "获取cid");
                return list;
            }
            //System.out.println(cid);
            String danmakuUrl = "https://comment.bilibili.com/" + cid + ".xml";

            // 使用 Jsoup 直接解析 XML
            Document doc = Jsoup.parse(new URL(danmakuUrl), 5000); // 超时 5000ms
            // 选择所有 <d> 节点
            Elements dNodes = doc.select("d");
            for (Element d : dNodes) {
                String text = d.text();
                list.add(text);
            }

        } catch (Exception e){
            System.out.println("fetchDanmaku 异常：" + e.getMessage());
        }
        return list;
    }

    public void close(){
        if(driver != null){
            driver.quit();
        }
    }
}
