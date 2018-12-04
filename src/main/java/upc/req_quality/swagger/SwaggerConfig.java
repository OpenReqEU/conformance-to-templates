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
    private static final String SWAGGER_API_VERSION = "0.2";
    private static final String LICENCE_TEXT = "License";
    private static final String title = "Requirement Conformance to Templates";
    private static final String description = "This service is meant to provide automation for checking requirements" +
            " conformance to boilerplates/templates. A boilerplate or template organizes the syntactic structure of a" +
            " requirement statement into a number of pre-defined slots.\n" +
            "\n" +
            " This API allows the writing of templates following a defined format. Those templates are stored in an" +
            " internal database distributed according to the organization that created them. Then you can check if a" +
            " set of requirements follows the structure of the previous templates. For doing so, the requirements are" +
            " first converted to include their part-of-speech and sentence" +
            " tags with a library that can be specified.\n" +
            "\n" +
            " Before writing a new template you should choose a NLP library of those permitted by this service because" +
            " each one has different tags. A template follows a BNF modified diagram:\n" +
            " -A template is defined by one or more rules (the input is defined by an array of strings).\n" +
            " -The first word of each rule must be written as ```#<name_of_the_rule> ::=``` that defines the name of the rule.\n" +
            " -The name of the first rule must be main.\n" +
            " -The first rule must define the structure of the requirement.\n" +
            " -The other rules should be used to define auxiliary structures.\n" +
            " -The auxiliary rules must be in incremental order.\n" +
            " -For writing rules are permitted the next tags: words (specified with \"%\"), pos tags of the template's library (specified with \"()\"), sentence tags of the template's library (specified with \"<>\") and service tags.\n\n" +
            " An example:\n" +
            "```\n" +
            "main rule --> #<main> ::=  <opt-condition> <np> (md) (vb) <np> | <opt-condition> <np> <modal> %PROVIDE <np> %WITH %THE %ABILITY <infinitive-vp> <np> | <opt-condition> <np> <modal> %BE %ABLE <vp> <np>\n" +
            "aux rule -->  #<conditional-keyword> ::= %IF | %AFTER | %AS %SOON %AS | %AS %LONG %AS\n" +
            "aux rule -->  #<modal-algo> ::= %SHALL | %SHOULD | %WOULD\n" +
            "aux rule -->  #<opt-condition> ::= <conditional-keyword> | (all)\n" +
            "aux rule -->  #<infinitive-vp> ::= %to <vp> \n```\n" +
            "";


    /*

    This service is meant to provide automation for checking requirements conformance to boilerplates/templates. A boilerplate or template organizes the syntactic structure of a requirement statement into a number of pre-defined slots.

    This API allows the writing of templates following a defined format. Those templates are stored on a internal database distributed according to the organization that created it. Next, it is allowed to check if a set of requirements that enter as input follow the structure of one or more templates saved in the database. For doing so, the requirements are first converted to include their part-of-speech and sentence tags with a library that can be specified.

    Before writing a new template you should choose a NLP library of those permitted by this service because each one has different tags. Then, you should follow the next rules:
    -If template is defined by one or more rules (the input is defined by an array of strings).
    -The first word of each rule must be between "#<word>" that defines the name of the rule.
    -The name of the first rule must be "#<main>".
    -In the first rule must be defined the structure of the requirement.
    -The other rules are used to define auxiliary structures.
    -The auxiliary rules must be in incremental order.

    An example:
    main rule --> #<main> ::=  <opt-condition> <np> (md) (vb) <np> <*> | <opt-condition> <np> <modal> "PROVIDE" <np> "WITH" "THE" "ABILITY" <infinitive-vp> <np> <*> | <opt-condition> <np> <modal> "BE" "ABLE" <vp> <np> <*> | <opt-condition> <np> <modal> <vp> <np> <*>
    aux rule -->  #<conditional-keyword> ::= %IF | %AFTER | %AS %SOON %AS | %AS %LONG %AS
    aux rule -->  #<modal> ::= %SHALL | %SHOULD | %WOULD
    aux rule -->  #<opt-condition> ::= <conditional-keyword> | (all)
    aux rule -->  #<infinitive-vp> ::= %to <vp>
     */

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
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .pathMapping("/")
                .select()
                /* Anything after upc will be included into my Swagger configuration */
                .paths(PathSelectors.regex("/upc.*"))
                .build();
    }
}
