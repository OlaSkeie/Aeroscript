package no.uio.aeroscript.ast.stmt;

import java.util.HashMap;
import no.uio.aeroscript.runtime.Interpreter;
import no.uio.aeroscript.type.Memory;
import no.uio.aeroscript.type.Point;

public class AcMove extends Statement {
    private Point destination; // Destinasjonspunktet for bevegelsen
    private boolean moveTo; // True for MOVE TO, false for MOVE BY
    private float distanceToMove; // Dersom det er MoveBy.
    private Interpreter interpreter;
    private HashMap<Memory, Object> heap;
    private float time;
    private float speed;

    public AcMove(Point destination, boolean moveTo, float distanceToMove, Interpreter interpreter,
            HashMap<Memory, Object> heap, float time, float speed) {
        this.destination = destination;
        this.moveTo = moveTo; // Hvis true, ellers er moveBy
        this.distanceToMove = distanceToMove;
        this.interpreter = interpreter;
        this.heap = heap;
        this.time = speed;
        this.speed = speed;
    }

    public AcMove(Point destination, boolean moveTo, float distanceToMove, Interpreter interpreter,
            HashMap<Memory, Object> heap) {
        this(destination, moveTo, distanceToMove, interpreter, heap, 0, 0);
    }

    // må jeg endre på distance travelled?
    @Override
    public void execute() {
        if (moveTo) {

            Point currentPosition = interpreter.getPosition(); // Henter live posisjon
            float distanceX = destination.getX() - currentPosition.getX();
            float distanceY = destination.getY() - currentPosition.getY();

            // avstanden til destinasjonspunktet.
            float totalDistance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);

            // oppdaterer dronen til den nye posisjonen.
            // assert heap.get(Memory.VARIABLES) instanceof HashMap;
            HashMap<String, Object> variables = (HashMap<String, Object>) heap.get(Memory.VARIABLES);
            float cost = (float) ((totalDistance * 0.7) + (time * 0.1) + (speed * 1));

            // Double currentBattery = (Double) variables.get("battery level") - cost;
            float currentBattery = ((Number) variables.get("battery level")).floatValue() - cost;
            if (currentBattery < 0.1) {
                System.out.println("Not enough battery to move to: " + destination);
                return;
            }
            System.out.println("Moving to point: " + destination);
            variables.put("current position", destination);

            // avstand dronen har reist totalt.
            float totalDistanceTravelled = (float) variables.get("distance travelled");
            totalDistanceTravelled += totalDistance;
            variables.put("distance travelled", totalDistanceTravelled);

            // Kostnaden

            variables.put("battery level", currentBattery);

            interpreter.checkBattery();

        } else {
            // MOVE BY: Flytt dronen relativt i enten x- eller y-retning
            Point currentPosition = interpreter.getPosition(); // Hent nåværende posisjon fra heap

            // Velger å flytte i x-retning.
            float newX = currentPosition.getX();
            newX = currentPosition.getX() + distanceToMove;

            Point newPoint = new Point(newX, interpreter.getPosition().getY());
            HashMap<String, Object> variables = (HashMap<String, Object>) heap.get(Memory.VARIABLES);
            variables.put("current position", newPoint);

            // avstand dronen har reist.
            float currentDistanceTravelled = (float) variables.get("distance travelled");
            variables.put("distance travelled", currentDistanceTravelled + distanceToMove);

            float cost = (float) ((distanceToMove * 0.5) + (time * 0.1) + (speed * 1));

            float currentBattery = (float) variables.get("battery level") - cost;
            if (currentBattery < 0.1) {
                System.out.println("Not enough battery to move by: " + distanceToMove);
                return;
            }
            System.out.println("Moving by: " + distanceToMove);
            variables.put("battery level", currentBattery);

            interpreter.checkBattery();
            // toString();

        }
    }
}
