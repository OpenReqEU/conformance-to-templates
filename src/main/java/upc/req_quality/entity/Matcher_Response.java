package upc.req_quality.entity;

import java.util.*;

public class Matcher_Response {

    private class Matcher_Error implements Comparator<Matcher_Error> {
        private int index;
        private String description;

        public Matcher_Error(int index, String description) {
            this.index = index;
            this.description = description;
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

    }

    private boolean result;
    private int tokens_lenght;
    private float score;
    private LinkedList<Matcher_Error> errors;

    public Matcher_Response(int tokens_lenght) {
        this.tokens_lenght = tokens_lenght;
        this.errors = new LinkedList<>();
        this.score = 0;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public void addError(int index, String description) {
        if (this.errors.size() > 0) {
            if (this.errors.getLast().index < index) {
                this.score = (float) index / this.tokens_lenght;
                if (this.score >= 1) this.score = 0.9f;
            }
        }
        this.errors.add(new Matcher_Error(index,description));
    }

    public List<String> getErrorDescriptions() {
        List<String> result = new ArrayList<>();
        if (errors.size() > 0) {
            int max_index = errors.getLast().index;
            boolean next = true;
            while (next) {
                if (errors.size() == 0) next = false;
                else {
                    if (errors.getLast().index < max_index) next = false;
                    else {
                        result.add(errors.getLast().description);
                        errors.removeLast();
                    }
                }
            }
        }

        return result;
    }

    public boolean isResult() {
        return result;
    }

    public float getScore() {
        return score;
    }
}
