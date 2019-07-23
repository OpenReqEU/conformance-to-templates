package upc.req_quality.entity.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "Templates", description = "A list of templates")
public class InputTemplates implements Serializable {

    @ApiModelProperty(value="templates")
    private List<InputTemplate> templatesArray;

    public InputTemplates() {
        this.templatesArray = new ArrayList<>();
    }

    public InputTemplates(List<InputTemplate> templates) {
        this.templatesArray = templates;
    }

    public List<InputTemplate> getTemplates() {
        return templatesArray;
    }
}
