import com.google.common.collect.ObjectArrays;
import upc.req_quality.adapter.AdapterPosTagger;
import upc.req_quality.adapter.AdapterTemplate;
import upc.req_quality.adapter.Generic_template;
import upc.req_quality.adapter.OpenNLP_PosTagger;
import upc.req_quality.entity.Requirement;
import upc.req_quality.exeption.BadBNFSyntaxException;

public class DriverGenericTemplate {
    public static void main(String[] args) throws BadBNFSyntaxException {
        AdapterPosTagger tagger = new OpenNLP_PosTagger();
        String text = "The system shall inform the guest wether his order has been accepted XOR rejected.o";
        Requirement aux_req = new Requirement("1",text);

        String[] tokens = tagger.tokenizer(aux_req.getText());
        String[] tokens_tagged = tagger.pos_tagger(tokens);
        String[] chunks = tagger.chunker(tokens,tokens_tagged);

        for (int i = 0; i < tokens.length; ++i) {
            String result = tokens[i] +  " " + tokens_tagged[i] + " " + chunks[i];
            System.out.println(result);
        }

        String name = "";
        String org = "";
        String library = "";
        String rules = "#<main> ::=  <opt-condition> <np> (md) (vb) <np> | \n" +
                "             <opt-condition> <np> <modal> %PROVIDE <np> %WITH %THE %ABILITY <infinitive-vp> <np> <*> | \n" +
                "             <opt-condition> <np> <modal> %BE %ABLE <vp> <np> | \n" +
                "             <opt-condition> <np> <modal> <vp> <np>\n" +
                "#<conditional-keyword> ::= %IF | %AFTER | %AS %SOON %AS | %AS %LONG %AS\n" +
                "#<modal> ::= %SHALL | %SHOULD | %WOULD\n" +
                "#<opt-condition> ::= <conditional-keyword> | (all)\n" +
                "#<infinitive-vp> ::= %to <vp>";
        String[] permited_clauses = ObjectArrays.concat(new OpenNLP_PosTagger().getPos_tags(), new OpenNLP_PosTagger().getSentence_tags(), String.class);
        AdapterTemplate at = new Generic_template(name,org,library,rules,permited_clauses);
        System.out.println("\n\n" + at.check_template(tokens,tokens_tagged,chunks));

    }
}
