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
    private String expectedTokens;
    @ApiModelProperty(value="comments")
    private String comments;

    public Tip (){}

    public Tip (String template, String index, String expectedTokens, String comments) throws InternalErrorException{
        this.template = template;
        try {
            String[] parts = index.split(":");
            int iniIndex = Integer.parseInt(parts[0]);
            int finIndex = Integer.parseInt(parts[1]);
            if (iniIndex == finIndex) this.index = iniIndex + "";
            else this.index = index;
        } catch (Exception e) {
            throw new InternalErrorException("Error while parsing tips.");
        }
        this.expectedTokens = expectedTokens;
        this.comments = comments;
    }

    public String getTemplate() {
        return template;
    }

    public String getIndex() {
        return index;
    }

    public String getExpectedTokens() {
        return expectedTokens;
    }

    public String getComments() {
        return comments;
    }
}
