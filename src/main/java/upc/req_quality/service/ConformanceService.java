package upc.req_quality.service;

import upc.req_quality.entity.*;
import upc.req_quality.entity.input_output.Templates;
import upc.req_quality.exeption.BadBNFSyntaxException;
import upc.req_quality.exeption.BadRequestException;

import java.sql.SQLException;
import java.util.List;

public interface ConformanceService {

    public Requirements check_conformance(String library, String organization, List<Requirement> reqs) throws BadRequestException;

    public PermitedClauses check_permited_clauses(String library) throws BadRequestException;

    public void enter_new_templates(Templates templates) throws BadRequestException, BadBNFSyntaxException, SQLException;

    public Templates check_all_templates(String organization) throws SQLException;

    public void clear_db(String organization) throws SQLException;
}
