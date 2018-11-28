package upc.req_quality.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "Models", description = "A list of models")
public class Models implements Serializable {

    @ApiModelProperty(value="models")
    private List<Model> models;

    public Models() {
        this.models = new ArrayList<>();
    }

    public Models(List<Model> models) {
        this.models = models;
    }

}
