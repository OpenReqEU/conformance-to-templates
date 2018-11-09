package upc.req_quality.adapter;

import upc.req_quality.entity.SpanOut;

import java.util.List;

public interface AdapterPosTagger {

    public String[] tokenizer(String sentence);

    public String[] pos_tagger(String[] tokens);

    public String[] chunker(String[] tokens, String[] tokens_tagged);

    public List<SpanOut> chunker_spans(String[] tokens, String[] tokens_tagged);
}
