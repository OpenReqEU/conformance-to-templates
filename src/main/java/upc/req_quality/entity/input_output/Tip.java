package upc.req_quality.entity.input_output;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import upc.req_quality.exception.InternalErrorException;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "Tip", description = "Advice to improve requirement structure to follow a template structure")
public class Tip implements Serializable {

    @ApiModelProperty(value="name_template")
    private String template;
    @ApiModelProperty(value="index")
    private String index;
    @ApiModelProperty(value="expected_tokens")
    private String expected_tokens;
    @ApiModelProperty(value="comments")
    private String comments;

    public Tip (){}

    public Tip (String template, String index, String expected_tokens, String comments) throws InternalErrorException{
        this.template = template;
        try {
            String[] parts = index.split(":");
            int ini_index = Integer.parseInt(parts[0]);
            int fin_index = Integer.parseInt(parts[1]);
            if (ini_index == fin_index) this.index = ini_index + "";
            else this.index = index;
        } catch (Exception e) {
            throw new InternalErrorException("Error while parsing tips.");
        }
        this.expected_tokens = expected_tokens;
        this.comments = comments;
    }

    public String getTemplate() {
        return template;
    }

    public String getIndex() {
        return index;
    }

    public String getExpected_tokens() {
        return expected_tokens;
    }

    public String getComments() {
        return comments;
    }
}
