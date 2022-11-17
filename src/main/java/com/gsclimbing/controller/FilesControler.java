package com.gsclimbing.controller;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.gsclimbing.ftp.FTPDownloadFiles;
import com.gsclimbing.ftp.FTPUploadFile;
import com.gsclimbing.utils.FileUtils;

@CrossOrigin(origins = "*", methods = { RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })

@Controller
@RequestMapping(path = "/api/files")
public class FilesControler {
	
	private static Logger logger = LoggerFactory.getLogger(FilesControler.class);
	
	@RequestMapping(method = RequestMethod.POST, value = "/upload")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
		try {
			final String uuid = UUID.randomUUID().toString().replace("-", "");
			File file = FileUtils.multipartToFile(multipartFile, uuid);
			boolean inserted = FTPUploadFile.uploadPdfFile2FTPServer(file, uuid, "despesas");
			return ResponseEntity.status(HttpStatus.OK).body("File uploaded");
		} catch (Exception e) {
			return new ResponseEntity<>("Could not upload the file", HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@RequestMapping("/download/{typeDocument}/{fileName}")
	public ResponseEntity<byte[]> getFileByReportId(final @PathVariable String typeDocument, final @PathVariable String fileName) {
		byte[] bytes = null;
		try {
			bytes = IOUtils.toByteArray(FTPDownloadFiles.downloadPdfReportByTeamToTempFile(typeDocument, fileName));
		} catch (Exception e) {
			logger.info(e.toString());
			return new ResponseEntity("Cannot dowload the file", HttpStatus.EXPECTATION_FAILED);
		}
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(bytes);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/teste")
	public ResponseEntity<?> teste() {
		
		try {
		
			return ResponseEntity.status(HttpStatus.OK).body("File uploaded");
		} catch (Exception e) {


			return new ResponseEntity<>("Could not upload the file", HttpStatus.EXPECTATION_FAILED);
		}
	}

}
