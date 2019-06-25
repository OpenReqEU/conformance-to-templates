package upc.req_quality.db;

import upc.req_quality.entity.input_output.Template;
import upc.req_quality.util.Control;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteDAO implements TemplateDatabase {

    private static Connection db;
    private static String url = "jdbc:sqlite:./templates.db";

    public SQLiteDAO() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        try {
            db = DriverManager.getConnection(url);
        } catch (SQLException e) {
            Control.getInstance().showErrorMessage(e.getMessage());
        }
    }

    private Template create_template(String name, String organization, String rules) {
        String[] r = rules.split("#####SEPARATION#####");
        List<String> aux = new ArrayList<>();
        for (int i = 0; i < r.length; ++i) aux.add(r[i]);
        return new Template(name,organization,aux);
    }

    @Override
    public void saveTemplate(Template template) throws SQLException {
        List<String> aux = template.getRules();
        String rules = "";
        for (int i = 0; i < aux.size(); ++i) {
            if (i != (aux.size() - 1)) rules = rules.concat(aux.get(i) + "#####SEPARATION#####");
            else rules = rules.concat(aux.get(i));
        }

        String sql = "INSERT INTO model (name, org, description) VALUES (?, ?, ?)";
        try (PreparedStatement ps = db.prepareStatement(sql)) {
            ps.setString(1, template.getName());
            ps.setString(2, template.getOrganization());
            ps.setString(3, rules);
            ps.execute();
        }
    }

    @Override
    public List<Template> getOrganizationTemplates(String organization) throws SQLException {
        List<Template> templates = new ArrayList<>();

        if (organization != null) {
            String sql = "SELECT name, org, description FROM model WHERE org = ?";

            String aux_name;
            String aux_organization;
            String aux_description;

            try(PreparedStatement pstmt = db.prepareStatement(sql)) {
                pstmt.setString(1, organization);

                try (ResultSet rs = pstmt.executeQuery()) {
                    // loop through the result set
                    while (rs.next()) {
                        aux_name = rs.getString("name");
                        aux_organization = rs.getString("org");
                        aux_description = rs.getString("description");
                        templates.add(create_template(aux_name, aux_organization, aux_description));
                    }
                }
            }
        } else {
            String sql = "SELECT name, org, description FROM model";

            String aux_name;
            String aux_organization;
            String aux_description;

            try (Statement stmt = db.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                // loop through the result set
                while (rs.next()) {
                    aux_name = rs.getString("name");
                    aux_organization = rs.getString("org");
                    aux_description = rs.getString("description");
                    templates.add(create_template(aux_name, aux_organization, aux_description));
                }

            }
        }
        return templates;
    }

    @Override
    public void clearDatabase(String organization) throws SQLException {
        String sql = "DELETE FROM model WHERE org = ?";
        try(PreparedStatement pstmt = db.prepareStatement(sql)) {
            pstmt.setString(1, organization);
            pstmt.executeUpdate();
            System.out.println("DB Cleared");
        }
    }

    @Override
    public void resetDatabase() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS model (\n"
                + "	name varchar,\n"
                + "	org varchar,\n"
                + "	description text NOT NULL,\n"
                + " PRIMARY KEY (name, org)"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        }
    }
}
