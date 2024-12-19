package fr.matthieu.chatop.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class to expose static resources for uploaded files.
 * <p>
 * 		This class maps the physical upload directory to a public URL path
 * 		so that uploaded files can be accessed via HTTP requests.
 * </p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${file.upload-dir}")
	private String uploadDir;

	@Value("${file.upload-relative-path}")
	private String uploadRelativePath;


	/**
	 * Configures resource handlers to map public URL paths to physical directories.
	 * <p>
	 * 		This method maps the public path defined by {@code uploadRelativePath}
	 * 		to the physical directory defined by {@code uploadDir}.
	 * </p>
	 *
	 * @param registry the {@link ResourceHandlerRegistry} used to register the resource handler
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/" + uploadRelativePath + "/**")
				.addResourceLocations("file:" + uploadDir + "/", "file:"+ uploadRelativePath + "/");
	}
}
