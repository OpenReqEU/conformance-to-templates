import upc.req_quality.adapter.Parser_Matcher;
import upc.req_quality.exeption.BadBNFSyntaxException;

public class DriverParser_Matcher {
    public static void main(String[] args) {
        String input1 = "\n<boilerplate> ::= <NP> <NP> <VP> \"something\" | <VP> <yep> | <NP> <nothing>\n" +
                        "<modal> ::= <VP> | <NP>\n" +
                        "<yep> ::= <VP> <NP> <modal>\n" +
                        "<nothing> ::= <VP> | \"\"";

        String input2 = "<boilerplate-conformant> ::= " +
                "<opt-condition> <np> <vp-starting-with-modal> <np> " +
                "<opt-details> | " +
                "<opt-condition> <np> <modal> \"PROVIDE\" <np> " +
                "\"WITH THE ABILITY\" <infinitive-vp> <np> <opt-details> | " +
                "<opt-condition> <np> <modal> \"BE ABLE\" <infinitive-vp> " +
                "<np> <opt-details>\n" +
                "<opt-details> ::= \"\" |" +
                "<token-sequence-without-subordinate-conjunctions>\n" +
                "<modal> ::= \"SHALL\" | \"SHOULD\" | \"WOULD\"\n" +
                "<conditional-keyword> ::= \"IF\" | \"AFTER\" | \"AS_SOON_AS\" |" +
                "\"AS_LONG_AS\"\n" +
                "<opt-condition> ::= \"\" |" +
                "<conditional-keyword> <non-punctuation-token>";

        //System.out.println(input);
        Parser_Matcher parser = new Parser_Matcher(input2);
        try {
            parser.generate_matcher();
        } catch (BadBNFSyntaxException e) {
            e.printStackTrace();
        }
        parser.print_trees();

        parser.print_matcher();
    }
}
