package upc.req_quality.service;

import com.google.common.collect.ObjectArrays;
import upc.req_quality.adapter.*;
import upc.req_quality.db.SQLiteDAO;
import upc.req_quality.db.Template_database;
import upc.req_quality.entity.input_output.Template;
import upc.req_quality.entity.input_output.Templates;
import upc.req_quality.exeption.BadBNFSyntaxException;
import upc.req_quality.exeption.BadRequestException;
import upc.req_quality.exeption.InternalErrorException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdapterFactory {

    private static AdapterFactory ourInstance;
    private HashMap<String,List<AdapterTemplate>> templates;

    private AdapterFactory() throws BadBNFSyntaxException, InternalErrorException {
        templates = new HashMap<>();
        load_models();
    }

    public static AdapterFactory getInstance() throws BadBNFSyntaxException, InternalErrorException {
        if (ourInstance == null) ourInstance = new AdapterFactory();
        return ourInstance;
    }

    public AdapterPosTagger getAdapterPosTagger(String library) throws BadRequestException {
        switch (library) {
            case "OpenNLP":
                return new OpenNLP_PosTagger();
            default:
                throw new BadRequestException("The library " + library + " does not exist");
        }
    }

    public List<AdapterTemplate> getAdapterTemplates(String organization) {
        return templates.get(organization);
    }

    public void enter_new_template(Template template) throws InternalErrorException, BadBNFSyntaxException {
        try {
            Template_database db = new SQLiteDAO();
            db.saveTemplate(template);
            String aux_name = template.getName();
            String aux_organization = template.getOrganization();
            List<String> aux_rules = template.getRules();
            String[] permited_pos_tags;
            String[] permited_sentence_tags;
            permited_pos_tags = new OpenNLP_PosTagger().getPos_tags();
            permited_sentence_tags = new OpenNLP_PosTagger().getSentence_tags();
            String[] permited_tags = ObjectArrays.concat(permited_pos_tags, permited_sentence_tags, String.class);
            AdapterTemplate aux_template = new Generic_template(aux_name, aux_organization, aux_rules, permited_tags);
            List<AdapterTemplate> organization_templates = templates.get(aux_organization);
            if (organization_templates == null) organization_templates = new ArrayList<>();
            organization_templates.add(aux_template);
            templates.put(aux_organization, organization_templates);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new InternalErrorException("Error loading database");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalErrorException("Database error: " + e.getMessage());
        }
    }

    public Templates check_organization_models(String organization) throws InternalErrorException {
        List<Template> aux_models = new ArrayList<>();
        Template_database db;
        try {
            db = new SQLiteDAO();
            aux_models = db.getOrganizationTemplates(organization);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new InternalErrorException("Error loading database");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalErrorException("Database error: " + e.getMessage());
        }
        return new Templates(aux_models);
    }

    public void clear_db(String organization) throws InternalErrorException {
        try {
            Template_database db = new SQLiteDAO();
            db.clearDB(organization);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new InternalErrorException("Error loading database");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalErrorException("Database error: " + e.getMessage());
        }
        //load_models();
    }

    private void load_models() throws InternalErrorException, BadBNFSyntaxException {
        try {
            Template_database db = new SQLiteDAO();
            List<Template> aux_templates = db.getOrganizationTemplates(null);
            for (int i = 0; i < aux_templates.size(); ++i) {
                Template template = aux_templates.get(i);
                String aux_name = template.getName();
                String aux_organization = template.getOrganization();
                List<String> aux_rules = template.getRules();
                String[] permited_pos_tags;
                String[] permited_sentence_tags;
                permited_pos_tags = new OpenNLP_PosTagger().getPos_tags();
                permited_sentence_tags = new OpenNLP_PosTagger().getSentence_tags();
                String[] permited_tags = ObjectArrays.concat(permited_pos_tags,permited_sentence_tags,String.class);
                AdapterTemplate aux_template = new Generic_template(aux_name,aux_organization,aux_rules,permited_tags);
                List<AdapterTemplate> organization_templates = templates.get(aux_organization);
                if (organization_templates == null) organization_templates = new ArrayList<>();
                organization_templates.add(aux_template);
                templates.put(aux_organization, organization_templates);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new InternalErrorException("Error loading database");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalErrorException("Database error: " + e.getMessage());
        }
    }
}
