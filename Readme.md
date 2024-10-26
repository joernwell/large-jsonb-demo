
# JSON Generator Service

The JSON Generator Service is a Spring Boot application that generates random JSON data with predefined attributes and saves it to a database. The application allows you to specify the number of records and the batch size for saving data through URL query parameters, making it customizable for different use cases.

## Features

- **Random JSON Generation**: Generates JSON data with 50 attributes, including strings, numeric values, booleans, and nested JSON objects.
- **Batch Processing**: Enables bulk storage of JSON data in user-defined batches.
- **Parallel Execution**: Optimized for concurrent processing, allowing faster generation and storage of JSON records.

## How It Works

The application consists of two main components:

1. **JsonGeneratorService**: 
   - This service is responsible for generating random JSON data. It creates a specific structure with multiple levels of attributes based on a fixed configuration.
   - Attributes include various data types (e.g., strings, numbers, booleans, and nested JSON objects), allowing for flexible data structures.

2. **JsonController**:
   - This controller provides an endpoint to trigger the JSON generation process.
   - You can set the number of records and batch size by providing these values as URL parameters.
   - JSON data is generated in parallel to improve performance.

## Usage

To start the JSON generation and storage process, use the `/generate-json` endpoint with optional query parameters for `numRecords` and `bulkSize`.

### Default Configuration

If you don't provide any query parameters, the service defaults to generating 10,000 records with a batch size of 100.

### Endpoints

#### Generate JSON

- **URL**: `/generate-json`
- **Method**: `GET`
- **Query Parameters**:
  - `numRecords` (optional): Specifies the total number of JSON records to generate (default is 10,000).
  - `bulkSize` (optional): Specifies the number of records to save in each batch (default is 100).

### Example `curl` Commands

1. **Generate JSON with Default Parameters**
   ```bash
   curl 'http://localhost:8080/generate-json'
   ```

2. **Generate JSON with Custom `numRecords` and `bulkSize`**
   ```bash
   curl 'http://localhost:8080/generate-json?numRecords=5000&bulkSize=200'
   ```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com.wellniak.json/
│   │       ├── JsonController.java       # Controller to handle API requests
│   │       ├── JsonGeneratorService.java # Service to generate JSON data
│   │       └── JsonTestRepository.java   # Repository to save JSON data to the database
│   └── resources/
│       ├── application.properties        # Application configuration
│       └── log4j2-spring.xml             # Log4j2 configuration file
└── test/
    └── java/
        └── com.wellniak.json/
            └── JsonGeneratorServiceTest.java # Unit tests for the JSON generator
```

## Getting Started

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/json-generator-service.git
   cd json-generator-service
   ```

2. **Configure the Database**:
   Set up the database properties in `application.properties` according to your environment.

3. **Build and Run the Application**:
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the Endpoint**:
   You can now use the `/generate-json` endpoint to trigger JSON generation.

## Testing

To run the unit tests, execute the following command:

```bash
./mvnw test
```

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

Enjoy using the JSON Generator Service to create and store JSON data easily and efficiently!
