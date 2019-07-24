package upc.req_quality.service;

import upc.req_quality.entity.*;
import upc.req_quality.entity.input.Requirements;
import upc.req_quality.entity.input.Templates;
import upc.req_quality.exception.BadBNFSyntaxException;
import upc.req_quality.exception.BadRequestException;
import upc.req_quality.exception.InternalErrorException;
import upc.req_quality.exception.NotFoundException;


import java.util.List;

public interface ConformanceService {

    Requirements checkConformance(String organization, List<Requirement> requirements) throws NotFoundException, InternalErrorException;

    void enterNewTemplates(Templates templates) throws InternalErrorException, BadBNFSyntaxException;

    List<String> checkOrganizationTemplates(String organization) throws NotFoundException, InternalErrorException;

    void clearOrganizationTemplates(String organization) throws NotFoundException, InternalErrorException;

    void clearDatabase() throws InternalErrorException;
}
