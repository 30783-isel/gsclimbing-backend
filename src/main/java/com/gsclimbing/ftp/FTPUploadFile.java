package com.gsclimbing.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

public class FTPUploadFile {

	public static boolean uploadFile2FTPServer(File firstLocalFile, String name) {
		String server = "localhost";
		int port = 2121;
		String user = "admin";
		String pass = "admin";
		boolean returnValue = false;

		FTPClient ftpClient = new FTPClient();
		try {

			ftpClient.connect(server, port);
			ftpClient.login(user, pass);
			ftpClient.enterLocalPassiveMode();

			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			String firstRemoteFile = name;
			InputStream inputStream = new FileInputStream(firstLocalFile);

			returnValue = ftpClient.storeFile(firstRemoteFile, inputStream);
			inputStream.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (ftpClient.isConnected()) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return returnValue;
	}

	public static void replaceFile2FTPServer(File temp_file, String hash, int imageChanges) {
		String server = "localhost";
		int port = 2121;
		String user = "admin";
		String pass = "admin";

		FTPClient ftpClient = new FTPClient();
		try {

			ftpClient.connect(server, port);
			ftpClient.login(user, pass);
			ftpClient.enterLocalPassiveMode();

			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			InputStream inputStream = new FileInputStream(temp_file);

			String path = "/oldImages/" + imageChanges + "/" + hash;
			boolean boolRename = ftpClient.rename(hash, path);

			ftpClient.storeFile(hash, inputStream);

			inputStream.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (ftpClient.isConnected()) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static boolean uploadPdfFile2FTPServer(File firstLocalFile, String name, String team) {
		String server = "localhost";
		int port = 2121;
		String user = "admin";
		String pass = "admin";
		boolean inserted = false;
		FTPClient ftpClient = new FTPClient();
		try {
			ftpClient.connect(server, port);
			ftpClient.login(user, pass);
			ftpClient.enterLocalPassiveMode();
			ftpClient.changeWorkingDirectory(team);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			String firstRemoteFile = name;
			InputStream inputStream = new FileInputStream(firstLocalFile);
			inserted = ftpClient.storeFile(firstRemoteFile, inputStream);
			inputStream.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (ftpClient.isConnected()) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return inserted;
	}

	

}
