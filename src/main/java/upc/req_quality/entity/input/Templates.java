package upc.req_quality.entity.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "Templates", description = "A list of templates")
public class Templates implements Serializable {

    @ApiModelProperty(value="templates")
    private List<Template> templatesArray;

    public Templates() {
        this.templatesArray = new ArrayList<>();
    }

    public Templates(List<Template> templates) {
        this.templatesArray = templates;
    }

    public List<Template> getTemplates() {
        return templatesArray;
    }
}
