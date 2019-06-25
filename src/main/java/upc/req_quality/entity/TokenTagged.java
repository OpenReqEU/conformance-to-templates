package upc.req_quality.entity;

public class TokenTagged {

    private String tagged;
    private String token;

    public TokenTagged(String tagged, String token) {
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
