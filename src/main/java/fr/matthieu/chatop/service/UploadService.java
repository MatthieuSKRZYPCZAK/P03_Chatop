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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
	private String port;

	@Value("${file.upload-dir}")
	private String uploadDir;

	@Value("${app.base-url}")
	private String baseUrl;

	@Value("${file.upload-relative-path}")
	private String uploadRelativePath;




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

		Path uploadPath = initializeUploadDirectory();

		validateFileType(picture);

		String filename = generateUniqueFilename(picture.getOriginalFilename(), name);

		saveFileToDisk(picture, uploadPath.resolve(filename));

		return generateFileUrl(filename);

	}

	/**
	 * Initializes the upload directory, creating it if necessary.
	 *
	 * @return The path to the upload directory.
	 * @throws FileStorageException If the directory cannot be created.
	 */
	private Path initializeUploadDirectory() {
		try {
			Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
			Files.createDirectories(path);
			log.info("Upload directory initialized at: {} ", path);
			return path;
		} catch (IOException e) {
			log.error("Could not create upload directory!", e);
			throw new FileStorageException("Could not create upload directory!");
		}
	}

	/**
	 * Validates the content type of the file.
	 *
	 * @param picture The file to validate.
	 * @throws FileStorageException If the content type is not allowed.
	 */
	private void validateFileType(MultipartFile picture) {
		String contentType = picture.getContentType();
		List<String> allowedContentTypes = Arrays.asList("image/jpeg", "image/png");

		if (!allowedContentTypes.contains(contentType)) {
			throw new FileStorageException("Unsupported file type. Only JPEG and PNG files are allowed.");
		}
	}

	/**
	 * Generates a unique filename using a UUID, date, and name.
	 *
	 * @param originalFilename The original filename of the uploaded file.
	 * @param name             The name to include in the new filename.
	 * @return The generated filename.
	 */
	private String generateUniqueFilename(String originalFilename, String name) {
		String extension = getFileExtension(originalFilename);
		return UUID.randomUUID() + "_" + LocalDate.now() + "_" + name + extension;
	}

	/**
	 * Extracts the file extension from the original filename.
	 *
	 * @param originalFilename The original filename.
	 * @return The file extension, or an empty string if none is found.
	 */
	private String getFileExtension(String originalFilename) {
		if (originalFilename == null || !originalFilename.contains(".")) {
			return "";
		}
		return originalFilename.substring(originalFilename.lastIndexOf("."));
	}

	/**
	 * Saves the file to the specified location on disk.
	 *
	 * @param picture        The file to save.
	 * @param destinationPath The path where the file should be saved.
	 * @throws FileStorageException If the file cannot be saved.
	 */
	private void saveFileToDisk(MultipartFile picture, Path destinationPath) {
		try {
			if (!destinationPath.getParent().equals(destinationPath.getParent().toAbsolutePath())) {
				throw new FileStorageException("Cannot store file outside current directory.");
			}

			try (var inputStream = picture.getInputStream()) {
				Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			log.error("Failed to store file.", e);
			throw new FileStorageException("Failed to store file.");
		}
	}

	/**
	 * Generates the public URL for accessing the uploaded file.
	 *
	 * @param filename The filename of the uploaded file.
	 * @return The public URL as a string.
	 */
	private String generateFileUrl(String filename) {
		return baseUrl + ":" + port + "/" + uploadRelativePath + "/" + filename;
	}
}
