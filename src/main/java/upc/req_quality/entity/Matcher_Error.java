package upc.req_quality.entity;

import upc.req_quality.adapter.String_Tree;

import java.util.Comparator;

public class Matcher_Error implements Comparator<Matcher_Error> {
    private int index;
    private int final_index;
    private String description;
    private String_Tree node;
    private String comment;

    public Matcher_Error(int index, int final_index, String description, String_Tree node, String comment) {
        this.index = index;
        this.final_index = final_index;
        this.description = description;
        this.node = node;
        this.comment = comment;
    }

    @Override
    public int compare(Matcher_Error a, Matcher_Error b) {
        int compare = 0;

        int valA =  a.index;
        int valB = b.index;
        if (valA > valB) compare = -1;
        else if (valA < valB) compare = 1;

        return compare;
    }

    public int getIndex() {
        return index;
    }

    public int getFinal_index() {
        return final_index;
    }

    public String getDescription() {
        return description;
    }

    public String_Tree getNode() {
        return node;
    }

    public String getComment() {
        return comment;
    }
}