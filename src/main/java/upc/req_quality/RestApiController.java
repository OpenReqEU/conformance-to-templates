package upc.req_quality;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import upc.req_quality.entity.Requirement;
import upc.req_quality.service.ConformanceService;
import upc.req_quality.entity.JsonIn;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/")
public class RestApiController {

    @Autowired
    ConformanceService conformanceService;

    @RequestMapping(value="/upc/reqquality/check-conformance-to-templates", method = RequestMethod.POST)
    public JSONObject check_conformance(@RequestBody String json) {
        JSONObject reqs = new JSONObject(json);
        System.out.println(reqs.toString());
        JSONArray meh = reqs.getJSONArray("requirements");
        List<Requirement> aux = new ArrayList<>();
        for (int i = 0; i < meh.length(); ++i) {
            JSONObject aux_json = meh.getJSONObject(i);
            Requirement req = new Requirement(aux_json.getString("id"),aux_json.getString("text"));
            aux.add(req);
        }
        return conformanceService.check_conformance(aux);
    }
}
