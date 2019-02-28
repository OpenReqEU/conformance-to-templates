package upc.req_quality.service;

import org.springframework.stereotype.Service;
import upc.req_quality.adapter.AdapterPosTagger;
import upc.req_quality.adapter.AdapterTemplate;
import upc.req_quality.entity.*;
import upc.req_quality.entity.input_output.Requirements;
import upc.req_quality.entity.input_output.Template;
import upc.req_quality.entity.input_output.Templates;
import upc.req_quality.entity.input_output.Tip;
import upc.req_quality.exeption.BadBNFSyntaxException;
import upc.req_quality.exeption.BadRequestException;
import upc.req_quality.exeption.InternalErrorException;

import java.util.ArrayList;
import java.util.List;

@Service("conformanceService")
public class ConformanceServiceImpl implements ConformanceService {

    @Override
    public Requirements check_conformance(String organization, List<Requirement> reqs) throws BadRequestException, BadBNFSyntaxException, InternalErrorException {

        String library = "OpenNLP";

        List<Requirement> result = new ArrayList<>();

        AdapterFactory factory = AdapterFactory.getInstance();
        AdapterPosTagger tagger = factory.getAdapterPosTagger(library);
        List<AdapterTemplate> templates = factory.getAdapterTemplates(organization);

        if (templates == null) throw new BadRequestException("This organization has zero templates.");

        for (int i = 0; i < reqs.size(); ++i) {
            Requirement aux_req = reqs.get(i);

            String[] tokens = tagger.tokenizer(aux_req.getText());
            String[] tokens_tagged = tagger.pos_tagger(tokens);
            String[] chunks = tagger.chunker(tokens,tokens_tagged);

            boolean ok = false;

            List<Tip> tips = new ArrayList<>();

            for (int j = 0; ((!ok) && j < templates.size()); ++j) {
                Matcher_Response matcher_response = templates.get(j).check_template(tokens,tokens_tagged,chunks);
                ok = matcher_response.isResult();
                if (!ok) {
                    tips.add(new Tip(templates.get(j).check_name(),matcher_response.getErrorDescriptions(),matcher_response.getScore()));
                }
            }

            if (!ok) result.add(new Requirement(aux_req.getId(),aux_req.getText(),tips,create_tokens_output(tokens,tokens_tagged,chunks)));
        }

        return new Requirements(result);
    }

    @Override
    public void enter_new_templates(Templates templates) throws BadBNFSyntaxException, InternalErrorException {
        AdapterFactory af = AdapterFactory.getInstance();
        List<Template> aux_templates = templates.getTemplates();
        for (int i = 0; i < aux_templates.size(); ++i) {
            af.enter_new_template(aux_templates.get(i));
        }
    }

    @Override
    public Templates check_organization_templates(String organization) throws InternalErrorException, BadBNFSyntaxException {
        AdapterFactory af = AdapterFactory.getInstance();
        return af.check_organization_models(organization);
    }

    @Override
    public void clear_db(String organization) throws InternalErrorException, BadBNFSyntaxException {
        AdapterFactory af = AdapterFactory.getInstance();
        af.clear_db(organization);
    }

    private List<String> create_tokens_output(String[] tokens, String[] tokens_tagged, String[] chunks) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < tokens.length; ++i) {
            result.add(tokens[i] + " " + tokens_tagged[i] + " " + chunks[i]);
        }
        return result;
    }
}
