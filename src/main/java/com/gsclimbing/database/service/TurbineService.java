package com.gsclimbing.database.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsclimbing.database.entity.Turbine;
import com.gsclimbing.database.repository.TurbineRepository;

@Service
public class TurbineService {

	@Autowired
	private TurbineRepository turbineRepository;
	
	public Optional<Turbine> getTurbine(int id) {
		return turbineRepository.findById(id);
	}
	
	public List<Turbine> getTurbinesByProjectId(int projectId) {
		return turbineRepository.findByProjectId(projectId);
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
