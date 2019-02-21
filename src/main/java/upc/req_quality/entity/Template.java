package upc.req_quality.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "Template", description = "A template to be checked against requirements")
public class Template implements Serializable {

    @ApiModelProperty(value="name")
    private String name;
    @ApiModelProperty(value="rules")
    private List<String> rules;
    @ApiModelProperty(value="organization")
    private String organization;
    @ApiModelProperty(value="library")
    private String library;

    public Template() {}

    public Template(String name, String organization, String library, List<String> rules) {
        this.organization = organization;
        this.name = name;
        this.rules = rules;
        this.library = library;
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

    public String getLibrary() {
        return library;
    }
}
