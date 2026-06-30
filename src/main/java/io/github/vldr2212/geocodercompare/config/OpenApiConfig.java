package io.github.vldr2212.geocodercompare.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Конфигурация OpenAPI: метаданные API и описание из swagger-description.md.
 */
@Configuration
public class OpenApiConfig {

    private static final String API_TITLE = "Geocoder Compare API";
    private static final String API_VERSION = "1.0.0";
    private static final String CONTACT_NAME = "Vldr22 - GitHub";
    private static final String CONTACT_URL = "https://github.com/Vldr22/geocoder-compare";
    private static final String DESCRIPTION_PATH = "/swagger-description.md";

    public static final String TAG_GEOCODE = "Сравнение геокодеров";
    private static final String TAG_GEOCODE_DESCRIPTION = "Сравнение координат адреса от Yandex и Dadata";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(API_TITLE)
                        .version(API_VERSION)
                        .description(loadDescription())
                        .contact(new Contact()
                                .name(CONTACT_NAME)
                                .url(CONTACT_URL)))
                .tags(List.of(
                        new Tag().name(TAG_GEOCODE).description(TAG_GEOCODE_DESCRIPTION)
                ));
    }

    private String loadDescription() {
        InputStream input = getClass().getResourceAsStream(DESCRIPTION_PATH);
        if (input == null) {
            return API_TITLE;
        }
        try (input) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return API_TITLE;
        }
    }
}
