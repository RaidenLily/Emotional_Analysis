package com.util;

import com.EmotionalWords;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.HashMap;

public class getData {
    public static HashMap<String,EmotionalWords> getDataFromExcel(String filePath) {
        HashMap<String,EmotionalWords> emotionalWordsHashMap=new HashMap<>();

        FileInputStream fis = null;
        Workbook wookbook = null;
        try {
            fis = new FileInputStream(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
            try {
                // 这里需要重新获取流对象，因为前面的异常导致了流的关闭
                fis = new FileInputStream(filePath);
                // 2007版本的excel，用.xlsx结尾
                wookbook = new XSSFWorkbook(filePath);// 得到工作簿
            } catch (IOException e) {
                e.printStackTrace();
            }
        Sheet sheet = wookbook.getSheetAt(0);// 得到一个工作表
        int totalRowNum = sheet.getLastRowNum();// 获得数据的总行数
        String word;//纬度
        String sentimentClassification;
        String partSpeed;
        int emotionalIntensity;
        int polarity;

        // 获得所有数据
        for (int i = 1; i <= totalRowNum; i++) {
            // 获得第i行对象
            Row row = sheet.getRow(i);
            // 获得纬度
            Cell cell;

            cell = row.getCell(0);
            word = cell.getStringCellValue();

            cell=row.getCell(4);
            sentimentClassification=cell.getStringCellValue();

            // 获得经度
            cell = row.getCell(5);
            emotionalIntensity = (int) cell.getNumericCellValue();

            cell=row.getCell(6);
            polarity= (int) cell.getNumericCellValue();

            cell=row.getCell(1);
            partSpeed=cell.getStringCellValue();

            EmotionalWords emotionalWords=new EmotionalWords(sentimentClassification,emotionalIntensity,polarity,partSpeed);
            emotionalWordsHashMap.put(word,emotionalWords);

        }
        return emotionalWordsHashMap;
    }

    public static void getDataFromtxt(String pathname,HashMap level_advMap,float weight){
        try{
            File filename = new File(pathname); // 要读取以上路径的input。txt文件
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
            while (line != null) {
                line = br.readLine();// 一次读入一行数据
                if(line==null){
                    break;
                }
                level_advMap.put(line,weight);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
