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
    @ApiModelProperty(value="index")
    private int index;
    @ApiModelProperty(value="expected_tokens")
    private List<String> errors;

    public Tip (){
        this.errors = new ArrayList<>();
    }

    public Tip (String template, List<String> description, int index) {
        this.template = template;
        this.errors = description;
        this.index = index;
    }

    public String getTemplate() {
        return template;
    }

    public int getIndex() {
        return index;
    }

    public List<String> getDescription() {
        return errors;
    }
}
