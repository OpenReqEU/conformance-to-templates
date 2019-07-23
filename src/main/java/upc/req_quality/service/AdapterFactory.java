package upc.req_quality.service;

import upc.req_quality.adapter_tagger.AdapterTagger;
import upc.req_quality.adapter_tagger.OpenNLPTagger;
import upc.req_quality.adapter_template.*;
import upc.req_quality.db.SQLiteDAO;
import upc.req_quality.db.TemplateDatabase;
import upc.req_quality.entity.input.InputTemplate;
import upc.req_quality.entity.input.InputTemplates;
import upc.req_quality.exception.BadBNFSyntaxException;
import upc.req_quality.exception.InternalErrorException;
import upc.req_quality.util.Control;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdapterFactory {

    private static AdapterFactory instance;
    private AdapterTagger adapterTagger;
    private AdapterTemplate adapterTemplate;
    private TemplateDatabase templateDatabase;

    private static final String ERROR1 = "Error loading database";
    private static final String ERROR2 = "Database error: ";

    private AdapterFactory() throws BadBNFSyntaxException, InternalErrorException {
        this.adapterTagger = new OpenNLPTagger();
        this.adapterTemplate = new
    }

    public static AdapterFactory getInstance() throws BadBNFSyntaxException, InternalErrorException {
        if (ourInstance == null) ourInstance = new AdapterFactory();
        return ourInstance;
    }

    public AdapterTagger getAdapterTagger() {
        return posTagger;
    }

    public List<AdapterTemplate> getAdapterTemplates(String organization) {
        return templates.get(organization);
    }

    public void enterNewTemplate(InputTemplate template) throws InternalErrorException, BadBNFSyntaxException {
        try {
            generateTemplate(template);
            TemplateDatabase db = new SQLiteDAO();
            db.saveTemplate(template);
        } catch (ClassNotFoundException e) {
            Control.getInstance().showErrorMessage(e.getMessage());
            throw new InternalErrorException(ERROR1);
        } catch (SQLException e) {
            Control.getInstance().showErrorMessage(e.getMessage());
            throw new InternalErrorException(ERROR2 + e.getMessage());
        }
    }

    public InputTemplates checkOrganizationModels(String organization) throws InternalErrorException {
        List<InputTemplate> auxModels;
        TemplateDatabase db;
        try {
            db = new SQLiteDAO();
            auxModels = db.getOrganizationTemplates(organization);
        } catch (ClassNotFoundException e) {
            Control.getInstance().showErrorMessage(e.getMessage());
            throw new InternalErrorException(ERROR1);
        } catch (SQLException e) {
            Control.getInstance().showErrorMessage(e.getMessage());
            throw new InternalErrorException(ERROR2 + e.getMessage());
        }
        return new InputTemplates(auxModels);
    }

    public void clearDatabase(String organization) throws InternalErrorException {
        try {
            TemplateDatabase db = new SQLiteDAO();
            db.clearDatabase(organization);
        } catch (ClassNotFoundException e) {
            Control.getInstance().showErrorMessage(e.getMessage());
            throw new InternalErrorException(ERROR1);
        } catch (SQLException e) {
            Control.getInstance().showErrorMessage(e.getMessage());
            throw new InternalErrorException(ERROR2 + e.getMessage());
        }
        templates.remove(organization);
    }

    private void loadModels() throws InternalErrorException, BadBNFSyntaxException {
        try {
            TemplateDatabase db = new SQLiteDAO();
            List<InputTemplate> auxTemplates = db.getOrganizationTemplates(null);
            for (int i = 0; i < auxTemplates.size(); ++i) {
                InputTemplate template = auxTemplates.get(i);
                generateTemplate(template);
            }
        } catch (ClassNotFoundException e) {
            Control.getInstance().showErrorMessage(e.getMessage());
            throw new InternalErrorException(ERROR1);
        } catch (SQLException e) {
            Control.getInstance().showErrorMessage(e.getMessage());
            throw new InternalErrorException(ERROR2 + e.getMessage());
        }
    }

    private void generateTemplate(InputTemplate template) throws BadBNFSyntaxException {
        String auxName = template.getName();
        String auxOrganization = template.getOrganization();
        List<String> auxRules = template.getRules();
        List<String> permitedPosTags = posTagger.getPosTags();
        List<String> permitedSentenceTags = posTagger.getSentenceTags();
        permitedPosTags.addAll(permitedSentenceTags);
        AdapterTemplate auxTemplate = new GenericTemplate(auxName,auxOrganization,auxRules,permitedPosTags);
        List<AdapterTemplate> organizationTemplates = templates.get(auxOrganization);
        if (organizationTemplates == null) organizationTemplates = new ArrayList<>();
        organizationTemplates.add(auxTemplate);
        templates.put(auxOrganization, organizationTemplates);
    }
}
