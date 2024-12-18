package fr.matthieu.chatop.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) representing the details of a rental property.
 * <p>
 * This DTO is used to transfer rental property data.
 * It includes information such as the rental's name, surface area, price, picture URL, description,
 * owner information, and timestamps for creation and updates.
 * </p>
 */
@Schema(name = "Rental details", description = "Represents rental details")
public record RentalDTO(

		@Schema(description = "The unique identifier of the rental", example = "1")
		Long id,

		@Schema(description = "The name of the rental property", example = "Seaside Apartment")
		String name,

		@Schema(description = "The surface area of the rental in square meters", example = "75")
		Double surface,

		@Schema(description = "The price of the rental per night in euros", example = "120")
		BigDecimal price,

		@Schema(description = "The URL of the rental property's picture file", example = "https://example.com/rental1.jpg")
		String picture,

		@Schema(description = "A brief description of the rental property", example = "A cozy apartment with a sea view")
		String description,

		@Schema(description = "The unique identifier of the owner", example = "10")
		Long owner_id,

		@Schema(description = "The creation date of the rental record", example = "2023/01/15")
		String created_at,

		@Schema(description = "The last update date of the rental record", example = "2023/06/10")
		String updated_at
) {
}
