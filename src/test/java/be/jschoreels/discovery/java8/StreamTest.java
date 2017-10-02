package be.jschoreels.discovery.java8;

import be.jschoreels.discovery.java8.domain.Person;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.*;


/**
 * Created by jschoreels on 03.07.17.
 */
public class StreamTest {

    @Test
    public void lazinessOfFlatMapTest() {
        Stream.of("Hello")
            .flatMap(dontcare ->
                Stream.iterate("World", s -> s + "a").limit(20)
            )
            .limit(5)
            .forEach(System.out::println);
    }

    @Test
    public void simpleListToMap() {

        List<Person> personsTest = Arrays.asList(
            new Person("jonathan", "schoreels", 24),
            new Person("frederic", "gendebien", 29),
            new Person("jonathan", "gendebien", 0)
        );

        final List<Person> collect = personsTest.stream()
            .filter(i -> i.getFirstname().equals("jonathan"))
            .filter(i -> i.getAge() > 20)
            .collect(Collectors.toList());
        System.out.println(collect);


    }

}
