package upc.req_quality.adapter;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.Span;
import upc.req_quality.entity.SpanOut;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class OpenNLP_PosTagger implements AdapterPosTagger {

    public String[] tokenizer(String sentence) {
        Tokenizer tokenizer = WhitespaceTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(sentence);
        return tokens;
    }

    public String[] pos_tagger(String[] tokens) {

        POSModel model = null;
        try {
            try (InputStream modelIn = new FileInputStream("en-pos-maxent.bin")) {
                model = new POSModel(modelIn);
        }
        } catch (IOException e) {
            e.printStackTrace();
        }

        POSTaggerME tagger = new POSTaggerME(model);
        String[] tags= tagger.tag(tokens);

        return tags;
    }

    public String[] chunker(String[] tokens, String[] tokens_tagged) {

        ChunkerModel model = null;
        try {
            try (InputStream modelIn = new FileInputStream("en-chunker.bin")) {
                model = new ChunkerModel(modelIn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ChunkerME chunker = new ChunkerME(model);
        String[] chunks = chunker.chunk(tokens,tokens_tagged);

        return chunks;
    }

    public List<SpanOut> chunker_spans(String[] tokens, String[] tokens_tagged) {

        ChunkerModel model = null;
        try {
            try (InputStream modelIn = new FileInputStream("en-chunker.bin")) {
                model = new ChunkerModel(modelIn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ChunkerME chunker = new ChunkerME(model);
        Span[] chunks = chunker.chunkAsSpans(tokens,tokens_tagged);

        List<SpanOut> aux = new ArrayList<>();

        for (int i = 0; i < chunks.length; ++i) {
            String value = chunks[i].getType();
            if (!value.equals("PP") && !value.equals("SBAR")) {
                int start = chunks[i].getStart();
                int end = chunks[i].getEnd();
                aux.add(new SpanOut(start, end, value));
            }
        }

        return aux;
    }
}
