package upc.req_quality.service;

import upc.req_quality.adapter_tagger.AdapterTagger;
import upc.req_quality.adapter_tagger.OpenNLPTagger;
import upc.req_quality.adapter_template.*;
import upc.req_quality.db.SQLiteDAO;
import upc.req_quality.db.TemplateDatabase;
import upc.req_quality.exception.InternalErrorException;
import upc.req_quality.util.Constants;
import upc.req_quality.util.Control;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdapterFactory {

    private static AdapterFactory instance = new AdapterFactory();
    private AdapterTagger adapterTagger;
    private AdapterTemplate adapterTemplate;
    private TemplateDatabase templateDatabase;

    private AdapterFactory() {
        try {
            this.adapterTagger = new OpenNLPTagger();
            this.adapterTemplate = new GenericTemplate();
            this.templateDatabase = new SQLiteDAO();
        } catch (InternalErrorException | ClassNotFoundException e) {
            Control.getInstance().showErrorMessage(e.getMessage());
        }
    }

    public static AdapterFactory getInstance() {
        return instance;
    }

    public AdapterTagger getAdapterTagger() {
        return adapterTagger;
    }

    public AdapterTemplate getAdapterTemplate() {
        return adapterTemplate;
    }

    public TemplateDatabase getTemplateDatabase() {
        return templateDatabase;
    }

    public List<String> getPermittedClauses() {
        String[] matcherTags = Constants.getInstance().getMatcherTags();
        List<String> postTags = adapterTagger.getPosTags();
        List<String> sentenceTags = adapterTagger.getSentenceTags();
        List<String> permittedClauses = new ArrayList<>(postTags);
        permittedClauses.addAll(sentenceTags);
        permittedClauses.addAll(Arrays.asList(matcherTags));
        return permittedClauses;
    }
}
