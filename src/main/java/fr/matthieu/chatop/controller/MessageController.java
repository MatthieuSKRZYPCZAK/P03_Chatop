package fr.matthieu.chatop.controller;

import fr.matthieu.chatop.common.ErrorResponse;
import fr.matthieu.chatop.dto.CreateMessageDTO;
import fr.matthieu.chatop.service.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import java.util.Map;

import static fr.matthieu.chatop.common.ApiRoutes.MESSAGES_URL;
import static fr.matthieu.chatop.common.ResponseMessages.MESSAGE_CREATED;

/**
 * REST Controller responsible for handling message-related actions.
 * It includes endpoints for creating messages.
 */
@Slf4j
@RestController
@Tag(name = "Messages", description = "Endpoints for managing messages")
public class MessageController {

	private final MessageService messageService;

	@Autowired
	public MessageController(MessageService messageService) {
		this.messageService = messageService;
	}


	/**
	 * Creates a new message with the details provided in the request body.
	 *
	 * @param createMessageDTO A DTO containing the details of the message to be created.
	 * @return A {@link ResponseEntity} containing a success message if the operation is successful,
	 *         or an error response with the appropriate status code in case of failure.
	 */
	@PostMapping( value = MESSAGES_URL)
	@Operation(
			summary = "Create a new message",
			description = "Creates a new message with the provided details.",
			security = @SecurityRequirement(name = "bearerAuth"),
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Message created successfully",
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateMessageDTO.class))
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
	public ResponseEntity<Object> createMessage(@Valid @RequestBody CreateMessageDTO createMessageDTO) {
		messageService.createMessage(createMessageDTO);
		return ResponseEntity.ok().body(Map.of("message", MESSAGE_CREATED));
	}
}
