package upc.req_quality.adapter;

import upc.req_quality.entity.MatcherResponse;

public interface AdapterTemplate {

    public MatcherResponse checkTemplate(String[] tokens, String[] tokens_tagged, String[] chunks);

    public String checkOrganization();

    public String checkName();
}
