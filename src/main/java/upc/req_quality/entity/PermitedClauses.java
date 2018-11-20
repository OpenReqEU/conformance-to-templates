package upc.req_quality.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = false)
@ApiModel(value = "Permited_clauses", description = "These are all the permited clauses in the API")
public class PermitedClauses implements Serializable {

    @JsonProperty(value="pos_tags")
    private String[] pos_tags;
    @JsonProperty(value="sentence_tags")
    private String[] sentence_tags;

    public PermitedClauses() {

    }

    public PermitedClauses(String[] pos_tags, String[] sentence_tags) {
        this.pos_tags = pos_tags;
        this.sentence_tags = sentence_tags;
    }
}
