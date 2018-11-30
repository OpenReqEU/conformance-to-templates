package upc.req_quality.adapter;

public class Generic_template implements AdapterTemplate{

    private Parser_Matcher parser_matcher;
    private String organization;
    private String library;

    public Generic_template(String name, String organization, String library, String rules, String[] permited_clauses) {
        System.out.println("Creating model: " + name);
        this.organization = organization;
        this.library = library;
        this.parser_matcher = new Parser_Matcher(rules,permited_clauses);
        System.out.println("Model " + name + " of " + organization + " loaded correctly");
    }

    @Override
    public boolean check_template(String[] tokens, String[] tokens_tagged, String[] chunks) {
        return this.parser_matcher.match(tokens,tokens_tagged,chunks);
    }

    @Override
    public String check_organization() {
        return organization;
    }
}
