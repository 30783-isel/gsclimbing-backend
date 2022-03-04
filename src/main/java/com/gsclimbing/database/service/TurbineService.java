package com.gsclimbing.database.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsclimbing.database.entity.Project;
import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.database.repository.TurbineRepository;

@Service
public class TurbineService {

	@Autowired
	private TurbineRepository turbineRepository;
	
	public Turbine getTurbine(Integer id) {
		return turbineRepository.findById(id).orElse(null);
	}
	
	public List<Turbine> getTurbinesByProject(Project project) {
		return turbineRepository.findByProject(project);
	}
	
	public void createTurbine(Turbine turbine) {
		turbineRepository.save(turbine);
	}
	
	public void updateTurbine(Turbine turbine) {
		turbineRepository.save(turbine);
	}
	
	public void deleteTurbine(Turbine turbine) {
		turbineRepository.delete(turbine);
	}
}
