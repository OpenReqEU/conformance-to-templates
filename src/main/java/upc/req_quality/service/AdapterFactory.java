package upc.req_quality.service;

import com.google.common.collect.ObjectArrays;
import javafx.util.Pair;
import upc.req_quality.adapter.*;
import upc.req_quality.db.Model_database;
import upc.req_quality.db.SQLiteDAO;
import upc.req_quality.entity.Model;
import upc.req_quality.entity.Models;

import java.util.ArrayList;
import java.util.List;

public class AdapterFactory {

    private static AdapterFactory ourInstance;
    private AdapterPosTagger posTagger;
    private List<AdapterTemplate> templates;
    private String[] permited_pos_tags;
    private String[] permited_sentence_tags;
    private String[] permited_matcher_tags;

    private AdapterFactory() {
        posTagger = new OpenNLP_PosTagger();
        templates = new ArrayList<>();
        permited_pos_tags = OpenNLP_PosTagger.getPos_tags();
        permited_sentence_tags = OpenNLP_PosTagger.getSentence_tags();
        permited_matcher_tags = Parser_Matcher.getMatcher_tags();
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

    public String[] check_permited_pos_tags() {
        return permited_pos_tags;
    }

    public String[] check_permited_sentence_tags() {
        return permited_sentence_tags;
    }

    public String[] check_matcher_tags() {
        return permited_matcher_tags;
    }

    public void enter_new_model(Model model) {
        templates.add(new Generic_template(model.getName(), model.getRules(), ObjectArrays.concat(permited_pos_tags,permited_sentence_tags,String.class)));
        try {
            Model_database db = new SQLiteDAO();
            db.saveModel(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Models check_all_models() {
        List<Model> aux_models = new ArrayList<>();
        try {
            Model_database db = new SQLiteDAO();
            aux_models = db.getAllModels();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Models(aux_models);
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
                templates.add(new Generic_template(aux_model.getName(), aux_model.getRules(),ObjectArrays.concat(permited_pos_tags,permited_sentence_tags,String.class)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
