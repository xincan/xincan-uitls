package com.xincan.utils.ftp;

/**
 * ftp配置文件实体类
 */
public class FTPConfig {

	// FTP路径
	private String url;

	// FTP端口号
	private int port;

	// FTP文件路径
	private String path;

	// FTP用户名
	private String userName;

	// FTP用户密码
	private String password;

	// 下载文件时保存到本地的路径
	private String downloadLocalFile;

	// 本地生成文件路径
	private String uploadFtpFile;

	public FTPConfig() {}

	public FTPConfig(String url, int port, String path, String userName, String password) {
		this.url = url;
		this.port = port;
		this.path = path;
		this.userName = userName;
		this.password = password;
	}


	public FTPConfig(String url, int port, String path, String userName, String password, String downloadLocalFile, String uploadFtpFile) {
		this.url = url;
		this.port = port;
		this.path = path;
		this.userName = userName;
		this.password = password;
		this.downloadLocalFile = downloadLocalFile;
		this.uploadFtpFile = uploadFtpFile;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDownloadLocalFile() {
		return downloadLocalFile;
	}

	public void setDownloadLocalFile(String downloadLocalFile) {
		this.downloadLocalFile = downloadLocalFile;
	}

	public String getUploadFtpFile() {
		return uploadFtpFile;
	}

	public void setUploadFtpFile(String uploadFtpFile) {
		this.uploadFtpFile = uploadFtpFile;
	}
}
