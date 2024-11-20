package no.uio.aeroscript.runtime;

import no.uio.aeroscript.antlr.AeroScriptBaseVisitor;
import no.uio.aeroscript.antlr.AeroScriptParser;
import no.uio.aeroscript.antlr.AeroScriptParser.ExecutionContext;
import no.uio.aeroscript.antlr.AeroScriptParser.PointContext;
import no.uio.aeroscript.antlr.AeroScriptParser.StatementContext;
import no.uio.aeroscript.ast.expr.Node;
import no.uio.aeroscript.ast.expr.NumberNode;
import no.uio.aeroscript.ast.expr.OperationNode;
import no.uio.aeroscript.ast.stmt.AcAscend;
import no.uio.aeroscript.ast.stmt.AcDescend;
import no.uio.aeroscript.ast.stmt.AcDock;
import no.uio.aeroscript.ast.stmt.AcMove;
import no.uio.aeroscript.ast.stmt.AcTurn;
import no.uio.aeroscript.ast.stmt.Event;
import no.uio.aeroscript.ast.stmt.EventType;
import no.uio.aeroscript.ast.stmt.Execution;
import no.uio.aeroscript.ast.stmt.ID;
import no.uio.aeroscript.ast.stmt.Reactions;
import no.uio.aeroscript.ast.stmt.Statement;
import no.uio.aeroscript.type.Memory;
import no.uio.aeroscript.type.Point;
import no.uio.aeroscript.type.Range;

import java.util.*;

import org.antlr.v4.runtime.tree.TerminalNode;

public class Interpreter extends AeroScriptBaseVisitor<Object> {
    final HashMap<Memory, Object> heap;
    private final Stack<Statement> stack;
    public Execution initialEx;

    public Interpreter(HashMap<Memory, Object> heap, Stack<Statement> stack) {
        this.heap = heap;
        this.stack = stack;
    }

    public Point getPosition() {
        assert heap.get(Memory.VARIABLES) instanceof HashMap;
        HashMap<String, Object> vars = (HashMap<String, Object>) heap.get(Memory.VARIABLES);
        return (Point) vars.get("current position");
    }

    public Float getDistanceTravelled() {

        assert heap.get(Memory.VARIABLES) instanceof HashMap;

        HashMap<String, Object> vars = (HashMap<String, Object>) heap.get(Memory.VARIABLES);
        return (Float) vars.get("distance travelled");
    }

    public float getBatteryLevel() {
        assert heap.get(Memory.VARIABLES) instanceof HashMap;
        HashMap<String, Object> vars = (HashMap<String, Object>) heap.get(Memory.VARIABLES);
        return (float) vars.get("battery level");
    }

    public Execution getFirstExecution() {
        return initialEx;
    }

    public boolean checkBattery() {
        if (getBatteryLevel() < 20) {
            Reactions reaction = (Reactions) ((HashMap) heap.get(Memory.REACTIONS)).get("low battery");
            if (reaction != null) {
                System.out.println("Executing emergency landing");
                reaction.execute();
                return true;
            }
        }
        return false;
    }

    @Override
    public Object visitProgram(AeroScriptParser.ProgramContext ctx) {
        for (ExecutionContext ex : ctx.execution()) {
            initialEx = (Execution) visitExecution(ex);
            initialEx.execute();
        }

        return ctx; // vet ikke hva jeg skal returnere
    }

    @Override
    public Object visitExecution(AeroScriptParser.ExecutionContext ctx) {
        HashMap<String, Execution> map = (HashMap) heap.get(Memory.EXECUTION_TABLE);

        String id = ctx.id1.getText();
        ArrayList<Statement> statements = new ArrayList<>();

        for (StatementContext sc : ctx.statement()) {
            Object statement = visitStatement(sc);
            if (statement instanceof Statement) {
                statements.add((Statement) statement);
            }

        }
        ID id1 = new ID(ctx.id1.getText());
        if (ctx.id2 != null) {
            ID id2 = new ID(ctx.id2.getText());
            Execution execution = new Execution(statements, id1, id2, stack, heap, this);
            map.put(id, execution);
            heap.put(Memory.EXECUTION_TABLE, map);

            return execution;
        } else {
            Execution execution = new Execution(statements, id1, null, stack, heap, this);
            map.put(id, execution);
            heap.put(Memory.EXECUTION_TABLE, map);
            return execution;
        }
    }

    @Override
    public Object visitStatement(AeroScriptParser.StatementContext ctx) {
        if (ctx.action() != null) {
            return visitAction(ctx.action());
        }

        if (ctx.reaction() != null) {
            return visitReaction(ctx.reaction());
        }
        if (ctx.execution() != null) {
            return visitExecution(ctx.execution());
        }

        return null;
    }

    @Override
    public Object visitAction(AeroScriptParser.ActionContext ctx) {

        if (ctx.acDescend() != null) {
            if (ctx.acDescend().DESCGRND() != null) {
                float d = ((Float) ((HashMap) heap.get(Memory.VARIABLES)).get("altitude")).floatValue();

                AcDescend descend = new AcDescend(heap,
                        d, this, true);
                return descend;
            } else {
                String[] s = ctx.acDescend().getText().split("by");
                AcDescend descend = new AcDescend(heap, Float.parseFloat(s[1]), this, false);
                return descend;
            }
        }
        if (ctx.acAscend() != null) {
            String[] s = ctx.acAscend().getText().split("by");
            AcAscend ascend = new AcAscend(heap, Float.parseFloat(s[1]), this);
            return ascend;
        }
        if (ctx.acMove() != null) {
            String[] move = ctx.acMove().getText().split("");
            if (move[4].equals("t")) {
                PointContext j = ctx.acMove().point();
                Point point = (Point) visitPoint(j);
                TerminalNode number = null;
                Integer speed = 0;
                Integer time = 0;
                if (ctx.acMove().SPEED() != null) {
                    number = ctx.acMove().expression().NUMBER();
                    String s = number.getText();
                    speed = Integer.parseInt(s);
                }
                AcMove acMove = new AcMove(point, true, 0, this, heap, time, speed);
                return acMove;
            } else {
                TerminalNode number = ctx.acMove().NUMBER();

                Integer speed = 0;
                Integer time = 0;
                if (ctx.acMove().SPEED() != null) {
                    String s = ctx.acMove().expression().NUMBER().getText();
                    speed = Integer.parseInt(s);
                }
                AcMove acMove = new AcMove(null, false, Integer.parseInt(number.getText()), this, heap, time,
                        speed);
                return acMove;
            }
        }
        if (ctx.acDock() != null) {
            AcDock acDock = new AcDock(this, heap, 0, 0);
            return acDock;
        }

        if (ctx.acTurn() != null) {
            String direction;
            if (ctx.acTurn().RIGHT() != null) {
                direction = "right";
            } else {
                direction = "left";
            }
            float angle = Float.parseFloat(ctx.acTurn().expression().getText());

            float time = 0;
            float speed = 0;
            // Opprett en ny instans av Acturn
            AcTurn acturn = new AcTurn(direction, angle, time, speed, this, heap);
            return acturn;
        }

        return null;
    }

    @Override
    public Object visitReaction(AeroScriptParser.ReactionContext ctx) {
        if (ctx.event().OBSTACLE() != null) {
            Event event = new Event(EventType.OBSTACLE, new ID(ctx.event().OBSTACLE().getText()));
            Reactions reaction = new Reactions(event, new ID(ctx.id3.getText()), heap);
            HashMap<String, Event> map = (HashMap<String, Event>) heap.get(Memory.REACTIONS);
            map.put("obstacle", event);
            return reaction;
        }
        if (ctx.event().LOWB() != null) {
            Event event = new Event(EventType.LOWB, new ID(ctx.event().LOWB().getText()));
            Reactions reaction = new Reactions(event, new ID(ctx.id3.getText()), heap);
            HashMap<String, Reactions> map = (HashMap<String, Reactions>) heap.get(Memory.REACTIONS);
            map.put("low battery", reaction);
            return reaction;
        }
        if (ctx.event().MESSAGE() != null) {
            Event event = new Event(EventType.MESSAGE, new ID(ctx.event().ID().getText()));
            Reactions reaction = new Reactions(event, new ID(ctx.id3.getText()), heap);
            HashMap<String, Reactions> map = (HashMap<String, Reactions>) heap.get(Memory.REACTIONS);
            map.put(ctx.event().ID().getText(), reaction);

            return reaction;
        }
        return null;

    }

    @Override
    public Object visitPoint(AeroScriptParser.PointContext ctx) {
        Node xNode = (Node) visit(ctx.expression(0));
        Node yNode = (Node) visit(ctx.expression(1));
        float x = Float.parseFloat(xNode.evaluate().toString());
        float y = Float.parseFloat(yNode.evaluate().toString());
        return new Point(x, y);
    }

    @Override
    public Object visitRange(AeroScriptParser.RangeContext ctx) {
        Node startNode = (Node) visit(ctx.expression(0));
        Node endNode = (Node) visit(ctx.expression(1));
        float start = Float.parseFloat(startNode.evaluate().toString());
        float end = Float.parseFloat(endNode.evaluate().toString());
        return new Range(start, end);
    }

    @Override
    public Node visitExpression(AeroScriptParser.ExpressionContext ctx) {
        // Case of binary operation
        if (ctx.PLUS() != null || ctx.MINUS() != null || ctx.TIMES() != null) {
            Node left = (Node) visit(ctx.expression(0));
            Node right = (Node) visit(ctx.expression(1));
            String operator = ctx.PLUS() != null ? "PLUS" : ctx.MINUS() != null ? "MINUS" : "TIMES";
            return new OperationNode(operator, left, right);
        }
        // Case of negation
        else if (ctx.NEG() != null) {
            Node expr = (Node) visit(ctx.expression(0));
            return new OperationNode("NEG", expr, null);
        }
        // Case of random
        else if (ctx.RANDOM() != null) {
            Node start;
            Node end;
            if (ctx.range() != null) {
                AeroScriptParser.RangeContext rangeContext = ctx.range();
                start = (Node) visit(rangeContext.expression(0));
                end = (Node) visit(rangeContext.expression(1));
            } else {
                start = new NumberNode((float) 0);
                end = new NumberNode(100F);
            }
            return new OperationNode("RANDOM", start, end);
        }
        // Case of point
        else if (ctx.POINT() != null) {
            AeroScriptParser.PointContext pointContext = ctx.point();
            Node x = (Node) visit(pointContext.expression(0));
            Node y = (Node) visit(pointContext.expression(1));
            return new OperationNode("POINT", x, y);
        }
        // Case of number
        else if (ctx.NUMBER() != null) {
            return new NumberNode(Float.parseFloat(ctx.NUMBER().getText()));
        }
        // Case of expression in parentheses
        else if (ctx.LPAREN() != null) {
            return (Node) visit(ctx.expression(0));
        }
        return new NumberNode(Float.parseFloat(ctx.NUMBER().getText()));
    }
}