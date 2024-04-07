package com.gsclimbing.database.repository;

import com.gsclimbing.database.entity.Project;
import com.gsclimbing.database.entity.Turbine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TurbineRepository extends JpaRepository<Turbine, Integer>{

	public List<Turbine> findByProject(Project project);
}
