package upc.req_quality.adapter;

import upc.req_quality.entity.MatcherResponse;
import upc.req_quality.exception.BadBNFSyntaxException;

import java.util.List;

public class GenericTemplate implements AdapterTemplate {

    private ParserMatcher parser_matcher;
    private String organization;
    private String name;

    public GenericTemplate(String name, String organization, List<String> rules, List<String> permited_clauses) throws BadBNFSyntaxException {
        this.organization = organization;
        this.name = name;
        this.parser_matcher = new ParserMatcher(rules,permited_clauses);
        this.parser_matcher.generate_matcher();
        System.out.println("Model " + name + " of " + organization + "generated correctly.");
    }

    @Override
    public MatcherResponse checkTemplate(String[] tokens, String[] tokens_tagged, String[] chunks) {
        return this.parser_matcher.match(tokens,tokens_tagged,chunks);
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
