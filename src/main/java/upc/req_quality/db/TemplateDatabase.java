package upc.req_quality.db;

import upc.req_quality.entity.input_output.Template;

import java.sql.SQLException;
import java.util.List;

public interface TemplateDatabase {

    void saveTemplate(Template model) throws SQLException;

    List<Template> getOrganizationTemplates(String organization) throws SQLException;

    void clearDatabase(String organization) throws SQLException;

    void resetDatabase() throws SQLException;

}
