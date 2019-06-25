package upc.req_quality.db;

import upc.req_quality.entity.input_output.Template;

import java.sql.SQLException;
import java.util.List;

public interface TemplateDatabase {

    public void saveTemplate(Template model) throws SQLException;

    public List<Template> getOrganizationTemplates(String organization) throws SQLException;

    public void clearDatabase(String organization) throws SQLException;

    public void resetDatabase() throws SQLException;

}
