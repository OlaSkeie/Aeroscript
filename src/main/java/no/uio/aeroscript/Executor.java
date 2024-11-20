package no.uio.aeroscript;

import no.uio.aeroscript.runtime.Program;

public class Executor {
    Program program;

    public Executor(Program program) {
        this.program = program;
    }

    public void receiveMessage(String message) {
        program.run(message);
    }
}
