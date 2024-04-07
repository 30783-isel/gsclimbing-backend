package com.gsclimbing.database.repository;

import com.gsclimbing.database.entity.FileData;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FileRepository  extends CrudRepository<FileData, Integer>{

	public List<FileData> findByUuid(String uuid);
	
	public FileData findByHash(String hash);
}




