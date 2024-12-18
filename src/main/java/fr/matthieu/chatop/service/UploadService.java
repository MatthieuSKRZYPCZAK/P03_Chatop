package fr.matthieu.chatop.service;

import fr.matthieu.chatop.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

import static fr.matthieu.chatop.common.ApiRoutes.APP_URL;

/**
 * Service class for handling file uploads.
 * <p>
 * This service is responsible for storing uploaded pictures in a predefined directory
 * and generating accessible URLs for the stored files.
 * </p>
 */
@Slf4j
@Service
public class UploadService {

	@Value("${server.port}")
	String port;

	private final Path uploadPath = Paths.get("uploads/rentals");

	/**
	 * Constructs the {@code UploadService} and ensures that the upload directory exists.
	 * <p>
	 * If the directory does not exist, it attempts to create it. If directory creation fails,
	 * a {@link FileStorageException} is thrown.
	 * </p>
	 */
	public UploadService() {
		try {
			Files.createDirectories(uploadPath);
		} catch (IOException e) {
			log.error("Could not create upload directory!", e);
			throw new FileStorageException("Could not create upload directory!");
		}
	}

	/**
	 * Stores a picture file in the upload directory and generates a URL for accessing it.
	 * <p>
	 * The stored file is renamed to a unique name based on a UUID, the current date,
	 * and the provided name. If the upload process fails, a {@link FileStorageException} is thrown.
	 * </p>
	 *
	 * @param picture The {@link MultipartFile} representing the uploaded picture.
	 * @param name    The name to include in the stored file's name.
	 * @return A URL string pointing to the uploaded file.
	 * @throws FileStorageException If file storage fails or an invalid file path is detected.
	 */
	protected String storePicture(MultipartFile picture, String name) {

		String originalFilename = picture.getOriginalFilename();
		String extension = "";

		if (originalFilename != null && originalFilename.contains(".")) {
			extension = originalFilename.substring(originalFilename.lastIndexOf("."));
		}

		String filename = UUID.randomUUID() + "_" + LocalDate.now() + "_" + name + extension;
		try {
			Path destinationFile = uploadPath.resolve(Paths.get(filename)).normalize().toAbsolutePath();

			if (!destinationFile.getParent().equals(uploadPath.toAbsolutePath())) {
				throw new FileStorageException("Cannot store file outside current directory.");
			}

			try (var inputStream = picture.getInputStream()) {
				Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
			}

			return APP_URL+ port + "/uploads/rentals/" + filename;
		} catch (IOException e) {
			log.error("Failed to store file.", e);
			throw new FileStorageException("Failed to store file.");
		}
	}
}
