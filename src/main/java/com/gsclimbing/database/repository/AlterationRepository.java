package com.gsclimbing.database.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gsclimbing.database.entity.Alteration;

public interface AlterationRepository extends JpaRepository<Alteration, Integer>{
	List<Alteration> findByIdHistoricReport(int idHistoricReport);
}
