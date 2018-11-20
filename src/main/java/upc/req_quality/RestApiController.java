package upc.req_quality;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import upc.req_quality.entity.*;
import upc.req_quality.service.ConformanceService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/")
public class RestApiController {

    @Autowired
    ConformanceService conformanceService;

    @RequestMapping(value="/upc/reqquality/check-conformance-to-templates/Conformance", method = RequestMethod.POST)
    public List<Requirement> check_conformance(@RequestBody List<Requirement> json) {
        return conformanceService.check_conformance(json);
    }

    @RequestMapping(value="/upc/reqquality/check-conformance-to-templates/Clauses", method = RequestMethod.GET)
    public PermitedClauses check_permited_clauses() {
        return conformanceService.check_permited_clauses();
    }

    @RequestMapping(value="/upc/reqquality/check-conformance-to-templates/InModels", method = RequestMethod.POST)
    public void enter_new_model(@RequestBody List<Model> json) {
        for (int i = 0; i < json.size(); ++i) {
            Model aux_model = json.get(i);
            conformanceService.enter_new_model(aux_model);
        }
        return;
    }

    @RequestMapping(value="/upc/reqquality/check-conformance-to-templates/OutModels", method = RequestMethod.GET)
    public List<Model> check_models() {
        return conformanceService.check_all_models();
    }

    @RequestMapping(value="/upc/reqquality/check-conformance-to-templates/DeleteModels", method = RequestMethod.DELETE)
    public void clear_models() {
        conformanceService.clear_db();
        return;
    }
}
