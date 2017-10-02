package be.jschoreels.discovery.java8.domain;

/**
 * Created by jschoreels on 12.07.17.
 */
public interface Condition {

    boolean isRespected();

    default boolean isNotRespected() {
        return !isRespected();
    }

}
