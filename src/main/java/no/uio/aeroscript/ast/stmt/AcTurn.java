package no.uio.aeroscript.ast.stmt;

import java.util.HashMap;
import no.uio.aeroscript.runtime.Interpreter;
import no.uio.aeroscript.type.Memory;

public class AcTurn extends Statement {
    private String direction;
    private float angle;
    private float time;
    private float speed;
    private Interpreter interpreter;
    private HashMap<Memory, Object> heap;

    public AcTurn(String direction, float angle, float time, float speed, Interpreter interpreter,
            HashMap<Memory, Object> heap) {
        this.direction = direction;
        this.angle = angle;
        this.time = time;
        this.speed = speed;
        this.interpreter = interpreter;
        this.heap = heap;
    }

    public AcTurn(String direction, float angle, Interpreter interpreter, HashMap<Memory, Object> heap) {
        this(direction, angle, 0, 0, interpreter, heap); // Setter time og speed til 0 som standardverdi
    }

    @Override
    public void execute() {
        HashMap<String, Object> variables = (HashMap<String, Object>) heap.get(Memory.VARIABLES);

        // kostnaden
        float cost = (float) ((angle * 0.3) + (time * 0.1) + (speed * 1));

        // oppdaterer batterinivået.
        float currentBattery = (float) variables.get("battery level") - cost;
        if (currentBattery < 0.1) {
            System.out.println("Not enough battery to do this operation");
        }
        variables.put("battery level", currentBattery);

        // sjekk batterinivå. Kan hende den skal sjekkes i interpreter.
        interpreter.checkBattery();
    }
}
