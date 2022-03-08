package com.gsclimbing.database.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.gsclimbing.database.entity.StatutoryInspectionReport;

public interface StatutoryInspectionReportRepository extends CrudRepository<StatutoryInspectionReport, Integer>{
	
	
	@Query("SELECT c FROM StatutoryInspectionReport c WHERE (:site is null or c.site = :site) and (:wtgNumber is null or c.wtgNumber = :wtgNumber)  and (:wtgType is null or c.wtgType = :wtgType) and (:yearConstruction is null or c.yearConstruction = :yearConstruction)")
	List<StatutoryInspectionReport> findBySiteAndWtgNumberAndWtgTypeAndYearConstruction(@Param(value = "site") String site, @Param(value = "wtgNumber") String wtgNumber, @Param(value = "wtgType") String wtgType, @Param(value = "wtgType") String yearConstruction);

	List<StatutoryInspectionReport> findByTurbineId(String turbineId);
	
}
