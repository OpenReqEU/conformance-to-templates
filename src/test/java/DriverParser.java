import com.google.common.collect.ObjectArrays;
import upc.req_quality.adapter.AdapterPosTagger;
import upc.req_quality.adapter.OpenNLP_PosTagger;
import upc.req_quality.adapter.Parser_Matcher;
import upc.req_quality.exeption.BadBNFSyntaxException;

public class DriverParser {

    public static void main(String[] args) {
        String input2 = "#<main> ::= \n" +
                "    <opt-condition> <np> (md) <vp> <np> <*> |\n" +
                "    <opt-condition> <np> <modal> \"PROVIDE\" <np> \"WITH\" \"THE\" \"ABILITY\" <vp> <np> <*> |\n" +
                "    <opt-condition> <np> <modal> \"BE\" \"ABLE\" <vp> <np> <*>\n" +
                "#<conditional-keyword> ::= \"IF\" | \"AFTER\" | \"AS\" \"SOON\" \"AS\" | \"AS\" \"LONG\" \"AS\"\n" +
                "#<modal> ::= \"SHALL\" | \"SHOULD\" | \"WOULD\"\n" +
                "#<opt-condition> ::= <conditional-keyword> | (all)";
        String[] permited_clauses = ObjectArrays.concat(OpenNLP_PosTagger.getPos_tags(),OpenNLP_PosTagger.getSentence_tags(),String.class);
        Parser_Matcher parser = new Parser_Matcher(input2,permited_clauses);
        try {
            parser.generate_matcher();
        } catch (BadBNFSyntaxException e) {
            e.printStackTrace();
        }
        parser.print_trees();
        parser.print_matcher();
    }
}
