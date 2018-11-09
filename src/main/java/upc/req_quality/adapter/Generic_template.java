package upc.req_quality.adapter;

import com.sun.java.accessibility.util.Translator;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ParseInfo;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import upc.req_quality.entity.SpanOut;
import bnf.parser.*;
import org.antlr.*;
import java.util.List;
import java.util.SortedMap;

public class Generic_template implements AdapterTemplate {

    @Override
    public boolean check_template(List<SpanOut> spans, List<String> words) {
        String input = "<boilerplate-conformant> ::= " +
                "<opt-condition> <np> <vp-starting-with-modal> <np> " +
                "<opt-details> | " +
                "<opt-condition> <np> <modal> \"PROVIDE\" <np> " +
                "\"WITH THE ABILITY\" <infinitive-vp> <np> <opt-details> | " +
                "<opt-condition> <np> <modal> \"BE ABLE\" <infinitive-vp> " +
                "<np> <opt-details> " +
                "<opt-condition> ::= \"\" | " +
                "<conditional-keyword> <non-punctuation-token>* \",\" " +
                "<opt-details> ::= \"\" | " +
                "<token-sequence-without-subordinate-conjunctions> " +
                "<modal> ::= \"SHALL\" | \"SHOULD\" | \"WOULD\" " +
                "<conditional-keyword> ::= \"IF\" | \"AFTER\" | \"AS SOON AS\" | " +
                "\"AS LONG AS\"";
        /*CharStream template = CharStreams.fromString(input);
        bnfLexer algo = new bnfLexer(template);
        CommonTokenStream tokens = new CommonTokenStream(algo);
        bnfParser parser = new bnfParser(tokens);
        String[] a = parser.getRuleNames();
        bnfListener listener = new listener();*/


        /*ParseTreePattern a = parser.compileParseTreePattern("<boilerplate-conformant>",1,algo);
        String meh = a.toString();
        System.out.println(meh);*/
        /*String result = parser.getSerializedATN();
        System.out.println(result);*/
        return false;
    }
}
