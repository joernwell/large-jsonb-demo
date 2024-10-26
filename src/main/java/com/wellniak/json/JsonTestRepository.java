package com.wellniak.json;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JsonTestRepository extends JpaRepository<JsonTest, UUID> {
}