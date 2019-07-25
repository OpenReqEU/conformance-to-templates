package upc.req_quality.util;

public class Constants {

    private static Constants instance;

    /*
    Generic constants
     */

    private int maxSyncIterations = 50;
    private int sleepTime = 500;
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

    public int getMaxSyncIterations() {
        return maxSyncIterations;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public String[] getMatcherTags() {
        return matcherTags;
    }

    public String getFinishClause() {
        return finishClause;
    }
}
