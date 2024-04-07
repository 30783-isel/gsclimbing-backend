package com.gsclimbing.database.repository;

import com.gsclimbing.database.entity.Medidas690V400V;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface Medidas690V400VRepository extends CrudRepository<Medidas690V400V, Integer>{
	
	
	@Query("SELECT c FROM Medidas690V400V c WHERE (:site is null or c.site = :site) and (:wtgNumber is null or c.wtgNumber = :wtgNumber)  and (:wtgType is null or c.wtgType = :wtgType) and (:yearConstruction is null or c.yearConstruction = :yearConstruction)")
	List<Medidas690V400V> findBySiteAndWtgNumberWtgTypeAndYearConstruction(@Param(value = "site") String site, @Param(value = "wtgNumber") String wtgNumber, @Param(value = "wtgType") String wtgType, @Param(value = "yearConstruction") String yearConstruction);

	List<Medidas690V400V> findByTurbineId(String turbineId);
	
}
