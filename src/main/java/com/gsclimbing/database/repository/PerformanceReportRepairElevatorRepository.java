package com.gsclimbing.database.repository;

import com.gsclimbing.database.entity.PerformanceReportRepairElevator;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PerformanceReportRepairElevatorRepository extends CrudRepository<PerformanceReportRepairElevator, Integer>{
	
	@Query("SELECT c FROM PerformanceReportRepairElevator c WHERE (:site is null or c.site = :site) and (:wtgNumber is null or c.wtgNumber = :wtgNumber)  and (:wtgType is null or c.wtgType = :wtgType) and (:yearConstruction is null or c.yearConstruction = :yearConstruction)")
	List<PerformanceReportRepairElevator> findBySiteAndWtgNumberAndWtgTypeAndYearConstruction(@Param(value = "site") String site, @Param(value = "wtgNumber") String wtgNumber, @Param(value = "wtgType") String wtgType, @Param(value = "wtgType") String yearConstruction);

	List<PerformanceReportRepairElevator> findByTurbineId(String turbineId);
	
}
