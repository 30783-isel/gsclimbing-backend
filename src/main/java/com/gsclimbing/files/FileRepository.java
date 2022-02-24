package com.gsclimbing.files;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface FileRepository  extends CrudRepository<FileData, Integer>{

	public List<FileData> findByUuid(String uuid);
	
	public FileData findByHash(String hash);
}




