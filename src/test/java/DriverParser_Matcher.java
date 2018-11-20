import upc.req_quality.adapter.Parser_Matcher;
import upc.req_quality.adapter.String_Tree;
import upc.req_quality.exeption.BadBNFSyntaxException;

import java.util.List;

public class DriverParser_Matcher {
    public static void main(String[] args) {
        /*String input1 = "\n<boilerplate> ::= <NP> <NP> <VP> \"something\" | <VP> <yep> | <NP> <nothing>\n" +
                        "<modal> ::= <VP> | <NP>\n" +
                        "<yep> ::= <VP> <NP> <modal>\n" +
                        "<nothing> ::= <VP> | \"\"";

        String input2 = "<boilerplate-conformant> ::= " +
                "<opt-condition> <np> <vp-starting-with-modal> <np> " +
                "<opt-details> | " +
                "<opt-condition> <np> <modal> \"PROVIDE\" <np> " +
                "\"WITH_THE_ABILITY\" <infinitive-vp> <np> <opt-details> | " +
                "<opt-condition> <np> <modal> \"BE_ABLE\" <infinitive-vp> " +
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
        //parser.print_trees();
        parser.print_matcher();

        String[] tokens = {"If","you","lie","lie","lie","lie"};
        String[] chunks = {"NP","<non-punctuation-token>","<NP>","<VP-starting-with-modal>","<np>","<token-sequence-without-subordinate-conjunctions>"};

        boolean result = parser.match(tokens,chunks);
        System.out.println(result);
    }

    private static void test_clone() {
        String_Tree aux = new String_Tree("main");
        String_Tree aux1 = new String_Tree("left");
        String_Tree aux2 = new String_Tree("down-left");
        String_Tree aux3 = new String_Tree("right");

        aux.add_children(aux1);
        aux1.add_children(aux2);
        aux.add_children(aux3);

        aux.print();

        System.out.println("\n\n");

        String_Tree aux_clone = aux.clone_top();

        aux_clone.print();

        List<String_Tree> aux_hojas = aux_clone.getHojas();
        System.out.println(aux_hojas.size());

        for (int i = 0; i < aux_hojas.size(); ++i) System.out.println(aux_hojas.get(i).getData());*/
    }
}
