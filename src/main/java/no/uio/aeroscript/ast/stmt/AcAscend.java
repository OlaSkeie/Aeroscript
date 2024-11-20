package no.uio.aeroscript.ast.stmt;

import java.util.HashMap;

import no.uio.aeroscript.runtime.Interpreter;
import no.uio.aeroscript.type.Memory;

@SuppressWarnings("unchecked")

public class AcAscend extends Statement {
    public float ascendBy;
    public HashMap<Memory, Object> heap;
    Interpreter interpreter;

    public AcAscend(HashMap<Memory, Object> heap, Float ascendBy, Interpreter interpreter) {
        this.ascendBy = ascendBy;
        this.heap = heap;
        this.interpreter = interpreter;
    }

    @Override
    public void execute() {
        // assert heap.get(Memory.VARIABLES) instanceof HashMap;
        HashMap<String, Object> vars = (HashMap<String, Object>) heap.get(Memory.VARIABLES);
        float cost = (float) (ascendBy * 0.6);
        float currentBattery = (float) vars.get("battery level") - cost;
        if (currentBattery < 0.1) {
            System.out.println("Not enough battery to ascend by: " + ascendBy);
            return;
        }
        System.out.println("Ascending by: " + ascendBy);
        vars.put("altitude", (float) vars.get("altitude") + ascendBy);
        vars.put("battery level", currentBattery);
        heap.put(Memory.VARIABLES, vars);
        interpreter.checkBattery();

    }

    public String toString() {
        HashMap vars = (HashMap) heap.get(Memory.VARIABLES);
        return "Ascended by: " + ascendBy + ". Altitude now: "
                + vars.get("altitude") + ". Battery now: " + vars.get("battery level");
    }
}
