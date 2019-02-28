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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class OpenNLP_PosTagger implements AdapterPosTagger {

    /*
    CC Coordinating conjunction
CD Cardinal number
DT Determiner
EX Existential there
FW Foreign word
IN Preposition or subordinating conjunction
JJ Adjective
JJR Adjective, comparative
JJS Adjective, superlative
LS List item marker
MD Modal
NN Noun, singular or mass
NNS Noun, plural
NNP Proper noun, singular
NNPS Proper noun, plural
PDT Predeterminer
POS Possessive ending
PRP Personal pronoun
PRP$ Possessive pronoun
RB Adverb
RBR Adverb, comparative
RBS Adverb, superlative
RP Particle
SYM Symbol
TO to
UH Interjection
VB Verb, base form
VBD Verb, past tense
VBG Verb, gerund or present participle
VBN Verb, past participle
VBP Verb, non­3rd person singular present
VBZ Verb, 3rd person singular present
WDT Wh­determiner
WP Wh­pronoun
WP$ Possessive wh­pronoun
WRB Wh­adverb
     */

    private String[] pos_tags = new String[]{"(cc)","(cd)","(dt)","(ex)","(fw)","(in)","(jj)","(jjr)","(jjs)","(ls)","(md)","(nn)","(nns)","(nnp)","(nnps)","(pdt)","(pos)","(prp)","(prp$)",
                                                    "(rb)","(rbr)","(rbs)","(rp)","(sym)","(to)","(uh)","(vb)","(vbd)","(vbg)","(vbn)","(vbp)","(vbp)","(vbz)","(wdt)","(wp)","(wp$)","(wrb)"};
    private String[] sentence_tags = new String[]{"<np>","<vp>"};

    @Override
    public String[] getPos_tags() {
        return pos_tags;
    }

    @Override
    public String[] getSentence_tags() {
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
}
