import upc.req_quality.db.Template_database;
import upc.req_quality.db.SQLiteDAO;
import upc.req_quality.entity.Template;

public class DriverDatabase {

    public static void main(String[] args) {
        try {
            Template_database db = new SQLiteDAO();
            ((SQLiteDAO) db).createDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
