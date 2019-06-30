package upc.req_quality.adapter;

import java.util.ArrayList;
import java.util.List;

public class StringTree {
    private String data;
    private List<StringTree> children;
    private List<StringTree> hojas;
    private StringTree father;

    public StringTree(StringTree father) {
        this.father = father;
        this.children = new ArrayList<>();
        this.hojas = new ArrayList<>();
    }

    public StringTree(StringTree father, String data) {
        this.father = father;
        this.data = data;
        this.children = new ArrayList<>();
        this.hojas= new ArrayList<>();
    }

    public StringTree addChildren(StringTree children) {
        this.children.add(children);
        children.father = this;
        return children;
    }

    public String getData() {
        return data;
    }

    public List<StringTree> getChildren() {
        return children;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setFather(StringTree father) {
        this.father = father;
    }

    public StringTree cloneTop() {
        List<StringTree> auxHojas = new ArrayList<>();
        StringTree result = new StringTree(father,data);
        for (int i = 0; i < children.size(); ++i) {
            StringTree aux = children.get(i).clone(result,auxHojas);
            if (aux != null) result.children.add(aux);
        }
        if (children.isEmpty()) {
            return null;
        }
        result.hojas = auxHojas;
        return result;
    }

    private StringTree clone(StringTree father, List<StringTree> auxHojas) {
        StringTree result = new StringTree(this.father,data);
        for (int i = 0; i < children.size(); ++i) {
            StringTree aux = children.get(i).clone(result,auxHojas);
            if (aux != null) result.children.add(aux);
        }
        if (children.isEmpty()) {
            auxHojas.add(father);
            return null;
        }
        return result;
    }

    public List<StringTree> getHojas() {
        return hojas;
    }

    public StringTree getFather() {
        return father;
    }

    public void print() {
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


