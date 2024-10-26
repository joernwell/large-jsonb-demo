
# JSON Generator Service

The JSON Generator Service is a Spring Boot application that generates large random JSON data with predefined attributes and saves it to a database. The application allows you to specify the number of records and the batch size for saving data through URL query parameters, making it customizable for different use cases.

This generated data can be used to test the performance of `JSONB` attributes in a Postgres database. 

## Features

- **Random JSON Generation**: Generates JSON data with 20 attributes, including strings, numeric values, booleans, and nested JSON objects. A JSON is approximately 170kByte in size.
- **Batch Processing**: Enables bulk storage of JSON data in user-defined batches.
- **Curl with adjustable parameters**: Call with curl. The number of entries and the bulk size can be passed as parameters.

## Getting Started

**1. Clone the Repository**

```bash
git clone https://github.com/joernwell/large-jsonb-demo.git
cd large-jsonb-demo
```

**2. Configure the Database**

Set up the database properties in `application.properties` and in `docker-compose.yaml` according to your environment.

Default configruation is

- Username: dummy
- Password: dummy
- Database: postgres
- Port: 5432

**3. Build and Run the Application with `make`**

A `Makefile` is provided to streamline the setup and running of Docker services and the Spring Boot application.

Start the application:

  ```bash
  make start
  ```
  
This command will:
  
  - Start Docker Compose services (e.g., PostgreSQL).
  - Run the Spring Boot application.

Stop the application:

  ```bash
  make stop
  ```

**4. Access the endpoint**

You can now use the `/generate-json` endpoint to trigger JSON generation.

## Usage

To start the JSON generation and storage process, use the `/generate-json` endpoint with optional query parameters for `numRecords` and `bulkSize`.

If you don't provide any query parameters, the service defaults to generating 10,000 records with a batch size of 100.

#### Endpoints

- URL: `/generate-json`
- Method: `GET`
- Query Parameters:

  - `numRecords` (optional): Specifies the total number of JSON records to generate (default is 10,000).
  - `bulkSize` (optional): Specifies the number of records to save in each batch (default is 100).

Generate JSON with Default Parameters
   ```bash
   curl 'http://localhost:8080/generate-json'
   ```

Generate JSON with custom `numRecords` and `bulkSize`
   ```bash
   curl 'http://localhost:8080/generate-json?numRecords=5000&bulkSize=200'
   ```

#### Useful SQL statements

```
select count(*) from json_test;

select * from json_test limit 20;

select * from json_test where data->'attribute_1_4'->>'attribute_2_2' > '30000' 
                        order by data->'attribute_1_4'->>'attribute_2_2' limit 1;
                        
SELECT pg_size_pretty(pg_total_relation_size('json_test')) AS main_table_size;
```

## Performance test results

#### Test scenario

- JSON object with approximately `185 kB`
- Number of rows: `1.050.000`
- Total table storage: `171 GB` (including the index storage)
- Index on `data->'attribute_1_4'->>'attribute_2_2'`

#### Search results with direct index on attribute

- Exact search of all rows where `attribute_2_2 = 27653`: `16 rows` in `101ms`
- Exact search of all rows where `attribute_2_2 = 24321`: `10 rows` in `95ms`
- Exact search of all rows where `attribute_2_2 = 24321` after `vacuum full`: `10 rows` in `68ms`
- Exact search over a not-index attribute: `3m 23s`

#### Other resource crtical actions

- Create GIN index on table: aborted after `30m`
- Vacuum full: `1h 3m`
- Create a new index on an attribute: `2m 19s`
- New attribute added with jsonb_set: `1h 13m`

Command to add new attribute 

```sql
UPDATE json_test
SET data = jsonb_set(
        data,
        '{attribute_1_4, newAttribute}',  -- Path to the new attribute
        '"abc"',                          -- Value of the new attribute as a JSONB literal
        true                              -- true to add the value if it doesn't exist
           )
WHERE data->'attribute_1_4' IS NOT NULL;
```

Note: Updating the JSONB data multiplies the memory requirements of the table (disk space). This was not automatically released again.

#### New test with only 50.000 rows

- Total table storage: `8.3 GB`
- Exact search of all rows: `44ms`
- New attribute added with jsonb_set: `3m 4s` (`16 GB` after the update)
- Vacuum full: `2m 58s`
- New attribute added with `||`: `3m 2s` (`16 GB` after the update)
- Another attribute added with `||`: `3m 5s` (`24 GB` after the update)
- `vacuum analyze`: `1m 16s`
- `vacuum full pg_toast.pg_toast_16430;`: `35s` (`8 GB` after the update)

Command to add new attribute with `||`:

```sql
UPDATE json_test
SET data = data || '{"newAttribute2": "def"}'
WHERE data->'attribute_1_4' IS NOT NULL;
```

Command to determine the `pg_toast` table name:

```sql
SELECT c.relname AS main_table,
       t.relname AS toast_table
FROM pg_class c
         LEFT JOIN pg_class t ON c.reltoastrelid = t.oid
WHERE c.relname = 'json_test';
```

Command to shrink the toast table only (shrinking the toast table only is much faster than shrinking the complete table):

```sql
vacuum full pg_toast.<<toast_table_name>>;
```

_Note_: `vacuum full` locks the table or the toast table while `vacuum` is running. No selects, inserts, updates or deletes are possible. `vacuum full` on the the toast table locks the the toast table only.


#### With GIN Index on table (59.000 rows)

- Create a GIN index: `12m 32s` (`24 GB` after creating the GIN index)
- Index on `attribute_2_2` removed. 
- Exact search on `attribute_2_2`: `13s`
- New attribute added with jsonb_set: more than `30m`

## Recommendations for JSONB

- Do not use a `GIN` index: GIN is not an index to optimize the exact search for data records via attributes in JSON. The GIN index is used for special JSON comparisons (e.g. `@>` operators). The GIN index requires additional storage (factor 3). The execution time for updates is also significantly longer than without the GIN index.
- For exact searches, create a _standard_ index for each required attribute in the `where` clause of the `select` statement. Searching for JSON attributes is then just as fast as searching via table columns.
- Run `vacuum full` on the `toast` tables (and not on the full table). Updating the 'toats' table is much faster than optimizing the entire table.

## How It Works (Java code)

The application consists of two main components:

**JsonGeneratorService**: 

   - This service is responsible for generating random JSON data. It creates a specific structure with multiple levels of attributes based on a fixed configuration.
   - Attributes include various data types (e.g., strings, numbers, booleans, and nested JSON objects), allowing for flexible data structures.

**JsonController**:

   - This controller provides an endpoint to trigger the JSON generation process.
   - You can set the number of records and batch size by providing these values as URL parameters.
   - JSON data is generated in parallel to improve performance.


## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

Enjoy using the JSON Generator Service to easily create and store large JSON data for Postgres performance testing!