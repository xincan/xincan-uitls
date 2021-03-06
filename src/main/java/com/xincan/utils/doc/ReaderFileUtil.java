package com.xincan.utils.doc;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import java.io.*;

/**
 * 读取文件工具类
 * @description 支持的文件类型有（doc,docx,txt）文本内容
 */
@Slf4j
public class ReaderFileUtil {


    public static void main(String[] args)  {
        String filePath= "D:/ocpp/messageFtpDownload/地质灾害风险预报[2015年第1期].txt";
        JSONObject content = read(filePath);
        System.out.println(content);

    }

    /**
     * 读取文件内容
     * @param path
     * @return
     */
    public static JSONObject read(String path){

        JSONObject result = new JSONObject();


        if (path.endsWith(".doc")) {

            result.put("data",readDoc(path));

        } else if (path.endsWith("docx")) {

            result.put("data",readDocx(path));

        } else if(path.endsWith(".txt")){

            result.put("data",readTxt(path));

        }else {
            result.put("code",500);
            result.put("data","");
            result.put("msg","此文件不是word或txt文件！");
            log.error("此文件不是word或txt文件！");
            return result;
        }
        result.put("code",200);
        result.put("msg","文件读取成功");
        log.info("文件提取内容成功");
        return result;
    }

    /**
     * 读取word文件
     * doc
     * @param path
     * @return
     */
    public static String readDoc(String path) {
        String buffer = "";
        try {
            InputStream is = new FileInputStream(new File(path));
            WordExtractor ex = new WordExtractor(is);
            buffer = ex.getText();
            ex.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("此文件不是word或txt文件！");
        }
        return buffer;
    }

    /**
     * 读取word文件
     * docx类型
     * @param path
     * @return
     */
    public static String readDocx(String path) {
        String buffer = "";
        try {
            OPCPackage opcPackage = POIXMLDocument.openPackage(path);
            POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
            buffer = extractor.getText();
            extractor.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("读取文件错误：" + e.getMessage());
        }
        return buffer;
    }


    /**
     * 读取txt文件内容
     * @param path
     * @return
     */
    public static String readTxt(String path) {
        String content = "";
        try {
            File file = new File(path);
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file),"UTF-8");
            BufferedReader bf = new BufferedReader(inputReader);
            // 按行读取字符串
            String str;
            while ((str = bf.readLine()) != null) {
                content = str;
            }
            bf.close();
            inputReader.close();
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}