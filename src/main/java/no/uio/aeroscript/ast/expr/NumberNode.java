package no.uio.aeroscript.ast.expr;

public class NumberNode extends Node {
    private final Float number;

    public NumberNode(Float number) {
        this.number = number;
    }

    @Override
    public Float evaluate() {
        return number;
    }
}
