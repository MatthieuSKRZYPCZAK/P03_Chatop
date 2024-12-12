package fr.matthieu.chatop.service;

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

@Slf4j
@Service
public class UploadService {

	@Value("${server.port}")
	String port;

	private final Path uploadPath = Paths.get("uploads/rentals");

	public UploadService() {
		try {
			Files.createDirectories(uploadPath);
		} catch (IOException e) {
			throw new RuntimeException("Could not create upload directory!", e);
		}
	}


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
				throw new RuntimeException("Cannot store file outside current directory.");
			}

			try (var inputStream = picture.getInputStream()) {
				Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
			}

			return APP_URL+ port + "/uploads/rentals/" + filename;
		} catch (IOException e) {
			throw new RuntimeException("Failed to store file.", e);
		}
	}
}
