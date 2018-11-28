package upc.req_quality.service;

import upc.req_quality.entity.*;

import java.util.List;

public interface ConformanceService {

    public Requirements check_conformance(List<Requirement> reqs);

    public PermitedClauses check_permited_clauses();

    public void enter_new_model(Model model);

    public Models check_all_models();

    public void clear_db();
}
