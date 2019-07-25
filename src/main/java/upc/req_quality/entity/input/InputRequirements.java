package upc.req_quality.entity.input;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import upc.req_quality.entity.Requirement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "Requirements", description = "A list of requirements with id and text")
public class InputRequirements implements Serializable {

    @ApiModelProperty(value="requirements")
    private List<Requirement> requirementsArray;

    public InputRequirements() {
        requirementsArray = new ArrayList<>();
    }

    public InputRequirements(List<Requirement> requirements) {
        this.requirementsArray = requirements;
    }

    public List<Requirement> getRequirements() {
        return requirementsArray;
    }
}
