package com.xincan.utils.ftp;

/**
 * ftp配置文件实体类
 */
public class FTPConfig {

	// FTP路径
	private String host;

	// FTP端口号
	private int port;

	// FTP文件路径
	private String path;

	// FTP用户名
	private String user;

	// FTP用户密码
	private String password;

	// 下载文件时保存到本地的路径
	private String localPath;

	// 本地生成文件路径
	private String uploadPath;

	public FTPConfig() {}

	public FTPConfig(String host, int port, String path, String user, String password) {
		this.host = host;
		this.port = port;
		this.path = path;
		this.user = user;
		this.password = password;
	}

	public FTPConfig(String host, int port, String path, String user, String password, String localPath, String uploadPath) {
		this.host = host;
		this.port = port;
		this.path = path;
		this.user = user;
		this.password = password;
		this.localPath = localPath;
		this.uploadPath = uploadPath;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
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

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String getUploadPath() {
		return uploadPath;
	}

	public void setUploadPath(String uploadPath) {
		this.uploadPath = uploadPath;
	}
}
