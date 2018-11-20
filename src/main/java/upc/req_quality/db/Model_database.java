package upc.req_quality.db;

import upc.req_quality.entity.Model;

import java.sql.SQLException;
import java.util.List;

public interface Model_database {

    public void saveModel(Model model) throws SQLException;

    public Model getModel(int id) throws SQLException;

    public List<Model> getAllModels() throws SQLException;

    public void clearDB() throws SQLException;

}
