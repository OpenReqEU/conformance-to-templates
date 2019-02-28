package upc.req_quality.entity.input_output;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "Tip", description = "Advice to improve requirement structure to follow a template structure")
public class Tip implements Serializable {

    @ApiModelProperty(value="name_template")
    private String template;
    @ApiModelProperty(value="score")
    private float score;
    @ApiModelProperty(value="description")
    private List<String> description;

    public Tip (){
        this.description = new ArrayList<>();
    }

    public Tip (String template, List<String> description, float score) {
        this.template = template;
        this.description = description;
        this.score = score;
    }

    public String getTemplate() {
        return template;
    }

    public float getScore() {
        return score;
    }

    public List<String> getDescription() {
        return description;
    }
}
