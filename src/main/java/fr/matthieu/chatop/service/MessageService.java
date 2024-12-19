package fr.matthieu.chatop.service;

import fr.matthieu.chatop.dto.CreateMessageDTO;
import fr.matthieu.chatop.exception.UnauthorizedException;
import fr.matthieu.chatop.model.MessageEntity;
import fr.matthieu.chatop.model.RentalEntity;
import fr.matthieu.chatop.model.UserEntity;
import fr.matthieu.chatop.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static fr.matthieu.chatop.common.ResponseMessages.UNAUTHORIZED_ACCESS;

/**
 * Service class responsible for handling message-related business logic.
 */
@Service
public class MessageService {

	private final UserService userService;
	private final MessageRepository messageRepository;
	private final RentalService rentalService;

	@Autowired
	public MessageService(UserService userService, MessageRepository messageRepository, RentalService rentalService) {
		this.userService = userService;
		this.messageRepository = messageRepository;
		this.rentalService = rentalService;
	}

	/**
	 * Creates a new message associated with a user and a rental.
	 * <p>
	 * This method retrieves the currently authenticated user and verifies
	 * that the user is authorized to create the message. If authorized,
	 * the message is saved in the repository.
	 * </p>
	 *
	 * @param createMessageDTO A DTO containing the details of the message to create,
	 *                         including the user ID, rental ID, and message content.
	 * @throws UnauthorizedException if the authenticated user does not match the user ID in the DTO.
	 */
	@Transactional
	public void createMessage(CreateMessageDTO createMessageDTO) {
		UserEntity userEntity = userService.getAuthenticateUser();

		if(!(Objects.equals(userEntity.getId(), createMessageDTO.user_id()))) {
			throw new UnauthorizedException(UNAUTHORIZED_ACCESS);
		}

		RentalEntity rentalEntity = rentalService.getRentalById(createMessageDTO.rental_id());

		MessageEntity messageEntity = new MessageEntity(
				createMessageDTO.message(),
				userEntity,
				rentalEntity
		);

		messageRepository.save(messageEntity);
	}
}
