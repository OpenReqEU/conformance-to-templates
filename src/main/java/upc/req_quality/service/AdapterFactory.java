package upc.req_quality.service;

import com.google.common.collect.ObjectArrays;
import upc.req_quality.adapter.*;
import upc.req_quality.db.SQLiteDAO;
import upc.req_quality.db.Template_database;
import upc.req_quality.entity.input_output.Template;
import upc.req_quality.entity.input_output.Templates;
import upc.req_quality.exeption.BadBNFSyntaxException;
import upc.req_quality.exeption.BadRequestException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdapterFactory {

    private static AdapterFactory ourInstance;
    private List<AdapterTemplate> templates;
    private String[] permited_matcher_tags;

    private AdapterFactory() {
        templates = new ArrayList<>();
        permited_matcher_tags = Parser_Matcher.getMatcher_tags();
        load_models();
    }

    public static AdapterFactory getInstance() {
        if (ourInstance == null) ourInstance = new AdapterFactory();
        return ourInstance;
    }

    public AdapterPosTagger getAdapterPosTagger(String library) throws BadRequestException{
        switch (library) {
            case "OpenNLP":
                return new OpenNLP_PosTagger();
            default:
                throw new BadRequestException("The library " + library + " does not exist");
        }
    }

    public List<AdapterTemplate> getAdapterTemplates() {
        return templates;
    }

    public String[] check_matcher_tags() {
        return permited_matcher_tags;
    }

    public void enter_new_template(Template template) throws BadRequestException, BadBNFSyntaxException, SQLException {
        String aux_name = template.getName();
        String aux_organization = template.getOrganization();
        String aux_library = template.getLibrary();
        List<String> aux_rules = template.getRules();
        String[] permited_pos_tags;
        String[] permited_sentence_tags;
        switch (aux_library) {
            case "OpenNLP":
                permited_pos_tags = new OpenNLP_PosTagger().getPos_tags();
                permited_sentence_tags = new OpenNLP_PosTagger().getSentence_tags();
                break;
            default:
                throw new BadRequestException("The library " + aux_library + " does not exist");
        }
        String[] permited_tags = ObjectArrays.concat(permited_pos_tags,permited_sentence_tags,String.class);
        AdapterTemplate aux_template = new Generic_template(aux_name,aux_organization,aux_library,aux_rules,permited_tags);
        templates.add(aux_template);
        try {
            Template_database db = new SQLiteDAO();
            db.saveTemplate(template);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Templates check_all_models(String organization) throws SQLException {
        List<Template> aux_models = new ArrayList<>();
        Template_database db;
        try {
            db = new SQLiteDAO();
            aux_models = db.getAllTemplates(organization);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new Templates(aux_models);
    }

    public void clear_db(String organization) throws SQLException{
        try {
            Template_database db = new SQLiteDAO();
            db.clearDB(organization);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //load_models();
    }

    private void load_models(){
        templates = new ArrayList<>();
        try {
            Template_database db = new SQLiteDAO();
            List<Template> aux_templates = db.getAllTemplates(null);
            for (int i = 0; i < aux_templates.size(); ++i) {
                Template template = aux_templates.get(i);
                String aux_name = template.getName();
                String aux_organization = template.getOrganization();
                String aux_library = template.getLibrary();
                List<String> aux_rules = template.getRules();
                String[] permited_pos_tags;
                String[] permited_sentence_tags;
                switch (aux_library) {
                    case "OpenNLP" :
                        permited_pos_tags = new OpenNLP_PosTagger().getPos_tags();
                        permited_sentence_tags = new OpenNLP_PosTagger().getSentence_tags();
                        break;
                    default:
                        throw new BadRequestException("The library " + aux_library + " does not exist");
                }
                String[] permited_tags = ObjectArrays.concat(permited_pos_tags,permited_sentence_tags,String.class);
                AdapterTemplate aux_template = new Generic_template(aux_name,aux_organization,aux_library,aux_rules,permited_tags);
                templates.add(aux_template);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
