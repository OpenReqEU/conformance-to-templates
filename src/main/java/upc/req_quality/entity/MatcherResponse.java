package upc.req_quality.entity;

import upc.req_quality.adapter.StringTree;

import java.util.*;

public class MatcherResponse {

    private boolean result;
    private int index;
    private int length;
    private LinkedList<MatcherError> errors;

    public MatcherResponse(int length) {
        this.errors = new LinkedList<>();
        this.index = 0;
        this.length = length;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public void addError(int index, int final_index, String description, StringTree node, String comment) {
        if (index > this.index) this.index = index;
        this.errors.add(new MatcherError(index,final_index,description,node,comment));
    }

    public List<StringTree> getLastErrorNodes() {
        List<StringTree> result = new ArrayList<>();
        if (errors.size() > 0) {
            int max_index = errors.getLast().getIndex();
            for (MatcherError error : errors) {
                if (error.getIndex() > max_index) max_index = error.getIndex();
            }
            for (MatcherError error : errors) {
                if (error.getIndex() == max_index) result.add(error.getNode());
            }
        }
        return result;
    }


    public int getSizeErrors() {
        return errors.size();
    }

    public void addAllErrors(LinkedList<MatcherError> errors) {
        for (MatcherError error: errors) {
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

    public LinkedList<MatcherError> getErrors() {
        return errors;
    }
}
