package be.jschoreels.discovery.java8;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


public class ProcedureMotor {

    public List<Exception> encounteredException = new ArrayList<>();

    public List<String> results = new ArrayList();

    public void runProcedure(Supplier<String> stringSupplier){
        try {
            results.add(stringSupplier.get());
        } catch (Exception e){
            encounteredException.add(e);
        }
    }

    public List<Exception> getEncounteredException() {
        return encounteredException;
    }

    public List<String> getResults() {
        return results;
    }

    public static void main(String[] args) {
        ProcedureMotor procedureMotor = new ProcedureMotor();
        procedureMotor.runProcedure(() -> "message 1");
        procedureMotor.runProcedure(() -> { throw new RuntimeException("exception 1");});
        procedureMotor.runProcedure(() -> "message 2");
        procedureMotor.runProcedure(() -> "message 3");
        procedureMotor.runProcedure(() -> { throw new RuntimeException("exception 2");});
        procedureMotor.runProcedure(() -> "message 4");
        procedureMotor.runProcedure(() -> "message 5");

        procedureMotor.getResults().stream()
            .forEach(s -> System.out.println(s));
        procedureMotor.getEncounteredException().stream()
            .forEach(e -> System.out.println(e.getMessage()));
    }

}
