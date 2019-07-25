package upc.req_quality.service;

import upc.req_quality.entity.*;
import upc.req_quality.entity.input.InputRequirements;
import upc.req_quality.entity.input.Templates;
import upc.req_quality.entity.output.OutputRequirements;
import upc.req_quality.exception.BadBNFSyntaxException;
import upc.req_quality.exception.BadRequestException;
import upc.req_quality.exception.InternalErrorException;
import upc.req_quality.exception.NotFoundException;


import java.util.List;

public interface ConformanceService {

    OutputRequirements checkConformance(String organization, List<Requirement> requirements) throws NotFoundException, BadRequestException, InternalErrorException;

    void enterNewTemplates(Templates templates) throws InternalErrorException, BadBNFSyntaxException;

    List<String> checkOrganizationTemplates(String organization) throws NotFoundException, InternalErrorException;

    void clearOrganizationTemplates(String organization) throws NotFoundException, InternalErrorException;

    void clearDatabase() throws InternalErrorException;
}
