package com.xincan.utils.file;


import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 文件处理工具
 */
public class FileUtil {

    /**
     * 判断文件夹是否存在，如果不存在则创建,并返回将要创建的文件路径
     * @param path
     * @param fileName
     * @return
     */
    public static File isDirectory(String path, String fileName) {
        // 创建文件对象
       File fileDir = new File(path);
       if(!fileDir.isDirectory()){
           fileDir.mkdir();
       }
       return new File(fileDir.getPath(), fileName);
    }

    /**
     * 通过指定路径和文件名来获取文件对象，当文件不存在时自动创建
     * @param path
     * @param fileName
     * @return
     * @throws IOException
     */
    public static File getFile(String path, String fileName) {
        // 创建文件对象
        try {
            File file;
            if (path != null && !path.equals("")) {
                file = new File(path, fileName);
            }else {
                file = new File(fileName);
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            // 返回文件
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得指定文件的输出流
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    public static FileOutputStream getFileStream(File file) throws FileNotFoundException {
        return new FileOutputStream(file);
    }

    /**
     * 将多个文件压缩
     * @param fileList 待压缩的文件列表
     * @param zipFileName 压缩文件名
     * @return 返回压缩好的文件
     * @throws IOException
     */
    public static File getZipFile(List<File> fileList, String zipSaveUrl, String zipFileName) {
        try{
            File zipFile = getFile(zipSaveUrl, zipFileName);
            // 文件输出流
            FileOutputStream outputStream = getFileStream(zipFile);
            // 压缩流
            ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

            int size = fileList.size();
            // 压缩列表中的文件
            for (int i = 0;i < size;i++) {
                File file = fileList.get(i);
                zipFile(file, zipOutputStream);
            }
            // 关闭压缩流、文件流
            zipOutputStream.close();
            outputStream.close();
            return zipFile;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将文件数据写入文件压缩流
     * @param file 带压缩文件
     * @param zipOutputStream 压缩文件流
     * @throws IOException
     */
    private static void zipFile(File file, ZipOutputStream zipOutputStream){
        try{
            if (file.exists()) {
                if (file.isFile()) {
                    FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    ZipEntry entry = new ZipEntry(file.getName());
                    zipOutputStream.putNextEntry(entry);

                    final int MAX_BYTE = 10 * 1024 * 1024; // 最大流为10MB
                    long streamTotal = 0; // 接收流的容量
                    int streamNum = 0; // 需要分开的流数目
                    int leaveByte = 0; // 文件剩下的字符数
                    byte[] buffer; // byte数据接受文件的数据

                    streamTotal = bis.available(); // 获取流的最大字符数
                    streamNum = (int) Math.floor(streamTotal / MAX_BYTE);
                    leaveByte = (int) (streamTotal % MAX_BYTE);

                    if (streamNum > 0) {
                        for (int i = 0;i < streamNum;i++) {
                            buffer = new byte[MAX_BYTE];
                            bis.read(buffer, 0, MAX_BYTE);
                            zipOutputStream.write(buffer, 0, MAX_BYTE);
                        }
                    }

                    // 写入剩下的流数据
                    buffer = new byte[leaveByte];
                    bis.read(buffer, 0, leaveByte); // 读入流
                    zipOutputStream.write(buffer, 0, leaveByte); // 写入流
                    zipOutputStream.closeEntry(); // 关闭当前的zip entry

                    // 关闭输入流
                    bis.close();
                    fis.close();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
