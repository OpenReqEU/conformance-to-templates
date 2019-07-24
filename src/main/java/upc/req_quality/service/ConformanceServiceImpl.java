package upc.req_quality.service;

import org.springframework.stereotype.Service;
import upc.req_quality.adapter_tagger.AdapterTagger;
import upc.req_quality.adapter_template.AdapterTemplate;
import upc.req_quality.db.TemplateDatabase;
import upc.req_quality.entity.*;
import upc.req_quality.entity.input.Requirements;
import upc.req_quality.entity.input.Template;
import upc.req_quality.entity.input.Templates;
import upc.req_quality.entity.output.Tip;
import upc.req_quality.exception.BadBNFSyntaxException;
import upc.req_quality.exception.InternalErrorException;
import upc.req_quality.exception.NotFoundException;
import upc.req_quality.util.Control;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("conformanceService")
public class ConformanceServiceImpl implements ConformanceService {

    @Override
    public Requirements checkConformance(String organization, List<Requirement> requirements) throws NotFoundException, InternalErrorException {

        List<Requirement> result = new ArrayList<>();

        AdapterFactory af = AdapterFactory.getInstance();
        AdapterTagger adapterTagger = af.getAdapterTagger();
        AdapterTemplate adapterTemplate = af.getAdapterTemplate();
        TemplateDatabase templateDatabase = af.getTemplateDatabase();

        List<ParsedTemplate> templates;
        try {
            templates = templateDatabase.getOrganizationTemplates(organization);
        } catch (SQLException sq) {
            Control.getInstance().showErrorMessage(sq.getMessage());
            throw new InternalErrorException("Error while loading the organization templates from the database");
        }

        for (Requirement requirement: requirements) {

            String[] tokens = adapterTagger.tokenizer(requirement.getDescription());
            String[] tokensTagged = adapterTagger.posTagger(tokens);
            String[] chunks = adapterTagger.chunker(tokens,tokensTagged);

            boolean ok = false;

            List<Tip> tips = new ArrayList<>();

            //TODO do all the loop and tell the user which templates the requirement is not conformance with
            for (int i = 0; ((!ok) && i < templates.size()); ++i) {
                ParsedTemplate template = templates.get(i);
                MatcherResponse matcherResponse = adapterTemplate.matchTemplate(template, tokens, tokensTagged, chunks);
                ok = matcherResponse.isResult();
                if (!ok) {
                    for (MatcherError matcher_error: matcherResponse.getErrors()) {
                        tips.add(new Tip(templates.get(i).getName(),matcher_error.getIndex()+":"+matcher_error.getFinalIndex(),explainError(matcher_error.getDescription(),adapterTagger),matcher_error.getComment()));
                    }
                }
            }

            if (!ok) result.add(new Requirement(requirement.getId(),requirement.getDescription(),tips));
        }

        return new Requirements(result);
    }

    @Override
    public void enterNewTemplates(Templates input) throws BadBNFSyntaxException, InternalErrorException {
        AdapterFactory af = AdapterFactory.getInstance();
        AdapterTemplate adapterTemplate = af.getAdapterTemplate();
        TemplateDatabase templateDatabase = af.getTemplateDatabase();
        List<Template> templates = input.getTemplates();
        try {
            for (Template template : templates) {
                String name = template.getName();
                String organization = template.getOrganization();
                List<String> rules = template.getRules();
                List<Rule> parsedRules = parseRulesAndCheck(rules);
                List<String> permittedClauses = af.getPermittedClauses();
                ParsedTemplate parsedTemplate = adapterTemplate.createTemplate(name, organization, parsedRules, permittedClauses);
                templateDatabase.saveTemplate(parsedTemplate);
            }
        } catch (SQLException sq) {
            Control.getInstance().showErrorMessage(sq.getMessage());
            throw new InternalErrorException("Error while saving a new template in the database");
        }
    }

    @Override
    public List<String> checkOrganizationTemplates(String organization) throws NotFoundException, InternalErrorException {
        AdapterFactory af = AdapterFactory.getInstance();
        TemplateDatabase templateDatabase = af.getTemplateDatabase();
        List<String> names;
        try {
            names = templateDatabase.getOrganizationTemplatesNames(organization);
        } catch (SQLException sq) {
            Control.getInstance().showErrorMessage(sq.getMessage());
            throw new InternalErrorException("Error while reading the organization templates from the database");
        }
        return names;
    }

    @Override
    public void clearOrganizationTemplates(String organization) throws NotFoundException, InternalErrorException {
        AdapterFactory af = AdapterFactory.getInstance();
        TemplateDatabase templateDatabase = af.getTemplateDatabase();
        try {
            if (!templateDatabase.existsOrganization(organization)) throw new NotFoundException("The specified organization has no templates in the database");
            templateDatabase.clearOrganizationTemplates(organization);
        } catch (SQLException sq) {
            Control.getInstance().showErrorMessage(sq.getMessage());
            throw new InternalErrorException("Error while clearing organization templates");
        }
    }

    @Override
    public void clearDatabase() throws InternalErrorException {
        AdapterFactory af = AdapterFactory.getInstance();
        TemplateDatabase templateDatabase = af.getTemplateDatabase();
        try {
            templateDatabase.clearDatabase();
        } catch (SQLException | IOException e) {
            Control.getInstance().showErrorMessage(e.getMessage());
            throw new InternalErrorException("Error while clearing the database");
        }
    }


    /*
    Auxiliary operations
     */

    public List<Rule> parseRulesAndCheck(List<String> rules) throws BadBNFSyntaxException {

        //To show all rules with their title and description
        HashMap<String,String> rulesWithDescription = new HashMap<>();
        //To show on which dependencies it depends
        HashMap<String, Set<String>> rulesWithDependencies = new HashMap<>();
        //To show which dependencies depend on it
        HashMap<String,Set<String>> rulesDependOnMe = new HashMap<>();
        //To show rules with not dependencies
        LinkedList<String> rulesWithoutDependencies = new LinkedList<>();

        if (rules.isEmpty()) throw new BadBNFSyntaxException("No rules specified");
        if (!rules.get(0).contains("main")) throw new BadBNFSyntaxException("No main rule specified");
        for (int i = 0; i < rules.size(); ++i) {
            String text = rules.get(i);
            Pattern pattern = Pattern.compile("(^<.*?> ::=)");
            Matcher matcher = pattern.matcher(text);
            if(!matcher.find()) throw new BadBNFSyntaxException("Rule in position " + i + " is not well written. It must start with \"<name_rule> ::=\"");
            String auxTitle = text.substring(matcher.start(), matcher.end());
            auxTitle = auxTitle.replaceAll("(\\s+)","");
            auxTitle = auxTitle.replaceAll("::=","");
            String auxDescription = text.replaceAll("(^<.*?> ::=)","");
            rulesWithDescription.put(auxTitle,auxDescription);
            rulesDependOnMe.put(auxTitle,new HashSet<>());
        }

        //Check if exist two rules with the same title
        if (rulesWithDescription.size() != rules.size()) throw new BadBNFSyntaxException("Exist two rules with the same title");

        //Check dependencies between rules
        Iterator it1 = rulesWithDescription.entrySet().iterator();
        while (it1.hasNext()) {
            Map.Entry pair = (Map.Entry)it1.next();
            String auxTitle = pair.getKey() + "";
            String auxDescription = pair.getValue() + "";
            checkRule(auxTitle,auxDescription,rulesWithDescription,rulesWithDependencies,rulesWithoutDependencies,rulesDependOnMe);
        }

        //Return ordered rules
        List<Rule> result = new ArrayList<>();
        while (!rulesWithoutDependencies.isEmpty()) {
            String title = rulesWithoutDependencies.getFirst();
            Set<String> rulesThatDependOnMe = rulesDependOnMe.get(title);
            Iterator<String> it2 = rulesThatDependOnMe.iterator();
            while (it2.hasNext()) {
                String aux = it2.next();
                rulesWithDependencies.get(aux).remove(title);
                if (rulesWithDependencies.get(aux).isEmpty()) {
                    rulesWithoutDependencies.addLast(aux);
                    rulesWithDependencies.remove(aux);
                }
            }
            result.add(new Rule(title,rulesWithDescription.get(title)));
            rulesWithoutDependencies.removeFirst();
        }

        //exists a cycle
        if (!rulesWithDependencies.isEmpty()) {
            Iterator it = rulesWithDependencies.entrySet().iterator();
            String aux = "";
            boolean firstComa = true;
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                if (firstComa) aux = aux.concat("" + pair.getKey());
                else aux = aux.concat(", " + pair.getKey());
                firstComa = false;
                it.remove();
            }
            throw new BadBNFSyntaxException("Exists a cycle between the input_output rules: " + aux);
        }

        //put main clause at index 0
        for (Rule aux_rule: result) {
            if (aux_rule.getTitle().equals("<main>")) {
                result.remove(aux_rule);
                result.add(0,aux_rule);
            }
        }

        return result;
    }

    private void checkRule(String title, String description, HashMap<String,String> rulesWithDescription, HashMap<String,Set<String>> rulesWithDependencies, LinkedList<String> rulesWithoutDependencies, HashMap<String,Set<String>> rulesDependOnMe) {
        String[] tokens = description.split("\\s+");
        boolean isolation = true;
        for (String text: tokens) {
            if (rulesWithDescription.containsKey(text)) {
                isolation = false;
                Set<String> newDependencies = rulesWithDependencies.get(title);
                if (newDependencies == null) newDependencies = new HashSet<>();
                newDependencies.add(text);
                rulesWithDependencies.put(title,newDependencies);
                Set<String> newDependOnMe = rulesDependOnMe.get(text);
                newDependOnMe.add(title);
                rulesDependOnMe.put(text,newDependOnMe);
            }
        }
        if (isolation) rulesWithoutDependencies.addLast(title);
    }

    private String explainError(String tag, AdapterTagger tagger) throws InternalErrorException {
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

    private String recognizeTag(String tag, AdapterTagger tagger) throws InternalErrorException {
        String aux = "";
        if (tag.contains(")")) aux = tagger.getTagDescription(tag);
        else {
            if (tag.contains("<")) aux = tagger.getTagDescription(tag);
            else aux = tag;
        }
        if (aux == null) throw new InternalErrorException("Tag "+ tag + " not recognized");
        return aux;
    }
}
