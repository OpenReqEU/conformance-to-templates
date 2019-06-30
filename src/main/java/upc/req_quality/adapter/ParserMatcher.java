package upc.req_quality.adapter;

import com.google.common.collect.ObjectArrays;
import upc.req_quality.entity.MatcherResponse;
import upc.req_quality.exception.BadBNFSyntaxException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserMatcher {

    private List<String> input;
    private StringTree myMatcher;
    private Map<String, StringTree> trees;
    private HashSet<String> words;
    private HashSet<String> permitedClauses;
    private static String[] matcherTags = {"<*>","(all)"};
    private static String finishClause = "***FINISH***";

    public static String[] getMatcherTags() {
        String[] aux = {"|"};
        return ObjectArrays.concat(aux,matcherTags,String.class);
    }

    //Is public just for testing
    public ParserMatcher() {
    }

    public ParserMatcher(List<String> input, List<String> permitedClauses) {
        this.input = input;
        this.trees = new HashMap<>();
        this.words = new HashSet<>();
        this.permitedClauses = new HashSet<>();
        for (int i = 0; i < permitedClauses.size(); ++i) {
            this.permitedClauses.add(permitedClauses.get(i));
        }
        for (int i = 0; i < matcherTags.length; ++i) {
            this.permitedClauses.add(matcherTags[i]);
        }
        this.myMatcher = new StringTree(null);
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
    public List<Rule> parseRulesAndCheck(List<String> rules) throws BadBNFSyntaxException {

        //To show all rules with their title and description
        HashMap<String,String> rulesWithDescription = new HashMap<>();
        //To show on which dependencies it depends
        HashMap<String,Set<String>> rulesWithDependencies = new HashMap<>();
        //To show which dependencies depend on it
        HashMap<String,Set<String>> rulesDependOnMe = new HashMap<>();
        //To show rules with not dependencies
        LinkedList<String> rulesWithoutDependencies = new LinkedList<>();

        if (rules.isEmpty()) throw new BadBNFSyntaxException("No rules specified");
        if (!rules.get(0).contains("main")) throw new BadBNFSyntaxException("No main rule specified");
        for (int i = 0; i < rules.size(); ++i) {
            String text = rules.get(i);
            Pattern pattern = Pattern.compile("(^<.*?> ::=)");
            Matcher matcher = pattern.matcher(text);
            if(!matcher.find()) throw new BadBNFSyntaxException("Rule in position " + i + " is not well written. It must start with \"<name_rule> ::=\"");
            String auxTitle = text.substring(matcher.start(), matcher.end());
            auxTitle = auxTitle.replaceAll("(\\s+)","");
            auxTitle = auxTitle.replaceAll("::=","");
            String auxDescription = text.replaceAll("(^<.*?> ::=)","");
            rulesWithDescription.put(auxTitle,auxDescription);
            rulesDependOnMe.put(auxTitle,new HashSet<>());
        }

        //Check if exist two rules with the same title
        if (rulesWithDescription.size() != rules.size()) throw new BadBNFSyntaxException("Exist two rules with the same title");

        //Check dependencies between rules
        Iterator it1 = rulesWithDescription.entrySet().iterator();
        while (it1.hasNext()) {
            Map.Entry pair = (Map.Entry)it1.next();
            String auxTitle = pair.getKey() + "";
            String auxDescription = pair.getValue() + "";
            checkRule(auxTitle,auxDescription,rulesWithDescription,rulesWithDependencies,rulesWithoutDependencies,rulesDependOnMe);
        }

        //Return ordered rules
        List<Rule> result = new ArrayList<>();
        while (!rulesWithoutDependencies.isEmpty()) {
            String title = rulesWithoutDependencies.getFirst();
            Set<String> rulesThatDependOnMe = rulesDependOnMe.get(title);
            Iterator<String> it2 = rulesThatDependOnMe.iterator();
            while (it2.hasNext()) {
                String aux = it2.next();
                rulesWithDependencies.get(aux).remove(title);
                if (rulesWithDependencies.get(aux).isEmpty()) {
                    rulesWithoutDependencies.addLast(aux);
                    rulesWithDependencies.remove(aux);
                }
            }
            result.add(new Rule(title,rulesWithDescription.get(title)));
            rulesWithoutDependencies.removeFirst();
        }

        //exists a cycle
        if (!rulesWithDependencies.isEmpty()) {
            Iterator it = rulesWithDependencies.entrySet().iterator();
            String aux = "";
            boolean firstComa = true;
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                if (firstComa) aux = aux.concat("" + pair.getKey());
                else aux = aux.concat(", " + pair.getKey());
                firstComa = false;
                it.remove();
            }
            throw new BadBNFSyntaxException("Exists a cycle between the input_output rules: " + aux);
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

    private void checkRule(String title, String description, HashMap<String,String> rulesWithDescription, HashMap<String,Set<String>> rulesWithDependencies, LinkedList<String> rulesWithoutDependencies, HashMap<String,Set<String>> rulesDependOnMe) {
        String[] tokens = description.split("\\s+");
        boolean isolation = true;
        for (String text: tokens) {
            if (rulesWithDescription.containsKey(text)) {
                isolation = false;
                Set<String> newDependencies = rulesWithDependencies.get(title);
                if (newDependencies == null) newDependencies = new HashSet<>();
                newDependencies.add(text);
                rulesWithDependencies.put(title,newDependencies);
                Set<String> newDependOnMe = rulesDependOnMe.get(text);
                newDependOnMe.add(title);
                rulesDependOnMe.put(text,newDependOnMe);
            }
        }
        if (isolation) rulesWithoutDependencies.addLast(title);
    }

    public void generateMatcher() throws BadBNFSyntaxException {

        List<Rule> rules = parseRulesAndCheck(input);

        Rule mainRule = rules.get(0);
        List<Rule> auxiliaryRules = rules.subList(1,rules.size());

        for (Rule auxiliaryRule: auxiliaryRules) {
            StringTree auxIniTree = new StringTree(null,auxiliaryRule.title);
            StringTree auxTree = iniTreeBuilderOr(auxiliaryRule.description, auxIniTree);
            trees.put(auxiliaryRule.title, auxTree);
        }

        this.myMatcher = new StringTree(null,"main");
        iniTreeBuilderOr(mainRule.description,this.myMatcher);
    }

    private StringTree iniTreeBuilderOr(String sentence, StringTree father) throws BadBNFSyntaxException {
        String[] parts = sentence.split("\\|");
        for (int i = 0; i < parts.length; ++i) {
            iniTreeBuilderSection(parts[i],father);
        }
        return father;
    }

    private void iniTreeBuilderSection(String sentence, StringTree father) throws BadBNFSyntaxException {
        String[] parts = sentence.split("\\s+");
        List<StringTree> precessors = new ArrayList<>();
        precessors.add(father);
        for (int i = 0; i < parts.length; ++i) {
            List<StringTree> newPrecessors = new ArrayList<>();
            for (int j = 0; j < precessors.size(); ++j) {
                List<StringTree> aux = new ArrayList<>();
                if (!parts[i].equals("")) {
                    aux = iniTreeBuilderNode(parts[i], precessors.get(j));
                }
                for (int m = 0; m < aux.size(); ++m) {
                    newPrecessors.add(aux.get(m));
                }
            }
            if (!parts[i].equals("")) {
                precessors = newPrecessors;
                if (i == parts.length - 1) {
                    for (int j = 0; j < precessors.size(); ++j) {
                        StringTree fin = new StringTree(precessors.get(j),finishClause);
                        precessors.get(j).addChildren(fin);
                    }
                }
            }
        }
    }

    private List<StringTree> iniTreeBuilderNode(String data, StringTree father) throws BadBNFSyntaxException {
        data = data.toLowerCase();
        boolean found = false;
        List<StringTree> newChildrens = new ArrayList<>();
        if (data.contains("%")) {
            found = true;
            data = data.replaceAll("%","");
            words.add(data);
        }
        else {
            switch (data) {
                case "":
                    newChildrens.add(father);
                    break;
                default:
                    if (this.permitedClauses.contains(data)) {
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
        if (dataMerge.equals(finishClause)) newChildrens.add(father);
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

    /*public void printTrees() {
        for (Map.Entry<String, StringTree> entry : trees.entrySet()) {
            StringTree auxTree = entry.getValue();
            String data = auxTree.getData();
            List<StringTree> children = auxTree.getChildren();
            for (int i = 0; i < children.size(); ++i) {
                printRecursion(data,children.get(i));
            }
        }
    }

    public void printMatcher() {
        String data = this.myMatcher.getData();
        List<StringTree> children = this.myMatcher.getChildren();
        for (int i = 0; i < children.size(); ++i) {
            printRecursion(data,children.get(i));
        }
    }*/

    /*private void printRecursion(String data, StringTree node) {
        data += " " + node.getData();
        List<StringTree> children = node.getChildren();
        if (!children.isEmpty()) {
            for (int i = 0; i < children.size(); ++i) {
                printRecursion(data, children.get(i));
            }
        }
    }*/

    public MatcherResponse match(String[] tokens, String[] tokensTagged, String[] chunks) {
        MatcherResponse result = new MatcherResponse();
        boolean found = false;
        List<StringTree> children = this.myMatcher.getChildren();
        for (int i = 0; ((!found) && (i < children.size())); ++i) {
            found = matchRecursion(children.get(i),tokens, tokensTagged, chunks,0, result);
        }
        result.setResult(found);
        if (!found) {
            List<StringTree> errorNodes = result.getLastErrorNodes();
            MatcherResponse auxBetter = null;
            int index = result.getIndex();
            for (StringTree node: errorNodes) {
                MatcherResponse aux = matchRecursionError(node,tokens,tokensTagged,chunks,index);
                if (auxBetter == null || (aux.getSizeErrors() < auxBetter.getSizeErrors())) {
                    auxBetter = aux;
                }
            }
            if (auxBetter != null) {
                result.removeAllErrors();
                result.addAllErrors(auxBetter.getErrors());
            }
        }
        return result;
    }

    private boolean matchRecursion(StringTree tree, String[] tokens, String[] tokensTagged, String[] chunks, int index, MatcherResponse response) {

        //matcher tags
        if (tree.getData().equals("<*>")) return true;
        if (tree.getData().equals("(all)")) {
            boolean found = false;
            for (int i = 0; ((!found) && (i < tree.getChildren().size())); ++i) {
                found = matchRecursion(tree.getChildren().get(i),tokens,tokensTagged,chunks,index,response);
            }
            return found;
        }

        //finish clauses
        if (tokens.length <= index && tree.getData().equals(finishClause)) return true;
        else if (tree.getData().equals(finishClause )) {
            if (!tree.getFather().getData().contains("<")) response.addError(index,tokens.length-1,"The requirement has more tokens than expected",tree,"");
            else response.addError(index,tokens.length-1,tree.getFather().getData(),tree,"");
            return false;
        }
        else if (tokens.length <= index) {
            response.addError(index,index,"The requirement has less tokens than expected",tree,"The clause " + tree.getData() + " does not appear in the suitable index.");
            return false;
        }

        else {
            List<StringTree> children = tree.getChildren();
            boolean result = false;
            boolean b1 = tree.getData().equalsIgnoreCase(tokens[index]);
            boolean b2 = tree.getData().equalsIgnoreCase(tokensTagged[index]);
            boolean b3 = tree.getData().equalsIgnoreCase(chunks[index]);
            if (b1 || b2) {
                //matches a [postag] or a "word", so we continue with the matcher_tree children
                boolean found = false;
                for (int i = 0; ((!found) && (i < children.size())); ++i) {
                    found = matchRecursion(children.get(i),tokens,tokensTagged,chunks,index + 1,response);
                }
                result = found;
            }
            if (b3 && !result) {
                //matches a sentence tag , so we pass by all elements that are inside the same sentence tag,
                //unless some child has a [postag] or "word" that matches some element with the same sentence tag

                //children_data keeps the children tags
                HashSet<String> childrenData = new HashSet<>();
                for (int i = 0; i < children.size(); ++i) {
                    childrenData.add(children.get(i).getData());
                }
                //data_permanent keeps the initial sentence tag
                String dataPermanent = chunks[index].toLowerCase();
                boolean different = false;
                ++index;
                while (!different && index < tokens.length) {
                    if (!chunks[index].equalsIgnoreCase(dataPermanent)) different = true;
                    else {
                        if (childrenData.contains(tokens[index].toLowerCase()) || childrenData.contains(tokensTagged[index].toLowerCase())) {
                            boolean found = false;
                            for (int i = 0; !found && i < children.size(); ++i) {
                                if (tokens[index].equalsIgnoreCase(children.get(i).getData()) || tokensTagged[index].equalsIgnoreCase(children.get(i).getData())) found = matchRecursion(children.get(i),tokens,tokensTagged,chunks,index,response);
                            }
                            result = result || found;
                        }
                        ++index;
                    }
                }
                if (!result) {
                    boolean found = false;
                    for (int i = 0; ((!found) && (i < children.size())); ++i) {
                        found = matchRecursion(children.get(i),tokens,tokensTagged,chunks,index,response);
                    }
                    result = found;
                }
            }
            if (!b1 && !b2 && !b3) {
                boolean found = false;
                if (tree.getData().contains("<")) {
                    int auxIndex = searchPhrase(tree.getData(), chunks, index);
                    if (auxIndex != -1) {
                        found = true;
                        --auxIndex;
                        if (!tree.getFather().getData().contains("<")) response.addError(index, auxIndex, tree.getData(), tree, "");
                        else response.addError(index, auxIndex, tree.getData() + "||" + tree.getFather().getData(), tree, "");
                    }
                } else {
                    int auxIndex = searchPhrase(tree.getData(), tokensTagged, index);
                    if (auxIndex != -1) {
                        found = true;
                        --auxIndex;
                        if (!tree.getFather().getData().contains("<")) response.addError(index,auxIndex,"This part does not correspond to the template",tree,"");
                        else response.addError(index, getMaxMatch(tree.getFather().getData(),chunks,index,auxIndex),tree.getFather().getData(),tree,"");
                    }
                }
                if (!found) {
                    response.addError(index,index,tree.getData(),tree,"");
                }
            }
            return result;
        }
    }

    private MatcherResponse matchRecursionError(StringTree tree, String[] tokens, String[] tokensTagged, String[] chunks, int index) {

        MatcherResponse response = new MatcherResponse();

        //matcher tags
        if (tree.getData().equals("<*>")) {
            response.setResult(true);
            return response;
        }

        if (tree.getData().equals("(all)")) {
            boolean found = false;
            MatcherResponse auxBetter = new MatcherResponse();
            Integer errorsMin = null;
            MatcherResponse aux;
            for (int i = 0; ((!found) && (i < tree.getChildren().size())); ++i) {
                aux = matchRecursionError(tree.getChildren().get(i),tokens,tokensTagged,chunks,index);
                found = aux.isResult();
                if (!found && (errorsMin == null || (errorsMin > aux.getSizeErrors()))) {
                    errorsMin = auxBetter.getSizeErrors();
                    auxBetter = aux;
                }
            }
            response.setResult(found);
            if (!found) {
                response.addAllErrors(auxBetter.getErrors());
            }
            return response;
        }

        //finish clauses
        if (tokens.length <= index && tree.getData().equals(finishClause)) {
            response.setResult(true);
            return response;
        }
        else if (tree.getData().equals(finishClause)) {
            if (!tree.getFather().getData().contains("<")) response.addError(index,tokens.length-1,"The requirement has more tokens than expected",tree,"");
            else response.addError(index,tokens.length-1,tree.getFather().getData(),tree,"");
            response.setResult(false);
            return response;
        }
        else if (tokens.length <= index) {
            response.addError(index,index,"The requirement has less tokens than expected",tree,"The clause " + tree.getData() + " does not appear in the requirement.");
            response.setResult(false);
            return response;
        }

        //body
        else {
            MatcherResponse auxBetter = new MatcherResponse();
            Integer errorsMin = null;

            List<StringTree> children = tree.getChildren();
            boolean result = false;
            boolean b1 = tree.getData().equalsIgnoreCase(tokens[index]);
            boolean b2 = tree.getData().equalsIgnoreCase(tokensTagged[index]);
            boolean b3 = tree.getData().equalsIgnoreCase(chunks[index]);
            if (b1 || b2) {
                //matches a [postag] or a "word", so we continue with the matcher_tree children
                boolean found = false;
                MatcherResponse aux;
                for (int i = 0; ((!found) && (i < children.size())); ++i) {
                    aux = matchRecursionError(children.get(i),tokens,tokensTagged,chunks,index + 1);
                    found = aux.isResult();
                    if (!found && (errorsMin == null || (errorsMin > aux.getSizeErrors()))) {
                        errorsMin = auxBetter.getSizeErrors();
                        auxBetter = aux;
                    }
                }
                result = found;
            }
            if (b3 && !result) {
                //matches a sentence tag , so we pass by all elements that are inside the same sentence tag,
                //unless some child has a [postag] or "word" that matches some element with the same sentence tag

                //children_data keeps the children tags
                HashSet<String> childrenData = new HashSet<>();
                for (int i = 0; i < children.size(); ++i) {
                    childrenData.add(children.get(i).getData());
                }
                //data_permanent keeps the initial sentence tag
                String dataPermanent = chunks[index].toLowerCase();
                boolean different = false;
                ++index;
                while (!different && index < tokens.length) {
                    if (!chunks[index].equalsIgnoreCase(dataPermanent)) different = true;
                    else {
                        if (childrenData.contains(tokens[index].toLowerCase()) || childrenData.contains(tokensTagged[index].toLowerCase())) {
                            boolean found = false;
                            MatcherResponse aux;
                            for (int i = 0; !found && i < children.size(); ++i) {
                                if (tokens[index].equalsIgnoreCase(children.get(i).getData()) || tokensTagged[index].equalsIgnoreCase(children.get(i).getData())) {
                                    aux = matchRecursionError(children.get(i),tokens,tokensTagged,chunks,index);
                                    found = aux.isResult();
                                    if (!found && (errorsMin == null || (errorsMin > aux.getSizeErrors()))) {
                                        errorsMin = auxBetter.getSizeErrors();
                                        auxBetter = aux;
                                    }
                                }
                            }
                            result = result || found;
                        }
                        ++index;
                    }
                }
                if (!result) {
                    boolean found = false;
                    MatcherResponse aux;
                    for (int i = 0; ((!found) && (i < children.size())); ++i) {
                        aux = matchRecursionError(children.get(i),tokens,tokensTagged,chunks,index);
                        found = aux.isResult();
                        if (!found && (errorsMin == null || (errorsMin > aux.getSizeErrors()))) {
                            errorsMin = auxBetter.getSizeErrors();
                            auxBetter = aux;
                        }
                    }
                    result = found;
                }
            }
            if (!b1 && !b2 && !b3) {
                boolean found = false;
                if (tree.getData().contains("<")) {
                    int auxIndex = searchPhrase(tree.getData(),chunks,index);
                    if (auxIndex != -1) {
                        boolean select = false;
                        MatcherResponse aux = matchRecursionError(tree,tokens,tokensTagged,chunks,auxIndex);
                        if ((errorsMin == null) || (errorsMin > aux.getSizeErrors())) {
                                errorsMin = auxBetter.getSizeErrors();
                                auxBetter = aux;
                                select = true;
                                found = true;
                        }
                        auxIndex -= 1;
                        if (select) {
                            if (!tree.getFather().getData().contains("<")) auxBetter.addError(index,auxIndex,tree.getData(),tree,"");
                            else auxBetter.addError(index,auxIndex,tree.getData()+"||"+tree.getFather().getData(),tree,"");
                        }
                    }
                } else  {
                    int auxIndex = searchPhrase(tree.getData(),tokensTagged,index);
                    if (auxIndex != -1) {
                        boolean select = false;
                        for (int i = 0; i < children.size(); ++i) {
                            MatcherResponse aux = matchRecursionError(children.get(i),tokens,tokensTagged,chunks,auxIndex+1);
                            if ((errorsMin == null) || (errorsMin > aux.getSizeErrors())) {
                                errorsMin = auxBetter.getSizeErrors();
                                auxBetter = aux;
                                select = true;
                                found = true;
                            }
                        }
                        auxIndex -= 1;
                        if (select) {
                            if (!tree.getFather().getData().contains("<")) auxBetter.addError(index,auxIndex,"This part does not correspond to the template",tree,"");
                            else auxBetter.addError(index,getMaxMatch(tree.getFather().getData(),chunks,index,auxIndex),tree.getFather().getData(),tree,"");
                        }
                    }
                }
                if (!found) {
                    response.addError(index,index,tree.getData(),tree,"");
                    MatcherResponse aux;
                    for (int i = 0; i < children.size(); ++i) {
                        aux = matchRecursionError(children.get(i),tokens,tokensTagged,chunks,index+1);
                        if ((errorsMin == null) || (errorsMin > aux.getSizeErrors())) {
                            errorsMin = auxBetter.getSizeErrors();
                            auxBetter = aux;
                        }
                    }
                }
            }
            response.setResult(result);
            if (!result) {
                response.addAllErrors(auxBetter.getErrors());
            }
            return response;
        }
    }

    private int getMaxMatch(String phrase, String[] words, int iniIndex, int finishIndex) {
        int max = iniIndex;
        for (int i = iniIndex; i < finishIndex; ++i) {
            if (!words[i].equals(phrase)) max = i;
        }
        return max;
    }

    private int searchPhrase(String phrase, String[] words, int index) {
        boolean found = false;
        int auxIndex = -1;
        for (int i = index; !found && i < words.length; ++i) {
            if (words[i].equals(phrase)) {
                found = true;
                auxIndex = i;
            }
        }
        return auxIndex;
    }
}
