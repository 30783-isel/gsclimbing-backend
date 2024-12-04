package com.gsclimbing.ftp;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;

/**
 * A program demonstrates how to upload files from local computer to a remote
 * FTP server using Apache Commons Net API.
 * 
 * @author www.codejava.net
 */
public class FTPDownloadFiles {

	public static byte[] downloadFile2FTPServer(String name) {
		String server = "localhost";
		int port = 2121;
		String user = "admin";
		String pass = "admin";

		FTPClient ftpClient = new FTPClient();
		byte[] bytes = null;
		try {

			ftpClient.connect(server, port);
			ftpClient.login(user, pass);
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			String remoteFile1 = name;
			InputStream inputStream = ftpClient.retrieveFileStream(remoteFile1);
			bytes = IOUtils.toByteArray(inputStream);

			if (bytes != null && bytes.length > 0) {
				return bytes;
			}

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
		return bytes;
	}

	public static void deleteFile2FTPServer(String name) {
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

			ftpClient.deleteFile(name);

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

	public static byte[] downloadPdfReportFromFTPServer(String name, String numberImages, String directory) {
		String server = "localhost";
		int port = 2121;
		String user = "admin";
		String pass = "admin";

		FTPClient ftpClient = new FTPClient();
		byte[] bytes = null;
		try {

			ftpClient.connect(server, port);
			ftpClient.login(user, pass);
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			// "performanceReportRepairElevator/"
			// "defectInspectionReport/"
			ftpClient.changeWorkingDirectory(directory);
			ftpClient.changeWorkingDirectory(numberImages);

			String remoteFile1 = name;
			InputStream inputStream = ftpClient.retrieveFileStream(remoteFile1);
			bytes = IOUtils.toByteArray(inputStream);

			if (bytes != null && bytes.length > 0) {
				return bytes;
			}

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
		return bytes;
	}

	public static byte[] downloadImageFromOldImages(String name, int imageChange) {
		String server = "localhost";
		int port = 2121;
		String user = "admin";
		String pass = "admin";

		FTPClient ftpClient = new FTPClient();
		byte[] bytes = null;
		try {

			ftpClient.connect(server, port);
			ftpClient.login(user, pass);
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			String path = "/oldImages/" + imageChange;

			boolean isDirectoryChanged = ftpClient.changeWorkingDirectory(path);

			FTPFile[] listFiles = ftpClient.listFiles();

			InputStream inputStream = ftpClient.retrieveFileStream(path + "/" + name);
			if (inputStream != null) {
				bytes = IOUtils.toByteArray(inputStream);
			} else {
				return null;
			}

			if (bytes != null && bytes.length > 0) {
				return bytes;
			}

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
		return bytes;
	}

	public static InputStream downloadPdfReportByTeamToTempFile(String path, String fileName) {
		String server = "localhost";
		int port = 2121;
		String user = "admin";
		String pass = "admin";
		InputStream inputStream = null;
		FTPClient ftpClient = new FTPClient();

		try {

			ftpClient.connect(server, port);
			Boolean login = ftpClient.login(user, pass);
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			boolean isDirectoryChanged = ftpClient.changeWorkingDirectory(path);

			String remoteFile2 = null;
			remoteFile2 = path + fileName;

			File downloadFile2 = File.createTempFile(fileName, null);
			OutputStream outputStream2 = new BufferedOutputStream(new FileOutputStream(downloadFile2));
			inputStream = ftpClient.retrieveFileStream(remoteFile2);

			//boolean success = ftpClient.completePendingCommand();

			outputStream2.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (ftpClient.isConnected()) {
					ftpClient.disconnect();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return inputStream;
	}

}
