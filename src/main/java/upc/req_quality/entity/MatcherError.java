package upc.req_quality.entity;

import upc.req_quality.adapter.StringTree;

import java.util.Comparator;

public class MatcherError implements Comparator<MatcherError> {
    private int index;
    private int final_index;
    private String description;
    private StringTree node;
    private String comment;

    public MatcherError(int index, int final_index, String description, StringTree node, String comment) {
        this.index = index;
        this.final_index = final_index;
        this.description = description;
        this.node = node;
        this.comment = comment;
    }

    @Override
    public int compare(MatcherError a, MatcherError b) {
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

    public StringTree getNode() {
        return node;
    }

    public String getComment() {
        return comment;
    }
}