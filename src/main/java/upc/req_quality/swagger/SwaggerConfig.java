package upc.req_quality.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    private static final String SWAGGER_API_VERSION = "0.1";
    private static final String LICENCE_TEXT = "License";
    private static final String title = "Requirement Conformance to Templates";
    private static final String description = "The check-conformance-to-templates microservice checks that the text of the " +
            "requirements received in the JSON follow one of the requirement templates defined. For doing so, the requirements" +
            " are first converted to include their part-of-speech (POS) tags and their sentence tags. Afterwards, they are checked " +
            "with respect to the different templates.\n\n" +
            "Description of right writing of templates:\n\n" +
            "The API uses UTF-8 charset.";

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .license(LICENCE_TEXT)
                .license(SWAGGER_API_VERSION)
                .build();
    }

    @Bean
    public Docket classifier_api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .pathMapping("/")
                .select()
                /* Anything after upc will be included into my Swagger configuration */
                .paths(PathSelectors.regex("/upc.*"))
                .build();
    }
}
