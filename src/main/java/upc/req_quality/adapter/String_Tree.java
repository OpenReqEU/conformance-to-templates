package upc.req_quality.adapter;

import java.util.ArrayList;
import java.util.List;

public class String_Tree {
    private String data;
    private List<String_Tree> children;
    private List<String_Tree> hojas;
    private String_Tree father;

    public String_Tree(String_Tree father) {
        this.father = father;
        this.children = new ArrayList<>();
        this.hojas = new ArrayList<>();
    }

    public String_Tree(String_Tree father, String data) {
        this.father = father;
        this.data = data;
        this.children = new ArrayList<>();
        this.hojas= new ArrayList<>();
    }

    public String_Tree add_children(String_Tree children) {
        this.children.add(children);
        children.father = this;
        return children;
    }

    public String getData() {
        return data;
    }

    public List<String_Tree> getChildren() {
        return children;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setFather(String_Tree father) {
        this.father = father;
    }

    public String_Tree clone_top() {
        List<String_Tree> aux_hojas = new ArrayList<>();
        //System.out.println(data);
        String_Tree result = new String_Tree(father,data);
        for (int i = 0; i < children.size(); ++i) {
            String_Tree aux = children.get(i).clone(result,aux_hojas);
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

    private String_Tree clone(String_Tree father, List<String_Tree> aux_hojas) {
        //System.out.println(data);
        String_Tree result = new String_Tree(this.father,data);
        for (int i = 0; i < children.size(); ++i) {
            String_Tree aux = children.get(i).clone(result,aux_hojas);
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

    public List<String_Tree> getHojas() {
        return hojas;
    }

    public String_Tree getFather() {
        return father;
    }

    public void print() {
        //System.out.println(data);
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
}


