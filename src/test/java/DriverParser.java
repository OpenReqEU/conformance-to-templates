import com.google.common.collect.ObjectArrays;
import upc.req_quality.adapter.OpenNLP_PosTagger;
import upc.req_quality.adapter.Parser_Matcher;
import upc.req_quality.exeption.BadBNFSyntaxException;

public class DriverParser {

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
        parser.print_trees();
        System.out.println("\n");
        parser.print_matcher();
    }
}
