package upc.req_quality.adapter_template;

import upc.req_quality.entity.MatcherResponse;
import upc.req_quality.entity.ParsedTemplate;
import upc.req_quality.entity.Rule;
import upc.req_quality.exception.BadBNFSyntaxException;

import java.util.List;

public class GenericTemplate implements AdapterTemplate {

    @Override
    public ParsedTemplate createTemplate(String name, String organization, List<Rule> rules, List<String> permittedClauses) throws BadBNFSyntaxException {
        Parser parser = new Parser();
        StringTree parsedRules = parser.parseRules(rules, permittedClauses);
        return new ParsedTemplate(name, organization, parsedRules);
    }

    @Override
    public MatcherResponse matchTemplate(ParsedTemplate parsedTemplate, String[] tokens, String[] tokensTagged, String[] chunks) {
        Matcher matcher = new Matcher();
        return matcher.matchTemplate(parsedTemplate.getRules(), tokens, tokensTagged, chunks);
    }
}
