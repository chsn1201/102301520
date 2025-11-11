package com.Demo.api;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 使用 Selenium 自动化抓取 B站搜索结果
 */
public class BiliSearchAPI {

    private WebDriver driver;

    public BiliSearchAPI() {
        // 设置本地 EdgeDriver 路径
        System.setProperty("webdriver.edge.driver", "D:\\ProgramsForJava\\msedgedriver.exe");
        EdgeOptions options = new EdgeOptions();
        //options.addArguments("--headless=new");
        driver = new EdgeDriver(options);
    }

    public BiliSearchAPI(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * 根据单个关键词获取至多 targetCount 个不重复的 BV
     * @param keyword 关键词
     * @param targetCount 搜索数量
     * @return set BV集合
     */
    public List<String> collectTopVideoByKeyword(String keyword, int targetCount) {
        Set<String> set = new LinkedHashSet<String>();
        int page = 1;

        //遍历每页保存BV
        while (set.size() < targetCount && page <= 30) {

            try {
                //拼接url
                String url;
                if(page == 1){
                    url = "https://search.bilibili.com/all?keyword=" +
                            URLEncoder.encode(keyword, "UTF-8");
                }else{
                    url = "https://search.bilibili.com/all?keyword=" +
                            URLEncoder.encode(keyword, "UTF-8") +
                            "&page=" + page;
                }
                driver.get(url);
                List<WebElement> items = driver.findElements(By.cssSelector(".bili-video-card__wrap"));
                if (items == null || items.isEmpty()) {
                    System.out.println("第 " + page + " 页未找到视频");
                    break;
                }

                // 遍历每条视频信息，提取 BV
                for(WebElement item : items){
                    try {
                        WebElement linkElement = item.findElement(By.cssSelector("a"));
                        String href = linkElement.getAttribute("href");
                        if (href != null && href.contains("/video/")) {
                            String bv = extractBV(href);
                            if (bv != null) {
                                set.add(bv);
                                if (set.size() >= targetCount) break;
                            }
                        }
                    } catch (Exception ignore) {}
                }
                page++;
                //限速
                Thread.sleep(300);
            } catch (Exception e) {
                System.out.println("Selenium 搜索异常：" + e.getMessage());
                break;
            }

        }
        return new ArrayList<>(set);
    }

    /**
     * 从 URL 提取 BV
     */
    private String extractBV(String url) {
        int idx = url.indexOf("/video/");
        if (idx >= 0) {
            String tail = url.substring(idx + 7);
            String[] parts = tail.split("[/?]");
            if (parts.length > 0) {
                String bv = parts[0];
                if (bv.startsWith("BV") || bv.startsWith("bv")){
                    return bv;
                }
            }
        }
        return null;
    }

    public void close() {
        if (driver != null) driver.quit();
    }
}
