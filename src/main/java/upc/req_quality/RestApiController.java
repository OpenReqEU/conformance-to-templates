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
import upc.req_quality.entity.input.InputTemplates;
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
    @ApiOperation(value = "Check requirements conformance to templates", notes = "The operation returns the ids" +
            " of the requirements that do not conform to any template saved in the database of the chosen organization.")
    @ApiResponses(value = {@ApiResponse(code=200, message = "OK"),
                           @ApiResponse(code=400, message = "Bad request"),
                           @ApiResponse(code=452, message = "Bad BNF syntax"),
                           @ApiResponse(code=500, message = "Bad request")})
    public ResponseEntity checkConformance(@ApiParam(value="The name of the organization", required = true, example = "UPC") @RequestParam String organization,
                                            @ApiParam(value="A OpenReqJson with requirements", required = true, example = "SQ-132") @RequestBody InputRequirements json) {
        try {
            return new ResponseEntity<>(conformanceService.checkConformance(organization,json.getRequirements()), HttpStatus.OK);
        } catch (ComponentException e) {
            return getComponentError(e);
        }
    }

    @RequestMapping(value="/InTemplates", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Save templates to the database", notes = "This operation saves the specified templates to the database. The templates must follow the structure explained in the " +
            "top description.")
    @ApiResponses(value = {@ApiResponse(code=200, message = "OK"),
                           @ApiResponse(code=452, message = "Bad BNF syntax"),
                           @ApiResponse(code=500, message = "Internal exception")})
    public ResponseEntity enterNewTemplates(@RequestBody InputTemplates json) {
        try {
            conformanceService.enterNewTemplates(json);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (ComponentException e) {
            return getComponentError(e);
        }
    }

    @RequestMapping(value="/OutTemplates", method = RequestMethod.GET)
    @ApiOperation(value = "Show the templates saved on the database", notes = "This operation shows the templates saved in the database of the specified organization")
    @ApiResponses(value = {@ApiResponse(code=200, message = "OK"),
                           @ApiResponse(code=452, message = "Bad BNF syntax"),
                           @ApiResponse(code=500, message = "Internal exception")})
    public ResponseEntity checkTemplates(@ApiParam(value="The name of the organization", required = true, example = "UPC") @RequestParam String organization) {
        try {
            return new ResponseEntity<>(conformanceService.checkOrganizationTemplates(organization),HttpStatus.OK);
        } catch (ComponentException e) {
            return getComponentError(e);
        }
    }

    @RequestMapping(value="/DeleteTemplates", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete templates on the database", notes = "This operation deletes all the templates of the specified organization.")
    @ApiResponses(value = {@ApiResponse(code=200, message = "OK"),
                           @ApiResponse(code=452, message = "Bad BNF syntax"),
                           @ApiResponse(code=500, message = "Internal exception")})
    public ResponseEntity clearTemplates(@ApiParam(value="The name of the organization", required = true, example = "UPC") @RequestParam String organization) {
        try {
            conformanceService.clearDatabase(organization);
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
