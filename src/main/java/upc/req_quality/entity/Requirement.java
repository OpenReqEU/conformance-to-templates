package upc.req_quality.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import upc.req_quality.entity.output.Tip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "Requirement", description = "A requirement with id and text")
public class Requirement implements Serializable {

    @ApiModelProperty(value="id", example = "UPC-1")
    private String id;
    @ApiModelProperty(value="description", example = "The Surveillance and Tracking module shall provide the system administrator with the ability to monitor system configuration changes posted to the database.")
    private String description;

    public Requirement(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
