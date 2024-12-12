package fr.matthieu.chatop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "Rental details", description = "Represents rental details")
public record RentalDTO(

		@Schema(description = "The unique identifier of the rental", example = "1")
		Long id,

		@Schema(description = "The name of the rental property", example = "Seaside Apartment")
		@NotBlank(message = "The rental name is required.")
		@Size(max = 100, message = "The rental name must not exceed {max} characters.")
		String name,

		@Schema(description = "The surface area of the rental in square meters", example = "75")
		@NotBlank(message = "The surface area is required.")
		@Min(value = 9, message = "The surface area must be at least {value} square meter.")
		@Max(value = 10000, message = "The surface area must not exceed {value} square meters.")
		Integer surface,

		@Schema(description = "The price of the rental per night in euros", example = "120")
		@NotBlank(message = "The rental price is required.")
		@Min(value = 0, message = "The rental price must be at least {min}.")
		@Max(value = 10000, message = "The rental price must not exceed {max} euros.")
		Integer price,

		@Schema(description = "The URL of the rental property's picture file", example = "https://example.com/rental1.jpg")
		String picture,

		@Schema(description = "A brief description of the rental property", example = "A cozy apartment with a sea view")
		@NotBlank(message = "The rental description is required.")
		@Size(min = 2, max = 500, message = "The description must be between {min} and {max} characters.")
		String description,

		@Schema(description = "The unique identifier of the owner", example = "10")
		Long owner_id,

		@Schema(description = "The creation date of the rental record", example = "2023/01/15")
		String created_at,

		@Schema(description = "The last update date of the rental record", example = "2023/06/10")
		String updated_at
) {
}
