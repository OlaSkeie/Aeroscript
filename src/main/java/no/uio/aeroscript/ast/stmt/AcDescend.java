package no.uio.aeroscript.ast.stmt;

import java.util.HashMap;

import no.uio.aeroscript.runtime.Interpreter;
import no.uio.aeroscript.type.Memory;

public class AcDescend extends Statement {
    public float descendBy;
    public HashMap<Memory, Object> heap;
    Interpreter interpreter;
    boolean desgrnd;

    public AcDescend(HashMap<Memory, Object> heap, float descendBy, Interpreter interpreter, boolean desgrnd) {
        this.descendBy = descendBy;
        this.heap = heap;
        this.interpreter = interpreter;
        this.desgrnd = desgrnd;
    }

    @Override
    public void execute() {
        assert heap.get(Memory.VARIABLES) instanceof HashMap;

        HashMap<String, Object> vars = (HashMap<String, Object>) heap.get(Memory.VARIABLES);
        if (desgrnd) {
            descendBy = (float) vars.get("altitude");
        }
        float cost = (float) (descendBy * 0.2);
        float currentBattery = (float) vars.get("battery level") - cost;
        if (currentBattery < 0.1 && !desgrnd) {
            System.out.println("Not enough battery to descend by: " + descendBy);
            interpreter.checkBattery();
            return;
        }
        System.out.println("Descending by: " + descendBy);
        vars.put("altitude", (Float) vars.get("altitude") - descendBy);
        if (!desgrnd) {
            vars.put("battery level", currentBattery);
        } else {
            vars.put("battery level", 0);
        }
        heap.put(Memory.VARIABLES, vars);
        System.out
                .println("Altitude: " + (Float) ((HashMap<Memory, Object>) heap.get(Memory.VARIABLES)).get("altitude"));

        // sjekk lavt batterinivå
        if (!desgrnd) {
            interpreter.checkBattery();
        }

    }

    public String toString() {
        HashMap vars = (HashMap) heap.get(Memory.VARIABLES);
        return "Descended by: " + descendBy + ". Altitude now: "
                + vars.get("altitude") + "Battery now: " + vars.get("battery level");
    }

}
