package no.uio.aeroscript.ast.stmt;

public class Event {

    public EventType type;
    public ID id;

    public Event(EventType type, ID id) {
        this.type = type;
        this.id = id;
    }

}
