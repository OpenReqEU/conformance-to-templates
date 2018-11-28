package upc.req_quality.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "Requirements", description = "A list of requirements with id and text")
public class Requirements implements Serializable {

    @ApiModelProperty(value="requirements")
    private List<Requirement> requirements;

    public Requirements() {
        requirements = new ArrayList<>();
    }

    public Requirements(List<Requirement> requirements) {
        this.requirements = requirements;
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }
}
