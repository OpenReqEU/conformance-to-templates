package upc.req_quality.adapter;

import upc.req_quality.entity.SpanOut;

import java.util.List;

public interface AdapterPosTagger {

    public List<String> getPosTags();

    public List<String> getSentenceTags();

    public String[] tokenizer(String sentence);

    public String[] posTagger(String[] tokens);

    public String[] chunker(String[] tokens, String[] tokens_tagged);

    public List<SpanOut> chunkerSpans(String[] tokens, String[] tokens_tagged);

    public String getTagDescription(String tag);
}
