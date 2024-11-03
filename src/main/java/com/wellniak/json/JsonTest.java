package com.wellniak.json;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JsonTest {
	@Id
	@Builder.Default
	private UUID id = UUID.randomUUID();

	@Type(JsonBinaryType.class)
	@Column(columnDefinition = "jsonb")
	private String data;
}
