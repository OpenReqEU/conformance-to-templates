package upc.req_quality.adapter;

import upc.req_quality.entity.MatcherResponse;

public interface AdapterTemplate {

    MatcherResponse checkTemplate(String[] tokens, String[] tokensTagged, String[] chunks);

    String checkOrganization();

    String checkName();
}
