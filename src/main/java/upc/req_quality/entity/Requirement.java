package upc.req_quality.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import upc.req_quality.entity.output.OutputTip;

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
    private List<OutputTip> tips;

    public Requirement() {
        this.tips = new ArrayList<>();
    }

    public Requirement(String id, String text, List<OutputTip> tips) {
        this.id = id;
        this.text = text;
        this.tips = tips;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public List<OutputTip> getTips() {
        return tips;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }
}
