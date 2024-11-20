package no.uio.aeroscript.runtime;

import no.uio.aeroscript.antlr.AeroScriptLexer;
import no.uio.aeroscript.antlr.AeroScriptParser;
import no.uio.aeroscript.ast.stmt.AcMove;
import no.uio.aeroscript.ast.stmt.Event;
import no.uio.aeroscript.ast.stmt.ID;
import no.uio.aeroscript.ast.stmt.Reactions;
import no.uio.aeroscript.ast.stmt.Statement;
import no.uio.aeroscript.type.Memory;
import no.uio.aeroscript.type.Point;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class InterpreterTest {
    private HashMap<Memory, Object> heap;
    private Stack<Statement> stack;

    private void initInterpreter() {
        this.heap = new HashMap<>();
        this.stack = new Stack<>();
        HashMap<Memory, HashMap<String, Object>> variables = new HashMap<>();
        variables.put(Memory.VARIABLES, new HashMap<>());
        HashMap<String, Object> vars = variables.get(Memory.VARIABLES);

        float batteryLevel = 100;
        int initialZ = 0;
        Point initialPosition = new Point(0, 0);

        vars.put("initial position", initialPosition);
        vars.put("current position", initialPosition);
        vars.put("altitude", initialZ);
        vars.put("initial battery level", batteryLevel);
        vars.put("battery level", batteryLevel);
        vars.put("battery low", false);
        vars.put("distance travelled", 0.0f);
        vars.put("initial execution", null);

        heap.put(Memory.EXECUTION_TABLE, new HashMap<>());
        heap.put(Memory.REACTIONS, new HashMap<>());
        heap.put(Memory.MESSAGES, new HashMap<>());
        heap.put(Memory.VARIABLES, vars);
    }

    private AeroScriptParser.ExpressionContext parseExpression(String expression) {
        AeroScriptLexer lexer = new AeroScriptLexer(CharStreams.fromString(expression));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        AeroScriptParser parser = new AeroScriptParser(tokens);
        return parser.expression();
    }

    @Test
    void testDistanceCalculation() {
        initInterpreter();
        Interpreter interpreter = new Interpreter(this.heap, this.stack);
        Point endPoint = new Point(3, 4); // Distance between these two points should be 5 units

        AcMove move = new AcMove(endPoint, true, 0, interpreter, heap);
        move.execute();

        float distance = (Float) ((HashMap<String, Object>) heap.get(Memory.VARIABLES)).get("distance travelled");
        assertEquals(5.0f, distance);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testEmergencyLanding() {
        initInterpreter();
        float battery = (float) 19;
        ((HashMap<String, Object>) this.heap.get(Memory.VARIABLES)).put("battery level", battery);
        Reactions lowb = new Reactions(new Event(null, new ID("Battery")), new ID("Battery"), heap);
        ((HashMap<String, Object>) this.heap.get(Memory.REACTIONS)).put("low battery", lowb);// Må bare legge inn noe
                                                                                             // siden hvis ikke blir det
                                                                                             // error messages
        Interpreter interpreter = new Interpreter(this.heap, this.stack);
        boolean emergency = interpreter.checkBattery();
        assertEquals(true, emergency);
    }

    @Test
    void getFirstExecution() {
        initInterpreter();
        Interpreter interpreter = new Interpreter(this.heap, this.stack);

        // Implement the test, ensure I have a first execution
        // Also, if you create a new execution and set if as first in the interpreter
        // you should get that
        // I dont really have a "first execution" because i dont use the stack for this
    }

    @Test
    void getPosition() {
        initInterpreter();
        Interpreter interpreter = new Interpreter(this.heap, this.stack);
        assertEquals(interpreter.heap, interpreter);
    }

    @Test
    void getDistanceTravelled() {
        initInterpreter();
        Interpreter interpreter = new Interpreter(this.heap, this.stack);
        assertEquals(0.0f, interpreter.getDistanceTravelled());
    }

    @Test
    void getBatteryLevel() {
        initInterpreter();
        Interpreter interpreter = new Interpreter(this.heap, this.stack);

        assertEquals(100.0f, interpreter.getBatteryLevel());
    }

    @Test
    void visitProgram() throws FileNotFoundException, IOException {
        String path = "/Users/olaskeie/Documents/3.år/IN2031-1/Documents/3.år/IN2031/aeroscript-3/src/test/resources/program.aero";
        initInterpreter();
        Interpreter interpreter = new Interpreter(this.heap, this.stack);
        AeroScriptParser.ProgramContext programContext = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            StringBuilder content = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
                System.out.println(line + "\n");

            }
            AeroScriptLexer lexer = new AeroScriptLexer(CharStreams.fromString(content.toString()));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            AeroScriptParser parser = new AeroScriptParser(tokens);
            programContext = parser.program();
            interpreter.visitProgram(programContext);
        }
        assertEquals((((HashMap) heap.get(Memory.EXECUTION_TABLE)).size()), 9);

        // Implement the test, read a file and parse it, then ensure you have the first
        // execution, and that the number
        // of exeuctions is correct (in the program.aero file there are 9 executions)
    }

    @Test
    void visitExpression() {
        initInterpreter();
        Interpreter interpreter = new Interpreter(this.heap, this.stack);

        assertEquals(5.0f,
                Float.parseFloat(interpreter.visitExpression(parseExpression("2 + 3")).evaluate().toString()));
        assertEquals(-1.0f,
                Float.parseFloat(interpreter.visitExpression(parseExpression("2 - 3")).evaluate().toString()));
        assertEquals(6.0f,
                Float.parseFloat(interpreter.visitExpression(parseExpression("2 * 3")).evaluate().toString()));
        assertEquals(-1, Float.parseFloat(interpreter.visitExpression(parseExpression("-- 1")).evaluate().toString()));
    }
}
