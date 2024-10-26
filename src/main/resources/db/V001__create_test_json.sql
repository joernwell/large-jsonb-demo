DO $$
BEGIN
    -- Drop the Json_Test table if it exists
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'json_test') THEN
        DROP TABLE Json_Test;
    END IF;

    -- Ensure the pgcrypto extension is available for UUID generation
    IF NOT EXISTS (SELECT 1 FROM pg_extension WHERE extname = 'pgcrypto') THEN
        CREATE EXTENSION pgcrypto;
    END IF;

    -- Create the Json_Test table
    CREATE TABLE Json_Test (
        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
        data JSONB
    );

    -- Create a GIN index on the entire JSONB data for flexible querying
	CREATE INDEX idx_json_test_attribute_1_1 ON Json_Test((data->>'attribute_1_1'));
	CREATE INDEX idx_json_test_attribute_1_2 ON Json_Test((data->>'attribute_1_2'));
	CREATE INDEX idx_json_test_attribute_1_3 ON Json_Test((data->>'attribute_1_3'));
	CREATE INDEX idx_json_test_attribute_1_4_2_2 ON Json_Test ((data->'attribute_1_4'->>'attribute_2_2'));
END
$$;