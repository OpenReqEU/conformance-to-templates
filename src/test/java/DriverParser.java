import com.google.common.collect.ObjectArrays;
import upc.req_quality.adapter.AdapterPosTagger;
import upc.req_quality.adapter.OpenNLP_PosTagger;
import upc.req_quality.adapter.Parser_Matcher;
import upc.req_quality.exception.BadBNFSyntaxException;
import upc.req_quality.exception.InternalErrorException;

import java.util.ArrayList;
import java.util.List;

public class DriverParser {

    public static void main(String[] args) throws BadBNFSyntaxException, InternalErrorException {

        String[] input = {"<main> ::=  <opt-condition> <np> (md) (vb) <np> | <opt-condition> <np> <modal> %PROVIDE <np> %WITH %THE %ABILITY <infinitive-vp> <np> <*> | <opt-condition> <np> <modal> %BE %ABLE <vp> <np> | <opt-condition> <np> <modal> <vp> <np>",
                "<conditional-keyword> ::= %IF | %AFTER | %AS %SOON %AS | %AS %LONG %AS",
                "<modal> ::= %SHALL | %SHOULD | %WOULD",
                "<opt-condition> ::= <conditional-keyword> | (all)",
                "<infinitive-vp> ::= %to <vp>"};

        List<String> input_modified = new ArrayList<>();
        for (String text: input) input_modified.add(text);

        test_initial_checking(input_modified);

        AdapterPosTagger tagger = OpenNLP_PosTagger.getInstance();
        List<String> pos_tags = tagger.getPos_tags();
        List<String> sen_tags = tagger.getSentence_tags();
        pos_tags.addAll(sen_tags);
        Parser_Matcher parser = new Parser_Matcher(input_modified,pos_tags);

        try {
            parser.generate_matcher();
        } catch (BadBNFSyntaxException e) {
            e.printStackTrace();
        }
        parser.print_trees();
        System.out.println("\n");
        parser.print_matcher();
    }

    private static void test_initial_checking(List<String> input) throws BadBNFSyntaxException{
        Parser_Matcher parser = new Parser_Matcher();
        List<Parser_Matcher.Rule> rules = parser.parse_rules_and_check(input);
        for (Parser_Matcher.Rule rule: rules) {
            System.out.println(rule.getTitle() + ". " + rule.getDescription());
        }
    }
}
