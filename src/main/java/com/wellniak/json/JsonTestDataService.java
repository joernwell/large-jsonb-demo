package com.wellniak.json;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collection;
import org.hibernate.Session;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for batch operations on {@link JsonTest} entities, leveraging JDBC batch processing for efficient database
 * inserts.
 * <p>
 * This service class is marked with {@link Transactional} with {@code Propagation.MANDATORY}, meaning it must be
 * invoked within an existing transaction context. It provides a method for saving a batch of {@code JsonTest} entities
 * to the database by directly unwrapping a {@link Connection} from the {@link EntityManager}.
 */
@Component
@Transactional(propagation = Propagation.MANDATORY)
class JsonTestDataService {
	private static final String INSERT_STATEMENT = "INSERT INTO json_test (id, data) VALUES (?, ?::jsonb)";

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Saves a batch of {@link JsonTest} entities to the database using a single JDBC batch operation.
	 * <p>
	 * This method unwraps a {@link Connection} from the {@link EntityManager} and uses a {@link PreparedStatement} to
	 * execute the batch insert of the given list of {@code JsonTest} entities. Each entity's {@code id} and
	 * {@code data} are set as parameters in the prepared statement. After executing the batch, the {@code batchList} is
	 * cleared, and the {@link EntityManager} cache is also cleared to prevent excessive memory usage.
	 *
	 * @param batchList a collection of {@code JsonTest} entities to be saved in bulk
	 * @throws JsonGeneratorSqlException if a database access error occurs or the batch execution fails
	 */
	public void saveBatch(Collection<JsonTest> batchList) {
		var session = entityManager.unwrap(Session.class);
		session.doWork(conn -> {
			try (var pstmt = conn.prepareStatement(INSERT_STATEMENT)) {
				for (var jsonTest : batchList) {
					pstmt.setObject(1, jsonTest.getId());
					pstmt.setString(2, jsonTest.getData());
					pstmt.addBatch();
				}
				pstmt.executeBatch();
			}
		});
	}
}
