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

    public StringTree add_children(StringTree children) {
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

    public StringTree clone_top() {
        List<StringTree> aux_hojas = new ArrayList<>();
        //System.out.println(data);
        StringTree result = new StringTree(father,data);
        for (int i = 0; i < children.size(); ++i) {
            StringTree aux = children.get(i).clone(result,aux_hojas);
            if (aux != null) result.children.add(aux);
        }
        //System.out.println(children.size());
        if (children.size() == 0) {
            //System.out.println("YEP");
            //aux_hojas.add(result);
            return null;
        }
        result.hojas = aux_hojas;
        return result;
    }

    private StringTree clone(StringTree father, List<StringTree> aux_hojas) {
        //System.out.println(data);
        StringTree result = new StringTree(this.father,data);
        for (int i = 0; i < children.size(); ++i) {
            StringTree aux = children.get(i).clone(result,aux_hojas);
            if (aux != null) result.children.add(aux);
        }
        //System.out.println(children.size());
        if (children.size() == 0) {
            //System.out.println("YEP");
            aux_hojas.add(father);
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
        //System.out.println(data);
        for (int i = 0; i < children.size(); ++i) {
            print_recursion(data,children.get(i));
        }
    }

    private void print_recursion(String data, StringTree node) {
        data += " " + node.getData();
        //System.out.println(data);
        List<StringTree> children = node.getChildren();
        if (children.size() <= 0) {
            System.out.println(data);
        }
        else {
            for (int i = 0; i < children.size(); ++i) {
                print_recursion(data, children.get(i));
            }
        }
    }
}


