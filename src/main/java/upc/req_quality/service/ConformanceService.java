package upc.req_quality.service;

import upc.req_quality.entity.*;
import upc.req_quality.entity.input_output.Requirements;
import upc.req_quality.entity.input_output.Templates;
import upc.req_quality.exeption.BadBNFSyntaxException;
import upc.req_quality.exeption.BadRequestException;
import upc.req_quality.exeption.InternalErrorException;


import java.util.List;

public interface ConformanceService {

    public Requirements check_conformance(String organization, List<Requirement> reqs) throws BadRequestException, BadBNFSyntaxException, InternalErrorException;

    public void enter_new_templates(Templates templates) throws InternalErrorException, BadBNFSyntaxException;

    public Templates check_organization_templates(String organization) throws InternalErrorException, BadBNFSyntaxException ;

    public void clear_db(String organization) throws InternalErrorException, BadBNFSyntaxException;
}
