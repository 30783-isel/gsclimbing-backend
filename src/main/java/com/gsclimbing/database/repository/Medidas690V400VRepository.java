package com.gsclimbing.database.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.gsclimbing.database.entity.Medidas690V400V;

public interface Medidas690V400VRepository extends CrudRepository<Medidas690V400V, Integer>{
	
	
	@Query("SELECT c FROM Medidas690V400V c WHERE (:site is null or c.site = :site) and (:wtgNumber is null or c.wtgNumber = :wtgNumber)  and (:wtgType is null or c.wtgType = :wtgType)")
	List<Medidas690V400V> findBySiteAndWtgNumberAndWtgType(@Param(value = "site") String site, @Param(value = "wtgNumber") String wtgNumber, @Param(value = "wtgType") String wtgType);

	List<Medidas690V400V> findByTurbineId(String turbineId);
	
}
