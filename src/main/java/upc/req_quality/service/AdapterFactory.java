package upc.req_quality.service;

import upc.req_quality.adapter.*;
import upc.req_quality.db.SQLiteDAO;
import upc.req_quality.db.TemplateDatabase;
import upc.req_quality.entity.input_output.Template;
import upc.req_quality.entity.input_output.Templates;
import upc.req_quality.exception.BadBNFSyntaxException;
import upc.req_quality.exception.InternalErrorException;
import upc.req_quality.util.Control;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdapterFactory {

    private static AdapterFactory ourInstance;
    private HashMap<String,List<AdapterTemplate>> templates;
    private AdapterPosTagger posTagger;

    private AdapterFactory() throws BadBNFSyntaxException, InternalErrorException {
        templates = new HashMap<>();
        posTagger = OpenNLPPosTagger.getInstance();
        loadModels();
    }

    public static AdapterFactory getInstance() throws BadBNFSyntaxException, InternalErrorException {
        if (ourInstance == null) ourInstance = new AdapterFactory();
        return ourInstance;
    }

    public AdapterPosTagger getAdapterPosTagger() {
        return posTagger;
    }

    public List<AdapterTemplate> getAdapterTemplates(String organization) {
        return templates.get(organization);
    }

    public void enterNewTemplate(Template template) throws InternalErrorException, BadBNFSyntaxException {
        try {
            String auxName = template.getName();
            String auxOrganization = template.getOrganization();
            List<String> auxRules = template.getRules();
            List<String> permitedPosTags = posTagger.getPosTags();
            List<String> permitedSentenceTags = posTagger.getSentenceTags();
            permitedPosTags.addAll(permitedSentenceTags);
            AdapterTemplate aux_template = new GenericTemplate(auxName, auxOrganization, auxRules, permitedPosTags);
            List<AdapterTemplate> organizationTemplates = templates.get(auxOrganization);
            if (organizationTemplates == null) organizationTemplates = new ArrayList<>();
            organizationTemplates.add(aux_template);
            templates.put(auxOrganization, organizationTemplates);
            TemplateDatabase db = new SQLiteDAO();
            db.saveTemplate(template);
        } catch (ClassNotFoundException e) {
            Control.getInstance().showErrorMessage(e.getMessage());
            throw new InternalErrorException("Error loading database");
        } catch (SQLException e) {
            Control.getInstance().showErrorMessage(e.getMessage());
            throw new InternalErrorException("Database error: " + e.getMessage());
        }
    }

    public Templates checkOrganizationModels(String organization) throws InternalErrorException {
        List<Template> auxModels;
        TemplateDatabase db;
        try {
            db = new SQLiteDAO();
            auxModels = db.getOrganizationTemplates(organization);
        } catch (ClassNotFoundException e) {
            Control.getInstance().showErrorMessage(e.getMessage());
            throw new InternalErrorException("Error loading database");
        } catch (SQLException e) {
            Control.getInstance().showErrorMessage(e.getMessage());
            throw new InternalErrorException("Database error: " + e.getMessage());
        }
        return new Templates(auxModels);
    }

    public void clearDatabase(String organization) throws InternalErrorException {
        try {
            TemplateDatabase db = new SQLiteDAO();
            db.clearDatabase(organization);
        } catch (ClassNotFoundException e) {
            Control.getInstance().showErrorMessage(e.getMessage());
            throw new InternalErrorException("Error loading database");
        } catch (SQLException e) {
            Control.getInstance().showErrorMessage(e.getMessage());
            throw new InternalErrorException("Database error: " + e.getMessage());
        }
        templates.remove(organization);
    }

    private void loadModels() throws InternalErrorException, BadBNFSyntaxException {
        try {
            TemplateDatabase db = new SQLiteDAO();
            List<Template> aux_templates = db.getOrganizationTemplates(null);
            for (int i = 0; i < aux_templates.size(); ++i) {
                Template template = aux_templates.get(i);
                String auxName = template.getName();
                String auxOrganization = template.getOrganization();
                List<String> auxRules = template.getRules();
                List<String> permitedPosTags = posTagger.getPosTags();
                List<String> permitedSentenceTags = posTagger.getSentenceTags();
                permitedPosTags.addAll(permitedSentenceTags);
                AdapterTemplate aux_template = new GenericTemplate(auxName,auxOrganization,auxRules,permitedPosTags);
                List<AdapterTemplate> organization_templates = templates.get(auxOrganization);
                if (organization_templates == null) organization_templates = new ArrayList<>();
                organization_templates.add(aux_template);
                templates.put(auxOrganization, organization_templates);
            }
        } catch (ClassNotFoundException e) {
            Control.getInstance().showErrorMessage(e.getMessage());
            throw new InternalErrorException("Error loading database");
        } catch (SQLException e) {
            Control.getInstance().showErrorMessage(e.getMessage());
            throw new InternalErrorException("Database error: " + e.getMessage());
        }
    }
}
