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

import java.util.HashSet;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    private static final String LICENCE_TEXT = "License";
    private static final String title = "Requirements Conformance to Templates";
    private static final String description = "<p> This service is meant to provide automation for checking requirements" +
            " conformance to boilerplates/templates. A boilerplate or template organizes the syntactic structure of a" +
            " requirement statement into a number of pre-defined slots.</p>" +
            " <p> This API has two main operations:</p>" +
            " <ul>" +
            " <li> InTemplates </li>" +
            " <li> Conformance </li></ul>" +
            " <p> The first operation serves to add templates to the API database. The templates must be written following a defined format explained later in this section. The " +
            "operation receives the template and organization names as parameters which identify the template in the database. We use the OpenNLP library to process the templates and the requirements. </p>" +
            " <p> The second operation checks if the input requirements follow one or more templates stored in the database. Each requirement is compared with all the templates " +
            "of the organization receive as parameter and only return if it doesn't conform to any of them. In this case, the API also returns a conformance score and one or more tips " +
            " to help improving the requirement. </p>" +
            " <p> A template follows a modified BNF diagram:</p>" +
            " <ul>" +
            " <li>A template is defined by one or more rules (the rules are defined by an array of strings).</li>" +
            " <li>The first word of each rule must be written as \" &ltname_of_the_rule&gt ::= \" that defines the name of the rule.</li>" +
            " <li>The name of the first rule must be main.</li>" +
            " <li>The first rule must define the structure of the requirement.</li>" +
            " <li>The other rules should be used to define auxiliary structures.</li>" +
            " <li> Are only permitted the next tags : <ul>" +
            " <li> plain words (specified with \"%\"). </li>" +
            " <li> pos tags of the <a href=\"http://dpdearing.com/posts/2011/12/opennlp-part-of-speech-pos-tags-penn-english-treebank/\">OpenNLP</a> library (specified with \"()\"). </li>" +
            " <li> sentence tags -NP or VP- (specified with \"<>\"). </li>" +
            " <li> component special tags:  " +
            " <ul> <li> | : OR </li>" +
            " <li> (all) : ignores the rule</li>" +
            " <li> <*> : accepts anything that comes after </li></ul></li></ul></li></ul>" +
            "<p> An example: </p> <ul> " +
            "<li> &ltmain&gt ::= &ltopt-condition&gt &ltnp&gt (md) (vb) &ltnp&gt | &ltopt-condition&gt &ltnp&gt &ltmodal&gt %PROVIDE &ltnp&gt %WITH %THE %ABILITY &ltinfinitive-vp&gt &ltnp&gt | &ltopt-condition&gt &ltnp&gt &ltmodal&gt %BE %ABLE &ltvp&gt &ltnp&gt </li>" +
            "<li> &ltconditional-keyword&gt ::= %IF | %AFTER | %AS %SOON %AS | %AS %LONG %AS </li>" +
            "<li> &ltmodal&gt ::= %SHALL | %SHOULD | %WOULD </li>" +
            "<li> &ltopt-condition&gt ::= &ltconditional-keyword&gt | (all) </li>" +
            "<li> &ltinfinitive-vp&gt ::= %to &ltvp&gt </li> </ul>" +
            "<p> The API uses UTF-8 charset. Also, it uses the OpenReq format for input JSONs. </p>";

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .license(LICENCE_TEXT)
                .build();
    }

    @Bean
    public Docket api() {
        HashSet<String> protocols = new HashSet<>();
        protocols.add("https");
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .host("api.openreq.eu/conformance-to-templates")
                .protocols(protocols)
                .pathMapping("/")
                .select()
                .paths(PathSelectors.regex("/upc.*"))
                .build();
    }
}
