package com.xincan.utils.doc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.PicturesTable;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.hwpf.usermodel.TableRow;


/**
 *

 * @Description:Word试卷文档模型化解析

 * @author <a href="mailto:thoslbt@163.com">Thos</a> 42  * @ClassName: WordToHtml 44  * @version V1.0
 *
 */
public class WordToHtml {

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

    public static String htmlText = "";
    public static String htmlTextTbl = "";
    public static int counter=0;
    public static int beginPosi=0;
    public static int endPosi=0;
    public static int beginArray[];
    public static int endArray[];
    public static String htmlTextArray[];
    public static boolean tblExist=false;

    public static void main(String argv[])
    {
        try {
            String inputFile= "D:/ocpp/messageFtpDownload/地质灾害风险预报[2015年第1期].doc";
            String basePath = inputFile.substring(0,inputFile.lastIndexOf("/"));

            File htmlFilePath = new File(basePath + "/html/");
            if(!htmlFilePath.exists()){
                htmlFilePath.mkdirs();
            }

            File imageFilePath = new File(basePath + "/image/");
            if(!imageFilePath.exists()){
                imageFilePath.mkdirs();
            }

            getWordAndStyle(inputFile, htmlFilePath.getPath(), imageFilePath.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取每个文字样式
     *
     * @param fileName
     * @throws Exception
     */


    public static void getWordAndStyle(String fileName, String htmlFilePath, String imageFilePath) throws Exception {
        FileInputStream in = new FileInputStream(new File(fileName));
        HWPFDocument doc = new HWPFDocument(in);

        Range rangetbl = doc.getRange();//得到文档的读取范围
        TableIterator it = new TableIterator(rangetbl);

        int num=100;

        beginArray=new int[num];
        endArray=new int[num];
        htmlTextArray=new String[num];

        // 取得文档中字符的总数
        int length = doc.characterLength();

        // 创建图片容器
        PicturesTable pTable = doc.getPicturesTable();

        htmlText = "<html><head><title>" + doc.getSummaryInformation().getTitle() + "</title></head><body>";
        // 创建临时字符串,好加以判断一串字符是否存在相同格式

        if(it.hasNext())
        {
            readTable(it,rangetbl);
        }

        int cur=0;

        String tempString = "";
        for (int i = 0; i < length - 1; i++) {
            // 整篇文章的字符通过一个个字符的来判断,range为得到文档的范围
            Range range = new Range(i, i + 1, doc);

            CharacterRun cr = range.getCharacterRun(0);

            if(tblExist)
            {
                if(i==beginArray[cur])
                {
                    htmlText+=tempString+htmlTextArray[cur];
                    tempString="";
                    i=endArray[cur]-1;
                    cur++;
                    continue;
                }
            }
            if (pTable.hasPicture(cr)) {
                htmlText +=  tempString ;
                // 读写图片
                readPicture(pTable, cr, imageFilePath);
                i = i +1;
                tempString = "";
            } else {

                Range range2 = new Range(i + 1, i + 2, doc);
                // 第二个字符
                CharacterRun cr2 = range2.getCharacterRun(0);
                char c = cr.text().charAt(0);

                // 判断是否为空格符
                if (c == SPACE_ASCII)
                    tempString += "&nbsp;";
                    // 判断是否为水平制表符
                else if (c == TABULATION_ASCII)
                    tempString += "&nbsp;&nbsp;&nbsp;&nbsp;";
                // 比较前后2个字符是否具有相同的格式
                boolean flag = compareCharStyle(cr, cr2);
                if (flag&&c !=ENTER_ASCII)
                    tempString += cr.text();
                else {
                    String fontStyle = "font-family:" + cr.getFontName() + ";font-size:" + cr.getFontSize() / 2 + "pt;color:"+getHexColor(cr.getIco24())+";";

                    if (cr.isBold())
                        fontStyle += "font-weight:bold;";
                    if (cr.isItalic())
                        fontStyle += "font-style:italic;";

                    htmlText += "<span style='" + fontStyle + "' >" + tempString + cr.text() + "</span>";
                    tempString = "";
                }
                // 判断是否为回车符
                if (c == ENTER_ASCII)
                    htmlText += "<br/>";

            }
        }

        htmlText += tempString+"</body></html>";
        //生成html文件
        writeFile(htmlText, fileName);
        System.out.println("------------WordToHtml转换成功----------------");
    }

    /**
     * 读写文档中的表格
     *
     * @throws Exception
     */
    public static void readTable(TableIterator it, Range rangetbl) throws Exception {

        htmlTextTbl="";
        //迭代文档中的表格

        counter=-1;
        while (it.hasNext())
        {
            tblExist=true;
            htmlTextTbl="";
            Table tb = (Table) it.next();
            beginPosi=tb.getStartOffset() ;
            endPosi=tb.getEndOffset();

            //System.out.println("............"+beginPosi+"...."+endPosi);
            counter=counter+1;
            //迭代行，默认从0开始
            beginArray[counter]=beginPosi;
            endArray[counter]=endPosi;

            htmlTextTbl+="<table border>";
            for (int i = 0; i < tb.numRows(); i++) {
                TableRow tr = tb.getRow(i);

                htmlTextTbl+="<tr>";
                //迭代列，默认从0开始
                for (int j = 0; j < tr.numCells(); j++) {
                    TableCell td = tr.getCell(j);//取得单元格
                    int cellWidth=td.getWidth();

                    //取得单元格的内容
                    for(int k=0;k<td.numParagraphs();k++){
                        Paragraph para =td.getParagraph(k);
                        String s = para.text().toString().trim();
                        if(s=="")
                        {
                            s=" ";
                        }
                        htmlTextTbl += "<td width="+cellWidth+ ">"+s+"</td>";
                    }
                }
            }
            htmlTextTbl+="</table>" ;
            htmlTextArray[counter]=htmlTextTbl;

        } //end while
    }

    /**
     * 读写文档中的图片
     *
     * @param pTable
     * @param cr
     * @throws Exception
     */
    public static void readPicture(PicturesTable pTable, CharacterRun cr, String imageFilePath) throws Exception {
        // 提取图片
        Picture pic = pTable.extractPicture(cr, false);
        // 返回POI建议的图片文件名
        String imageName = pic.suggestFullFileName();
//        File file = new File(imageFilePath + "/" + imageName);
//        System.out.println(file.mkdirs());
        OutputStream out = new FileOutputStream(imageFilePath + "/" + imageName);
        pic.writeImageContent(out);
        htmlText += "<img src='"+ imageFilePath + "/" + imageName + "' mce_src='"+ imageFilePath + "/" + imageName + "' />";
    }


    public static boolean compareCharStyle(CharacterRun cr1, CharacterRun cr2)
    {
        boolean flag = false;
        if (cr1.isBold() == cr2.isBold() && cr1.isItalic() == cr2.isItalic() && cr1.getFontName().equals(cr2.getFontName())
                && cr1.getFontSize() == cr2.getFontSize()&& cr1.getColor() == cr2.getColor())
        {
            flag = true;
        }
        return flag;
    }

    /*** 字体颜色模块start ********/
    public static int red(int c) {
        return c & 0XFF;
    }

    public static int green(int c) {
        return (c >> 8) & 0XFF;
    }

    public static int blue(int c) {
        return (c >> 16) & 0XFF;
    }

    public static int rgb(int c) {
        return (red(c) << 16) | (green(c) << 8) | blue(c);
    }

    public static String rgbToSix(String rgb) {
        int length = 6 - rgb.length();
        String str = "";
        while (length > 0) {
            str += "0";
            length--;
        }
        return str + rgb;
    }


    public static String getHexColor(int color) {
        color = color == -1 ? 0 : color;
        int rgb = rgb(color);
        return "#" + rgbToSix(Integer.toHexString(rgb));
    }
    /** 字体颜色模块end ******/

    /**
     * 写文件
     *
     * @param html
     */
    public static void writeFile(String html, String fileName) {
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        PrintWriter writer = null;
        try {

            String htmlPath = fileName.substring(0, fileName.lastIndexOf("/")) + "/html/" + fileName.substring(fileName.lastIndexOf("/") + 1, fileName.lastIndexOf(".") ) + ".html";

            File file = new File(htmlPath);
            fos = new FileOutputStream(file);
            bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(html);
            bw.close();
            fos.close();
            //编码转换
            writer = new PrintWriter(file, "GB2312");
            writer.write(html);
            writer.flush();
            writer.close();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

}