package be.jschoreels.discovery.java8;

import java.util.Arrays;
import java.util.List;


public class VisitorExample {

    public static void main(String[] args) {
        ZooVisitor visitor = new ZooVisitor();
        List<Animal> bestiary = Arrays.asList(new Wolf(), new Snake(), new Wolf());

        for (Animal animal: bestiary) {
            animal.accept(visitor);
        }
    }


    public interface Animal extends VisitableAnimal {
        // Nothing in common, for example's sake
    }

    public static class Wolf implements Animal {
        public void bark(){
            System.out.println("Waf Waf");
        }

        @Override
        public void accept(final AnimalVisitor visitor) {
            visitor.visit(this);
        }
    }

    public static class Snake implements Animal {
        public void molt(){
            System.out.println("Losing current skin");
        }

        @Override
        public void accept(final AnimalVisitor visitor) {
            visitor.visit(this);
        }
    }

    public static class ZooVisitor implements AnimalVisitor {
        public void visit(final Wolf wolf) {
            wolf.bark();
        }
        public void visit(final Snake snake) {
            snake.molt();
        }
    }

    public interface VisitableAnimal {
        void accept(AnimalVisitor visitor);
    }

    public interface AnimalVisitor {
        void visit(Wolf wolf);

        void visit(Snake snake);
    }

}
