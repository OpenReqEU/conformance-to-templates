package upc.req_quality.adapter;

public class Generic_template implements AdapterTemplate{

    private Parser_Matcher parser_matcher;

    public Generic_template(String name, String model, String[] permited_clauses) {
        System.out.println("Creating model: " + name);
        this.parser_matcher = new Parser_Matcher(model,permited_clauses);
        System.out.println("Model " + name + " loaded correctly");
    }

    @Override
    public boolean check_template(String[] tokens, String[] tokens_tagged, String[] chunks) {
        return this.parser_matcher.match(tokens,tokens_tagged,chunks);
    }
}
