package com.xincan.utils.ftp;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;


/**
  * FTP操作工具类
  * @ClassName: FTPUtil
  * @author JiangXincan
  *
  */
@Slf4j
public class FTPUtil {


	/**
	 * 本地字符编码
	 */
	private static String DEFAULT_CHARSET = "GBK";

	/**
	 * 本地字符编码
	 */
	private static String LOCAL_CHARSET = "UTF-8";

	/**
	 * FTP协议里面，规定文件名编码为ISO-8859-1
	 */
	private static String SERVER_CHARSET = "ISO-8859-1";

	/**
	 * 判断属于那个字符编码 UTF-8:true, GBK：false
	 */
	private static boolean IS_CHARSET = false;

	/**
	 * FTP客户端
	 */
	private static FTPClient ftpClient = null;

	/**
	 *
	 * 连接并登录FTP服务器
	 *
	 * @author JiangXincan
	 * @Title: ftpLogin
	 * @param @return    设定文件
	 * @return boolean    返回类型
	 */
	public static boolean ftpLogin(FTPConfig config) {


		try {
			// 如果没有创建FTP客户端则创建客户端
			if (ftpClient == null)  ftpClient = new FTPClient();
			// 判断FTP是否连接,如果连接则直接返回
			if (ftpClient.isConnected())  return true;
			// 判断FTP是否有端口
			if (config.getPort() > 0) {
				ftpClient.connect(config.getHost(), config.getPort());
			} else {
				ftpClient.connect(config.getHost());
			}
			if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
				// 登录FTP
				if (ftpClient.login(config.getUser(), config.getPassword())) {
					// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
					if (FTPReply.isPositiveCompletion(ftpClient.sendCommand("OPTS UTF8", "ON"))) {
						IS_CHARSET = true;
					}
					ftpClient.setControlEncoding( IS_CHARSET ? LOCAL_CHARSET : DEFAULT_CHARSET);
					ftpClient.changeWorkingDirectory(config.getPath());	// 设置FTP下的文件夹
					ftpClient.enterLocalPassiveMode();					// 设置被动模式
					ftpClient.setFileType(FTP.BINARY_FILE_TYPE);		// 设置传输的模式
//					ftpClient.setBufferSize(4096);						// 设置传输大小
//					ftpClient.setDataTimeout(2000);						// 设置超时
				}
			}
			log.info("成功登陆FTP服务器：" + config.getHost() + " 端口号：" + config.getPort() + " 目录：" + config.getPath());
			return true;
		} catch (SocketException e) {
			e.printStackTrace();
			log.error("连接FTP服务失败！" + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.error("登录FTP服务失败！" + e.getMessage());
		}
		return false;
	}

	/**
	 *
	 * 退出并关闭FTP连接
	 *
	 * @author JiangXincan
	 * @Title: close
	 * @return void    返回类型
	 * @throws
	 */
	public static void close() {
		if (null != ftpClient && ftpClient.isConnected()) {
			try {
				if (ftpClient.logout()) {
					log.info("退出并关闭FTP服务器的连接");
				}
			} catch (IOException e) {
				e.printStackTrace();
				log.error("退出FTP服务器异常！" + e.getMessage());
			} finally {
				try {
					ftpClient.disconnect();// 关闭FTP服务器的连接
				} catch (IOException e) {
					e.printStackTrace();
					log.error("关闭FTP服务器的连接异常！" + e.getMessage());
				}
			}
		}
	}

	/**
	 *
	 * 下载文件
	 *
	 * 下载到指定路径
	 *
	 * @author JiangXincan
	 * @Title: downloadFile
	 * @param @param localFilePath   本地文件名及路径
	 * @param @param remoteFileName  远程文件名称
	 * @param @return    设定文件
	 * @return boolean    返回类型
	 */
	public static boolean downloadFile(String localFilePath, String remoteFileName) {
		BufferedOutputStream outStream = null;
		boolean success = false;
		try {
			File file = new File(localFilePath.substring(0,localFilePath.lastIndexOf("/")));
			if(!file.exists()){
				file.mkdirs();
			}
			outStream = new BufferedOutputStream(new FileOutputStream(localFilePath));
			remoteFileName = new String(remoteFileName.getBytes(IS_CHARSET ? LOCAL_CHARSET : DEFAULT_CHARSET), SERVER_CHARSET);
			success = ftpClient.retrieveFile(remoteFileName, outStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.error("FTP服务器不存在文件：【"+ remoteFileName + "】");
		} catch (IOException e) {
			e.printStackTrace();
			log.error("FTP服务器不存在文件：【"+ remoteFileName + "】");
		} finally {
			if (outStream != null) {
				try {
					outStream.flush();
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return success;
	}

	/**
	 *
	 * 下载文件
	 * 下载到指定文件
	 *
	 * @author JiangXincan
	 * @Title: downloadFile
	 * @param @param localFile      本地文件
	 * @param @param remoteFileName 远程文件名称
	 * @param @return    设定文件
	 * @return boolean    返回类型
	 */
	public static boolean downloadFile(File localFile, String remoteFileName) {
		BufferedOutputStream outStream = null;
		FileOutputStream outStr = null;
		boolean success = false;
		try {
			outStr = new FileOutputStream(localFile);
			outStream = new BufferedOutputStream(outStr);
			remoteFileName = new String(remoteFileName.getBytes(IS_CHARSET ? LOCAL_CHARSET : DEFAULT_CHARSET), SERVER_CHARSET);
			success = ftpClient.retrieveFile(remoteFileName, outStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.error("文件下载失败:" + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.error("文件下载失败:" + e.getMessage());
		} finally {
			try {
				if (null != outStream) {
					try {
						outStream.flush();
						outStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (null != outStr) {
					try {
						outStr.flush();
						outStr.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return success;
	}

	/**
	 *
	 * 文件上传
	 * 根据文件路径上传文件
	 *
	 * @author JiangXincan
	 * @Title: uploadFile
	 * @param @param localFilePath  本地文件路径及名称
	 * @param @param remoteFileName FTP 服务器文件名称
	 * @param @return    设定文件
	 * @return boolean    返回类型
	 */
	public static boolean uploadFile(String localFilePath, String remoteFileName) {
		BufferedInputStream inStream = null;
		try {
			File file = new File(localFilePath);
			inStream = new BufferedInputStream(new FileInputStream(file));
			remoteFileName = new String(remoteFileName.getBytes(IS_CHARSET ? LOCAL_CHARSET : DEFAULT_CHARSET), SERVER_CHARSET);
			return ftpClient.storeFile(remoteFileName, inStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.error("文件上传失败" + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.error("文件上传失败" + e.getMessage());
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 *
	 * 文件上传
	 * 根据文件对象上传文件
	 *
	 * @author JiangXincan
	 * @Title: uploadFile
	 * @param @param localFile      本地文件
	 * @param @param remoteFileName 服务器文件名称
	 * @param @return    设定文件
	 * @return boolean    返回类型
	 */
	public static boolean uploadFile(File localFile, String remoteFileName) {
		BufferedInputStream inStream = null;
		try {
			inStream = new BufferedInputStream(new FileInputStream(localFile));
			remoteFileName = new String(remoteFileName.getBytes(IS_CHARSET ? LOCAL_CHARSET : DEFAULT_CHARSET), SERVER_CHARSET);
			return ftpClient.storeFile(remoteFileName, inStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.error("文件上传失败" + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.error("文件上传失败" + e.getMessage());
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	/**
	 *
	 * 变更工作目录
	 * 单个目录变更
	 * @author JiangXincan
	 * @Title: changeDir
	 * @param @param remoteDir   目录路径
	 * @return void    返回类型
	 */
	public static void changeDir(String remoteDir) {
		try {
			ftpClient.changeWorkingDirectory(remoteDir);
			log.info("变更工作目录为:" + remoteDir);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("变更工作目录为:" + remoteDir + "时出错！" + e.getMessage());
		}
	}

	/**
	 *
	 * 变更工作目录
	 * 多个目录变更
	 *
	 * @author JiangXincan
	 * @Title: changeDir
	 * @param @param remoteDirs    目录路径
	 * @return void    返回类型
	 */
	public static void changeDir(String[] remoteDirs) {
		String dir = "";
		try {
			for (int i = 0; i < remoteDirs.length; i++) {
				ftpClient.changeWorkingDirectory(remoteDirs[i]);
				dir = dir + remoteDirs[i] + "/";
			}
			log.info("变更工作目录为:【" + dir +"】");
		} catch (IOException e) {
			e.printStackTrace();
			log.error("变更工作目录为:【" + dir + "】时出错！" + e.getMessage());
		}
	}

	/**
	 *
	 * 返回根目录
	 *
	 * @author JiangXincan
	 * @Title: toParentDir
	 * @param @param remoteDirs    设定文件
	 * @return void    返回类型
	 */
	public static void toParentDir(String[] remoteDirs) {
		try {
			for (int i = 0; i < remoteDirs.length; i++) {
				 ftpClient.changeToParentDirectory();
			}
			log.info("返回上级目录成功");
		} catch (IOException e) {
			e.printStackTrace();
			log.error("返回上级目录时出错！" + e.getMessage());
		}
	}

	/**
	 *
	 * 返回上级目录
	 *
	 * @author JiangXincan
	 * @Title: toParentDir
	 * @return void    返回类型
	 */
	public static void toParentDir() {
		try {
			ftpClient.changeToParentDirectory();
			log.info("返回上级目录成功");
		} catch (IOException e) {
			e.printStackTrace();
			log.error("返回上级目录时出错！" + e.getMessage());
		}
	}

	/**
	 * 获得FTP 服务器下所有的文件名列表
	 *
	 * @author JiangXincan
	 * @Title: getFileList
	 * @return void    返回类型
	 */
    public static List<String> getFileList() {
		List<String> nameList = new ArrayList<>();
        try {
            String[] array = ftpClient.listNames();
            if(array == null || array.length <= 0) return null;
            for(String name : array){
				nameList.add(name);
			}
        } catch (IOException e) {
            e.printStackTrace();
            log.error("获取FTP文件列表失败" + e.getMessage());
        }
        return nameList;
    }

	/**
	 *
	 * 删除文件
	 *
	 * @author JiangXincan
	 * @Title: deleteFile
	 * @Description: 删除文件
	 * @param @param ftpCommon
	 * @param @param filePath
	 * @return boolean    返回类型
	 * @throws
	 */
	public static boolean deleteFile(String fileName){
		boolean flag = false;
		try {
			flag = ftpClient.deleteFile(fileName);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("删除文件"+fileName+"，删除状态："+flag+"，删除文件失败" + e.getMessage());
		}
		return flag;
	}

	/**
	 *
	 * 删除文件夹
	 * 根据文件名称删除指定文件夹
	 *
	 * @author JiangXincan
	 * @Title: deleteDir
	 * @param @param ftpCommon
	 * @param @param ftpPath
	 * @return boolean    返回类型布尔值
	 * @throws
	 */
	public static boolean deleteDir(String fileName){
		boolean flag = false;
		try {
			flag = iterateDelete(fileName);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("删除文件夹："+fileName+"，删除状态："+flag+"，删除文件夹失败" + e.getMessage());
		}
		return flag;
	}

	/**
	 *
	 * 删除文件夹
	 * 根据文件夹地址删除文件夹
	 *
	 * @author JiangXincan
	 * @Title: iterateDelete
	 * @param @param ftpPath 文件夹的地址
	 * @param @throws IOException
	 * @return boolean    true 表似成功，false 失败
	 */
	public static boolean iterateDelete(String ftpPath) throws IOException{
		// 获取该文件下的所有文件、文件夹
		FTPFile[] files = ftpClient.listFiles(ftpPath);
		boolean flag = false;
		for(FTPFile f:files){
			String path = ftpPath+File.separator+f.getName();
			if(f.isFile()){
				// 是文件就删除文件
				ftpClient.deleteFile(path);
			}else if(f.isDirectory()){
				iterateDelete(path);
			}
		}
		// 每次删除文件夹以后就去查看该文件夹下面是否还有文件，没有就删除该空文件夹
		FTPFile[] files2 = ftpClient.listFiles(ftpPath);
		if(files2.length==0){
			flag = ftpClient.removeDirectory(ftpPath);
		}else{
			flag = false;
		}
		return flag;
	}

	/**
	 * 递归创建远程服务器目录 例如：/安德森/bbb/啊啊啊/ 最后必须有"/"
	 *
	 * @param remote 远程服务器文件绝对路径
	 * @return 目录创建是否成功
	 * @throws IOException
	 */
	public static boolean createFTPDirecroty(String remote) {
		try{
			String directory = remote.substring(0, remote.lastIndexOf("/") + 1);
			if (!directory.equalsIgnoreCase("/")
					&& !ftpClient.changeWorkingDirectory(new String(directory.getBytes("UTF-8"), SERVER_CHARSET))) {
				// 如果远程目录不存在，则递归创建远程服务器目录
				int start = directory.startsWith("/") ? 1 : 0
				,end = directory.indexOf("/", start);
				while (true) {
					String subDirectory = new String(remote.substring(start, end).getBytes("UTF-8"), SERVER_CHARSET);
					if (!ftpClient.changeWorkingDirectory(subDirectory)) {
						if (ftpClient.makeDirectory(subDirectory)) {
							ftpClient.changeWorkingDirectory(subDirectory);
						} else {
							log.info("创建目录失败");
							return false;
						}
					}
					start = end + 1;
					end = directory.indexOf("/", start);
					// 查所有目录是否创建完
					if (end <= start) {
						break;
					}
				}
			}
		}catch (Exception e){
			log.error("创建目录失败" + e.getMessage());
		}
		return true;
	}

	/**
	 * 获取FTP最新文件
	 * @return
	 */
	public static JSONObject getNewFile() {
		JSONObject result = new JSONObject();
		try {
			FTPFile[] files = ftpClient.listFiles();
			Date maxDate = null;
			String remoteFileName = "";
			for(FTPFile file : files){
				if (!file.isFile()) continue;	// 判断是否是文件
				Calendar calendar = file.getTimestamp();
				Date date = calendar.getTime();
				if(maxDate == null){
					maxDate = date;
					remoteFileName = file.getName();
				}else {
					if (maxDate.compareTo(date) < 0) {
						maxDate = date;
						remoteFileName = file.getName();
					}
				}
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			result = new JSONObject();
			result.put("fileName",remoteFileName);
			result.put("fileCreateTime",sdf.format(maxDate));
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			log.error("获取文件失败:" + e.getMessage());
		}
		return result;
	}

	public static void main(String[] args){
		FTPConfig config = new FTPConfig(
				"192.168.1.133",21,"/word",
				"xincan","xincan-0818","D:/ocpp/messageFtpDownload",null
		);
		boolean bool = ftpLogin(config);
		System.out.println(bool);

		// 文件上传（根据路径上传）
//		uploadFile("D:/ocpp/messageFtpDownload/测试.txt", "测试.txt");

		// 文件上传（根据文件对象上传）
//		File file = new File("D:/ocpp/messageFtpDownload/测试安山.txt");
//		uploadFile(file, "测试安.txt");


		// 下载文件（下载到指定路径）
		downloadFile("D:/ocpp/messageFtpDownload/地质灾害风险预报[2015年第1期].doc", "地质灾害风险预报[2015年第1期].doc");

		// 下载文件（下载到指定文件）
//		File file = new File("D:/ocpp/messageFtpDownload/春运专报201503.doc");
//		downloadFile(file, "春运专报201503.doc");

		// 远程创建文件夹
//		createFTPDirecroty("/行数/为/啊啊啊/asdf.jpg");

		// 获取FTP目录下所有文件
//		List<String> list = getFileList();
//		list.forEach(name -> System.out.println(name));

		// 获取最新文件
//		JSONObject result = getNewFile();
//		System.out.println(result);

//
//		changeDir(new String[]{"/pic"});
//		deleteFile("xincan.png");
//		deleteDir("apache-tomcat-8.0.26");
//		changeDir();

		close();
	}

}
