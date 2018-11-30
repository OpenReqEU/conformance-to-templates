package upc.req_quality.db;

import upc.req_quality.entity.Template;

import java.sql.SQLException;
import java.util.List;

public interface Template_database {

    public void saveTemplate(Template model) throws SQLException;

    public List<Template> getAllTemplates(String organization) throws SQLException;

    public void clearDB(String organization) throws SQLException;

}
