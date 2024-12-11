package fr.matthieu.chatop.controller;

import fr.matthieu.chatop.common.ErrorResponse;
import fr.matthieu.chatop.dto.RentalDTO;
import fr.matthieu.chatop.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static fr.matthieu.chatop.common.ApiRoutes.*;


@Slf4j
@RestController
@Tag(name = "Rentals", description = "Endpoints for managing rentals")
public class RentalController {

	private final RentalService rentalService;

	@Autowired
	public RentalController(RentalService rentalService) {
		this.rentalService = rentalService;
	}

	@GetMapping(RENTALS_URL)
	@Operation(
			summary = "Get all rentals",
			description = "Fetches a list of all available rentals.",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Successful operation. Returns a list of rentals.",
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = RentalDTO.class))
					),
					@ApiResponse(
							responseCode = "401",
							description = "Unauthorized. Invalid or missing JWT token.",
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
					)
			}
	)
	public ResponseEntity<Object> getAllRentals() {
		List<RentalDTO> rentals = rentalService.getAllRentalsDTO();
		if (rentals.isEmpty()) {
			return ResponseEntity.status(HttpStatus.OK)
					.body(Map.of("message", "No rentals available"));
		}
		return ResponseEntity.ok().body(Map.of("rentals", rentals));
	}


}
