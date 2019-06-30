package upc.req_quality.entity;

import upc.req_quality.adapter.StringTree;

import java.util.*;

public class MatcherResponse {

    private boolean result;
    private int index;
    private LinkedList<MatcherError> errors;

    public MatcherResponse() {
        this.errors = new LinkedList<>();
        this.index = 0;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public void addError(int index, int finalIndex, String description, StringTree node, String comment) {
        if (index > this.index) this.index = index;
        this.errors.add(new MatcherError(index,finalIndex,description,node,comment));
    }

    public List<StringTree> getLastErrorNodes() {
        List<StringTree> array = new ArrayList<>();
        if (!errors.isEmpty()) {
            int maxIndex = errors.getLast().getIndex();
            for (MatcherError error : errors) {
                if (error.getIndex() > maxIndex) maxIndex = error.getIndex();
            }
            for (MatcherError error : errors) {
                if (error.getIndex() == maxIndex) array.add(error.getNode());
            }
        }
        return array;
    }


    public int getSizeErrors() {
        return errors.size();
    }

    public void addAllErrors(List<MatcherError> errors) {
        for (MatcherError error: errors) {
            this.addError(error.getIndex(),error.getFinalIndex(),error.getDescription(),error.getNode(),error.getComment());
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

    public List<MatcherError> getErrors() {
        return errors;
    }
}
