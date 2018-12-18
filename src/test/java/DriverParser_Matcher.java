import com.google.common.collect.ObjectArrays;
import upc.req_quality.adapter.AdapterPosTagger;
import upc.req_quality.adapter.OpenNLP_PosTagger;
import upc.req_quality.adapter.Parser_Matcher;
import upc.req_quality.entity.Requirement;
import upc.req_quality.exeption.BadBNFSyntaxException;

public class DriverParser_Matcher {
    public static void main(String[] args) {

        String input = "#<main> ::=  <opt-condition> <np> (md) (vb) <np> | <opt-condition> <np> <modal> %PROVIDE <np> %WITH %THE %ABILITY <infinitive-vp> <np> <*> | <opt-condition> <np> <modal> %BE %ABLE <vp> <np> | <opt-condition> <np> <modal> <vp> <np>\n" +
                "#<conditional-keyword> ::= %IF | %AFTER | %AS %SOON %AS | %AS %LONG %AS \n" +
                "#<modal> ::= %SHALL | %SHOULD | %WOULD\n" +
                "#<opt-condition> ::= <conditional-keyword> | (all)\n" +
                "#<infinitive-vp> ::= %to <vp>";


        String[] permited_clauses = ObjectArrays.concat(new OpenNLP_PosTagger().getPos_tags(), new OpenNLP_PosTagger().getSentence_tags(), String.class);
        Parser_Matcher parser = new Parser_Matcher(input,permited_clauses);
        try {
            parser.generate_matcher();
        } catch (BadBNFSyntaxException e) {
            e.printStackTrace();
        }

        //parser.print_trees();
        parser.print_matcher();

        AdapterPosTagger tagger = new OpenNLP_PosTagger();
        String text = "As soon as the dishes have been prepared, the system shall inform the guest that the dishes are ready for collection.";
        Requirement aux_req = new Requirement("1",text);

        String[] tokens = tagger.tokenizer(aux_req.getText());
        String[] tokens_tagged = tagger.pos_tagger(tokens);
        String[] chunks = tagger.chunker(tokens,tokens_tagged);

        for (int i = 0; i < tokens.length; ++i) {
            String result = tokens[i] +  " " + tokens_tagged[i] + " " + chunks[i];
            System.out.println(result);
        }

        boolean result = parser.match(tokens,tokens_tagged,chunks);
        System.out.println(result);
    }
}
