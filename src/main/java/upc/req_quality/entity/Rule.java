package upc.req_quality.entity;

public class Rule {

    private String title;
    private String description;

    public Rule(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
