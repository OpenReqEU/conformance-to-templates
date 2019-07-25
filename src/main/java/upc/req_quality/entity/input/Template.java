package upc.req_quality.entity.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "Template", description = "A template to be checked against requirements")
public class Template implements Serializable {

    @ApiModelProperty(value="name", example = "Rupp")
    private String name;
    @ApiModelProperty(value="rules", example = "[<main> ::= <np> \"to\" (vb) <*>]")
    private List<String> rules;
    @ApiModelProperty(value="organization", example = "UPC")
    private String organization;

    public Template() {}

    public Template(String name, String organization, List<String> rules) {
        this.organization = organization;
        this.name = name;
        this.rules = rules;
    }

    public String getOrganization() {
        return organization;
    }

    public String getName() {
        return name;
    }

    public List<String> getRules() {
        return rules;
    }
}
