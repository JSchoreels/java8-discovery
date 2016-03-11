package be.jschoreels.discovery.java8.domain;

/**
 * Created by jschoreels on 11.03.16.
 */
public class Response {

    private final String body;


    public Response(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
