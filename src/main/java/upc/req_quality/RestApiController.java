package upc.req_quality;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upc.req_quality.entity.input.InputRequirements;
import upc.req_quality.entity.input.Templates;
import upc.req_quality.exception.ComponentException;
import upc.req_quality.service.ConformanceService;


import java.util.LinkedHashMap;

@RestController
@RequestMapping(value = "/upc/reqquality/check-conformance-to-templates")
public class RestApiController {

    @Autowired
    ConformanceService conformanceService;

    @CrossOrigin
    @RequestMapping(value="/Conformance", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Check requirements conformance to templates", notes = "<p>Checks if the input requirements follow at least one of the templates of the specified organization. " +
            "The method returns the requirements that do not conform to any template and for each one a set of tips to correct the structure of the requirement to " +
            "fit the organization's templates.</p>", tags = "Main methods")
    @ApiResponses(value = {@ApiResponse(code=200, message = "OK"),
                           @ApiResponse(code=400, message = "Bad request: An input requirement has no id"),
                           @ApiResponse(code=404, message = "Not found: The organization UPC has no templates in the database"),
                           @ApiResponse(code=500, message = "Internal error")})
    public ResponseEntity checkConformance(@ApiParam(value="The name of the organization", required = true, example = "UPC") @RequestParam String organization,
                                            @ApiParam(value="A OpenReqJson with requirements", required = true, example = "SQ-132") @RequestBody InputRequirements json) {
        try {
            return new ResponseEntity<>(conformanceService.checkConformance(organization,json.getRequirements()), HttpStatus.OK);
        } catch (ComponentException e) {
            return getComponentError(e);
        }
    }

    @RequestMapping(value="/InTemplates", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds templates to an organization", notes = "<p>Adds the input templates to the service's database and assigns them to a specified organization. " +
            "The templates must be written following a defined format explained in the top description.</p>", tags = "Main methods")
    @ApiResponses(value = {@ApiResponse(code=200, message = "OK"),
                           @ApiResponse(code=400, message = "Bad BNF syntax"),
                           @ApiResponse(code=500, message = "Internal error")})
    public ResponseEntity enterNewTemplates(@RequestBody Templates json) {
        try {
            conformanceService.enterNewTemplates(json);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (ComponentException e) {
            return getComponentError(e);
        }
    }

    @RequestMapping(value="/OutTemplates", method = RequestMethod.GET)
    @ApiOperation(value = "Returns the templates of an organization", notes = "Returns an array with the names of the templates saved in the database of the specified organization", tags = "Main methods")
    @ApiResponses(value = {@ApiResponse(code=200, message = "OK"),
                           @ApiResponse(code=404, message = "Not found: The organization UPC has no templates in the database"),
                           @ApiResponse(code=500, message = "Internal error")})
    public ResponseEntity checkTemplates(@ApiParam(value="The name of the organization", required = true, example = "UPC") @RequestParam String organization) {
        try {
            return new ResponseEntity<>(conformanceService.checkOrganizationTemplates(organization),HttpStatus.OK);
        } catch (ComponentException e) {
            return getComponentError(e);
        }
    }

    @RequestMapping(value="/DeleteOrganizationTemplates", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete all templates of an organization", notes = "Deletes all the templates of the specified organization.", tags = "Auxiliary methods")
    @ApiResponses(value = {@ApiResponse(code=200, message = "OK"),
            @ApiResponse(code=404, message = "Not found: The organization UPC has no templates in the database"),
            @ApiResponse(code=500, message = "Internal error")})
    public ResponseEntity clearTemplates(@ApiParam(value="The name of the organization", required = true, example = "UPC") @RequestParam String organization) {
        try {
            conformanceService.clearOrganizationTemplates(organization);
            return new ResponseEntity<>(null,HttpStatus.OK);
        } catch (ComponentException e) {
            return getComponentError(e);
        }
    }

    @RequestMapping(value="/ClearDatabase", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete all data from the database", notes = "Deletes all data from the database. If this method is called while a calculation is being carried out, unforeseen results may occur.", tags = "Auxiliary methods")
    @ApiResponses(value = {@ApiResponse(code=200, message = "OK"),
                           @ApiResponse(code=500, message = "Internal error")})
    public ResponseEntity clearTemplates() {
        try {
            conformanceService.clearDatabase();
            return new ResponseEntity<>(null,HttpStatus.OK);
        } catch (ComponentException e) {
            return getComponentError(e);
        }
    }

    private ResponseEntity getComponentError(ComponentException e) {
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        result.put("status", e.getStatus()+"");
        result.put("error", e.getError());
        result.put("message", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.valueOf(e.getStatus()));
    }

}
