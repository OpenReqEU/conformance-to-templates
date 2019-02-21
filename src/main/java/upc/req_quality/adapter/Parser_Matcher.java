package upc.req_quality.adapter;

import com.google.common.collect.ObjectArrays;
import upc.req_quality.exeption.BadBNFSyntaxException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser_Matcher {

    private List<String> input;
    private String_Tree Mymatcher;
    private Map<String,String_Tree> trees;
    private HashSet<String> words;
    private HashSet<String> permited_clauses;
    private String[] permited_static;
    private static String[] matcher_tags = {"<*>","(all)"};

    public static String[] getMatcher_tags() {
        String[] aux = {"|"};
        return ObjectArrays.concat(aux,matcher_tags,String.class);
    }

    //Is public just for testing
    public Parser_Matcher() {
    }

    public Parser_Matcher(List<String> input, String[] permited_static) {
        this.input = input;
        this.trees = new HashMap<>();
        this.words = new HashSet<>();
        this.permited_clauses = new HashSet<>();
        this.permited_static = permited_static;
        for (int i = 0; i < permited_static.length; ++i) {
            permited_clauses.add(permited_static[i]);
        }
        for (int i = 0; i < matcher_tags.length; ++i) {
            permited_clauses.add(matcher_tags[i]);
        }
        this.Mymatcher = new String_Tree();
    }

    //Is public just for testing
    public class Rule {
        private String title;
        private String description;
        public Rule(String title, String description) {
            this.title = title;
            this.description = description;
        }
        //just for testing
        public String getTitle() {
            return title;
        }
        //just for testing
        public String getDescription() {
            return description;
        }
    }

    //Is public just for testing
    public List<Rule> parse_rules_and_check(List<String> rules) throws BadBNFSyntaxException {

        //To show all rules with their title and description
        HashMap<String,String> rules_with_description = new HashMap<>();
        //To show on which dependencies it depends
        HashMap<String,Set<String>> rules_with_dependencies = new HashMap<>();
        //To show which dependencies depend on it
        HashMap<String,Set<String>> rules_depend_on_me = new HashMap<>();
        //To show rules with not dependencies
        LinkedList<String> rules_without_dependencies = new LinkedList<>();

        if (rules.size() <= 0) throw new BadBNFSyntaxException("No rules specified");
        if (!rules.get(0).contains("main")) throw new BadBNFSyntaxException("No main rule specified");
        for (int i = 0; i < rules.size(); ++i) {
            String text = rules.get(i);
            Pattern pattern = Pattern.compile("(^<.*?> ::=)");
            Matcher matcher = pattern.matcher(text);
            if(!matcher.find()) throw new BadBNFSyntaxException("Rule in position " + i + " is not well written. It must start with \"<name_rule> ::=\"");
            String aux_title = text.substring(matcher.start(), matcher.end());
            aux_title = aux_title.replaceAll("(\\s+)","");
            aux_title = aux_title.replaceAll("::=","");
            String aux_description = text.replaceAll("(^<.*?> ::=)","");
            rules_with_description.put(aux_title,aux_description);
            //rules_with_dependencies.put(aux_title,new HashSet<>());
            rules_depend_on_me.put(aux_title,new HashSet<>());
        }

        //Check if exist two rules with the same title
        if (rules_with_description.size() != rules.size()) throw new BadBNFSyntaxException("Exist two rules with the same title");

        //Check dependencies between rules
        Iterator it1 = rules_with_description.entrySet().iterator();
        while (it1.hasNext()) {
            Map.Entry pair = (Map.Entry)it1.next();
            String aux_title = pair.getKey() + "";
            String aux_description = pair.getValue() + "";
            check_rule(aux_title,aux_description,rules_with_description,rules_with_dependencies,rules_without_dependencies,rules_depend_on_me,0);
        }

        //Return ordered rules
        List<Rule> result = new ArrayList<>();
        while (rules_without_dependencies.size() > 0) {
            String title = rules_without_dependencies.getFirst();
            Set<String> rules_that_depend_on_me = rules_depend_on_me.get(title);
            Iterator<String> it2 = rules_that_depend_on_me.iterator();
            while (it2.hasNext()) {
                String aux = it2.next();
                rules_with_dependencies.get(aux).remove(title);
                if (rules_with_dependencies.get(aux).size() == 0) {
                    rules_without_dependencies.addLast(aux);
                    rules_with_dependencies.remove(aux);
                }
            }
            result.add(new Rule(title,rules_with_description.get(title)));
            rules_without_dependencies.removeFirst();
        }

        //exists a cycle
        if (rules_with_dependencies.size() > 0) {
            Iterator it = rules_with_dependencies.entrySet().iterator();
            String aux = "";
            boolean firstComa = true;
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                if (firstComa) aux = aux.concat("" + pair.getKey());
                else aux = aux.concat(", " + pair.getKey());
                firstComa = false;
                it.remove();
            }
            throw new BadBNFSyntaxException("Exists a cycle between the input rules: " + aux);
        }

        //put main clause at index 0
        for (Rule aux_rule: result) {
            if (aux_rule.getTitle().equals("<main>")) {
                result.remove(aux_rule);
                result.add(0,aux_rule);
            }
        }

        return result;
    }

    private void check_rule(String title, String description, HashMap<String,String> rules_with_description, HashMap<String,Set<String>> rules_with_dependencies, LinkedList<String> rules_without_dependencies, HashMap<String,Set<String>> rules_depend_on_me, int total) {
        String[] tokens = description.split("\\s+");
        boolean isolation = true;
        for (String text: tokens) {
            if (rules_with_description.containsKey(text)) {
                isolation = false;
                Set<String> new_dependencies = rules_with_dependencies.get(title);
                if (new_dependencies == null) new_dependencies = new HashSet<>();
                new_dependencies.add(text);
                rules_with_dependencies.put(title,new_dependencies);
                Set<String> new_depend_on_me = rules_depend_on_me.get(text);
                new_depend_on_me.add(title);
                rules_depend_on_me.put(text,new_depend_on_me);
            }
        }
        if (isolation) rules_without_dependencies.addLast(title);
    }

    public void generate_matcher() throws BadBNFSyntaxException {

        List<Rule> rules = parse_rules_and_check(input);

        Rule main_rule = rules.get(0);
        List<Rule> auxiliary_rules = rules.subList(1,rules.size());

        for (Rule auxiliary_rule: auxiliary_rules) {
            String_Tree aux_ini_tree = new String_Tree(auxiliary_rule.title);
            String_Tree aux_tree = Ini_tree_builder_OR(auxiliary_rule.description, aux_ini_tree);
            trees.put(auxiliary_rule.title, aux_tree);
        }

        Mymatcher = new String_Tree("main");
        Ini_tree_builder_OR(main_rule.description,Mymatcher);
    }

    private String_Tree Ini_tree_builder_OR(String sentence, String_Tree father) throws BadBNFSyntaxException {
        String[] parts = sentence.split("\\|");
        for (int i = 0; i < parts.length; ++i) {
            Ini_tree_builder_Section(parts[i],father);
        }
        return father;
    }

    private void Ini_tree_builder_Section(String sentence, String_Tree father) throws BadBNFSyntaxException {
        String[] parts = sentence.split("\\s+");
        List<String_Tree> precessors = new ArrayList<>();
        precessors.add(father);
        for (int i = 0; i < parts.length; ++i) {
            List<String_Tree> new_precessors = new ArrayList<>();
            for (int j = 0; j < precessors.size(); ++j) {
                List<String_Tree> aux = new ArrayList<>();
                if (!parts[i].equals("")) {
                    aux = Ini_tree_builder_Node(parts[i], precessors.get(j));
                }
                for (int m = 0; m < aux.size(); ++m) {
                    new_precessors.add(aux.get(m));
                }
            }
            if (!parts[i].equals("")) {
                precessors = new_precessors;
                if (i == parts.length - 1) {
                    for (int j = 0; j < precessors.size(); ++j) {
                        String_Tree fin = new String_Tree("***FINISH***");
                        precessors.get(j).add_children(fin);
                    }
                }
            }
        }
    }

    private List<String_Tree> Ini_tree_builder_Node(String data, String_Tree father) throws BadBNFSyntaxException {
        data = data.toLowerCase();
        boolean found = false;
        List<String_Tree> new_childrens = new ArrayList<>();
        if (data.contains("%")) {
            found = true;
            data = data.replaceAll("%","");
            words.add(data);
        }
        else {
            switch (data) {
                case "":
                    new_childrens.add(father);
                    break;
                default:
                    if (permited_clauses.contains(data)) {
                        found = true;
                    }
                    else {
                        if (trees.containsKey(data)) {
                            merge_arbol_top(father, trees.get(data), new_childrens);
                        } else throw new BadBNFSyntaxException("The BNF diagram is not well build. The word " + data + " was not recognized by the parser");
                    }
                    break;
            }
        }
        if (found) {
            List<String_Tree> children = father.getChildren();
            boolean repeated = false;
            for (int i = 0; ((!repeated) && (i < children.size())); ++i) {
                String_Tree aux_children = children.get(i);
                if (aux_children.getData().equals(data)) {
                    new_childrens.add(aux_children);
                    repeated = true;
                }
            }
            if (!repeated) {
                String_Tree aux = father.add_children(new String_Tree(data));
                new_childrens.add(aux);
            }
        }
        return new_childrens;
    }
    private void merge_arbol_top(String_Tree father, String_Tree tree_to_merge, List<String_Tree> new_childrens) {
        //Mezcla los hijos del arbol padre con el arbol auxiliar(tree_to_merge)
        List<String_Tree> children_to_merge = tree_to_merge.getChildren();
        for (int i = 0; i < children_to_merge.size(); ++i) {
            merge_arbol(father,children_to_merge.get(i),new_childrens);
        }
    }


    private void merge_arbol(String_Tree father, String_Tree tree_to_merge, List<String_Tree> new_childrens) {
        //Mezcla los hijos del arbol padre con el arbol auxiliar(tree_to_merge)
        String data_merge = tree_to_merge.getData();
        if (data_merge.equals("***FINISH***")) new_childrens.add(father);
        else {
            List<String_Tree> children = father.getChildren();
            boolean found = false;
            for (int i = 0; (!found) && (i < children.size()); ++i) {
                String_Tree children_father = children.get(i);
                if (children_father.getData().equals(data_merge)) {
                    found = true;
                    List<String_Tree> children_to_merge = tree_to_merge.getChildren();
                    for (int j = 0; j < children_to_merge.size(); ++j) {
                        merge_arbol(children_father, children_to_merge.get(j), new_childrens);
                    }
                    //if (children_to_merge.size() == 0) new_childrens.add(children_father);
                }
            }
            if (!found) {
                String_Tree aux = tree_to_merge.clone_top();
                if (aux != null) {
                    father.add_children(aux);
                    List<String_Tree> aux_hojas = aux.getHojas();
                    //System.out.println(aux_hojas.size());
                    for (int i = 0; i < aux_hojas.size(); ++i) {
                        new_childrens.add(aux_hojas.get(i));
                    }
                }
            }
        }
    }

    public void print_trees() {
        for (Map.Entry<String, String_Tree> entry : trees.entrySet()) {
            String_Tree aux_tree = entry.getValue();
            String data = aux_tree.getData();
            List<String_Tree> children = aux_tree.getChildren();
            for (int i = 0; i < children.size(); ++i) {
                print_recursion(data,children.get(i));
            }
        }
    }

    public void print_matcher() {
        String data = Mymatcher.getData();
        //System.out.println(data);
        List<String_Tree> children = Mymatcher.getChildren();
        for (int i = 0; i < children.size(); ++i) {
            print_recursion(data,children.get(i));
        }
    }

    private void print_recursion(String data, String_Tree node) {
        data += " " + node.getData();
        List<String_Tree> children = node.getChildren();
        if (children.size() <= 0) {
            System.out.println(data);
        }
        else {
            for (int i = 0; i < children.size(); ++i) {
                print_recursion(data, children.get(i));
            }
        }
    }

    public boolean match(String[] tokens, String[] tokens_tagged, String[] chunks) {
        boolean found = false;
        List<String_Tree> children = Mymatcher.getChildren();
        for (int i = 0; ((!found) && (i < children.size())); ++i) {
            found = match_recursion(children.get(i),tokens, tokens_tagged, chunks,0);
        }
        if (found) return true;
        else return false; //throw exception main clause vacia
    }

    private boolean match_recursion(String_Tree tree, String[] tokens, String[] tokens_tagged, String[] chunks, int index) {

        //matcher tags
        if (tree.getData().equals("<*>")) return true;
        if (tree.getData().equals("(all)")) {
            boolean found = false;
            for (int i = 0; ((!found) && (i < tree.getChildren().size())); ++i) {
                found = match_recursion(tree.getChildren().get(i), tokens, tokens_tagged, chunks, index);
            }
            return found;
        }

        //finish clauses
        if (tokens.length <= index && tree.getData().equals("***FINISH***")) return true;
        else if (tree.getData().equals("***FINISH***")) return false;
        else if (tokens.length <= index) return false;

        else {
            List<String_Tree> children = tree.getChildren();
            boolean result = false;
            boolean b1 = tree.getData().toLowerCase().equals(tokens[index]);
            boolean b2 = tree.getData().toLowerCase().equals(tokens_tagged[index]);
            boolean b3 = tree.getData().toLowerCase().equals(chunks[index]);
            if (b1 || b2) {
                //matches a [postag] or a "word", so we continue with the matcher_tree children
                boolean found = false;
                for (int i = 0; ((!found) && (i < children.size())); ++i) {
                    found = match_recursion(children.get(i), tokens, tokens_tagged, chunks, index + 1);
                }
                result = found;
            }
            if (b3 && !result) {
                //matches a sentence tag , so we pass by all elements that are inside the same sentence tag,
                //unless some child has a [postag] or "word" that matches some element with the same sentence tag

                //children_data keeps the children tags
                HashSet<String> children_data = new HashSet<>();
                for (int i = 0; i < children.size(); ++i) {
                    children_data.add(children.get(i).getData());
                }
                //data_permanent keeps the initial sentence tag
                String data_permanent = chunks[index].toLowerCase();
                boolean different = false;
                ++index;
                while (!different && index < tokens.length) {
                    if (!chunks[index].toLowerCase().equals(data_permanent)) different = true;
                    else {
                        if (children_data.contains(tokens[index].toLowerCase()) || children_data.contains(tokens_tagged[index].toLowerCase())) {
                            boolean found = false;
                            for (int i = 0; !found && i < children.size(); ++i) {
                                if (tokens[index].toLowerCase().equals(children.get(i).getData()) || tokens_tagged[index].toLowerCase().equals(children.get(i).getData())) found = match_recursion(children.get(i), tokens, tokens_tagged, chunks, index);
                            }
                            result = result || found;
                        }
                        ++index;
                    }
                }
                if (!result) {
                    boolean found = false;
                    for (int i = 0; ((!found) && (i < children.size())); ++i) {
                        found = match_recursion(children.get(i), tokens, tokens_tagged, chunks, index);
                    }
                    result = found;
                }
            }
            return result;
        }
    }


    public String[] getPermited_static() {
        return permited_static;
    }

    public void setPermited_static(String[] permited_static) {
        this.permited_static = permited_static;
        this.permited_clauses = new HashSet<>();
        for (int i = 0; i < permited_static.length; ++i) {
            permited_clauses.add(permited_static[i]);
        }
    }

    public void setMymatcher(String_Tree main) {
        this.Mymatcher = main;
    }
}
