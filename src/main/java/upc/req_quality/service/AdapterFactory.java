package upc.req_quality.service;

import upc.req_quality.adapter.AdapterPosTagger;
import upc.req_quality.adapter.AdapterTemplate;
import upc.req_quality.adapter.OpenNLP_PosTagger;
import upc.req_quality.adapter.Generic_template;
import upc.req_quality.db.Model_database;
import upc.req_quality.db.SQLiteDAO;
import upc.req_quality.entity.JsonModel;
import upc.req_quality.entity.Model;

import java.util.ArrayList;
import java.util.List;

public class AdapterFactory {

    private static AdapterFactory ourInstance;
    private AdapterPosTagger posTagger;
    private List<AdapterTemplate> templates;

    private AdapterFactory() {
        posTagger = new OpenNLP_PosTagger();
        templates = new ArrayList<>();
        load_models();
        //templates.add(new Generic_template());
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

    public String[] check_permited_clauses() {
        return Generic_template.check_permited_clauses();
    }

    public void enter_new_model(Model model) {
        templates.add(new Generic_template(model.getName(), model.getRules()));
        try {
            Model_database db = new SQLiteDAO();
            db.saveModel(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Model> check_all_models() {
        List<Model> aux_models = new ArrayList<>();
        try {
            Model_database db = new SQLiteDAO();
            aux_models = db.getAllModels();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aux_models;
    }

    public void clear_db() {
        try {
            Model_database db = new SQLiteDAO();
            db.clearDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
        load_models();
        return;
    }

    private void load_models() {
        templates = new ArrayList<>();
        try {
            Model_database db = new SQLiteDAO();
            List<Model> aux_models = db.getAllModels();
            for (int i = 0; i < aux_models.size(); ++i) {
                Model aux_model = aux_models.get(i);
                templates.add(new Generic_template(aux_model.getName(), aux_model.getRules()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
