package com.wellniak.json;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;
import org.hibernate.annotations.Type;

/**
 * Entity representing a JSON-based record stored in the "json_test" table.
 * <p>
 * This class defines a primary key and a JSONB data field, which allows for storing and querying structured JSON data
 * directly in the database.
 * <p>
 * The {@code data} field is stored as a JSONB type using PostgreSQL, providing efficient storage and query capabilities
 * for JSON data.
 */
@Entity
@Table(name = "json_test")
@Data
public class JsonTest {
	@Id
	@GeneratedValue
	private UUID id;

	@Type(JsonBinaryType.class)
	@Column(columnDefinition = "jsonb")
	private String data;
}
