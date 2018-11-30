package upc.req_quality.entity;

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
    private List<Template> templates;

    public Templates() {
        this.templates = new ArrayList<>();
    }

    public Templates(List<Template> templates) {
        this.templates = templates;
    }

    public List<Template> getTemplates() {
        return templates;
    }
}
