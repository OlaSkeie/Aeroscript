package no.uio.aeroscript.ast.stmt;

import java.util.HashMap;

import no.uio.aeroscript.runtime.Interpreter;
import no.uio.aeroscript.type.Memory;

public class Reactions extends Statement {
    public Event event;
    public ID id;
    Interpreter interpreter;
    HashMap<Memory, Object> heap;

    public Reactions(Event event, ID id, HashMap<Memory, Object> heap) {
        this.event = event;
        this.id = id;
        this.heap = heap;
    }

    @Override
    public void execute() {
        switch (event.id.name) {
            case "low battery":
                emergencyLanding();
                break;
            case "obstacle":
                ((Execution) ((HashMap<Memory, Object>) heap.get(Memory.EXECUTION_TABLE)).get(event.id.name)).execute();
                break;
        }
    }

    public void emergencyLanding() {

        Execution ex = ((HashMap<String, Execution>) heap.get(Memory.EXECUTION_TABLE)).get(id.name);
        if (ex != null) {
            ex.execute();
            printInfo((HashMap<String, Object>) heap.get(Memory.VARIABLES));
            System.exit(0);
        } else {
            System.out.println("Not in heap");
        }

    }

    public String toString() {
        return "Event: " + event.type + ". ID: " + id.name;
    }

    public void printInfo(HashMap<String, Object> vars) {

        System.out.println("Battery at: " + vars.get("battery level"));
        System.out.println("Altitude at: " + vars.get("altitude"));
        System.out.println("Position: " + vars.get("current position"));
        System.out.println("Distance travelled: " + vars.get("distance travelled"));
    }

}
