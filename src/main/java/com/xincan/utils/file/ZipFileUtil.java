package com.xincan.utils.file;

import com.github.junrar.Archive;
import com.github.junrar.Junrar;
import com.github.junrar.exception.RarException;
import com.github.junrar.impl.FileVolumeManager;
import com.github.junrar.rarfile.FileHeader;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class ZipFileUtil {


    /**
     * 解压文件
     *
     * 类型：zip
     * @Title jieyaZip
     *
     * @param filePath
     * @return
     */
    public static boolean jieyaZip(String filePath){

        try {
            ZipInputStream Zin = new ZipInputStream(new FileInputStream(filePath));//输入源zip路径
            BufferedInputStream Bin = new BufferedInputStream(Zin);
            String outPath = filePath.substring(0,filePath.lastIndexOf("/")); //输出路径（文件夹目录）
            System.out.println(outPath);
            File file = null;
            ZipEntry entry;
            try {
                while((entry = Zin.getNextEntry()) != null && !entry.isDirectory()){
                    file= new File(outPath,entry.getName());
                    if(!file.exists()){
                        (new File(file.getParent())).mkdirs();
                    }
                    FileOutputStream out=new FileOutputStream(file);
                    BufferedOutputStream Bout=new BufferedOutputStream(out);
                    int b;
                    while((b=Bin.read())!=-1){
                        Bout.write(b);
                    }
                    Bout.close();
                    out.close();
                    log.info("解压成功，解压路径为：" + outPath);
                }
                Bin.close();
                Zin.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                log.error("解压文件失败,解压文件路径为：" + filePath + "  " + e.getMessage());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("解压文件失败,解压文件路径为：" + filePath + "  " + e.getMessage());
        }

        return false;
    }



    public static void main(String[] args) {
        jieyaZip("D:/ocpp/messageFtpDownload/春运专报201503.zip");
    }

}
