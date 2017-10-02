package be.jschoreels.discovery.java8;

import be.jschoreels.discovery.java8.domain.Person;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.util.Comparator.*;

/**
 * Created by jschoreels on 15.04.16.
 */
public class SortingTest {

    @Test
    public void simpleSortByLastnameReversed() {

        List<Person> persons = Arrays.asList(
            new Person("jonathan", "schoreels", 24),
            new Person("frederic", "gendebien", 29),
            new Person("jonathan", "gendebien", 0)
        );

        // Java 7
        Collections.sort(persons, new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                return - o1.getLastname().compareTo(o2.getLastname());
            }
        });
        System.out.println(persons);

        // Java 8
        persons.sort(comparing(Person::getLastname).reversed());
        System.out.println(persons);
    }

    @Test
    public void simpleSortByLastnameThenAgeDecreasing() {

        List<Person> persons = Arrays.asList(
            new Person("jonathan", "schoreels", 24),
            new Person("frederic", "gendebien", 29),
            new Person("jonathan", "gendebien", 0)
        );

        // Java 7
        Collections.sort(persons, new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                int lastnameResult = o1.getLastname().compareTo(o2.getLastname());
                if (lastnameResult == 0) {
                    return o2.getAge() - o1.getAge();
                }
                else {
                    return lastnameResult;
                }
            }
        });
        System.out.println(persons);

        // Java 8
        persons.sort(comparing(Person::getLastname).thenComparing(Person::getAge, reverseOrder()));
        System.out.println(persons);

    }


}
