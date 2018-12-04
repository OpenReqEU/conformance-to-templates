import upc.req_quality.adapter.AdapterPosTagger;
import upc.req_quality.adapter.OpenNLP_PosTagger;
import upc.req_quality.entity.Requirement;

public class DriverOpenNLP {

    public static void main(String[] args) {

        AdapterPosTagger tagger = new OpenNLP_PosTagger();
        String text = "If the guest is registered, the system should display the customer-specific menu to the guest.";
        Requirement aux_req = new Requirement("1",text);

        String[] tokens = tagger.tokenizer(aux_req.getText());
        String[] tokens_tagged = tagger.pos_tagger(tokens);
        String[] chunks = tagger.chunker(tokens,tokens_tagged);

        for (int i = 0; i < tokens.length; ++i) {
            String result = tokens[i] +  " " + tokens_tagged[i] + " " + chunks[i];
            System.out.println(result);
        }
    }
}
