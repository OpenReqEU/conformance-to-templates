package upc.req_quality.adapter;

import upc.req_quality.entity.SpanOut;
import upc.req_quality.exeption.InternalErrorException;

import java.util.List;

public interface AdapterPosTagger {

    public String[] getPos_tags();

    public String[] getSentence_tags();

    public String[] tokenizer(String sentence);

    public String[] pos_tagger(String[] tokens);

    public String[] chunker(String[] tokens, String[] tokens_tagged);

    public List<SpanOut> chunker_spans(String[] tokens, String[] tokens_tagged);
}
