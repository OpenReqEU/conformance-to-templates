package upc.req_quality.service;

import upc.req_quality.entity.*;
import upc.req_quality.entity.input_output.Requirements;
import upc.req_quality.entity.input_output.Templates;
import upc.req_quality.exception.BadBNFSyntaxException;
import upc.req_quality.exception.BadRequestException;
import upc.req_quality.exception.InternalErrorException;


import java.util.List;

public interface ConformanceService {

    Requirements checkConformance(String organization, List<Requirement> reqs) throws BadRequestException, BadBNFSyntaxException, InternalErrorException;

    void enterNewTemplates(Templates templates) throws InternalErrorException, BadBNFSyntaxException;

    Templates checkOrganizationTemplates(String organization) throws InternalErrorException, BadBNFSyntaxException ;

    void clearDatabase(String organization) throws InternalErrorException, BadBNFSyntaxException;
}
