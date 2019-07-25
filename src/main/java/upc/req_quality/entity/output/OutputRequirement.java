package upc.req_quality.entity.output;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

public class OutputRequirement implements Serializable {

    @ApiModelProperty(value="id")
    private String id;
    @ApiModelProperty(value="description")
    private String description;
    @ApiModelProperty(value="tips")
    private List<Tip> tips;

    public OutputRequirement(String id, String description, List<Tip> tips) {
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
}
