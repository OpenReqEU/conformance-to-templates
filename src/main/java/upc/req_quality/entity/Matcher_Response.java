package upc.req_quality.entity;

import java.util.*;

public class Matcher_Response {

    private boolean result;
    private int tokens_lenght;
    private float score;
    private SortedMap<Integer,String> errors;

    public Matcher_Response(int tokens_lenght) {
        this.tokens_lenght = tokens_lenght;
        this.errors = new TreeMap<>();
        this.score = 0;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public void addError(int index, String description) {
        this.errors.put(index,description);
        if (this.errors.lastKey() < index) {
            this.score = (float)index/this.tokens_lenght;
            if (this.score >= 1) this.score = 0.9f;
        }
    }

    public List<String> getErrorDescriptions() {
        List<String> result = new ArrayList<>();
        if (errors.values().size() > 0) result.add(errors.get(errors.lastKey()));
        errors.remove(errors.lastKey());
        if (errors.values().size() > 0) result.add(errors.get(errors.lastKey()));
        return result;
    }

    public boolean isResult() {
        return result;
    }

    public float getScore() {
        return score;
    }
}
