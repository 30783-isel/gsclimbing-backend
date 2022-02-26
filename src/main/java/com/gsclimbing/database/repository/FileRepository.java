package com.gsclimbing.database.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.gsclimbing.database.entity.FileData;

public interface FileRepository  extends CrudRepository<FileData, Integer>{

	public List<FileData> findByUuid(String uuid);
	
	public FileData findByHash(String hash);
}




