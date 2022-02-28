package com.gsclimbing.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gsclimbing.database.entity.Alteration;

public interface AlterationRepository extends JpaRepository<Alteration, Integer>{}
