package upc.req_quality.util;

import upc.req_quality.adapter_template.StringTree;

import java.util.List;
import java.util.Map;

public class TestMethods {

    public void printTrees(Map<String, StringTree> trees) {
        for (Map.Entry<String, StringTree> entry : trees.entrySet()) {
            StringTree auxTree = entry.getValue();
            String data = auxTree.getData();
            List<StringTree> children = auxTree.getChildren();
            for (int i = 0; i < children.size(); ++i) {
                printRecursion(data,children.get(i));
            }
        }
    }

    public void printMatcher(StringTree matcher) {
        String data = matcher.getData();
        List<StringTree> children = matcher.getChildren();
        for (int i = 0; i < children.size(); ++i) {
            printRecursion(data,children.get(i));
        }
    }

    private void printRecursion(String data, StringTree node) {
        data += " " + node.getData();
        List<StringTree> children = node.getChildren();
        if (!children.isEmpty()) {
            for (int i = 0; i < children.size(); ++i) {
                printRecursion(data, children.get(i));
            }
        }
    }
}
