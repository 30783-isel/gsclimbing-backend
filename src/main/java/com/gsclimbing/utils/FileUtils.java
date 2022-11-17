package com.gsclimbing.utils;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public class FileUtils {

	public static File multipartToFile(MultipartFile multipart, String fileName) throws IllegalStateException, IOException {

		File convFile = File.createTempFile(fileName, null);
		multipart.transferTo(convFile);
		return convFile;
	}
	
}
