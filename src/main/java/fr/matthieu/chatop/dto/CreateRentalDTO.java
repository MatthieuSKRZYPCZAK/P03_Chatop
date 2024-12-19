package fr.matthieu.chatop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) representing the data required to create a rental property.
 * <p>
 * This DTO is used to validate and transfer user input for creating a new rental property.
 * It includes details such as the rental's name, surface area, price, picture, and description.
 * </p>
 */
@Schema(name = "Create Rental", description = "Represents the data required to create a rental")
public record CreateRentalDTO(

		@Schema(description = "The name of the rental property", example = "Seaside Apartment")
		@NotBlank(message = "The rental name is required.")
		@Size(min = 1, max = 100, message = "The rental name must be between {min} and {max} characters.")
		String name,

		@Schema(description = "The surface area of the rental in square meters", example = "75")
		@NotNull(message = "The surface area is required.")
		@Min(value = 9, message = "The surface area must be at least {value} square meter.")
		@Max(value = 10000, message = "The surface area must not exceed {value} square meters.")
		Double surface,

		@Schema(description = "The price of the rental per night in euros", example = "120")
		@NotNull(message = "The rental price is required.")
		@Min(value = 0, message = "The rental price must be at least {min}.")
		@Max(value = 10000, message = "The rental price must not exceed {max} euros.")
		BigDecimal price,

		@Schema(description = "The picture file of the rental property", type = "file", format = "binary")
		MultipartFile picture,

		@Schema(description = "A brief description of the rental property", example = "A cozy apartment with a sea view")
		@NotBlank(message = "The rental description is required.")
		@Size(min = 1, max = 1000, message = "The description must be between {min} and {max} characters.")
		String description
) {
}
