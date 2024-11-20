package no.uio.aeroscript.ast.stmt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import no.uio.aeroscript.runtime.Interpreter;
import no.uio.aeroscript.type.Memory;

//her må man utføre de ulike executionsene og senke batterinivået (i heapen) og altitude (heapen). Og sjekke low battery 

public class Execution extends Statement {
    // Add all the variables that you need for the exeuction
    ArrayList<Statement> statements;
    public ID id1;
    ID id2;
    Stack<Statement> stack;
    HashMap<Memory, Object> heap;
    HashSet<String> executedExecutions = new HashSet<String>();
    static ArrayList<String> list = new ArrayList<>();
    Interpreter interpreter;

    public Execution(ArrayList<Statement> statements, ID id1, ID id2, Stack<Statement> stack, HashMap heap,
            Interpreter interpreter) {
        this.statements = statements;
        this.id1 = id1;
        this.id2 = id2;
        this.stack = stack;
        this.heap = heap;
        this.interpreter = interpreter;

    }

    @Override
    public void execute() {
        HashMap<String, Execution> executions = (HashMap<String, Execution>) heap.get(Memory.EXECUTION_TABLE);

        Execution exec = executions.get(id1.name);

        for (Statement statement : exec.statements) {
            if (statement instanceof Reactions) {
                Reactions reaction = (Reactions) statement;
                if (reaction.event.type == EventType.MESSAGE) {
                    HashMap<String, String> messages = (HashMap<String, String>) heap.get(Memory.MESSAGES);
                    messages.put(reaction.event.id.name, reaction.id.name);
                    break;
                }
            }
            if (statement != null && !(statement instanceof Reactions)) {
                statement.execute();
            }
        }
        if (id2 != null) {
            executions.put(id1.name, null); // Kan bli en evig loop hvis jeg ikke fjerner den her
            Execution next = executions.get(id2.name);
            next.execute();
        }
        executions.put(id1.name, null);

    }

    public String toString() {
        return "ID: " + id1.name + "Statements: " + statements.toString();

    }
}
