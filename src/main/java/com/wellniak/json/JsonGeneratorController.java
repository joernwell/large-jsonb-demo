package com.wellniak.json;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling JSON generation requests.
 * <p>
 * This controller provides an endpoint to generate and store a specified number of JSON records in the database. It
 * also returns a summary of the generated and total records in the response.
 */
@RestController
@Log4j2
public class JsonGeneratorController {

	@Autowired
	private JsonGeneratorService jsonGeneratorService;

	/**
	 * Generates a specified number of JSON records and saves them to the database.
	 * <p>
	 * This endpoint triggers the generation of JSON records in batches based on the provided {@code bulkSize}. If
	 * {@code numRecords} is not evenly divisible by {@code bulkSize}, any remaining records are processed separately.
	 * The total count of records after the operation is also returned.
	 *
	 * @param numRecords the total number of JSON records to generate (default: 10,000)
	 * @param bulkSize the size of each batch to be generated and saved (default: 100)
	 * @return a {@link JsonResponse} object containing the number of new records, the total records in the database,
	 *         and a summary message
	 */
	@GetMapping("/generate-json")
	public JsonResponse generateJson(@RequestParam(value = "numRecords", defaultValue = "10000") int numRecords,
			@RequestParam(value = "bulkSize", defaultValue = "100") int bulkSize) {

		log.info("Start generating JSON with numRecords={} and bulkSize={}", numRecords, bulkSize);

		var fullBatches = numRecords / bulkSize;
		IntStream.range(0, fullBatches).forEach(i -> {
			jsonGeneratorService.generateAndSaveJson(bulkSize);
			log.debug("{} entries written", bulkSize * (i + 1));
		});

		var remainingRecords = numRecords % bulkSize;
		if (remainingRecords > 0) {
			jsonGeneratorService.generateAndSaveJson(remainingRecords);
			log.debug("{} entries written (remaining)", numRecords);
		}

		var numberFormat = NumberFormat.getNumberInstance(Locale.US);
		var formattedNumRecords = numberFormat.format(numRecords);
		var totalRecordCount = jsonGeneratorService.getTotalRecordCount();
		var formattedTotalRecords = numberFormat.format(totalRecordCount);
		var message = "%s new JSONs were generated and saved to the database (total records: %s)."
				.formatted(formattedNumRecords, formattedTotalRecords);
		log.info(message);
		return new JsonResponse(numRecords, totalRecordCount, message);
	}

	@Data
	@AllArgsConstructor
	public static class JsonResponse {
		private int newRecords;
		private long totalRecords;
		private String message;
	}
}
