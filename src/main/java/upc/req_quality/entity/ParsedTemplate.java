package upc.req_quality.entity;

import upc.req_quality.adapter.StringTree;

public class ParsedTemplate {

    private String name;
    private String organization;
    private StringTree rules;

    public ParsedTemplate(String name, String organization, StringTree rules) {
        this.name = name;
        this.organization = organization;
        this.rules = rules;
    }

    public String getName() {
        return name;
    }

    public String getOrganization() {
        return organization;
    }

    public StringTree getRules() {
        return rules;
    }
}
