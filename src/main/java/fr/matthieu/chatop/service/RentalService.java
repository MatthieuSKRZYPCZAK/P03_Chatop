package fr.matthieu.chatop.service;

import fr.matthieu.chatop.dto.CreateRentalDTO;
import fr.matthieu.chatop.dto.RentalDTO;
import fr.matthieu.chatop.exception.RentalNotFoundException;
import fr.matthieu.chatop.exception.UnauthorizedException;
import fr.matthieu.chatop.model.RentalEntity;
import fr.matthieu.chatop.model.UserEntity;
import fr.matthieu.chatop.repository.RentalRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static fr.matthieu.chatop.common.ResponseMessages.RENTAL_NOT_FOUND;
import static fr.matthieu.chatop.common.ResponseMessages.UNAUTHORIZED_ACCESS;

/**
 * Service class for managing rental-related operations.
 */
@Slf4j
@Service
public class RentalService {

	private final RentalRepository rentalRepository;
	private final UserService userService;
	private final UploadService uploadService;

	@Autowired
	public RentalService(RentalRepository rentalRepository, UserService userService, UploadService uploadService) {
		this.rentalRepository = rentalRepository;
		this.userService = userService;
		this.uploadService = uploadService;
	}

	/**
	 * Retrieves all rentals and converts them into DTOs.
	 *
	 * @return A list of {@link RentalDTO} objects representing all rentals.
	 */
	public List<RentalDTO> getAllRentalsDTO() {
		List<RentalEntity> rentalEntities = rentalRepository.findAll();
		return rentalEntities.stream()
				.map(this::convertToDTO)
				.toList();
	}

	/**
	 * Retrieves a rental by its ID and converts it into a DTO.
	 *
	 * @param id The ID of the rental to retrieve.
	 * @return A {@link RentalDTO} representing the rental.
	 */
	public RentalDTO getRentalDTOById(Long id) {
		RentalEntity rentalEntity = getRentalById(id);
		return convertToDTO(rentalEntity);
	}

	/**
	 * Creates a new rental and saves it to the database.
	 * <p>
	 * The rental is associated with the currently authenticated user. The picture
	 * is uploaded and its URL is saved with the rental.
	 * </p>
	 * @param createRentalDTO The details of the rental to create.
	 */
	@Transactional
	public void createRental(CreateRentalDTO createRentalDTO) {
		UserEntity userEntity = userService.getAuthenticateUser();
		String picture = uploadService.storePicture(createRentalDTO.picture(), userEntity.getName());
		RentalEntity rentalEntity = new RentalEntity(createRentalDTO.name(), createRentalDTO.surface(), createRentalDTO.price(), picture, createRentalDTO.description(), userEntity);
		rentalRepository.save(rentalEntity);
	}

	/**
	 * Retrieves a rental by its ID.
	 *
	 * @param id The ID of the rental to retrieve.
	 * @return The {@link RentalEntity} object
	 */
	protected RentalEntity getRentalById(Long id) {
		return rentalRepository.findById(id)
				.orElseThrow(() -> new RentalNotFoundException(
						String.format(RENTAL_NOT_FOUND, id)
				));
	}

	/**
	 * Verifies that the currently authenticated user is the owner of the specified rental.
	 *
	 * @param id The ID of the rental to check.
	 * @return The {@link RentalEntity} object if the user is the owner.
	 */
	public RentalEntity checkOwner(Long id) {
		UserEntity userEntity = userService.getAuthenticateUser();
		RentalEntity rentalEntity = getRentalById(id);
		if(rentalEntity.getOwner().equals(userEntity)) {
			return rentalEntity;
		}
		throw new UnauthorizedException(UNAUTHORIZED_ACCESS);
	}

	/**
	 * Updates the details of an existing rental.
	 *
	 * @param rentalEntity           The {@link RentalEntity} object to update.
	 * @param createRentalDTO  The new details for the rental.
	 */
	@Transactional
	public void UpdateRental(RentalEntity rentalEntity, CreateRentalDTO createRentalDTO) {

		rentalEntity.setName(createRentalDTO.name());
		rentalEntity.setSurface(createRentalDTO.surface());
		rentalEntity.setPrice(createRentalDTO.price());
		rentalEntity.setDescription(createRentalDTO.description());

		rentalRepository.save(rentalEntity);
	}

	/**
	 * Converts a {@link RentalEntity} object into a {@link RentalDTO}.
	 *
	 * @param rentalEntity The {@link RentalEntity} object to convert.
	 * @return A {@link RentalDTO} containing the rental details.
	 */
	private RentalDTO convertToDTO(RentalEntity rentalEntity) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		return new RentalDTO(
				rentalEntity.getId(),
				rentalEntity.getName(),
				rentalEntity.getSurface(),
				rentalEntity.getPrice(),
				rentalEntity.getPicture(),
				rentalEntity.getDescription(),
				rentalEntity.getOwner().getId(),
				rentalEntity.getCreatedAt().format(formatter),
				rentalEntity.getUpdatedAt() != null ? rentalEntity.getUpdatedAt().format(formatter) : null
		);
	}
}
