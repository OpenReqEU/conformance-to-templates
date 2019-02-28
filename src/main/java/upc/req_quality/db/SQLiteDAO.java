package upc.req_quality.db;

import upc.req_quality.entity.input_output.Template;

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
        PreparedStatement ps = null;
        try {
            ps = db.prepareStatement("INSERT INTO model (name, org, description) VALUES (?, ?, ?)");
            ps.setString(1, template.getName());
            ps.setString(2, template.getOrganization());
            ps.setString(3, rules);
            ps.execute();
        } finally {
            if (ps != null) ps.close();
        }
    }

    @Override
    public List<Template> getOrganizationTemplates(String organization) throws SQLException {
        List<Template> templates = new ArrayList<>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Statement stmt = null;
        try {
            if (organization != null) {
                System.out.println("Getting all models");
                System.out.println(organization);
                String sql = "SELECT name, org, description FROM model WHERE org = ?";

                String aux_name;
                String aux_organization;
                String aux_description;

                pstmt = db.prepareStatement(sql);
                pstmt.setString(1, organization);

                rs = pstmt.executeQuery();

                // loop through the result set
                while (rs.next()) {
                    aux_name = rs.getString("name");
                    aux_organization = rs.getString("org");
                    aux_description = rs.getString("description");
                    templates.add(create_template(aux_name, aux_organization, aux_description));
                }
            } else {
                System.out.println("Getting all models");
                String sql = "SELECT name, org, description FROM model";

                String aux_name;
                String aux_organization;
                String aux_description;

                stmt = db.createStatement();
                rs = stmt.executeQuery(sql);

                // loop through the result set
                while (rs.next()) {
                    aux_name = rs.getString("name");
                    aux_organization = rs.getString("org");
                    aux_description = rs.getString("description");
                    templates.add(create_template(aux_name, aux_organization, aux_description));
                }
            }
            return templates;
        } finally {
            if (pstmt != null) pstmt.close();
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
    }

    @Override
    public void clearDB(String organization) throws SQLException {
        PreparedStatement pstmt = null;
        try {
            String sql = "DELETE FROM model WHERE org = ?";
            pstmt = db.prepareStatement(sql);
            pstmt.setString(1, organization);
            pstmt.executeUpdate();
            System.out.println("DB Cleared");
        } finally {
            if (pstmt != null) pstmt.close();
        }
    }

    private void createDB() throws SQLException {
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
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
