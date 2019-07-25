package upc.req_quality.entity.output;

import io.swagger.annotations.ApiModelProperty;
import upc.req_quality.entity.Requirement;
import java.util.List;

public class OutputRequirements {

    @ApiModelProperty(value="requirements")
    private List<OutputRequirement> requirementsArray;

    public OutputRequirements(List<OutputRequirement> requirements) {
        this.requirementsArray = requirements;
    }

    public List<OutputRequirement> getRequirements() {
        return requirementsArray;
    }
}
