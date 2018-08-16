package com.xincan.utils.ftp;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.TimeZone;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;


import com.alibaba.fastjson.JSONArray;

/**
  * FTP操作工具类
  * @ClassName: FTPUtil
  * @author JiangXincan
  *
  */
public class FTPUtil {

	    private static FTPClient ftpClient = new FTPClient();

	    /**
	      *
	      * ftpLogin(连接并登录FTP服务器)
	      *
	      * @author JiangXincan
	      * @Title: ftpLogin
	      * @param @return    设定文件
	      * @return boolean    返回类型
	     */
		public static boolean ftpLogin(FTPConfig config) {
	        boolean isLogin = false;
	        FTPClientConfig ftpClientConfig = new FTPClientConfig(FTPClientConfig.SYST_NT);
	        ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());
	        try {
		        ftpClient.setControlEncoding("UTF-8");
		        ftpClient.configure(ftpClientConfig);
	            if (config.getPort() > 0) {
	                ftpClient.connect(config.getUrl(), config.getPort());
	            } else {
	                ftpClient.connect(config.getUrl());
	            }
	            // FTP服务器连接回答
	            int reply = ftpClient.getReplyCode();
	            if (!FTPReply.isPositiveCompletion(reply)) {
	                ftpClient.disconnect();
	                return isLogin;
	            }
	            ftpClient.login(config.getUserName(), config.getPassword());
	            ftpClient.changeWorkingDirectory(config.getPath());
	            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	            System.out.println("成功登陆FTP服务器：" + config.getUrl() + " 端口号：" + config.getPort() + " 目录：" + config.getPath());
	            isLogin = true;
	        } catch (SocketException e) {
	            e.printStackTrace();
	            System.out.println("连接FTP服务失败！");
	            System.out.println(e.getMessage());
	        } catch (IOException e) {
	            e.printStackTrace();
	            System.out.println("登录FTP服务失败！");
	            System.out.println(e.getMessage());
	        }
	        ftpClient.setBufferSize(1024 * 20);
	        ftpClient.setDataTimeout(30);
	        return isLogin;
	    }

	    /**
	      *
	      * close(退出并关闭FTP连接  )
	      *
	      * @author JiangXincan
	      * @Title: close
	      * @return void    返回类型
	      * @throws
	     */
	    public static void close() {
	        if (null != ftpClient && ftpClient.isConnected()) {
	            try {
	                boolean reuslt = ftpClient.logout();// 退出FTP服务器
	                if (reuslt) {
	                	 System.out.println("退出并关闭FTP服务器的连接");
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	                System.out.println("退出FTP服务器异常！");
	                System.out.println(e.getMessage());
	            } finally {
	                try {
	                    ftpClient.disconnect();// 关闭FTP服务器的连接
	                } catch (IOException e) {
	                    e.printStackTrace();
	                    System.out.println("关闭FTP服务器的连接异常！");
	                    System.out.println(e.getMessage());
	                }
	            }
	        }
	    }

	    /**
	      *
	      * isOpenFTPConnection(检查FTP服务器是否关闭 ，如果关闭接则连接登录FTP  )
	      *
	      * @author JiangXincan
	      * @Title: isOpenFTPConnection
	      * @param @return    设定文件
	      * @return boolean    返回类型
	     */
	    public static boolean isOpenFTPConnection(FTPConfig config) {
	        boolean isOpen = false;
	        if (null == ftpClient) {
	            return false;
	        }
	        try {
	            // 没有连接
	            if (!ftpClient.isConnected()) {
	                isOpen = ftpLogin(config);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.out.println("FTP服务器连接登录异常！");
	            System.out.println(e.getMessage());
	            isOpen = false;
	        }
	        return isOpen;
	    }

	    /**
	      *
	      * setFileType(设置传输文件的类型[文本文件或者二进制文件])
	      *
	      * @author JiangXincan
	      * @Title: setFileType
	      * @param @param fileType    FTPClient.BINARY_FILE_TYPE,FTPClient.ASCII_FILE_TYPE
	      * @return void    返回类型
	     */
	    public static void setFileType(int fileType) {
	        try {
	            ftpClient.setFileType(fileType);
	        } catch (IOException e) {
	            e.printStackTrace();
	            System.out.println("设置传输文件的类型异常！");
	            System.out.println(e.getMessage());
	        }
	    }

	/**
	 *
	 * downloadFile(下载文件  )
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
			success = ftpClient.retrieveFile(remoteFileName, outStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
	      * downloadFile(下载文件 )
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
	          //add by jiangxincan 处理ftp上文件不存在，却创建1kb录音文件的问题
	            //ftpClient.
	            String[] strs = remoteFileName.split("\\\\");
	            String fileName = strs[strs.length-1];
	            String filePath = "";
	            for(int i=0;i<strs.length-1;i++){
	            	filePath +=  strs[i]+"/";
	            }
	            String[] ftpFiles = ftpClient.listNames(filePath);
	            if(ftpFiles!=null&&ftpFiles.length>0){
	            	for(String ftpFile:ftpFiles){
	            		if(ftpFile.contains(fileName)){
	            			success = ftpClient.retrieveFile(remoteFileName, outStream);
	            			break;
	            		}
	            	}

	            }else{

					System.out.println("消息通知：ftp服务器不存在文件：【"+ remoteFileName + "】");
	            }
	            //success = ftpClient.retrieveFile(remoteFileName, outStream);
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();


				System.out.println("消息通知：ftp服务器不存在文件：【"+ remoteFileName + "】");
	        } catch (IOException e) {
	            e.printStackTrace();
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
	      * uploadFile
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
	        boolean success = false;
	        try {
	            inStream = new BufferedInputStream(new FileInputStream(localFilePath));
	            success = ftpClient.storeFile(remoteFileName, inStream);
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (inStream != null) {
	                try {
	                    inStream.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	        return success;
	    }

	/**
	 *
	 * uploadFile(上传文件)
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
		boolean success = false;
		try {
			inStream = new BufferedInputStream(new FileInputStream(localFile));
			success = ftpClient.storeFile(remoteFileName, inStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return success;
	}

	/**
	      *
	      * changeDir(变更工作目录)
	      *
	      * @author JiangXincan
	      * @Title: changeDir
	      * @param @param remoteDir   目录路径
	      * @return void    返回类型
	     */
	    public static void changeDir(String remoteDir) {
	        try {
	            ftpClient.changeWorkingDirectory(remoteDir);
	            System.out.println("变更工作目录为:" + remoteDir);
	        } catch (IOException e) {
	            e.printStackTrace();
	            System.out.println("变更工作目录为:" + remoteDir + "时出错！");
	            System.out.println(e.getMessage());
	        }
	    }

	    /**
	      *
	      * changeDir(变更工作目录 )
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
	            System.out.println("变更工作目录为:" + dir);
	        } catch (IOException e) {
	            e.printStackTrace();
	            System.out.println("变更工作目录为:" + dir + "时出错！");
	            System.out.println(e.getMessage());
	        }
	    }

	    /**
	      *
	      * toParentDir(返回上级目录 )
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
	            System.out.println("返回上级目录");
	        } catch (IOException e) {
	            e.printStackTrace();
	            System.out.println("返回上级目录时出错！");
	            System.out.println(e.getMessage());
	        }
	    }

	    /**
	      *
	      * toParentDir(返回上级目录)
	      *
	      * @author JiangXincan
	      * @Title: toParentDir
	      * @return void    返回类型
	     */
	    public static void toParentDir() {
	        try {
	            ftpClient.changeToParentDirectory();
	            System.out.println("返回上级目录");
	        } catch (IOException e) {
	            e.printStackTrace();
	            System.out.println("返回上级目录时出错！");
	            System.out.println(e.getMessage());
	        }
	    }

	    /**
	     * 获得FTP 服务器下所有的文件名列表
	     * @return
	     */
	    public static JSONArray getListFiels() {
	        JSONArray json = new JSONArray();
	        try {
	            String[] array = ftpClient.listNames();
	            if(array == null || array.length<=0){
	            	return null;
	            }
	            for(int i = 0; i<array.length; i++){
	            	json.add(array[i]);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return json;
	    }

	    /**
	     *
	      * deleteFile(删除文件)
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
	            System.out.println("删除文件"+fileName+"，删除状态："+flag+"，删除文件失败");
	        }
	        return flag;
	    }

	    /**
	      *
	      * deleteDir(删除FTP 上指定的文件夹)
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
	            System.out.println("删除文件夹："+fileName+"，删除状态："+flag+"，删除文件夹失败");
	        }
	        return flag;
	    }

	    /**
	      *
	      * iterateDelete(删除文件夹)
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

	    public static void main(String[] args){
	    	FTPConfig config = new FTPConfig(
	    			"192.168.1.123",21,"warn",
					"ftptest","123456",null
			);
	    	ftpLogin(config);
			File file = new File("D:\\images\\channel\\01-短信.png");
	    	uploadFile("D:\\images\\channel\\01-短信.png", "短信.png");
//	    	File file = new File("G:/img/xincan.png");
//	    	uploadFile(file, "xincan.png");
//	    	JSONArray files = getListFiels();
//	    	if(files!=null){
//	    		for(int i = 0; i<files.size(); i++){
//	    			downloadFile("C:/wechat/"+files.get(i).toString(), files.get(i).toString());
//	    		}
//	    	}
//
//	    	changeDir(new String[]{"/pic"});
//	    	deleteFile("xincan.png");
//	    	deleteDir("apache-tomcat-8.0.26");
//	    	changeDir();
	    	close();
	    }

}
