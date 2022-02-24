package com.gsclimbing.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gsclimbing.database.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Integer>{

	public Project findByName(String name);
}
