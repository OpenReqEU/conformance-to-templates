package upc.req_quality.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    private static final String TITLE = "Requirements Conformance to Templates";
    private static final String DESCRIPTION = "" +
            "<p> This service is meant to provide automation for checking requirements " +
            "conformance to boilerplates/templates. A boilerplate or template organizes the syntactic structure of a " +
            "requirement statement into some pre-defined slots.</p>" +
            "<p> There are two type of operations (each method has a more extensive description in its own operation box):</p>" +
            "<ul>" +
            "<li> Main methods " +
            "<ul>" +
            "<li>InTemplates: Adds the input templates to the service's database and assigns them to a specified organization. The templates must be written following a defined format explained later in this section.</li>" +
            "<li>OutTemplates: Returns the templates of a specified organization.</li>" +
            "<li>Conformance: Checks if the input requirements follow at least one of the templates of a specified organization.</li>" +
            "</ul></li>" +
            "<li> Auxiliary methods" +
            "<ul>" +
            "<li>DeleteOrganizationTemplates: Deletes the templates of a specified organization.</li>" +
            "<li>ClearDatabase: Deletes all data from the database.</li>" +
            "</ul></li>" +
            "</ul>" +
            " <p> A template follows a modified BNF diagram:</p>" +
            " <ul>" +
            " <li>A template is defined by one or more rules (the rules are defined by an array of strings).</li>" +
            " <li>The first word of each rule must be written as \" &ltname_of_the_rule&gt ::= \" that defines the name of the rule.</li>" +
            " <li>The name of the first rule must be main.</li>" +
            " <li>The first rule must define the structure of the requirement.</li>" +
            " <li>The other rules should be used to define auxiliary structures.</li>" +
            " <li> The permitted tags are: <ul>" +
            " <li> plain words (specified with \"%\"). </li>" +
            " <li> pos tags of the <a href=\"http://dpdearing.com/posts/2011/12/opennlp-part-of-speech-pos-tags-penn-english-treebank/\">OpenNLP</a> library (specified with \"()\"). </li>" +
            " <li> sentence tags -NP or VP- (specified with \"<>\"). </li>" +
            " <li> component special tags:  " +
            " <ul> <li> | : OR </li>" +
            " <li> (all) : ignores the rule</li>" +
            " <li> <*> : accepts anything that comes after </li></ul></li></ul></li></ul>" +
            "<p> An example (Rupp template): </p> <ul> " +
            "<li> &ltmain&gt ::= &ltopt-condition&gt &ltnp&gt (md) (vb) &ltnp&gt | &ltopt-condition&gt &ltnp&gt &ltmodal&gt %PROVIDE &ltnp&gt %WITH %THE %ABILITY &ltinfinitive-vp&gt &ltnp&gt | &ltopt-condition&gt &ltnp&gt &ltmodal&gt %BE %ABLE &ltvp&gt &ltnp&gt </li>" +
            "<li> &ltconditional-keyword&gt ::= %IF | %AFTER | %AS %SOON %AS | %AS %LONG %AS </li>" +
            "<li> &ltmodal&gt ::= %SHALL | %SHOULD | %WOULD </li>" +
            "<li> &ltopt-condition&gt ::= &ltconditional-keyword&gt | (all) </li>" +
            "<li> &ltinfinitive-vp&gt ::= %to &ltvp&gt </li> </ul>" +
            "<p> The API uses UTF-8 charset. Also, it uses the OpenReq format for input JSONs. </p>";

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(TITLE)
                .description(DESCRIPTION)
                .license("License").licenseUrl("https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.txt")
                .version("2.0")
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
