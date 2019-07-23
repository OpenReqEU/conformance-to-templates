package upc.req_quality.util;

public class Constants {

    private static Constants instance;

    /*
    Generic constants
     */

    private String[] matcherTags = {"<*>","(all)"};

    /*
    Adapter constants
     */

    private String finishClause = "***FINISH***";



    private Constants() {
        //empty
    }

    public static Constants getInstance() {
        if (instance == null) instance = new Constants();
        return instance;
    }

    public String[] getMatcherTags() {
        return matcherTags;
    }

    public String getFinishClause() {
        return finishClause;
    }
}
