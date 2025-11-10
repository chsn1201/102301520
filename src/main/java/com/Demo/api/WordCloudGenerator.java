package com.Demo.api;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.palette.ColorPalette;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 使用 Kumo 库将词频生成词云图片
 */
public class WordCloudGenerator {

    /**
     * 生成词云
     * @param freq 词频Map
     * @param filePath 文件输出位置
     */
    public void generateWordCloud(Map<String, Integer> freq, String filePath) {
        try {
            List<WordFrequency> wordFrequencies = freq.entrySet().stream()
                    .map(e -> new WordFrequency(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());

            // 画布大小（可调）
            Dimension dimension = new Dimension(800, 800);

            // 创建词云对象，指定像素完美碰撞检测模式
            WordCloud wc = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);

            //设置白色底色
            //wc.setBackgroundColor(Color.WHITE);

            // 边距
            wc.setPadding(2);

            // 背景：圆形（中心对称）
            wc.setBackground(new CircleBackground(350));

            // 调色板
            wc.setColorPalette(new ColorPalette(
                    new Color(0x9AD9EA),   // 浅蓝
                    new Color(0xA8E6CF),   // 薄荷绿
                    new Color(0xFFD3B6),   // 桃色
                    new Color(0xFFAAA5),   // 浅粉
                    new Color(0xD5AAFF)    // 淡紫
            ));

            // 字体缩放：最小 20，最大 80
            wc.setFontScalar(new LinearFontScalar(20, 80));

            //指定中文字体
            Font font = new Font("SimHei", Font.PLAIN, 30);
            wc.setKumoFont(new com.kennycason.kumo.font.KumoFont(font));
            // 构建并写文件
            wc.build(wordFrequencies);
            wc.writeToFile(filePath);

            System.out.println("词云生成成功: " + filePath);
        } catch (Exception e) {
            System.out.println("词云生成失败: " + e.getMessage());
        }
    }
}
