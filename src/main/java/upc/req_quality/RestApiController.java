package upc.req_quality;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import upc.req_quality.entity.*;
import upc.req_quality.service.ConformanceService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/upc/reqquality/check-conformance-to-templates")
public class RestApiController {

    @Autowired
    ConformanceService conformanceService;

    /*

    @CrossOrigin
    @RequestMapping(value = "/DB/AddReqs", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Add requirements to the Semilar library database", notes = "This method is only a precondition when using semilar component in other operations." +
            " It is responsible for processing the requirements and saving them in a database. For a good performance of the API, very long sentences will be ignored. However" +
            " the processing of requirements can take several minutes. ")
    @ApiResponses(value = {@ApiResponse(code=200, message = "OK"),
            @ApiResponse(code=410, message = "Not Found"),
            @ApiResponse(code=411, message = "Bad request"),
            @ApiResponse(code=511, message = "Component Error")})
    public ResponseEntity<?> addRequirements(@ApiParam(value="OpenreqJson with requirements", required = true, example = "SQ-132") @RequestBody Requirements json) {
        try {
            similarityService.addRequirements(json.getRequirements());
            return new ResponseEntity<>(null,HttpStatus.OK);
        } catch (ComponentException e) {
            return getComponentError(e);
        } catch (BadRequestException e) {
            return getResponseBadRequest(e);
        } catch (NotFoundException e) {
            return getResponseNotFound(e);
        }
    }
     */

    @CrossOrigin
    @RequestMapping(value="/Conformance", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {@ApiResponse(code=200, message = "OK"),
            @ApiResponse(code=410, message = "Not Found")})
    public Requirements check_conformance(@ApiParam(value="A Json with requirements", required = true, example = "SQ-132") @RequestBody Requirements json) {
        return conformanceService.check_conformance(json.getRequirements());
    }

    @RequestMapping(value="/Clauses", method = RequestMethod.GET)
    @ApiOperation(value = "Returns the API's permited clauses", notes = "")
    public PermitedClauses check_permited_clauses() {
        return conformanceService.check_permited_clauses();
    }

    @RequestMapping(value="/InModels", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void enter_new_model(@RequestBody List<Model> json) {
        for (int i = 0; i < json.size(); ++i) {
            Model aux_model = json.get(i);
            conformanceService.enter_new_model(aux_model);
        }
        return;
    }

    @RequestMapping(value="/OutModels", method = RequestMethod.GET)
    public Models check_models() {
        return conformanceService.check_all_models();
    }

    @RequestMapping(value="/DeleteModels", method = RequestMethod.DELETE)
    public void clear_models() {
        conformanceService.clear_db();
        return;
    }
}
