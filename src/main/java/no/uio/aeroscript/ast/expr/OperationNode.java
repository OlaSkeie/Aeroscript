package no.uio.aeroscript.ast.expr;

import no.uio.aeroscript.type.Point;
import java.util.Random;

public class OperationNode extends Node {
    private final String operation;
    private final Node left;
    private final Node right;

    public OperationNode(String operation, Node left, Node right) {
        this.operation = operation;
        this.left = left;
        this.right = right;
    }

    @Override
    public Object evaluate() {
        return switch (operation) {
            case "PLUS" -> {
                if (left.evaluate() instanceof Point point && right.evaluate() instanceof Point otherPoint) {
                    yield new Point((Float) point.getX() + (Float) otherPoint.getX(), (Float) point.getY() + (Float) otherPoint.getY());
                } else if (left.evaluate() instanceof Float && right.evaluate() instanceof Float) {
                    yield (Float) left.evaluate() + (Float) right.evaluate();
                } else {
                    throw new IllegalArgumentException("Invalid operation: " + operation);
                }
            }
            case "MINUS" -> {
                if (left.evaluate() instanceof Point point && right.evaluate() instanceof Point otherPoint) {
                    yield new Point((Float) point.getX() - (Float) otherPoint.getX(), (Float) point.getY() - (Float) otherPoint.getY());
                } else if (left.evaluate() instanceof Float && right.evaluate() instanceof Float) {
                    yield (Float) left.evaluate() - (Float) right.evaluate();
                } else {
                    throw new IllegalArgumentException("Invalid operation: " + operation);
                }
            }
            case "TIMES" -> {
                if (left.evaluate() instanceof Point point && right.evaluate() instanceof Float) {
                    yield new Point((Float) point.getX() * (Float) right.evaluate(), (Float) point.getY() * (Float) right.evaluate());
                } else if (left.evaluate() instanceof Float && right.evaluate() instanceof Point point) {
                    yield new Point((Float) left.evaluate() * (Float) point.getX(), (Float) left.evaluate() * (Float) point.getY());
                } else if (left.evaluate() instanceof Float && right.evaluate() instanceof Float) {
                    yield (Float) left.evaluate() * (Float) right.evaluate();
                } else {
                    throw new IllegalArgumentException("Invalid operation: " + operation);
                }
            }
            case "NEG" -> (Float) left.evaluate() * (-1);
            // For RANDOM return a random number between left and right
            case "RANDOM" -> {
                Random r = new Random();
                float leftValue = (Float) left.evaluate();
                float rightValue = (Float) right.evaluate();
                yield r.nextFloat((rightValue-leftValue)) + leftValue;
            }
            case "POINT" -> new Point((Float) left.evaluate(), (Float) right.evaluate());
            default -> throw new IllegalArgumentException("Invalid operation: " + operation);
        };
    }
}
