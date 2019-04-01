package upc.req_quality.entity;

import upc.req_quality.adapter.String_Tree;

import java.util.*;

public class Matcher_Response {

    private boolean result;
    private int index;
    private int length;
    private LinkedList<Matcher_Error> errors;

    public Matcher_Response(int length) {
        this.errors = new LinkedList<>();
        this.index = 0;
        this.length = length;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public void addError(int index, int final_index, String description, String_Tree node, String comment) {
        if (index > this.index) this.index = index;
        this.errors.add(new Matcher_Error(index,final_index,description,node,comment));
    }

    public List<String_Tree> getLastErrorNodes() {
        List<String_Tree> result = new ArrayList<>();
        if (errors.size() > 0) {
            int max_index = errors.getLast().getIndex();
            for (Matcher_Error error : errors) {
                if (error.getIndex() > max_index) max_index = error.getIndex();
            }
            for (Matcher_Error error : errors) {
                if (error.getIndex() == max_index) result.add(error.getNode());
            }
        }
        return result;
    }


    public int getSizeErrors() {
        return errors.size();
    }

    public void addAllErrors(LinkedList<Matcher_Error> errors) {
        for (Matcher_Error error: errors) {
            this.addError(error.getIndex(),error.getFinal_index(),error.getDescription(),error.getNode(),error.getComment());
        }
    }

    public void removeAllErrors() {
        errors = new LinkedList<>();
    }

    public boolean isResult() {
        return result;
    }

    public int getIndex() {
        return this.index;
    }

    public LinkedList<Matcher_Error> getErrors() {
        return errors;
    }
}
