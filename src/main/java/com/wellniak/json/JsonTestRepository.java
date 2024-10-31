package com.wellniak.json;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JsonTestRepository extends JpaRepository<JsonTest, UUID> {
	@Query(value = "SELECT * FROM Json_Test j WHERE j.data->'attribute_1_4'->>'attribute_2_2' = :value", nativeQuery = true)
	List<JsonTest> findByAttributeNestedValue(@Param("value") String value);
}