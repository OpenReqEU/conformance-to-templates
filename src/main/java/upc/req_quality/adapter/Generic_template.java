package upc.req_quality.adapter;

import upc.req_quality.entity.Matcher_Response;
import upc.req_quality.exception.BadBNFSyntaxException;

import java.util.List;

public class Generic_template implements AdapterTemplate{

    private Parser_Matcher parser_matcher;
    private String organization;
    private String name;

    public Generic_template(String name, String organization, List<String> rules, List<String> permited_clauses) throws BadBNFSyntaxException {
        this.organization = organization;
        this.name = name;
        this.parser_matcher = new Parser_Matcher(rules,permited_clauses);
        this.parser_matcher.generate_matcher();
        System.out.println("Model " + name + " of " + organization + "generated correctly.");
    }

    @Override
    public Matcher_Response check_template(String[] tokens, String[] tokens_tagged, String[] chunks) {
        return this.parser_matcher.match(tokens,tokens_tagged,chunks);
    }

    @Override
    public String check_organization() {
        return organization;
    }

    @Override
    public String check_name() {
        return name;
    }
}
