package com.gsclimbing.historic;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AlterationRepository extends JpaRepository<Alteration, Integer>{

	List<Alteration> findByIdHistoricReport(int idHistoricReport);
	
}
