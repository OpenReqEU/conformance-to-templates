package upc.req_quality.adapter;

import upc.req_quality.entity.SpanOut;

import java.util.List;

public class Generic_template implements AdapterTemplate{

    private static String[] permited_clauses =  new String[]{"<np>","<vp>","<non-punctuation-token>","<vp-starting-with-modal>","<modal>","<infinitive-vp>","<token-sequence-without-subordinate-conjunctions>"};

    private Parser_Matcher parser_matcher;

    public Generic_template(String name, String model) {
        System.out.println("Creating model: " + name);
        this.parser_matcher = new Parser_Matcher(model,permited_clauses);
        System.out.println("Model " + name + " loaded correctly");
    }

    @Override
    public boolean check_template(String[] tokens, String[] tokens_tagged, String[] chunks) {
        return this.parser_matcher.match(tokens,tokens_tagged,chunks);
    }

    public static String[] check_permited_clauses() {
        return permited_clauses;
    }
}
