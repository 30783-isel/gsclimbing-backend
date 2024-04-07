package com.gsclimbing.database.repository;

import com.gsclimbing.database.entity.OnboardCraneInspectionReport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OnboardCraneInspectionReportRepository extends CrudRepository<OnboardCraneInspectionReport, Integer>{
	
	
	@Query("SELECT c FROM OnboardCraneInspectionReport c WHERE (:site is null or c.site = :site) and (:wtgNumber is null or c.wtgNumber = :wtgNumber)  and (:wtgType is null or c.wtgType = :wtgType) and (:yearConstruction is null or c.yearConstruction = :yearConstruction)")
	List<OnboardCraneInspectionReport> findBySiteAndWtgNumberAndWtgTypeAndYearConstruction(@Param(value = "site") String site, @Param(value = "wtgNumber") String wtgNumber, @Param(value = "wtgType") String wtgType, @Param(value = "wtgType") String yearConstruction);

	List<OnboardCraneInspectionReport> findByTurbineId(String turbineId);
	
}
