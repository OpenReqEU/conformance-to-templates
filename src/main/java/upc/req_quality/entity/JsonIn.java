package upc.req_quality.entity;

import java.util.ArrayList;
import java.util.List;

public class JsonIn {

    private List<Requirement> reqs;

    public JsonIn() {
    }

    public JsonIn(List<Requirement> reqs) {
        this.reqs = reqs;
    }

    public List<Requirement> getReqs() {
        return reqs;
    }

    public void setReqs(List<Requirement> reqs) {
        this.reqs = reqs;
    }
}
