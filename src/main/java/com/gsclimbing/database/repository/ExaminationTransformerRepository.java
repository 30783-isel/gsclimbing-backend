package com.gsclimbing.database.repository;

import com.gsclimbing.database.entity.ExaminationTransformer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExaminationTransformerRepository extends CrudRepository<ExaminationTransformer, Integer>{
	@Query("SELECT c FROM ExaminationTransformer c WHERE (:site is null or c.site = :site) and (:wtgNumber is null or c.wtgNumber = :wtgNumber)  and (:wtgType is null or c.wtgType = :wtgType)")
	List<ExaminationTransformer> findBySiteAndWtgNumberAndWtgType(@Param(value = "site") String site, @Param(value = "wtgNumber") String wtgNumber, @Param(value = "wtgType") String wtgType);
	List<ExaminationTransformer> findByTurbineId(String turbineId);
}
