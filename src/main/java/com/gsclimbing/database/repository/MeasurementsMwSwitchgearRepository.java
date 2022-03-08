package com.gsclimbing.database.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.gsclimbing.database.entity.MeasurementsMwSwitchgear;

public interface MeasurementsMwSwitchgearRepository extends CrudRepository<MeasurementsMwSwitchgear, Integer>{
	
	
	@Query("SELECT c FROM MeasurementsMwSwitchgear c WHERE (:site is null or c.site = :site) and (:wtgNumber is null or c.wtgNumber = :wtgNumber)  and (:wtgType is null or c.wtgType = :wtgType) and (:yearConstruction is null or c.yearConstruction = :yearConstruction)")
	List<MeasurementsMwSwitchgear> findBySiteAndWtgNumberAndWtgType(@Param(value = "site") String site, @Param(value = "wtgNumber") String wtgNumber, @Param(value = "wtgType") String wtgType, @Param(value = "yearConstruction") String yearConstruction);

	List<MeasurementsMwSwitchgear> findByTurbineId(String turbineId);
	
}
