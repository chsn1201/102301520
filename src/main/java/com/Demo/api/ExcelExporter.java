package com.Demo.api;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

/**
 * 使用 Apache POI 将词频统计与 top8 输出为 xlsx 文件
 */
public class ExcelExporter {

    /**
     * 将词频与 top8 导出为 Excel
     * @param filePath 输出文件路径
     * @param wordFreq 词频 Map
     * @param top top8 列表
     */
    public void exportWordFreq(String filePath, Map<String, Integer> wordFreq,
                               List<Map.Entry<String, Integer>> top){
        try(Workbook wb = new XSSFWorkbook()){
            //词频表
            Sheet sheet1 = wb.createSheet("词频表");
            Row head1 = sheet1.createRow(0);
            head1.createCell(0).setCellValue("词语");
            head1.createCell(1).setCellValue("次数");

            int row = 1;
            for(Map.Entry<String, Integer> e : wordFreq.entrySet()){
                Row r = sheet1.createRow(row++);
                r.createCell(0).setCellValue(e.getKey());
                r.createCell(1).setCellValue(e.getValue());
            }

            //top表
            Sheet sheet2 = wb.createSheet("TopN表");
            Row head2 = sheet2.createRow(0);
            head2.createCell(0).setCellValue("词语");
            head2.createCell(0).setCellValue("次数");

            row = 1;
            for(Map.Entry<String, Integer> e : top){
                Row r = sheet2.createRow(row++);
                r.createCell(0).setCellValue(e.getKey());
                r.createCell(1).setCellValue(e.getValue());
            }

            //写入excel
            try(FileOutputStream fos = new FileOutputStream(filePath)){
                wb.write(fos);
            }
            System.out.println("Excel 导出成功: " + filePath);
        } catch (Exception e){
            System.out.println("Excel 导出异常: " + e.getMessage());
        }
    }
}
