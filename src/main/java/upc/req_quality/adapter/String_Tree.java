package upc.req_quality.adapter;

import java.util.ArrayList;
import java.util.List;

public class String_Tree {
    private String data;
    private String_Tree parent;
    private List<String_Tree> children;
    private List<String_Tree> hojas;

    private String_Tree() {
    }

    public String_Tree(String data) {
        this.data = data;
        this.children = new ArrayList<>();
        this.hojas= new ArrayList<>();
    }

    public String_Tree(String data, List<String_Tree> children) {
        this.data = data;
        this.children = children;
        this.hojas= new ArrayList<>();
    }

    public String_Tree add_children(String_Tree children) {
        this.children.add(children);
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

    public String_Tree clone_top() {
        List<String_Tree> aux_hojas = new ArrayList<>();
        //System.out.println(data);
        String_Tree result = new String_Tree(data);
        for (int i = 0; i < children.size(); ++i) {
            result.children.add(children.get(i).clone(aux_hojas));
        }
        //System.out.println(children.size());
        if (children.size() == 0) {
            //System.out.println("YEP");
            aux_hojas.add(result);
        }
        result.hojas = aux_hojas;
        return result;
    }


    private String_Tree clone(List<String_Tree> aux_hojas) {
        //System.out.println(data);
        String_Tree result = new String_Tree(data);
        for (int i = 0; i < children.size(); ++i) {
            result.children.add(children.get(i).clone(aux_hojas));
        }
        //System.out.println(children.size());
        if (children.size() == 0) {
            //System.out.println("YEP");
            aux_hojas.add(result);
        }
        return result;
    }

    public List<String_Tree> getHojas() {
        return hojas;
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


/*public class String_Tree<String> {
    private Node<String> root;

    public String_Tree(String rootData) {
        root = new Node<String>();
        root.data = rootData;
        root.children = new ArrayList<Node<String>>();
    }

    public static class Node<String> {
        private String data;
        private Node<String> parent;
        private List<Node<String>> children;

        public Node() {}

        public Node(String data) {
            this.data = data;
        }

        public Node(String data, List<Node<String>> children) {
            this.data = data;
            this.children = children;
        }

        public void add_children(Node<String> node) {
            children.add(node);
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}*/


