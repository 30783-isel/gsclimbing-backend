package com.gsclimbing.database.repository;

import com.gsclimbing.database.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Integer>{

	public Project findByName(String name);
}
