package upc.req_quality.service;

import org.springframework.stereotype.Service;
import upc.req_quality.adapter.AdapterPosTagger;
import upc.req_quality.adapter.AdapterTemplate;
import upc.req_quality.entity.*;

import java.util.ArrayList;
import java.util.List;

@Service("conformanceService")
public class ConformanceServiceImpl implements ConformanceService {

    @Override
    public List<Requirement> check_conformance(List<Requirement> reqs) {

        List<Requirement> result = new ArrayList<>();

        AdapterFactory factory = AdapterFactory.getInstance();
        AdapterPosTagger tagger = factory.getAdapterPosTagger();
        List<AdapterTemplate> templates = factory.getAdapterTemplates();

        for (int i = 0; i < reqs.size(); ++i) {
            Requirement aux_req = reqs.get(i);

            String[] tokens = tagger.tokenizer(aux_req.getText());
            String[] tokens_tagged = tagger.pos_tagger(tokens);
            String[] chunks = tagger.chunker(tokens,tokens_tagged);

            boolean ok = false;

            for (int j = 0; ((!ok) && j < templates.size()); ++j) {
                ok = templates.get(j).check_template(tokens,tokens_tagged,chunks);
            }

            if(!ok) result.add(new Requirement(aux_req.getId(),aux_req.getText()));

            System.out.println(ok);
        }

        return result;
    }

    @Override
    public PermitedClauses check_permited_clauses() {
        AdapterFactory af = AdapterFactory.getInstance();
        String[] permited_clauses = af.check_permited_clauses();
        PermitedClauses aux = new PermitedClauses(null,permited_clauses);
        return aux;
    }

    @Override
    public void enter_new_model(Model model) {
        AdapterFactory af = AdapterFactory.getInstance();
        af.enter_new_model(model);
    }

    @Override
    public List<Model> check_all_models() {
        AdapterFactory af = AdapterFactory.getInstance();
        return af.check_all_models();
    }

    @Override
    public void clear_db() {
        AdapterFactory af = AdapterFactory.getInstance();
        af.clear_db();
        return;
    }
}
