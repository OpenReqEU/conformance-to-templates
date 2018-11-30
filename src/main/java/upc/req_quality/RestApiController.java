package upc.req_quality;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import upc.req_quality.entity.*;
import upc.req_quality.exeption.BadRequestException;
import upc.req_quality.service.ConformanceService;

import java.sql.SQLException;

@RestController
@RequestMapping(value = "/upc/reqquality/check-conformance-to-templates")
public class RestApiController {

    @Autowired
    ConformanceService conformanceService;

    @CrossOrigin
    @RequestMapping(value="/Conformance", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Check requirements conformance to templates", notes = "The operation returns the ids" +
            "of the requirements that do not conform to any template of the chosen organization saved in the database. " +
            "To transform the requirements the operation can use different libraries. The available library is OpenNLP. " +
            "In the future more libraries will be added.")
    @ApiResponses(value = {@ApiResponse(code=200, message = "OK"),
            @ApiResponse(code=410, message = "Not Found")})
    public Requirements check_conformance(@ApiParam(value="The name of the organization", required = true, example = "UPC") @RequestParam String organization,
                                          @ApiParam(value="The library to use", required = true, example = "OpenNLP") @RequestParam String library,
                                          @ApiParam(value="A OpenReqJson with requirements", required = true, example = "SQ-132") @RequestBody Requirements json) throws BadRequestException {
        return conformanceService.check_conformance(library,organization,json.getRequirements());
    }

    @RequestMapping(value="/Clauses", method = RequestMethod.GET)
    @ApiOperation(value = "Returns the API's permitted clauses", notes = "Returns the different clauses permitted with the library selected and the particular API clauses. " +
            "The available library is OpenNLP. In the future more libraries will be added.")
    @ApiResponses(value = {@ApiResponse(code=200, message = "OK")})
    public PermitedClauses check_permited_clauses(@ApiParam(value="The name of the library", required = true, example = "OpenNLP") @RequestParam String library) throws BadRequestException{
        return conformanceService.check_permited_clauses(library);
    }

    @RequestMapping(value="/InTemplates", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Save templates to the database", notes = "This operation save the specified templates to the database. Is needed to follow the rules explained in the top description" +
            " for a proper functioning. ")
    public void enter_new_templates(@RequestBody Templates json) throws BadRequestException{
        conformanceService.enter_new_templates(json);
    }

    @RequestMapping(value="/OutTemplates", method = RequestMethod.GET)
    @ApiOperation(value = "Show the templates saved on the database", notes = "This operation show the templates of the specified organization")
    public Templates check_templates(@ApiParam(value="The name of the organization", required = true, example = "UPC") @RequestParam String organization) throws SQLException {
        return conformanceService.check_all_templates(organization);
    }

    @RequestMapping(value="/DeleteTemplates", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete templates on the database", notes = "This operation deletes all the templates of the specified organization")
    public void clear_templates(@ApiParam(value="The name of the organization", required = true, example = "UPC") @RequestParam String organization) throws SQLException {
        conformanceService.clear_db(organization);
        return;
    }
}
