package com.wellniak.json;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for performing CRUD operations on {@link JsonTest} entities.
 */
public interface JsonTestRepository extends JpaRepository<JsonTest, UUID> {
	/**
	 * Finds all {@link JsonTest} entities where a nested attribute within the JSON data matches a specified value.
	 * <p>
	 * The query targets the nested JSON attribute at path <code>attribute_1_4 -> attribute_2_2</code>.
	 * 
	 * @param value the value to match within the nested JSON attribute
	 * @return a list of {@link JsonTest} entities with matching attribute values
	 */
	@Query(value = "SELECT * FROM Json_Test j WHERE j.data->'attribute_1_4'->>'attribute_2_2' = :value",
			nativeQuery = true)
	List<JsonTest> findByAttributeNestedValue(@Param("value") String value);
}
