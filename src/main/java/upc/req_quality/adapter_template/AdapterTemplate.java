package upc.req_quality.adapter_template;

import upc.req_quality.entity.MatcherResponse;
import upc.req_quality.entity.ParsedTemplate;
import upc.req_quality.entity.Rule;
import upc.req_quality.exception.BadBNFSyntaxException;

import java.util.List;

public interface AdapterTemplate {

    ParsedTemplate createTemplate(String name, String organization, List<Rule> rules, List<String> permitedClauses) throws BadBNFSyntaxException;

    MatcherResponse matchTemplate(ParsedTemplate parsedTemplate, String[] tokens, String[] tokensTagged, String[] chunks);
}
