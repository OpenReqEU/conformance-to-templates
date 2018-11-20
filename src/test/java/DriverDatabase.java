import upc.req_quality.db.Model_database;
import upc.req_quality.db.SQLiteDAO;

public class DriverDatabase {

    public static void main(String[] args) {
        try {
            Model_database db = new SQLiteDAO();
            /*db.saveModel(1,"<boilerplate-conformant> ::= \" +\n" +
                    "                \"<opt-condition> <np> <vp-starting-with-modal> <np> \" +\n" +
                    "                \"<opt-details> | \" +\n" +
                    "                \"<opt-condition> <np> <modal> \\\"PROVIDE\\\" <np> \" +\n" +
                    "                \"\\\"WITH_THE_ABILITY\\\" <infinitive-vp> <np> <opt-details> | \" +\n" +
                    "                \"<opt-condition> <np> <modal> \\\"BE_ABLE\\\" <infinitive-vp> \" +\n" +
                    "                \"<np> <opt-details>\\n\" +\n" +
                    "                \"<opt-details> ::= \\\"\\\" |\" +\n" +
                    "                \"<token-sequence-without-subordinate-conjunctions>\\n\" +\n" +
                    "                \"<modal> ::= \\\"SHALL\\\" | \\\"SHOULD\\\" | \\\"WOULD\\\"\\n\" +\n" +
                    "                \"<conditional-keyword> ::= \\\"IF\\\" | \\\"AFTER\\\" | \\\"AS_SOON_AS\\\" |\" +\n" +
                    "                \"\\\"AS_LONG_AS\\\"\\n\" +\n" +
                    "                \"<opt-condition> ::= \\\"\\\" |\" +\n" +
                    "                \"<conditional-keyword> <non-punctuation-token>");*/
            db.getModel(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
