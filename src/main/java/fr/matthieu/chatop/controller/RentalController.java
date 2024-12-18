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

/**
 * REST Controller responsible for handling rental-related actions.
 * It includes endpoints for fetching, creating, and updating rentals.
 */
@Slf4j
@RestController
@Tag(name = "Rentals", description = "Endpoints for managing rentals")
public class RentalController {

	private final RentalService rentalService;

	@Autowired
	public RentalController(RentalService rentalService) {
		this.rentalService = rentalService;
	}

	/**
	 * Retrieves a list of all rentals.
	 *
	 * @return A {@link ResponseEntity} containing the list of rentals or a message if no rentals are available.
	 */
	@GetMapping(RENTALS_URL)
	@Operation(
			summary = "Get all rentals",
			description = "Fetches a list of all available rentals.",
			security = @SecurityRequirement(name = "bearerAuth"),
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

	/**
	 * Retrieves the details of a specific rental by its ID.
	 *
	 * @param id The ID of the rental to retrieve.
	 * @return A {@link ResponseEntity} containing the rental details.
	 */
	@GetMapping(RENTAL_ID_URL)
	@Operation(
		summary = "Get rental by ID",
		description = "Fetches the details of a specific rental by its ID.",
		security = @SecurityRequirement(name = "bearerAuth"),
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

	/**
	 * Creates a new rental with the provided details.
	 *
	 * @param createRentalDTO A DTO containing the details of the rental to be created.
	 * @return A {@link ResponseEntity} containing a success message.
	 */
	@PostMapping( value = RENTALS_URL, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(
		summary = "Create a new rental",
		description = "Creates a new rental with the given details.",
		security = @SecurityRequirement(name = "bearerAuth"),
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

	/**
	 * Updates the details of a specific rental by its ID.
	 *
	 * @param id              The ID of the rental to update.
	 * @param createRentalDTO A DTO containing the updated details of the rental.
	 * @return A {@link ResponseEntity} containing a success message.
	 */
	@PutMapping(RENTAL_ID_URL)
	@Operation(
		summary = "Update rental by ID",
		description = "Updates the details of a specific rental by its ID.",
		security = @SecurityRequirement(name = "bearerAuth"),
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
			),
			@ApiResponse(
					responseCode = "401",
					description = "Unauthorized. Invalid or missing JWT token.",
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
