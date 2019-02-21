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
import upc.req_quality.entity.*;
import upc.req_quality.exeption.BadBNFSyntaxException;
import upc.req_quality.exeption.BadRequestException;
import upc.req_quality.service.ConformanceService;



import java.io.*;
import java.sql.SQLException;
import java.util.LinkedHashMap;

@RestController
@RequestMapping(value = "/upc/reqquality/check-conformance-to-templates")
public class RestApiController {

    @Autowired
    ConformanceService conformanceService;




    @CrossOrigin
    @RequestMapping(value="/Conformance", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Check requirements conformance to templates", notes = "The operation returns the ids" +
            " of the requirements that do not conform to any template of the chosen library and organization saved in the database." +
            " To transform the requirements the operation can use different libraries. The available library is OpenNLP." +
            " In the future more libraries will be added.")
    @ApiResponses(value = {@ApiResponse(code=200, message = "OK"),
                           @ApiResponse(code=411, message = "Bad request")})
    public ResponseEntity<?> check_conformance(@ApiParam(value="The name of the organization", required = true, example = "UPC") @RequestParam String organization,
                                            @ApiParam(value="The library to use", required = true, example = "OpenNLP") @RequestParam String library,
                                            @ApiParam(value="A OpenReqJson with requirements", required = true, example = "SQ-132") @RequestBody Requirements json) {
        try {
            return new ResponseEntity<>(conformanceService.check_conformance(library,organization,json.getRequirements()), HttpStatus.OK);
        } catch (BadRequestException e) {
           return getBadRequest(e);
        }
    }

    @RequestMapping(value="/Clauses", method = RequestMethod.GET)
    @ApiOperation(value = "Returns the API's permitted clauses", notes = "Returns the different clauses permitted by the library selected and the particular API clauses. " +
            "The available library is OpenNLP. In the future more libraries will be added.")
    @ApiResponses(value = {@ApiResponse(code=200, message = "OK"),
                           @ApiResponse(code=411, message = "Bad request")})
    public ResponseEntity<?> check_permited_clauses(@ApiParam(value="The name of the library", required = true, example = "OpenNLP") @RequestParam String library) {
        try {
            return new ResponseEntity<>(conformanceService.check_permited_clauses(library),HttpStatus.OK);
        } catch (BadRequestException e) {
            return getBadRequest(e);
        }
    }

    @RequestMapping(value="/InTemplates", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Save templates to the database", notes = "This operation save the specified templates to the database. Is needed to follow the rules explained in the top description" +
            " for a proper functioning. It must be specified the library used to write the template.")
    @ApiResponses(value = {@ApiResponse(code=200, message = "OK"),
                           @ApiResponse(code=411, message = "Bad request"),
                           @ApiResponse(code=412, message = "Bad BNF syntax")})
    public ResponseEntity<?> enter_new_templates(@RequestBody Templates json) {
        try {
            conformanceService.enter_new_templates(json);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (BadRequestException e) {
            return getBadRequest(e);
        } catch (BadBNFSyntaxException e) {
            e.printStackTrace();
            return getBadBNFSyntax(e);
        } catch (SQLException e) {
            return getDatabaseException(e);
        }
    }

    @RequestMapping(value="/OutTemplates", method = RequestMethod.GET)
    @ApiOperation(value = "Show the templates saved on the database", notes = "This operation show the templates of the specified organization")
    @ApiResponses(value = {@ApiResponse(code=200, message = "OK"),
                           @ApiResponse(code=413, message = "Database exception")})
    public ResponseEntity<?> check_templates(@ApiParam(value="The name of the organization", required = true, example = "UPC") @RequestParam String organization) {
        try {
            return new ResponseEntity<>(conformanceService.check_all_templates(organization),HttpStatus.OK);
        } catch (SQLException e) {
            return getDatabaseException(e);
        }
    }

    @RequestMapping(value="/DeleteTemplates", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete templates on the database", notes = "This operation deletes all the templates of the specified organization")
    @ApiResponses(value = {@ApiResponse(code=200, message = "OK"),
                           @ApiResponse(code=413, message = "Database exception")})
    public ResponseEntity<?> clear_templates(@ApiParam(value="The name of the organization", required = true, example = "UPC") @RequestParam String organization) {
        try {
            conformanceService.clear_db(organization);
            return new ResponseEntity<>(null,HttpStatus.OK);
        } catch (SQLException e) {
            return getDatabaseException(e);
        }
    }

    private ResponseEntity<?> getBadRequest(BadRequestException e) {
        e.printStackTrace();
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        result.put("status", "411");
        result.put("error", "Bad request");
        result.put("message", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.valueOf(411));
    }

    private ResponseEntity<?> getBadBNFSyntax(BadBNFSyntaxException e) {
        e.printStackTrace();
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        result.put("status", "412");
        result.put("error", "Bad BNF syntax");
        result.put("message", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.valueOf(412));
    }

    private ResponseEntity<?> getDatabaseException(SQLException e) {
        e.printStackTrace();
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        result.put("status", "413");
        result.put("error", "Database exception");
        result.put("message", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.valueOf(413));
    }

}
