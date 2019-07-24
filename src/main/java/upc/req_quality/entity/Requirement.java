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

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "Requirement", description = "A requirement with id and text")
public class Requirement implements Serializable {

    @ApiModelProperty(value="id")
    private String id;
    @ApiModelProperty(value="description")
    private String description;
    @JsonIgnore
    @ApiModelProperty(value="tips")
    private List<Tip> tips;

    public Requirement() {
        this.tips = new ArrayList<>();
    }

    public Requirement(String id, String description, List<Tip> tips) {
        this.id = id;
        this.description = description;
        this.tips = tips;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public List<Tip> getTips() {
        return tips;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
