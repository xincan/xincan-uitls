package com.xincan.utils.doc;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * @ClassName DocUtil
 * @Description TODO
 * @Author Xincan
 * @Version 1.0
 **/
@Slf4j
public class DocUtil {

    public static String docToHtml(String filePath){
        try {


            String basePath = filePath.substring(0, filePath.lastIndexOf("/"));
            String imagePath = basePath + "/image/";
            String htmlUrl = basePath + "/html/"+filePath.substring(filePath.lastIndexOf("/")+1, filePath.lastIndexOf("."))+".html";
            // 判断图片路径是否存在，如果不存在则创建
            File file = new File(imagePath);
            if(!file.exists()) {
                file.mkdirs();
            }
            // 判断即将生成html的路径是否存在，如果不存在则创建
            File htmlPath = new File(htmlUrl.substring(0, htmlUrl.lastIndexOf("/")));
            if(!htmlPath.exists()) {
                htmlPath.mkdirs();
            }
            File baseUrl = new File(filePath);
            HWPFDocument wordDocument = new HWPFDocument(new FileInputStream(baseUrl));
            org.w3c.dom.Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(document);
            //保存图片，并返回图片的相对路径
            wordToHtmlConverter.setPicturesManager((content, pictureType, name, width, height) -> {
                try (FileOutputStream out = new FileOutputStream(imagePath + name)) {
                    out.write(content);
                } catch (Exception e) {
                    e.printStackTrace();

                }
                return "image/" + name;
            });
            wordToHtmlConverter.processDocument(wordDocument);
            org.w3c.dom.Document htmlDocument = wordToHtmlConverter.getDocument();
            DOMSource domSource = new DOMSource(htmlDocument);
            StreamResult streamResult = new StreamResult(new File(htmlUrl));
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer serializer = tf.newTransformer();
            serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.METHOD, "html");
            serializer.transform(domSource, streamResult);
            return htmlUrl;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        docToHtml("D:/ocpp/messageFtpDownload/重大气象信息专报[2015]第2期-2015年春运天气趋势预测.doc".replaceAll("\\\\","/"));

    }
}
