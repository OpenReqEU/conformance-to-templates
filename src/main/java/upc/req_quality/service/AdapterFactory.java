package upc.req_quality.service;

import upc.req_quality.adapter.AdapterPosTagger;
import upc.req_quality.adapter.AdapterTemplate;
import upc.req_quality.adapter.OpenNLP_PosTagger;
import upc.req_quality.adapter.Generic_template;

import java.util.ArrayList;
import java.util.List;

public class AdapterFactory {

    private static AdapterFactory ourInstance;
    private static AdapterPosTagger posTagger;
    private static List<AdapterTemplate> templates;

    private AdapterFactory() {
        posTagger = new OpenNLP_PosTagger();
        templates = new ArrayList<>();
        templates.add(new Generic_template());
    }

    public static AdapterFactory getInstance() {
        if (ourInstance == null) ourInstance = new AdapterFactory();
        return ourInstance;
    }

    public AdapterPosTagger getAdapterPosTagger() {
        return posTagger;
    }

    public List<AdapterTemplate> getAdapterTemplates() {
        return templates;
    }
}
