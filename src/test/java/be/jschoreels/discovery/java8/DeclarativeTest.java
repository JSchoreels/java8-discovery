package be.jschoreels.discovery.java8;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Created by jschoreels on 12.07.17.
 */
public class DeclarativeTest {

    @Test
    public void notDeclarativeFilteringTest(){
        List<Integer> integerListExample = Arrays.asList(
            1, 5, 2, 5, 2
        );
        List<Integer> resultNotDeclarative = new ArrayList<>();
        for (int i = 0; i < integerListExample.size(); i++){
            final Integer currentInteger = integerListExample.get(i);
            if (currentInteger % 2 == 0){
                resultNotDeclarative.add(currentInteger);
            }
        }
        printlist(resultNotDeclarative);
        final List<Integer> resultDeclarative = integerListExample.stream()
            .filter(i -> i % 2 == 0)
            .collect(Collectors.toList());
        printlist(resultDeclarative);
    }

    @Test
    public void notDeclarativeAssertionTest(){

        String personNullable = null;
        final String person = Optional.ofNullable(personNullable)
            .map(s -> s.toLowerCase())
            .map(s -> s.toUpperCase())
            .map(s -> s.toString())
            .orElse("Unknown");
        System.out.println(person);
    }


    public void printlist(List list){
        list.stream().forEach(elem -> System.out.println(elem));
    }

    private void assertNoContradiction(boolean condition){
        if (condition){
            throw new AssertionError("Assertion not respected, contradiction detected");
        }
    }

}
