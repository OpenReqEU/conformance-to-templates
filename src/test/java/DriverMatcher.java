import org.json.JSONArray;
import org.json.JSONObject;
import upc.req_quality.adapter.Parser_Matcher;
import upc.req_quality.adapter.String_Tree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DriverMatcher {

    private static class TagsAndOuput {

        private String[] tokens;
        private String[] tokens_tags;
        private String[] sentence_tags;

        public TagsAndOuput(List<String> aux_tokens, List<String> aux_tokens_tags, List<String> aux_sentence_tags) {

            tokens = new String[aux_tokens.size()];
            tokens = aux_tokens.toArray(tokens);

            tokens_tags = new String[aux_tokens_tags.size()];
            tokens_tags = aux_tokens_tags.toArray(tokens_tags);

            sentence_tags = new String[aux_sentence_tags.size()];
            sentence_tags = aux_sentence_tags.toArray(sentence_tags);
        }

        public String[] getTokens() {
            return tokens;
        }

        public String[] getTokens_tags() {
            return tokens_tags;
        }

        public String[] getSentence_tags() {
            return sentence_tags;
        }

        public void print() {
            System.out.println("tokens");
            String result = "";
            for (int i = 0; i < tokens.length; ++i) result += tokens[i] + " ";
            System.out.println(result + " " + tokens.length);
            System.out.println("tokens_tags");
            result = "";
            for (int i = 0; i < tokens_tags.length; ++i) result += tokens_tags[i] + " ";
            System.out.println(result + " " + tokens_tags.length);
            System.out.println("sentence_tags");
            result = "";
            for (int i = 0; i < sentence_tags.length; ++i) result += sentence_tags[i] + " ";
            System.out.println(result + " " + sentence_tags.length);
        }
    }


    private static Input_Output io = Input_Output.getInstance();
    private static Parser_Matcher ma = new Parser_Matcher();

    public static void main(String[] args) throws Exception {
        String PATH = "/home/ferran/Documents/trabajo/repositories/conformance_to_templates";

        System.out.println("Tests with one element on the matcher");
        one_test(1, true,PATH+"/testing/unit_tests/matcher/test1/tree.txt", PATH+"/testing/unit_tests/matcher/test1/req1.json");

        System.out.println("\nTests with two elements on the matcher");
        one_test(1, true,PATH+"/testing/unit_tests/matcher/test2/tree1.txt", PATH+"/testing/unit_tests/matcher/test1/req1.json");
        one_test(2, false,PATH+"/testing/unit_tests/matcher/test2/tree2.txt", PATH+"/testing/unit_tests/matcher/test1/req1.json");
        one_test(3, false,PATH+"/testing/unit_tests/matcher/test2/tree3.txt", PATH+"/testing/unit_tests/matcher/test1/req1.json");
        one_test(4, false,PATH+"/testing/unit_tests/matcher/test2/tree4.txt", PATH+"/testing/unit_tests/matcher/test1/req1.json");
        one_test(5, false,PATH+"/testing/unit_tests/matcher/test2/tree5.txt", PATH+"/testing/unit_tests/matcher/test1/req1.json");
        one_test(6, false,PATH+"/testing/unit_tests/matcher/test2/tree6.txt", PATH+"/testing/unit_tests/matcher/test1/req1.json");


    }

    public static void one_test(int id, boolean b, String input_tree_path, String input_req_path) throws Exception {

        List<List<String>> aux_tree = io.read_tree_matrix(input_tree_path);
        String_Tree tree = parse_input_tree(aux_tree);

        ma.setMymatcher(tree);
        //System.out.println("-------------------------------------");
        //ma.print_matcher();
        //System.out.println("-------------------------------------");

        TagsAndOuput tags = read_input_tags(input_req_path);
        //tags.print();

        //System.out.println("\n\n\n\n");

        boolean result = ma.match(tags.getTokens(),tags.getTokens_tags(),tags.getSentence_tags());

        if (result == b) System.out.println("Test OK with id: " + id);
        else System.out.println("FAILED test with id: " + id + "--------------------");


    }

    private static String_Tree parse_input_tree(List<List<String>> aux) {
        String_Tree result = new String_Tree("<main>");
        for (int i = 0; i < aux.size(); ++i) {
            parse_input_row(aux.get(i),result);
        }
        return result;
    }

    private static void parse_input_row(List<String> row, String_Tree father) {
        for (int i = 1; i < row.size(); ++i) {
            father = parse_input_node(row.get(i),father);
        }
    }

    private static String_Tree parse_input_node(String data, String_Tree father) {
        String_Tree node = new String_Tree(data);
        father.add_children(node);
        return node;
    }

    private static TagsAndOuput read_input_tags(String path) throws Exception{
        JSONObject input_req = io.read_json_file(path);
        JSONArray aux_array_tokens = input_req.getJSONArray("tokens");
        List<String> array_tokens = new ArrayList<>();
        for (int i = 0; i < aux_array_tokens.length(); ++i) array_tokens.add(aux_array_tokens.getString(i));
        JSONArray aux_array_tokens_tags = input_req.getJSONArray("tokens_tags");
        List<String> array_tokens_tags = new ArrayList<>();
        for (int i = 0; i < aux_array_tokens_tags.length(); ++i) array_tokens_tags.add(aux_array_tokens_tags.getString(i));
        JSONArray aux_array_sentence_tags = input_req.getJSONArray("sentence_tags");
        List<String> array_sentence_tags = new ArrayList<>();
        for (int i = 0; i < aux_array_sentence_tags.length(); ++i) array_sentence_tags.add(aux_array_sentence_tags.getString(i));
        return new TagsAndOuput(array_tokens,array_tokens_tags,array_sentence_tags);
    }
}
