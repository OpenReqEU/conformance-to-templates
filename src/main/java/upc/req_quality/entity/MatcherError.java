package upc.req_quality.entity;

import upc.req_quality.adapter_template.StringTree;

public class MatcherError {
    private int index;
    private int finalIndex;
    private String description;
    private StringTree node;
    private String comment;

    public MatcherError(int index, int finalIndex, String description, StringTree node, String comment) {
        this.index = index;
        this.finalIndex = finalIndex;
        this.description = description;
        this.node = node;
        this.comment = comment;
    }

    public int getIndex() {
        return index;
    }

    public int getFinalIndex() {
        return finalIndex;
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