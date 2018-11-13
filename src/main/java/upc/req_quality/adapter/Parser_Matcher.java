package upc.req_quality.adapter;

import upc.req_quality.exeption.BadBNFSyntaxException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser_Matcher {

    private String input;
    private String_Tree Mymatcher;
    private Map<String,String_Tree> trees;
    private HashSet<String> words;
    private HashSet<String> permited_clauses;
    private String[] permited_static;

    private Parser_Matcher() {
    }

    public Parser_Matcher(String input) {
        this.input = input;
        this.trees = new HashMap<>();
        this.words = new HashSet<>();
        this.permited_clauses = new HashSet<>();
        this.permited_static = new String[]{"<np>","<vp>","<NP>","<VP>","<non-punctuation-token>","<vp-starting-with-modal>","<modal>","<infinitive-vp>","<token-sequence-without-subordinate-conjunctions>"};
        for (int i = 0; i < permited_static.length; ++i) {
            //System.out.println(permited[i]);
            permited_clauses.add(permited_static[i]);
        }
    }

    public void generate_matcher() throws BadBNFSyntaxException{
        //System.out.println(input);
        input = input.replaceAll("\"\"","<All>");
        System.out.println(input);
        //String me = "";
        //for(int i = 0; i < input.length(); ++i) me += " " + input.charAt(i)  + i;
        //System.out.println(me);
        //String[] sentences = input.split("<.*?> ::=");
        String[] sentences = input.split("((^<.*?> ::=)|(\n<.*?> ::=))");
        //System.out.println(sentences.length);
        //System.out.println(sentences[0]);
        //for (int i = 0; i < sentences.length; ++i) System.out.println(sentences[i]);
        Pattern pattern = Pattern.compile("((^<.*?> ::=)|(\n<.*?> ::=))");
        Matcher matcher = pattern.matcher(input);

        int count = 1;
        while(matcher.find()) {
            if (count < sentences.length && count > 1) {
                //System.out.println("found: " + count + " : " + matcher.start() + " - " + matcher.end());
                //System.out.println(input.substring(matcher.start()+1,matcher.end()-5));
                //System.out.println(matcher.start() + " " + matcher.end());
                String aux_title = input.substring(matcher.start(), matcher.end());
                aux_title = aux_title.replaceAll("(\\s+)","");
                aux_title = aux_title.replaceAll("::=","");
                //System.out.println(aux_title);
                //System.out.println(aux_title + "------->" + sentences[count]);
                String_Tree aux_ini_tree = new String_Tree(aux_title);
                String_Tree aux_tree = Ini_tree_builder_OR(sentences[count], aux_ini_tree);
                trees.put(aux_title, aux_tree);
                //System.out.println(aux);
            }
            ++count;
        }

        Mymatcher = new String_Tree("main");
        //System.out.println(sentences[2]);
        String[] parts = sentences[1].split("\\|");
        //for (int i = 0; i < parts.length; ++i) System.out.println(parts.length + " " + parts[i]);
        for (int i = 0; i < parts.length; ++i) {
            Ini_tree_builder_OR(parts[i],Mymatcher);
        }
    }

    private String_Tree Ini_tree_builder_OR(String sentence, String_Tree father) throws BadBNFSyntaxException{
        //System.out.println(sentence + "----");
        String[] parts = sentence.split("\\|");
        //for(int i = 0; i < parts.length; ++i) System.out.println("num:" + parts.length +  " " + parts[i]);
        for (int i = 0; i < parts.length; ++i) {
            Ini_tree_builder_Section(parts[i],father);
        }
        //System.out.println(sentence);
        return father;
    }

    private void Ini_tree_builder_Section(String sentence, String_Tree father) throws BadBNFSyntaxException{
        //System.out.println(sentence);
        String[] parts = sentence.split("\\s+");
        //System.out.println(parts.length);
        //for(int i = 0; i < parts.length; ++i) System.out.println("num:" + parts.length +  " " + parts[i]);
        List<String_Tree> precessors = new ArrayList<>();
        precessors.add(father);
        for (int i = 0; i < parts.length; ++i) {
            System.out.println(":_______________________________________");
            for (int k = 0; k < precessors.size(); ++k) {
                System.out.println(precessors.get(k).getData());
            }
            System.out.println(":_______________________________________\n");
            List<String_Tree> new_precessors = new ArrayList<>();
            for (int j = 0; j < precessors.size(); ++j) {
                List<String_Tree> aux = new ArrayList<>();
                System.out.println("padre: " + precessors.get(j).getData() + "siguiente: " + parts[i]);
                /*if(!parts[i].equals(""))*/ aux = Ini_tree_builder_Node(parts[i], precessors.get(j));
                for (int m = 0; m < aux.size(); ++m) {
                    new_precessors.add(aux.get(m));
                }
                //for (int k = 0; k < precessors.size(); ++k) System.out.println(precessors.get(k).getData());
            }
            precessors = new_precessors;
        }
    }

    private List<String_Tree> Ini_tree_builder_Node(String data, String_Tree father) throws BadBNFSyntaxException {
        System.out.println(data);
        boolean found = false;
        List<String_Tree> new_childrens = new ArrayList<>();
        if (data.contains("\"")) {
            found = true;
            String aux_data = data.replaceAll("\"","");
            words.add(data);
        }
        else {
            switch (data) {
                case "":
                    new_childrens.add(father);
                    break;
                case "<All>":
                    found = true;
                    break;
                default:
                    if (data.equals("<non-punctuation-token>")) System.out.println("Estamos");
                    if (permited_clauses.contains(data)) {
                        System.out.println("VAMOS BIEN");
                        found = true;
                    }
                    else {
                        if (trees.containsKey(data)) {
                            //System.out.println("FOUND");
                            merge_arbol_top(father, trees.get(data), new_childrens);
                        } else throw new BadBNFSyntaxException("The BNF diagram is not well build. The word " + data + " is not recognized by the parser");
                    }
                    break;
            }
        }
        if (found) {
            System.out.println("Padre: " + father.getData());
            List<String_Tree> children = father.getChildren();
            boolean repeated = false;
            for (int i = 0; ((!repeated) && (i < children.size())); ++i) {
                String_Tree aux_children = children.get(i);
                if (aux_children.getData().equals(data)) {
                    //System.out.println("FOUND");
                    new_childrens.add(aux_children);
                    repeated = true;
                }
            }
            if (!repeated) {
                new_childrens.add(new String_Tree(data));
                father.add_children(new String_Tree(data));
            }
        }
        System.out.println("SALIMOS2");
        return new_childrens;
    }
    private void merge_arbol_top(String_Tree father, String_Tree tree_to_merge, List<String_Tree> new_childrens) {
        //Mezcla los hijos del arbol padre con el arbol auxiliar(tree_to_merge)
        System.out.println("Mezclamos: " + father.getData() + "  con: " + tree_to_merge.getData());
        List<String_Tree> children_to_merge = tree_to_merge.getChildren();
        for (int i = 0; i < children_to_merge.size(); ++i) {
            merge_arbol(father,children_to_merge.get(i),new_childrens);
        }
        System.out.println("SALIMOS1");
    }


    private void merge_arbol(String_Tree father, String_Tree tree_to_merge, List<String_Tree> new_childrens) {
        //Mezcla los hijos del arbol padre con el arbol auxiliar(tree_to_merge)
        String data_merge = tree_to_merge.getData();
        List<String_Tree> children = father.getChildren();
        //System.out.println("estamos: "+tree_to_merge.getData());
        boolean found = false;
        for (int i = 0; (!found) && (i <children.size()); ++i) {
            //System.out.println(children.size());
            String_Tree children_father = children.get(i);
            if (children_father.getData().equals(data_merge)) {
                found = true;
                List<String_Tree> children_to_merge = tree_to_merge.getChildren();
                for (int j = 0; j < children_to_merge.size(); ++j) {
                    merge_arbol(children_father,children_to_merge.get(j),new_childrens);
                }
            }
        }
        if (!found) {
            father.add_children(tree_to_merge);
            new_childrens.add(tree_to_merge);//esta mal--_> hay que incluir todas las hojas del arbol tree_to_merge
            //System.out.println("1.Ponemos el nodo: " + tree_to_merge.getData() + " al padre: " + father.getData());
        }
    }

    public void print_trees() {
        System.out.println("");
        for (Map.Entry<String, String_Tree> entry : trees.entrySet()) {
            String_Tree aux_tree = entry.getValue();
            String data = aux_tree.getData();
            List<String_Tree> children = aux_tree.getChildren();
            for (int i = 0; i < children.size(); ++i) {
                print_recursion(data,children.get(i));
            }
            System.out.println("");
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
        //System.out.println(data);
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

    public boolean match() {
        return false;
    }
}
