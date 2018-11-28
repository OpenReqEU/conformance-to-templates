package upc.req_quality.adapter;

import com.google.common.collect.ObjectArrays;
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
    private static String[] matcher_tags = {"<*>","<all>"};

    public static String[] getMatcher_tags() {
        String[] aux = {"|"};
        return ObjectArrays.concat(aux,matcher_tags,String.class);
    }

    //hacerla privada, es publica por los tests
    public Parser_Matcher() {
    }

    public Parser_Matcher(String input, String[] permited_static) {
        this.input = input;
        this.trees = new HashMap<>();
        this.words = new HashSet<>();
        this.permited_clauses = new HashSet<>();
        this.permited_static = permited_static;
        for (int i = 0; i < permited_static.length; ++i) {
            //System.out.println(permited[i]);
            permited_clauses.add(permited_static[i]);
        }
        for (int i = 0; i < matcher_tags.length; ++i) {
            permited_clauses.add(matcher_tags[i]);
        }
        this.Mymatcher = new String_Tree();
    }

    public void generate_matcher() throws BadBNFSyntaxException{
        //System.out.println(input);
        input = input.replaceAll("\"\"","<All>");
        //System.out.println(input);
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

        //System.out.println("\n\n ---------------------------------");

        Mymatcher = new String_Tree("main");
        //System.out.println(sentences[2]);
        String[] parts = sentences[1].split("\\|");
        //for (int i = 0; i < parts.length; ++i) System.out.println(parts.length + " " + parts[i]);
        Ini_tree_builder_OR(sentences[1],Mymatcher);
        /*for (int i = 0; i < parts.length; ++i) {
            Ini_tree_builder_OR(parts[i],Mymatcher);
        }*/
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
        //System.out.println(sentence + "tree_builer_section__________________");
        String[] parts = sentence.split("\\s+");
        //System.out.println(parts.length);
        /*for(int i = 0; i < parts.length; ++i) {
            System.out.println("num:" + parts.length +  " " + parts[i]);
        }*/
        List<String_Tree> precessors = new ArrayList<>();
        precessors.add(father);
        for (int i = 0; i < parts.length; ++i) {
            List<String_Tree> new_precessors = new ArrayList<>();
            for (int j = 0; j < precessors.size(); ++j) {
                List<String_Tree> aux = new ArrayList<>();
                if (!parts[i].equals("")) {
                    //System.out.println("padre: " + precessors.get(j).getData() + "siguiente: " + parts[i]);
                    aux = Ini_tree_builder_Node(parts[i], precessors.get(j));
                }
                for (int m = 0; m < aux.size(); ++m) {
                    new_precessors.add(aux.get(m));
                }
                //for (int k = 0; k < precessors.size(); ++k) System.out.println(precessors.get(k).getData());
            }
            if (!parts[i].equals("")) precessors = new_precessors;
        }
    }

    private List<String_Tree> Ini_tree_builder_Node(String data, String_Tree father) throws BadBNFSyntaxException {
        //System.out.println(data);
        data = data.toLowerCase();
        boolean found = false;
        List<String_Tree> new_childrens = new ArrayList<>();
        if (data.contains("\"")) {
            found = true;
            data = data.replaceAll("\"","");
            words.add(data);
        }
        else {
            switch (data) {
                case "":
                    new_childrens.add(father);
                    break;
                /*case "<all>":
                    found = true;
                    break;*/
                default:
                    //if (data.equals("<non-punctuation-token>")) System.out.println("Estamos");
                    if (permited_clauses.contains(data)) {
                        //System.out.println("VAMOS BIEN");
                        found = true;
                    }
                    else {
                        if (trees.containsKey(data)) {
                            //System.out.println("FOUND");
                            //System.out.println("\n\n ---------------------------------");
                            //if (data.equals("<conditional-keyword>")) print_trees();
                            merge_arbol_top(father, trees.get(data), new_childrens);
                            //for (int i = 0; i < new_childrens.size(); ++i) System.out.println("childrens___" + i + ": " + new_childrens.get(i).getData());
                            //if (data.equals("<conditional-keyword>")) print_trees();
                            //System.out.println("---------------------------------\n\n ");
                        } else throw new BadBNFSyntaxException("The BNF diagram is not well build. The word " + data + " was not recognized by the parser");
                    }
                    break;
            }
        }
        if (found) {
            //System.out.println("Padre: " + father.getData());
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
                String_Tree aux = father.add_children(new String_Tree(data));
                new_childrens.add(aux);
            }
        }
        //System.out.println("SALIMOS2");
        //for (int i = 0; i < new_childrens.size(); ++i) System.out.println("childrens___" + i + ": " + new_childrens.get(i).getData());
        return new_childrens;
    }
    private void merge_arbol_top(String_Tree father, String_Tree tree_to_merge, List<String_Tree> new_childrens) {
        //Mezcla los hijos del arbol padre con el arbol auxiliar(tree_to_merge)
        //System.out.println("Mezclamos: " + father.getData() + "  con: " + tree_to_merge.getData());
        List<String_Tree> children_to_merge = tree_to_merge.getChildren();
        for (int i = 0; i < children_to_merge.size(); ++i) {
            merge_arbol(father,children_to_merge.get(i),new_childrens);
            //for (int j = 0; j < new_childrens.size(); ++j) System.out.println("childrens___" + j + ": " + new_childrens.get(j).getData());
        }
        //System.out.println("SALIMOS1");
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
                if (children_to_merge.size() == 0) new_childrens.add(children_father);
            }
        }
        if (!found) {
            //System.out.println("estamos: "+tree_to_merge.getData());
            String_Tree aux = tree_to_merge.clone_top();
            father.add_children(aux);
            List<String_Tree> aux_hojas = aux.getHojas();
            //System.out.println(aux_hojas.size());
            for (int i = 0; i < aux_hojas.size(); ++i) {
                new_childrens.add(aux_hojas.get(i));
            }
            //new_childrens.add(tree_to_merge.);//esta mal--_> hay que incluir todas las hojas del arbol tree_to_merge
            //System.out.println("1.Ponemos el nodo: " + tree_to_merge.getData() + " al padre: " + father.getData());
        }
    }

    public void print_trees() {
        //System.out.println("");
        for (Map.Entry<String, String_Tree> entry : trees.entrySet()) {
            String_Tree aux_tree = entry.getValue();
            String data = aux_tree.getData();
            List<String_Tree> children = aux_tree.getChildren();
            for (int i = 0; i < children.size(); ++i) {
                print_recursion(data,children.get(i));
            }
            //System.out.println("");
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

    public boolean match(String[] tokens, String[] tokens_tagged, String[] chunks) {
        //List<String> to_match = new ArrayList<>();
        //for (String s: words) System.out.println(s);
        /*for (int i = 0; i < tokens.length; ++i) {
            if (words.contains(tokens[i].toLowerCase())) to_match.add(tokens[i].toLowerCase());
            else to_match.add(chunks[i].toLowerCase());
        }*/
        //for (int i = 0; i < to_match.size(); ++i) System.out.println(to_match.get(i));
        boolean found = false;
        List<String_Tree> children = Mymatcher.getChildren();
        for (int i = 0; ((!found) && (i < children.size())); ++i) {
            found = match_recursion(children.get(i),tokens, tokens_tagged, chunks,0);
        }
        if (found) return true;
        else return false; //throw exception main clause vacia
    }

    private boolean match_recursion(String_Tree tree, String[] tokens, String[] tokens_tagged, String[] chunks, int index) {
        //System.out.println(tree.getData());
        //System.out.println(to_match.get(index));

        List<String_Tree> children = tree.getChildren();
        boolean b1 = tree.getData().toLowerCase().equals(tokens[index]);
        boolean b2 = tree.getData().toLowerCase().equals(tokens_tagged[index]);
        boolean b3 = tree.getData().toLowerCase().equals(chunks[index]);
        if (!b1 && !b2 && !b3) return false;
        else {
            if (b1 || b2) {
                //matches a [postag] or a "word", so we continue the the matcher with the children
                if (children.size() == 0) return true;
                else {
                    boolean found = false;
                    for (int i = 0; ((!found) && (i < children.size())); ++i) {
                        found = match_recursion(children.get(i), tokens, tokens_tagged, chunks, index + 1);
                    }
                    return found;
                }
            } else {
                if (children.size() == 0) return true;
                else {
                    //matches a sentence tag (nor [postag] nor "word"), so we pass by all elements that are inside the same sentence tag,
                    //unless some child has a [postag] or "word" that matches some element with the same sentence tag
                    HashSet<String> children_data = new HashSet<>();
                    //children_data keeps the children tags
                    for (int i = 0; i < children.size(); ++i) {
                        children_data.add(children.get(i).getData());
                    }
                    String data_permanent = chunks[index];
                    //data_permanent keeps the initial sentence tag
                    boolean different = false;
                    ++index;
                    while (!different && index < tokens.length /*&& children_data.size() > 0*/) {
                        if (!chunks[index].equals(data_permanent)) different = true;
                        else {
                            if (children_data.contains(tokens[index]) || children_data.contains(tokens_tagged[index])) {
                                boolean found = false;
                                for (int i = 0; !found && i < children.size(); ++i) {
                                    if (tokens[index].equals(children.get(i).getData()) || tokens_tagged[index].equals(children.get(i).getData())) found = match_recursion(children.get(i), tokens, tokens_tagged, chunks, index);
                                }
                                return found;
                            }
                            ++index;
                        }
                    }
                    if (different) {
                        boolean found = false;
                        for (int i = 0; ((!found) && (i < children.size())); ++i) {
                            found = match_recursion(children.get(i), tokens, tokens_tagged, chunks, index);
                        }
                        return found;
                    } else return false;
                }
            }
        }
    }

    public String[] getPermited_static() {
        return permited_static;
    }

    public void setPermited_static(String[] permited_static) {
        this.permited_static = permited_static;
        this.permited_clauses = new HashSet<>();
        for (int i = 0; i < permited_static.length; ++i) {
            //System.out.println(permited[i]);
            permited_clauses.add(permited_static[i]);
        }
    }

    public void setMymatcher(String_Tree main) {
        this.Mymatcher = main;
    }
}
