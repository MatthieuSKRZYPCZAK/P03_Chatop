package fr.matthieu.chatop.controller;

import fr.matthieu.chatop.common.ErrorResponse;
import fr.matthieu.chatop.dto.CreateRentalDTO;
import fr.matthieu.chatop.dto.RentalDTO;
import fr.matthieu.chatop.model.Rental;
import fr.matthieu.chatop.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static fr.matthieu.chatop.common.ApiRoutes.*;
import static fr.matthieu.chatop.common.ResponseMessages.RENTAL_CREATED;
import static fr.matthieu.chatop.common.ResponseMessages.RENTAL_UPDATED;


@Slf4j
@RestController
@SecurityRequirement(name = "bearerAuth")
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


	@GetMapping(RENTAL_ID_URL)
	@Operation(
		summary = "Get rental by ID",
		description = "Fetches the details of a specific rental by its ID.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "Successful operation",
				content = @Content(mediaType = "application/json", schema = @Schema(implementation = RentalDTO.class))
			),
			@ApiResponse(
					responseCode = "401",
					description = "Unauthorized. Invalid or missing JWT token.",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
					responseCode = "404",
					description = "Rental not found.",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<Object> getRentalById(@PathVariable Long id) {
		RentalDTO rental = rentalService.getRentalDTOById(id);
		return ResponseEntity.ok(rental);
	}

	@PostMapping( value = RENTALS_URL, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(
		summary = "Create a new rental",
		description = "Creates a new rental with the given details.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "Rental created successfully",
				content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateRentalDTO.class))
			),
			@ApiResponse(
					responseCode = "400",
					description = "Bad Request. Errors include: missing or invalid data",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
				responseCode = "401",
				description = "Unauthorized. Invalid or missing JWT token.",
				content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<Object> createRental(@Valid @ModelAttribute CreateRentalDTO createRentalDTO) {
		if(createRentalDTO.picture().isEmpty()) {
			return ResponseEntity.status(HttpStatus.OK)
					.body(Map.of("picture", "The rental picture is required."));
		}

		rentalService.createRental(createRentalDTO);
		return ResponseEntity.ok().body(Map.of("message", RENTAL_CREATED));
	}


	@PutMapping(RENTAL_ID_URL)
	@Operation(
		summary = "Update rental by ID",
		description = "Updates the details of a specific rental by its ID.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "Rental updated successfully",
				content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateRentalDTO.class))
			),
			@ApiResponse(
				responseCode = "404",
				description = "Rental not found",
				content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
			)
		}
	)
	public ResponseEntity<Object> updateRental(@PathVariable Long id, @Valid @ModelAttribute CreateRentalDTO createRentalDTO) {
		Rental rental = rentalService.checkOwner(id);
		rentalService.UpdateRental(rental, createRentalDTO);
		return ResponseEntity.ok().body(Map.of("message", RENTAL_UPDATED));
	}
}
