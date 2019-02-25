package upc.req_quality.adapter;

import upc.req_quality.entity.Matcher_Response;
import upc.req_quality.entity.SpanOut;

import java.util.List;

public interface AdapterTemplate {

    public Matcher_Response check_template(String[] tokens, String[] tokens_tagged, String[] chunks);

    public String check_organization();

    public String check_name();
}
