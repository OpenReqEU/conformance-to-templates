package upc.req_quality.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import upc.req_quality.entity.input_output.Tip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "Requirement", description = "A requirement with id and text")
public class Requirement implements Serializable {

    @ApiModelProperty(value="id")
    private String id;
    @ApiModelProperty(value="text")
    private String text;
    @ApiModelProperty(value="tips")
    private List<Tip> tips;
    @ApiModelProperty(value="tokens")
    private List<String> tokens;

    public Requirement() {
        this.tips = new ArrayList<>();
    }

    public Requirement(String id, String text, List<Tip> tips, List<String> tokens) {
        this.id = id;
        this.text = text;
        this.tips = tips;
        this.tokens = tokens;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public List<Tip> getTips() {
        return tips;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }
}
