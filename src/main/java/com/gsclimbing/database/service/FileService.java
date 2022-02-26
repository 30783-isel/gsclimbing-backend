package com.gsclimbing.database.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.repository.FileRepository;

@Service
public class FileService {

	
	@Autowired
	private FileRepository fileRepository;

	public void createFile(FileData file) {
		fileRepository.save(file);
	}

	public Optional<FileData> readFile(Integer id) {
		return fileRepository.findById(id);
	}

	public List<FileData> readAllFile() {
		List<FileData> files = new ArrayList<FileData>();
		fileRepository.findAll().forEach(files::add);
		return files;
	}

	public void updateFile(Integer id, FileData file) {
		fileRepository.save(file);
	}

	public void deleteFile(Integer id) {
		fileRepository.deleteById(id);
	}
	
	public List<FileData> readFile(String uuid) {
		return fileRepository.findByUuid(uuid);
	}
	
	public FileData readFileByHash(String hash) {
		return fileRepository.findByHash(hash);
	}
	
	public void saveFile(FileData file) {
		fileRepository.save(file);
	}

	
}


