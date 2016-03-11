package be.jschoreels.discovery.java8;

import be.jschoreels.discovery.java8.domain.Request;
import be.jschoreels.discovery.java8.domain.Response;

/**
 * Hello world!
 */
public class Service {

    public Response send(Request r) {
        return new Response("Response" + r.getBody());
    }

}
