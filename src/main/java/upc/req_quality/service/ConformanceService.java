package upc.req_quality.service;

import upc.req_quality.entity.*;
import upc.req_quality.entity.input.InputRequirements;
import upc.req_quality.entity.input.InputTemplates;
import upc.req_quality.exception.BadBNFSyntaxException;
import upc.req_quality.exception.BadRequestException;
import upc.req_quality.exception.InternalErrorException;


import java.util.List;

public interface ConformanceService {

    InputRequirements checkConformance(String organization, List<Requirement> reqs) throws BadRequestException, BadBNFSyntaxException, InternalErrorException;

    void enterNewTemplates(InputTemplates inputTemplates) throws InternalErrorException, BadBNFSyntaxException;

    InputTemplates checkOrganizationTemplates(String organization) throws InternalErrorException, BadBNFSyntaxException ;

    void clearDatabase(String organization) throws InternalErrorException, BadBNFSyntaxException;
}
