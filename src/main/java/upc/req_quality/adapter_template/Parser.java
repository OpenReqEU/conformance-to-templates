package upc.req_quality.adapter_template;

import upc.req_quality.entity.Rule;
import upc.req_quality.exception.BadBNFSyntaxException;
import upc.req_quality.util.Constants;

import java.util.*;

public class Parser {

    public StringTree parseRules(List<Rule> rules, List<String> permittedClauses) throws BadBNFSyntaxException {
        Rule mainRule = rules.get(0);
        List<Rule> auxiliaryRules = rules.subList(1,rules.size());
        Set<String> permittedClausesHash = new HashSet<>(permittedClauses);
        Map<String, StringTree> trees = new HashMap<>(); //contains the auxiliary rules

        for (Rule auxiliaryRule: auxiliaryRules) {
            StringTree auxIniTree = new StringTree(null,auxiliaryRule.getTitle());
            StringTree auxTree = iniTreeBuilderOr(auxiliaryRule.getDescription(), auxIniTree, permittedClausesHash, trees);
            trees.put(auxiliaryRule.getTitle(), auxTree);
        }

        StringTree result = new StringTree(null,"main");
        iniTreeBuilderOr(mainRule.getDescription(), result, permittedClausesHash, trees);
        return result;
    }

    private StringTree iniTreeBuilderOr(String sentence, StringTree father, Set<String> permittedClauses, Map<String, StringTree> trees) throws BadBNFSyntaxException {
        String[] parts = sentence.split("\\|");
        for (int i = 0; i < parts.length; ++i) {
            iniTreeBuilderSection(parts[i], father, permittedClauses, trees);
        }
        return father;
    }

    private void iniTreeBuilderSection(String sentence, StringTree father, Set<String> permittedClauses, Map<String, StringTree> trees) throws BadBNFSyntaxException {
        String[] parts = sentence.split("\\s+");
        List<StringTree> precessors = new ArrayList<>();
        precessors.add(father);
        for (int i = 0; i < parts.length; ++i) {
            List<StringTree> newPrecessors = new ArrayList<>();
            for (int j = 0; j < precessors.size(); ++j) {
                List<StringTree> aux = new ArrayList<>();
                if (!parts[i].equals("")) {
                    aux = iniTreeBuilderNode(parts[i], precessors.get(j), permittedClauses, trees);
                }
                for (int m = 0; m < aux.size(); ++m) {
                    newPrecessors.add(aux.get(m));
                }
            }
            if (!parts[i].equals("")) {
                precessors = newPrecessors;
                if (i == parts.length - 1) {
                    for (int j = 0; j < precessors.size(); ++j) {
                        StringTree fin = new StringTree(precessors.get(j), Constants.getInstance().getFinishClause());
                        precessors.get(j).addChildren(fin);
                    }
                }
            }
        }
    }

    private List<StringTree> iniTreeBuilderNode(String data, StringTree father, Set<String> permittedClauses, Map<String, StringTree> trees) throws BadBNFSyntaxException {
        data = data.toLowerCase();
        boolean found = false;
        List<StringTree> newChildrens = new ArrayList<>();
        if (data.contains("%")) {
            found = true;
            data = data.replaceAll("%","");
            //words.add(data); //TODO check this
        }
        else {
            switch (data) {
                case "":
                    newChildrens.add(father);
                    break;
                default:
                    if (permittedClauses.contains(data)) {
                        found = true;
                    }
                    else {
                        if (trees.containsKey(data)) {
                            mergeTreeTop(father, trees.get(data), newChildrens);
                        } else throw new BadBNFSyntaxException("The BNF diagram is not well build. The word " + data + " was not recognized by the parser");
                    }
                    break;
            }
        }
        if (found) {
            List<StringTree> children = father.getChildren();
            boolean repeated = false;
            for (int i = 0; ((!repeated) && (i < children.size())); ++i) {
                StringTree auxChildren = children.get(i);
                if (auxChildren.getData().equals(data)) {
                    newChildrens.add(auxChildren);
                    repeated = true;
                }
            }
            if (!repeated) {
                StringTree aux = father.addChildren(new StringTree(father,data));
                newChildrens.add(aux);
            }
        }
        return newChildrens;
    }
    private void mergeTreeTop(StringTree father, StringTree treeToMerge, List<StringTree> newChildrens) {
        //Mezcla los hijos del arbol padre con el arbol auxiliar(tree_to_merge)
        List<StringTree> childrenToMerge = treeToMerge.getChildren();
        for (int i = 0; i < childrenToMerge.size(); ++i) {
            mergeTree(father,childrenToMerge.get(i),newChildrens);
        }
    }


    private void mergeTree(StringTree father, StringTree treeToMerge, List<StringTree> newChildrens) {
        //Mezcla los hijos del arbol padre con el arbol auxiliar(tree_to_merge)
        String dataMerge = treeToMerge.getData();
        if (dataMerge.equals(Constants.getInstance().getFinishClause())) newChildrens.add(father);
        else {
            List<StringTree> children = father.getChildren();
            boolean found = false;
            for (int i = 0; (!found) && (i < children.size()); ++i) {
                StringTree childrenFather = children.get(i);
                if (childrenFather.getData().equals(dataMerge)) {
                    found = true;
                    List<StringTree> childrenToMerge = treeToMerge.getChildren();
                    for (int j = 0; j < childrenToMerge.size(); ++j) {
                        mergeTree(childrenFather, childrenToMerge.get(j), newChildrens);
                    }
                }
            }
            if (!found) {
                StringTree aux = treeToMerge.cloneTop();
                if (aux != null) {
                    father.addChildren(aux);
                    aux.setFather(father);
                    List<StringTree> auxHojas = aux.getHojas();
                    for (int i = 0; i < auxHojas.size(); ++i) {
                        newChildrens.add(auxHojas.get(i));
                    }
                }
            }
        }
    }
}
