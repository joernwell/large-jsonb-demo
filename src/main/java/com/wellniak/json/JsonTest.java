package com.wellniak.json;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import java.util.UUID;
import org.hibernate.annotations.Type;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

@Entity
@Table(name = "Json_Test")
public class JsonTest {

    @Id
    @GeneratedValue
    private UUID id;

    @Type(JsonBinaryType.class)  // Verwende den JSONB-Typ direkt
    @Column(columnDefinition = "jsonb")
    private String data;

    // Getter und Setter
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}