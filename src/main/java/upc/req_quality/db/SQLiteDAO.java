package upc.req_quality.db;

import upc.req_quality.entity.Template;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteDAO implements Template_database {

    private static Connection db;
    private static String url = "jdbc:sqlite:./templates.db";

    public SQLiteDAO() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        try {
            db = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Template create_template(String name, String organization, String library, String rules) {
        String[] r = rules.split("#####SEPARATION#####");
        List<String> aux = new ArrayList<>();
        for (int i = 0; i < r.length; ++i) aux.add(r[i]);
        return new Template(name,organization,library,aux);
    }

    @Override
    public void saveTemplate(Template template) throws SQLException {
        List<String> aux = template.getRules();
        String rules = "";
        for (int i = 0; i < aux.size(); ++i) {
            if (i != (aux.size() - 1)) rules += aux.get(i) + "#####SEPARATION#####";
        }
        PreparedStatement ps;
        ps = db.prepareStatement ("INSERT INTO model (name, org, library, description) VALUES (?, ?, ?, ?)");
        ps.setString(1, template.getName());
        ps.setString(2, template.getOrganization());
        ps.setString(3, template.getLibrary());
        ps.setString(4, rules);
        ps.execute();
    }

    @Override
    public List<Template> getAllTemplates(String organization) throws SQLException{
        if (organization != null) {
            System.out.println("Getting all models");
            System.out.println(organization);
            String sql = "SELECT name, org, library, description FROM model WHERE org = ?";

            List<Template> templates = new ArrayList<>();

            String aux_name;
            String aux_organization;
            String aux_library;
            String aux_description;

            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setString(1, organization);

            ResultSet rs = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                aux_name = rs.getString("name");
                aux_organization = rs.getString("org");
                aux_library = rs.getString("library");
                aux_description = rs.getString("description");
                templates.add(create_template(aux_name, aux_organization, aux_library, aux_description));
            }
            return templates;
        } else {
            System.out.println("Getting all models");
            String sql = "SELECT name, org, library, description FROM model";

            List<Template> templates = new ArrayList<>();

            String aux_name;
            String aux_organization;
            String aux_library;
            String aux_description;

            Statement stmt  = db.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                aux_name = rs.getString("name");
                aux_organization = rs.getString("org");
                aux_library = rs.getString("library");
                aux_description = rs.getString("description");
                templates.add(create_template(aux_name, aux_organization, aux_library, aux_description));
            }

            return templates;
        }
    }

    @Override
    public void clearDB(String organization) throws SQLException{
        String sql = "DELETE FROM model WHERE org = ?";
        PreparedStatement pstmt = db.prepareStatement(sql);
        pstmt.setString(1, organization);
        pstmt.executeUpdate();
        System.out.println("DB Cleared");
    }

    private void createDB() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS model (\n"
                + "	name varchar,\n"
                + "	org varchar,\n"
                + " library varchar,\n"
                + "	description text NOT NULL,\n"
                + " PRIMARY KEY (name, org, library)"
                + ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
