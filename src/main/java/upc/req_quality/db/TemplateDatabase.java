package upc.req_quality.db;

import upc.req_quality.entity.ParsedTemplate;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface TemplateDatabase {

    void saveTemplate(ParsedTemplate template) throws SQLException;

    List<ParsedTemplate> getOrganizationTemplates(String organization) throws SQLException;

    void clearOrganizationTemplates(String organization) throws SQLException;

    void clearDatabase() throws SQLException, IOException;

}
