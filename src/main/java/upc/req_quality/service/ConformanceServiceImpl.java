package upc.req_quality.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import upc.req_quality.adapter.AdapterPosTagger;
import upc.req_quality.adapter.AdapterTemplate;
import upc.req_quality.entity.Requirement;
import upc.req_quality.entity.SpanOut;

import java.util.ArrayList;
import java.util.List;

@Service("conformanceService")
public class ConformanceServiceImpl implements ConformanceService {

    @Override
    public JSONObject check_conformance(List<Requirement> reqs) {

        List<Requirement> result = new ArrayList<>();
        AdapterFactory factory = AdapterFactory.getInstance();
        AdapterPosTagger tagger = factory.getAdapterPosTagger();
        List<AdapterTemplate> templates = factory.getAdapterTemplates();

        System.out.println("size: " + reqs.size());

        for (int i = 0; i < reqs.size(); ++i) {
            String[] tokens = tagger.tokenizer(reqs.get(i).getText());
            String[] tokens_tagged = tagger.pos_tagger(tokens);
            String[] chunks = tagger.chunker(tokens,tokens_tagged);

            List<String> tokens_list = new ArrayList<>();

            for (int j = 0; j < tokens.length; ++j) {
                tokens_list.add(tokens[j]);
            }

            for (int j = 0; j < chunks.length; ++j) {
                System.out.println(tokens[j] + " " + tokens_tagged[j] + " " + chunks[j]);
            }

            List<SpanOut> spans = tagger.chunker_spans(tokens,tokens_tagged);


            for (int j = 0; j < spans.size(); ++j) {
                System.out.println(spans.get(j).toString());
            }

            boolean ok = false;

            for (int j = 0; j < templates.size() && !ok; ++j) {
                ok = templates.get(j).check_template(spans,tokens_list);
            }

            System.out.println(ok);
        }

        return null;
    }
}
