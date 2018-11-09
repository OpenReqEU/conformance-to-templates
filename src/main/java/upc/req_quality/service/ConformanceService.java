package upc.req_quality.service;

import org.json.JSONObject;
import upc.req_quality.entity.Requirement;

import java.util.List;

public interface ConformanceService {
    public JSONObject check_conformance(List<Requirement> reqs);
}
