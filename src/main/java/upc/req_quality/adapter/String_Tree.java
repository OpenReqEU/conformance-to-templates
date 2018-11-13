package upc.req_quality.adapter;

import java.util.ArrayList;
import java.util.List;

public class String_Tree {
    private String data;
    private String_Tree parent;
    private List<String_Tree> children;

    private String_Tree() {
    }

    public String_Tree(String data) {
        this.data = data;
        this.children = new ArrayList<>();
    }

    public String_Tree(String data, List<String_Tree> children) {
        this.data = data;
        this.children = children;
    }

    public void add_children(String_Tree children) {
        this.children.add(children);
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


