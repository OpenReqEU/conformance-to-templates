package upc.req_quality.db;

import upc.req_quality.entity.Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteDAO implements Model_database {

    private static Connection db;
    private static String url = "jdbc:sqlite:./models.db";

    public SQLiteDAO() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        try {
            db = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveModel(Model model) throws SQLException {
        PreparedStatement ps;
        ps = db.prepareStatement ("INSERT INTO model (id, description, name) VALUES (?, ?, ?)");
        ps.setString(1, Integer.toString(model.getId()));
        ps.setString(2, model.getRules());
        ps.setString(3, model.getName());
        ps.execute();
    }

    @Override
    public Model getModel(int id) throws SQLException{
        String sql = "SELECT id, description, name FROM model WHERE id = ?";

        int aux_id = 0;
        String aux_description = "";
        String aux_name = "";

        try {
             PreparedStatement pstmt  = db.prepareStatement(sql);

            // set the value
            pstmt.setInt(id,id);
            //
            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            rs.next();
            aux_id = rs.getInt("id");
            aux_description = rs.getString("description");
            aux_name = rs.getString("name");
            //System.out.println(aux_id + aux_name + aux_description);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return (new Model(aux_id,aux_name,aux_description));
    }

    @Override
    public List<Model> getAllModels() throws SQLException{
        System.out.println("Getting all models");
        String sql = "SELECT id, description, name FROM model";

        List<Model> models = new ArrayList<>();

        int aux_id;
        String aux_description;
        String aux_name;

        try {

            Statement stmt = db.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                aux_id = rs.getInt("id");
                aux_description = rs.getString("description");
                aux_name = rs.getString("name");
                models.add(new Model(aux_id,aux_name,aux_description));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return models;
    }

    @Override
    public void clearDB() {
        try {
            PreparedStatement ps;
            ps = db.prepareStatement("DELETE FROM model");
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("DB Cleared");
    }

    private void createDB() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS model (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	description text NOT NULL,\n"
                + "	name varchar\n"
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
