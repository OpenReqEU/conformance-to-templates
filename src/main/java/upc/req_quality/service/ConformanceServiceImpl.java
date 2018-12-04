package upc.req_quality.service;

import org.springframework.stereotype.Service;
import upc.req_quality.adapter.AdapterPosTagger;
import upc.req_quality.adapter.AdapterTemplate;
import upc.req_quality.entity.*;
import upc.req_quality.exeption.BadBNFSyntaxException;
import upc.req_quality.exeption.BadRequestException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service("conformanceService")
public class ConformanceServiceImpl implements ConformanceService {

    @Override
    public Requirements check_conformance(String library, String organization, List<Requirement> reqs) throws BadRequestException{

        List<Requirement> result = new ArrayList<>();

        AdapterFactory factory = AdapterFactory.getInstance();
        AdapterPosTagger tagger = factory.getAdapterPosTagger(library);
        List<AdapterTemplate> templates = factory.getAdapterTemplates();

        for (int i = 0; i < reqs.size(); ++i) {
            Requirement aux_req = reqs.get(i);

            String[] tokens = tagger.tokenizer(aux_req.getText());
            String[] tokens_tagged = tagger.pos_tagger(tokens);
            String[] chunks = tagger.chunker(tokens,tokens_tagged);

            boolean ok = false;

            for (int j = 0; ((!ok) && j < templates.size()); ++j) {
                if (templates.get(j).check_library().equals(library)) ok = templates.get(j).check_template(tokens,tokens_tagged,chunks);
            }

            if(!ok) result.add(new Requirement(aux_req.getId(),aux_req.getText()));
        }

        return new Requirements(result);
    }

    @Override
    public PermitedClauses check_permited_clauses(String library) throws BadRequestException {
        AdapterFactory af = AdapterFactory.getInstance();
        AdapterPosTagger ap = af.getAdapterPosTagger(library);
        String[] permited_pos_tags = ap.getPos_tags();
        String[] permited_sentence_tags = ap.getSentence_tags();
        String[] permited_matcher_tags = af.check_matcher_tags();
        PermitedClauses aux = new PermitedClauses(permited_pos_tags,permited_sentence_tags,permited_matcher_tags);
        return aux;
    }

    @Override
    public void enter_new_templates(Templates templates) throws BadRequestException, BadBNFSyntaxException, SQLException {
        AdapterFactory af = AdapterFactory.getInstance();
        List<Template> aux_templates = templates.getTemplates();
        for (int i = 0; i < aux_templates.size(); ++i) {
            af.enter_new_template(aux_templates.get(i));
        }
    }

    @Override
    public Templates check_all_templates(String organization) throws SQLException {
        AdapterFactory af = AdapterFactory.getInstance();
        return af.check_all_models(organization);
    }

    @Override
    public void clear_db(String organization) throws SQLException{
        AdapterFactory af = AdapterFactory.getInstance();
        af.clear_db(organization);
        return;
    }
}
