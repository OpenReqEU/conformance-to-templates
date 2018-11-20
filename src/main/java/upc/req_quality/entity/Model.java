package upc.req_quality.entity;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "Model", description = "A model to be cheched against requirements")
public class Model implements Serializable {

    @ApiModelProperty(value="id")
    private int id;
    @ApiModelProperty(value="name")
    private String name;
    @ApiModelProperty(value="rules")
    private String rules;

    public Model() {}

    public Model(String name, String rules) {
        this.id = id;
        this.name = name;
        this.rules = rules;
    }

    public Model(int id, String name, String rules) {
        this.id = id;
        this.name = name;
        this.rules = rules;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRules() {
        return rules;
    }
}
