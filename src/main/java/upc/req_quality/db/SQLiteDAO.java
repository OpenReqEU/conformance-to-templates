package upc.req_quality.db;

import org.json.JSONArray;
import org.json.JSONObject;
import upc.req_quality.adapter_template.StringTree;
import upc.req_quality.entity.ParsedTemplate;
import upc.req_quality.exception.BadBNFSyntaxException;
import upc.req_quality.exception.InternalErrorException;
import upc.req_quality.exception.NotFoundException;
import upc.req_quality.util.Constants;
import upc.req_quality.util.Control;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class SQLiteDAO implements TemplateDatabase {

    private AtomicBoolean mainDbLock = new AtomicBoolean(false);
    private static String path = "./data/";
    private static String dbName = "templates.db";
    private static String drivers = "jdbc:sqlite:";

    public static void setPath(String path) {
        SQLiteDAO.path = path;
    }

    public static String getDbName() {
        return dbName;
    }

    private String buildDbUrl() {
        return drivers + path + dbName;
    }

    public SQLiteDAO() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
    }

    @Override
    public boolean existsOrganization(String organization) throws SQLException {

        boolean found = false;
        String sql = "SELECT name FROM templates WHERE organization = ?";

        try(Connection connection = getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, organization);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                }
            }
        }
        return found;
    }

    @Override
    public void saveTemplate(ParsedTemplate template) throws InternalErrorException, SQLException {

        //TODO asegurar que existe como minimo el nodo top
        String sql = "INSERT OR REPLACE INTO templates (name, organization, description) VALUES (?, ?, ?)";
        getAccessToUpdate();
        try (Connection connection = getConnection();
              PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, template.getName());
            ps.setString(2, template.getOrganization());
            ps.setString(3, transformRules(template.getRules()).toString());
            ps.execute();
        }
        releaseAccessToUpdate();
    }

    @Override
    public List<ParsedTemplate> getOrganizationTemplates(String organization) throws NotFoundException, SQLException {

        List<ParsedTemplate> templates = new ArrayList<>();
        String sql = "SELECT name, organization, description FROM templates WHERE organization = ?";

        try(Connection connection = getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, organization);

            try (ResultSet rs = pstmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    ++count;
                    String auxName = rs.getString("name");
                    String auxOrganization = rs.getString("organization");
                    String auxDescription = rs.getString("description");
                    templates.add(new ParsedTemplate(auxName, auxOrganization, loadRules(auxDescription)));
                }
                if (count == 0) throw new NotFoundException("The organization " + organization + " has no templates in the database");
            }
        }

        return templates;
    }

    @Override
    public List<String> getOrganizationTemplatesNames(String organization) throws NotFoundException, SQLException {

        List<String> names = new ArrayList<>();
        String sql = "SELECT name FROM templates WHERE organization = ?";

        try(Connection connection = getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, organization);

            try (ResultSet rs = pstmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    ++count;
                    names.add(rs.getString("name"));
                }
                if (count == 0) throw new NotFoundException("The organization " + organization + " has no templates in the database");
            }
        }

        return names;
    }

    @Override
    public void clearOrganizationTemplates(String organization) throws InternalErrorException, SQLException {
        String sql = "DELETE FROM templates WHERE organization = ?";
        getAccessToUpdate();
        try(Connection connection = getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, organization);
            pstmt.executeUpdate();
        }
        releaseAccessToUpdate();
    }

    @Override
    public void clearDatabase() throws SQLException, IOException, InternalErrorException {

        getAccessToUpdate();
        deleteDataFiles("db");
        File file = new File(path+dbName);
        if (!file.createNewFile()) throw new IOException("Error while creating database");

        String sql = "CREATE TABLE templates (\n"
                + "	name varchar,\n"
                + "	organization varchar,\n"
                + "	description text NOT NULL,\n"
                + " PRIMARY KEY (name, organization)"
                + ");";

        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            configureDatabase(connection);
            stmt.execute(sql);
        }
        releaseAccessToUpdate();
    }


    /*
    Auxiliary operations
     */
    private void getAccessToUpdate() throws InternalErrorException {
        int maxIterations = Constants.getInstance().getMaxSyncIterations();
        int sleepTime = Constants.getInstance().getSleepTime();
        boolean correct = false;
        int count = 0;
        Random random = new Random();
        while (!correct && count <= maxIterations) {
            correct = mainDbLock.compareAndSet(false,true);
            if (!correct) {
                ++count;
                try {
                    Thread.sleep(random.nextInt(sleepTime));
                } catch (InterruptedException e) {
                    Control.getInstance().showErrorMessage(e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (count == (maxIterations + 1)) throw new InternalErrorException("Synchronization error in the main database");
    }

    public void releaseAccessToUpdate() {
        mainDbLock.set(false);
    }


    public void deleteDataFiles(String text) throws IOException, InternalErrorException {
        Path dirPath = Paths.get(path);
        class Control {
            private volatile boolean error = false;
        }
        final Control control = new Control();
        try (Stream<Path> walk = Files.walk(dirPath)) {
            walk.map(Path::toFile)
                    .forEach(file -> {
                                if (!file.isDirectory() && file.getName().contains(text)) {
                                    if(!file.delete()) control.error = true;
                                }
                            }
                    );
        }
        if (control.error) throw new InternalErrorException("Error while deleting a file");
    }

    private void configureDatabase(Connection connection) throws SQLException {
        String sql = "PRAGMA journal_mode=WAL;";
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

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(buildDbUrl());
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
