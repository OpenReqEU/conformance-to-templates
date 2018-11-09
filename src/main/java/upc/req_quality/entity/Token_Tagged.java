package upc.req_quality.entity;

public class Token_Tagged {

    private String tagged;
    private String token;

    public Token_Tagged(String tagged, String token) {
        this.tagged = tagged;
        this.token = token;
    }

    public String getTagged() {
        return tagged;
    }

    public String getToken() {
        return  token;
    }
}
