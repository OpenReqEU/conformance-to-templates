package upc.req_quality.adapter;

import upc.req_quality.entity.SpanOut;

import java.util.List;

public interface AdapterTemplate {

    public boolean check_template(String[] tokens, String[] tokens_tagged, String[] chunks);
}
