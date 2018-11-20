package upc.req_quality.service;

import upc.req_quality.entity.*;

import java.util.List;

public interface ConformanceService {

    public List<Requirement> check_conformance(List<Requirement> reqs);

    public PermitedClauses check_permited_clauses();

    public void enter_new_model(Model model);

    public List<Model> check_all_models();

    public void clear_db();
}
