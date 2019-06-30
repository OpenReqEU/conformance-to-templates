package upc.req_quality.entity.drivers;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import upc.req_quality.adapter.AdapterPosTagger;
import upc.req_quality.adapter.OpenNLPPosTagger;
import upc.req_quality.entity.Requirement;
import upc.req_quality.exception.InternalErrorException;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


public class DriverOpenNLP {

    public static void main(String[] args) throws InternalErrorException {

        AdapterPosTagger tagger = OpenNLPPosTagger.getInstance();
        String text = "When the water level falls below the Low Water Threshold, the software shall open the water valve to fill the tank to the High Water Threshold.";
        Requirement aux_req = new Requirement();
        aux_req.setId("1");
        aux_req.setText(text);

        String[] tokens = tagger.tokenizer(aux_req.getText());
        String[] tokens_tagged = tagger.posTagger(tokens);
        String[] chunks = tagger.chunker(tokens, tokens_tagged);

        for (int i = 0; i < tokens.length; ++i) {
            String result = tokens[i] + " " + tokens_tagged[i] + " " + chunks[i];
            System.out.println(result);
        }

        System.out.println("\n\nStanford");

        MaxentTagger tagger2 = new MaxentTagger("./models/english-left3words-distsim.tagger");

        List<TaggedWord> aux = new ArrayList<>();

        List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new BufferedReader(new StringReader(text)));
        for (List<HasWord> sentence : sentences) {
            List<TaggedWord> tSentence = tagger2.tagSentence(sentence);
            aux.addAll(tSentence);
        }

        for (TaggedWord word: aux) {
            System.out.println(word.tag());
        }



    }
}
