package be.jschoreels.discovery.java8.domain;

/**
 * Created by jschoreels on 15.04.16.
 */
public class Person {

    private final String firstname;
    private final String lastname;
    private final int age;

    public Person(String firstname, String lastname, int age) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "Person{"+firstname+","+lastname+","+age+"}";
    }
}
