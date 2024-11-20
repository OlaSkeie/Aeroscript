package no.uio.aeroscript;

import no.uio.aeroscript.runtime.Program;
import no.uio.aeroscript.type.Memory;

import java.util.HashMap;

public class Reactor {
    private final Executor messageHandler;
    private final HashMap<Memory, Object> heap;
    private final HashMap<String, Runnable> listeners;

    public Reactor(HashMap<String, Runnable> listeners, Program program, HashMap<Memory, Object> heap) {
        this.listeners = listeners;
        this.heap = heap;
        this.messageHandler = new Executor(program);
        execute();
    }

    private void execute() {
        for (Memory key : heap.keySet()) {
            Object value = heap.get(key);

            if (value instanceof HashMap<?, ?>) {
                HashMap<String, Object> eventMap = (HashMap<String, Object>) value;

                for (String event : eventMap.keySet()) {
                    listeners.put(event, () -> {
                        String message = Main.getLastMessage();
                        if (message != null && message.equals(event)) {
                            this.messageHandler.receiveMessage(message);
                        } else {
                            System.out.println("No matching message received for event: " + event);
                        }
                    });
                }
            }
        }
    }
}