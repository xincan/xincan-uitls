package com.xincan.utils.doc;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.model.PicturesTable;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Picture;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @ClassName Test
 * @Description TODO
 * @Author Xincan
 * @Version 1.0
 **/
@Slf4j
public class Test {

    /**
     * 回车符ASCII码
     */
    private static final short ENTER_ASCII = 13;

    /**
     * 空格符ASCII码
     */
    private static final short SPACE_ASCII = 32;

    /**
     * 水平制表符ASCII码
     */
    private static final short TABULATION_ASCII = 9;

    private static String html = "";

    private static boolean compareCharStyle(CharacterRun cr1, CharacterRun cr2) {
        boolean flag = false;
        if (cr1.isBold() == cr2.isBold() && cr1.isItalic() == cr2.isItalic()
                && cr1.getFontName().equals(cr2.getFontName()) && cr1.getFontSize() == cr2.getFontSize()) {
            flag = true;
        }
        return flag;
    }

    /**
     * 保存文件
     * @param html      解析后的html内容
     * @param filePath  将要存放的文件路径
     * @param basePath  根路径
     * @return
     */
    private static boolean writeFile(String html, String filePath, String basePath) {
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        html = html.replaceAll("EMBED", "").replaceAll("Equation.DSMT4", "");
        try {
//            makeDir(projectRealPath, tempPath);// 创建文件夹
            File file = new File(filePath);
            if (file.exists()) {
                return false;
            }
            fos = new FileOutputStream(file);
            bw = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
            bw.write(html);
            // System.out.println(filePath + "文件写入成功！");
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fos != null)
                    fos.close();
            } catch (IOException ie) {
                ie.printStackTrace();
            }
        }
        return true;
    }

    /**
     *
     * 读写文档中的图片
     * @param pTable    图片表格
     * @param cr
     * @param imagePath  图片保存路径
     * @throws Exception
     */
    private static void readPicture(PicturesTable pTable, CharacterRun cr, String imagePath) throws Exception {

        // 提取图片
        Picture pic = pTable.extractPicture(cr, false);

        BufferedImage image = null;// 图片对象
        // 获取图片样式
//        int picHeight = pic.getHeight() * pic.getAspectRatioY() / 100;
//        int picWidth = pic.getAspectRatioX() * pic.getWidth() / 100;

//        int picHeight = pic.getHeight() / 100;
//        int picWidth = pic.getWidth() / 100;

        int picHeight = pic.getHeight();
        int picWidth = pic.getWidth();
        if (picWidth > 500) {
            picHeight = 500 * picHeight / picWidth;
            picWidth = 500;
        }
        String style = "style='height:" + picHeight + "px;width:" + picWidth + "px'";

        // 返回POI建议的图片文件名
        String imageName = pic.suggestFullFileName();

        // 获取文件信息
        File imageFile = new File(imagePath + imageName);

        int picSize = cr.getFontSize();
        int myHeight = 0;

        if (imageName.indexOf(".wmf") > 0) {
            OutputStream out = new FileOutputStream(imageFile);
            out.write(pic.getContent());
            out.close();
//            afileName = Wmf2Png.convert(projectRealPath + directory + afileName);
            int pHeight = image.getHeight();
            int pWidth = image.getWidth();
            if (pWidth > 500) {
                html += "<img style='width:" + pWidth + "px; height:" + myHeight + "px；'" + " src='" +  imageFile.getPath() + "' />";
            } else {
                myHeight = (int) (pHeight / (pHeight / (picSize * 1.0)) * 1.5);
                html += "<img style='vertical-align:middle; width:" + picSize * 1.5 + "px; height:" + myHeight + "px'  src='" +  imageFile.getPath() + "' />";
            }
        } else {
            OutputStream out = new FileOutputStream(imageFile);
            out.write(pic.getContent());
            out.close();
            // 处理jpg或其他（即除png外）
            if (imageName.indexOf(".png") == -1) {
                try {
                    image = ImageIO.read(imageFile);
                    picHeight = image.getHeight();
                    picWidth = image.getWidth();
                    if (picWidth > 500) {
                        picHeight = 500 * picHeight / picWidth;
                        picWidth = 500;
                    }
                    style = " style='height:" + picHeight + "px;width:" + picWidth + "px'";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            html += "<img " + style + " src='" + imageFile.getPath() + "'/>";

            System.out.println("<img " + style + " src='" + imageFile.getPath() + "'/>");
        }
//        if (pic.getWidth() > 450) {
//            html += "<br/>";
//        }
    }

    /**
     * 转换开始
     * @param filePath
     * @return
     */
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

            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(document);
            //保存图片，并返回图片的相对路径
            wordToHtmlConverter.setPicturesManager((content, pictureType, name, width, height) -> {
                try (FileOutputStream out = new FileOutputStream(imagePath + name)) {
                    out.write(content);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("图片保存失败：" + e.getMessage());
                    return null;
                }
                return imagePath + name;
            });
            wordToHtmlConverter.processDocument(wordDocument);
            Document htmlDocument = wordToHtmlConverter.getDocument();
            DOMSource domSource = new DOMSource(htmlDocument);


            String a = htmlDocument.getPrefix();
            System.out.println(a);

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
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {

        String url = "D:/ocpp/messageFtpDownload/地质灾害风险预报[2015年第1期].doc";

        docToHtml(url.replaceAll("\\\\","/"));

    }
}
