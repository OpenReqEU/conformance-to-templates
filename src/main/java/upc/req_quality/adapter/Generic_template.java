package upc.req_quality.adapter;

import upc.req_quality.entity.SpanOut;

import java.util.List;

public class Generic_template implements AdapterTemplate{

    @Override
    public boolean check_template(List<SpanOut> spans, List<String> words) {
        return false;
    }
}
