package upc.req_quality.adapter_template;

import upc.req_quality.entity.MatcherResponse;
import upc.req_quality.util.Constants;

import java.util.HashSet;
import java.util.List;

public class Matcher {

    public MatcherResponse matchTemplate(StringTree parseRules, String[] tokens, String[] tokensTagged, String[] chunks) {
        MatcherResponse result = new MatcherResponse();
        boolean found = false;
        List<StringTree> children = parseRules.getChildren();
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
        if (tokens.length <= index && tree.getData().equals(Constants.getInstance().getFinishClause())) return true;
        else if (tree.getData().equals(Constants.getInstance().getFinishClause())) {
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
        if (tokens.length <= index && tree.getData().equals(Constants.getInstance().getFinishClause())) {
            response.setResult(true);
            return response;
        }
        else if (tree.getData().equals(Constants.getInstance().getFinishClause())) {
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
