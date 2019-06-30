package upc.req_quality.adapter;

import java.util.List;

public interface AdapterPosTagger {

    List<String> getPosTags();

    List<String> getSentenceTags();

    String[] tokenizer(String sentence);

    String[] posTagger(String[] tokens);

    String[] chunker(String[] tokens, String[] tokensTagged);

    String getTagDescription(String tag);
}
