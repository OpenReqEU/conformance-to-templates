package upc.req_quality.db;

import org.json.JSONArray;
import org.json.JSONObject;
import upc.req_quality.adapter_template.StringTree;
import upc.req_quality.entity.ParsedTemplate;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteDAO implements TemplateDatabase {

    private Connection connection;
    private static String url = "jdbc:sqlite:./templates.db";

    public SQLiteDAO() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        String sql = "PRAGMA journal_mode=WAL;";
        Connection connection = getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            boolean correct = true;
            if (rs.next()) {
                String response = rs.getString(1);
                if (!response.equals("wal")) correct = false;
            } else correct = false;
            if (!correct) throw new SQLException("Error while setting wal-mode");
        }
    }

    @Override
    public void saveTemplate(ParsedTemplate template) throws SQLException {

        //TODO asegurar que existe como minimo el nodo top
        Connection connection = getConnection();
        String sql = "INSERT INTO templates (name, organization, description) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, template.getName());
            ps.setString(2, template.getOrganization());
            ps.setString(3, transformRules(template.getRules()).toString());
            ps.execute();
        }
    }

    @Override
    public List<ParsedTemplate> getOrganizationTemplates(String organization) throws SQLException {

        List<ParsedTemplate> templates = new ArrayList<>();
        Connection connection = getConnection();
        String sql = "SELECT name, organization, description FROM templates WHERE organization = ?";

        try(PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, organization);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String auxName = rs.getString("name");
                    String auxOrganization = rs.getString("organization");
                    String auxDescription = rs.getString("description");
                    templates.add(new ParsedTemplate(auxName, auxOrganization, loadRules(auxDescription)));
                }
            }
        }

        return templates;
    }

    @Override
    public void clearOrganizationTemplates(String organization) throws SQLException {
        String sql = "DELETE FROM templates WHERE organization = ?";
        Connection connection = getConnection();
        try(PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, organization);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void clearDatabase() throws SQLException, IOException {

        File file = new File("./templates.db");
        file.delete();
        if (!file.createNewFile()) throw new IOException("Error while creating database");

        String sql = "CREATE TABLE templates (\n"
                + "	name varchar,\n"
                + "	organization varchar,\n"
                + "	description text NOT NULL,\n"
                + " PRIMARY KEY (name, organization)"
                + ");";

        Connection connection = getConnection();

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }


    /*
    Auxiliary operations
     */

    private synchronized void createConnection() throws SQLException {
        if (connection == null || connection.isClosed()) connection = DriverManager.getConnection(url);
    }

    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) createConnection();
        return connection;
    }

    private JSONObject transformRules(StringTree node) {
        JSONObject result = new JSONObject();
        result.put("data", node.getData());
        JSONArray children = new JSONArray();
        for (StringTree child: node.getChildren()) {
            children.put(transformRules(child));
        }
        result.put("children", children);
        return result;
    }

    private StringTree loadRules(String description) {
        JSONObject json = new JSONObject(description);
        List<StringTree> leaves = new ArrayList<>();
        return loadNode(json, leaves);
    }

    private StringTree loadNode(JSONObject node, List<StringTree> leaves) {
        String data = node.getString("data");
        StringTree result = new StringTree(null, data);
        JSONArray children = node.getJSONArray("children");
        if (children.length() > 0) {
            for (int i = 0; i < children.length(); ++i) {
                JSONObject child = children.getJSONObject(i);
                result.addChildren(loadNode(child, leaves));
            }
        } else leaves.add(result);
        return result;
    }
}
