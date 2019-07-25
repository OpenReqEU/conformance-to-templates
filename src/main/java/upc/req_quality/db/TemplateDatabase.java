package upc.req_quality.db;

import upc.req_quality.entity.ParsedTemplate;
import upc.req_quality.exception.InternalErrorException;
import upc.req_quality.exception.NotFoundException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface TemplateDatabase {

    boolean existsOrganization(String organization) throws SQLException;

    void saveTemplate(ParsedTemplate template) throws InternalErrorException, SQLException;

    List<ParsedTemplate> getOrganizationTemplates(String organization) throws NotFoundException, SQLException;

    List<String> getOrganizationTemplatesNames(String organization) throws NotFoundException, SQLException;

    void clearOrganizationTemplates(String organization) throws InternalErrorException, SQLException;

    void clearDatabase() throws IOException, InternalErrorException, SQLException;

}
