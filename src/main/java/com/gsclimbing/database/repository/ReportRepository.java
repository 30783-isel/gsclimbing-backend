package com.gsclimbing.database.repository;

import com.gsclimbing.database.entity.Report;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends CrudRepository<Report, Integer>, QuerydslPredicateExecutor<Report>{
	
	
	@Query("SELECT c FROM Report c WHERE (:site is null or c.site = :site) and (:wtgNumber is null or c.wtgNumber = :wtgNumber)  and (:wtgType is null or c.wtgType = :wtgType)  and (:yearConstruction is null or c.yearConstruction = :yearConstruction)")
	List<Report> findBySiteAndWtgNumberAndWtgTypeAndYearConstruction(@Param(value = "site") String site, @Param(value = "wtgNumber") String wtgNumber, @Param(value = "wtgType") String wtgType, @Param(value = "yearConstruction") String yearConstruction);

	List<Report> findByTurbineId(String turbineId);

	Optional<Report> findByUuid(String uuid);

	/**
	 * Encontrar todos os relatórios de uma turbina
	 */
	List<Report> findByTurbinaId(Integer turbinaId);

	/**
	 * Encontrar todos os relatórios de um projeto
	 */
	List<Report> findByProjectoId(Integer projectoId);
	
}
