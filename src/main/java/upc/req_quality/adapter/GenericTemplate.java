package upc.req_quality.adapter;

import upc.req_quality.entity.MatcherResponse;
import upc.req_quality.exception.BadBNFSyntaxException;

import java.util.List;

public class GenericTemplate implements AdapterTemplate {

    private ParserMatcher parserMatcher;
    private String organization;
    private String name;

    public GenericTemplate(String name, String organization, List<String> rules, List<String> permitedClauses) throws BadBNFSyntaxException {
        this.organization = organization;
        this.name = name;
        this.parserMatcher = new ParserMatcher(rules,permitedClauses);
        this.parserMatcher.generateMatcher();
        System.out.println("Model " + name + " of " + organization + "generated correctly.");
    }

    @Override
    public MatcherResponse checkTemplate(String[] tokens, String[] tokensTagged, String[] chunks) {
        return this.parserMatcher.match(tokens,tokensTagged,chunks);
    }

    @Override
    public String checkOrganization() {
        return organization;
    }

    @Override
    public String checkName() {
        return name;
    }
}
