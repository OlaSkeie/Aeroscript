package no.uio.aeroscript.runtime;

import java.util.HashMap;
import java.util.Stack;
import no.uio.aeroscript.ast.stmt.Execution;
import no.uio.aeroscript.ast.stmt.Statement;
import no.uio.aeroscript.type.Memory;

public class Program {
    // Add the variables that you might be needing
    Stack<Statement> stack;
    HashMap<Memory, HashMap> heap;

    public Program(Stack<Statement> stack, HashMap<Memory, HashMap> heap) {
        this.stack = stack;
        this.heap = heap;
    }

    // Hint: we should keep track of the executions and add them to a stack

    public void run(String message) {
        HashMap map = heap.get(Memory.MESSAGES);
        String mess = (String) map.get(message);
        if (mess == null) {
            System.out.println("Message not available");
            return;
        }
        System.out.println("Message available");
        HashMap executions = heap.get(Memory.EXECUTION_TABLE);
        Execution execution = (Execution) executions.get(mess);
        if (execution == null) {
            System.out.println("This execution is not possible to execute");
            return;
        }

        stack.push(execution);
        // velger å bare legge executions på stacken, og gjøre resten inne i metodene
        while (!stack.isEmpty()) {

            Statement s = stack.pop();
            s.execute();

        }
    }
}
