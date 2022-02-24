package com.gsclimbing.database.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gsclimbing.database.entity.Turbine;

public interface TurbineRepository extends JpaRepository<Turbine, Integer>{

	public List<Turbine> findByProjectId(int projectId);
}
