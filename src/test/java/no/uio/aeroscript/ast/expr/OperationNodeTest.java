package no.uio.aeroscript.ast.expr;

import no.uio.aeroscript.type.Point;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OperationNodeTest {
    Node left = new NumberNode(2.0f);
    Node right = new NumberNode(3.0f);

    @Test
    void evaluateSum() {
        Node node = new OperationNode("PLUS", left, right);
        assertEquals(5.0f, node.evaluate());
    }

    @Test
    void evaluateSub() {
        Node node = new OperationNode("MINUS", left, right);
        assertEquals(-1.0f, node.evaluate());
    }

    @Test
    void evaluateMul() {
        Node node = new OperationNode("TIMES", left, right);
        assertEquals(6.0f, node.evaluate());
    }

    @Test
    void evaluateNeg() {
        Node node = new OperationNode("NEG", left, right);
        assertEquals(2.0f * -1, node.evaluate());
    }

    @Test
    void evaluateRandom() {
        Node node = new OperationNode("RANDOM", left, right);
        float value = (Float) node.evaluate();
        assertTrue(value >= 2.0f && value <= 3.0f);
    }

    @Test
    void evaluatePoint() {
        Node node = new OperationNode("POINT", left, right);
        Point point = (Point) node.evaluate();
        assertEquals(2.0f, point.getX());
        assertEquals(3.0f, point.getY());
    }
}