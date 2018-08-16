package com.xincan.utils.ftp;


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
	private String localFile;

	public FTPConfig() {
		super();
	}

	public FTPConfig(String url, int port, String path, String userName, String password, String localFile) {
		super();
		this.url = url;
		this.port = port;
		this.path = path;
		this.userName = userName;
		this.password = password;
		this.localFile = localFile;
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

	public String getLocalFile() {
		return localFile;
	}

	public void setLocalFile(String localFile) {
		this.localFile = localFile;
	}

}
