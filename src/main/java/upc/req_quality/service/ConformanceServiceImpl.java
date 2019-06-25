package upc.req_quality.service;

import org.springframework.stereotype.Service;
import upc.req_quality.adapter.AdapterPosTagger;
import upc.req_quality.adapter.AdapterTemplate;
import upc.req_quality.entity.*;
import upc.req_quality.entity.input_output.Requirements;
import upc.req_quality.entity.input_output.Template;
import upc.req_quality.entity.input_output.Templates;
import upc.req_quality.entity.input_output.Tip;
import upc.req_quality.exception.BadBNFSyntaxException;
import upc.req_quality.exception.BadRequestException;
import upc.req_quality.exception.InternalErrorException;

import java.util.ArrayList;
import java.util.List;

@Service("conformanceService")
public class ConformanceServiceImpl implements ConformanceService {

    @Override
    public Requirements checkConformance(String organization, List<Requirement> reqs) throws BadRequestException, BadBNFSyntaxException, InternalErrorException {

        List<Requirement> result = new ArrayList<>();

        AdapterFactory factory = AdapterFactory.getInstance();
        AdapterPosTagger tagger = factory.getAdapterPosTagger();
        List<AdapterTemplate> templates = factory.getAdapterTemplates(organization);

        if (templates == null) throw new BadRequestException("This organization has zero templates.");

        for (int i = 0; i < reqs.size(); ++i) {
            Requirement aux_req = reqs.get(i);

            String[] tokens = tagger.tokenizer(aux_req.getText());
            String[] tokens_tagged = tagger.posTagger(tokens);
            String[] chunks = tagger.chunker(tokens,tokens_tagged);

            boolean ok = false;

            List<Tip> tips = new ArrayList<>();

            for (int j = 0; ((!ok) && j < templates.size()); ++j) {
                MatcherResponse matcher_response = templates.get(j).checkTemplate(tokens,tokens_tagged,chunks);
                ok = matcher_response.isResult();
                if (!ok) {
                    for (MatcherError matcher_error: matcher_response.getErrors()) {
                        tips.add(new Tip(templates.get(j).checkName(),matcher_error.getIndex()+":"+matcher_error.getFinal_index(),explainError(matcher_error.getDescription(),tagger),matcher_error.getComment()));
                    }
                }
            }

            if (!ok) result.add(new Requirement(aux_req.getId(),aux_req.getText(),tips));
        }

        return new Requirements(result);
    }

    @Override
    public void enterNewTemplates(Templates templates) throws BadBNFSyntaxException, InternalErrorException {
        AdapterFactory af = AdapterFactory.getInstance();
        List<Template> aux_templates = templates.getTemplates();
        for (int i = 0; i < aux_templates.size(); ++i) {
            af.enterNewTemplate(aux_templates.get(i));
        }
    }

    @Override
    public Templates checkOrganizationTemplates(String organization) throws InternalErrorException, BadBNFSyntaxException {
        AdapterFactory af = AdapterFactory.getInstance();
        return af.checkOrganizationModels(organization);
    }

    @Override
    public void clearDatabase(String organization) throws InternalErrorException, BadBNFSyntaxException {
        AdapterFactory af = AdapterFactory.getInstance();
        af.clearDatabase(organization);
    }

    private String explainError(String tag, AdapterPosTagger tagger) throws InternalErrorException {
        if (tag.contains("||")) {
            String[] parts = tag.split("\\|\\|");
            boolean first = true;
            String aux = "";
            for (String part: parts) {
                if (first) {
                    aux = aux.concat(recognizeTag(part,tagger));
                    first = false;
                }
                else aux = aux.concat(" or " + recognizeTag(part,tagger));
            }
            tag = aux;
        }
        return recognizeTag(tag,tagger);
    }

    private String recognizeTag(String tag,AdapterPosTagger tagger) throws InternalErrorException {
        String aux = "";
        if (tag.contains(")")) aux = tagger.getTagDescription(tag);
        else {
            if (tag.contains("<")) aux = tagger.getTagDescription(tag);
            else aux = tag;
        }
        if (aux == null) throw new InternalErrorException("Tag "+ tag + " not recognized");
        return aux;
    }

    private List<String> createTokensOutput(String[] tokens, String[] tokens_tagged, String[] chunks) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < tokens.length; ++i) {
            result.add(tokens[i] + " " + tokens_tagged[i] + " " + chunks[i]);
        }
        return result;
    }
}
