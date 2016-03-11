package be.jschoreels.discovery.java8;

import be.jschoreels.discovery.java8.domain.Request;
import be.jschoreels.discovery.java8.domain.Response;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class ServiceTest {

    private final List<String> bodies = ImmutableList.of(
            "1", "2", "3", "4"
    );

    private final Service service = new Service();

    public void commonAssertions(List<Request> requests, List<Response> responses) {

        assertEquals(requests.size(), 4);
        assertEquals(requests.get(0).getBody(), "1");
        assertEquals(requests.get(1).getBody(), "2");
        assertEquals(requests.get(2).getBody(), "3");
        assertEquals(requests.get(3).getBody(), "4");

        assertEquals(responses.size(), 4);
        assertEquals(responses.get(0).getBody(), "Response1");
        assertEquals(responses.get(1).getBody(), "Response2");
        assertEquals(responses.get(2).getBody(), "Response3");
        assertEquals(responses.get(3).getBody(), "Response4");

    }

    @Test
    public void BasicRequestResponseStreamToList() {

        final List<Request> requests = bodies.stream()
                .map(Request::new)
                .collect(Collectors.toList());

        final List<Response> responses = requests.stream()
                .map(service::send)
                .collect(Collectors.toList());

        commonAssertions(requests, responses);

    }

    @Test
    public void StatefulMapperStreamRequestResponseToList() {

        final List<Request> requests = new ArrayList<>();
        final List<Response> responses = bodies.stream()
                .map(Request::new)
                .map(x -> {
                    requests.add(x);
                    return service.send(x);
                })
                .collect(Collectors.toList());

        commonAssertions(requests, responses);

    }

    @Test
    public void ChainedStreamRequestResponseToList() {

        final List<Request> requests;
        final List<Response> responses;

        responses = (
                requests = bodies.stream()
                        .map(Request::new)
                        .collect(Collectors.toList()))
                .stream()
                .map(service::send)
                .collect(Collectors.toList());

        commonAssertions(requests, responses);

    }

}
