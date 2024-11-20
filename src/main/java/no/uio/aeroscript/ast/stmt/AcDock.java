package no.uio.aeroscript.ast.stmt;

import java.util.HashMap;
import no.uio.aeroscript.runtime.Interpreter;
import no.uio.aeroscript.type.Memory;
import no.uio.aeroscript.type.Point;

public class AcDock extends Statement {
    private HashMap<Memory, Object> heap;
    private Interpreter interpreter;
    private float time;
    private float speed;

    public AcDock(Interpreter interpreter, HashMap<Memory, Object> heap, float time, float speed) {
        this.interpreter = interpreter;
        this.heap = heap;
        this.time = time;
        this.speed = speed;
    }

    public AcDock(Interpreter interpreter, HashMap<Memory, Object> heap) {
        this(interpreter, heap, 0, 0);
    }

    @Override
    public void execute() {
        HashMap<String, Object> variables = (HashMap<String, Object>) heap.get(Memory.VARIABLES);

        float altitude = (float) variables.get("altitude");

        // kostnaden
        float cost = (float) (altitude + (time * 0.1) + (speed * 1));

        // oppdatere batterinivået
        float currentBattery = (float) variables.get("battery level") - cost;
        if (currentBattery < 0.1) {
            System.out.println("Not enough battery to do this operation");
            return;
        }
        System.out.println("Returning to base");
        variables.put("battery level", currentBattery);

        // oppdatere posisjonen til initialPosisiton.
        Point basePosition = new Point(0, 0);
        variables.put("initial position", basePosition);

        System.out.println("Drone is back to base.");

    }
}