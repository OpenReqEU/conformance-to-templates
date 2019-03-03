package upc.req_quality.adapter;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.Span;
import upc.req_quality.entity.SpanOut;
import upc.req_quality.exception.InternalErrorException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OpenNLP_PosTagger implements AdapterPosTagger {

    static {
        try {
            pos_tags = new ArrayList<>();
            sentence_tags = new ArrayList<>();
            hash_descriptions = new HashMap<>();
            load_information("./opennlp_tags");
        } catch (InternalErrorException e) {
            e.printStackTrace();
        }
    }

    private static List<String> pos_tags;
    private static List<String> sentence_tags;
    private static HashMap<String,String> hash_descriptions;

    private static void load_information(String path) throws InternalErrorException {
        String line = "";
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(path);
            bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split("->");
                if (parts.length != 2) throw new InternalErrorException("Error loading tags data");
                parts[0] = parts[0].replaceAll(" ", "");
                parts[1] = parts[1].replaceAll(" ", "");
                String tag = parts[0];
                String description = parts[1];
                if (tag.contains("<")) sentence_tags.add(tag);
                else if (tag.contains("(")) pos_tags.add(tag);
                else throw new InternalErrorException("Error loading tags data");
                hash_descriptions.put(tag,description);
            }
            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalErrorException("Error loading tags data");
        }
        finally {
            try {
                if (fileReader != null) fileReader.close();
                if (bufferedReader != null) bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new InternalErrorException("Error loading tags data");
            }
        }
    }


    @Override
    public List<String> getPos_tags() {
        return pos_tags;
    }

    @Override
    public List<String> getSentence_tags() {
        return sentence_tags;
    }


    @Override
    public String[] tokenizer(String sentence) {
        Tokenizer tokenizer = WhitespaceTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(sentence);
        return tokens;
    }


    @Override
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
        String[] tags = tagger.tag(tokens);
        for (int i = 0; i < tags.length; ++i) {
            tags[i] = "("+tags[i].toLowerCase()+")";
        }

        return tags;
    }

    @Override
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
        for (int i = 0; i < chunks.length; ++i) {
            chunks[i] = chunks[i].toLowerCase();
            if (chunks[i].contains("vp")) chunks[i] = "<vp>";
            else chunks[i] = "<np>";
        }
        return chunks;
    }

    @Override
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

    @Override
    public String getTagDescription(String tag) {
        return hash_descriptions.get(tag);
    }
}
