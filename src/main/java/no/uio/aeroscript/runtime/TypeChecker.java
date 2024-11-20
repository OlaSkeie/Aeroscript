package no.uio.aeroscript.runtime;

import no.uio.aeroscript.antlr.AeroScriptBaseVisitor;
import no.uio.aeroscript.antlr.AeroScriptParser;
import no.uio.aeroscript.antlr.AeroScriptParser.ActionContext;
import no.uio.aeroscript.antlr.AeroScriptParser.ExecutionContext;
import no.uio.aeroscript.antlr.AeroScriptParser.ExpressionContext;
import no.uio.aeroscript.antlr.AeroScriptParser.StatementContext;
import no.uio.aeroscript.error.TypeError;

public class TypeChecker extends AeroScriptBaseVisitor<Object> {

    AeroScriptParser.ProgramContext ctx;

    public TypeChecker(AeroScriptParser.ProgramContext ctx) {
        this.ctx = ctx;
    }

    public void check() {
        visitProgram(ctx);
    }

    public Object visitProgram(AeroScriptParser.ProgramContext ctx) {

        if (ctx == null)
            return null;
        for (ExecutionContext executionContext : ctx.execution()) {
            visit(executionContext);
        }
        return null;
    }

    private Object visit(ExecutionContext executionContext) {
        if (executionContext == null) {
            return null;
        }
        for (StatementContext statementContext : executionContext.statement()) {
            visit(statementContext);
        }
        return null;

    }

    private Object visit(StatementContext statementContext) {
        if (statementContext == null) {
            return null;
        }
        ActionContext ctx = statementContext.action();
        if (ctx != null) {
            visitAction(ctx);
        }

        return true;
    }

    public Object visitAction(ActionContext ctx) {
        if (ctx.acAscend() != null) {
            validateAscend(ctx);
        }
        if (ctx.acDescend() != null) {
            validateDescend(ctx);
        }
        if (ctx.acTurn() != null) {
            validateTurn(ctx);
        }
        if (ctx.acMove() != null) {
            validateMove(ctx);
        }
        return ctx;
    }

    public void validateAscend(ActionContext ctx) {
        String type = evaluateExpression(ctx.acAscend().expression());
        if (type.equals("NUMBER")) {
            return;
        }
        throw new TypeError("Wrong type in ascend");
    }

    public void validateDescend(ActionContext ctx) {
        String type = evaluateExpression(ctx.acDescend().expression());
        if (type.equals("NUMBER")) {
            return;
        }
        throw new TypeError("Wrong type in descend");
    }

    public void validateMove(ActionContext ctx) {
        if (ctx.acMove().TO() != null) {
            if (ctx.acMove().point() == null) {
                throw new TypeError("Move requires Point type" + ctx.getText());
            }
            String pointType = validatePoint(ctx.acMove().point());
            if (!"POINT".equals(pointType)) {
                throw new TypeError("Move to action requires a Point type: " + ctx.getText());
            }
        }
        if (ctx.acMove().SPEED() != null) {
            String speedType = evaluateExpression(ctx.acMove().expression());

            if (!"NUMBER".equals(speedType)) {
                throw new TypeError("Move speed requires type Num: " + ctx.getText());
            }
        }
    }

    public void validateTurn(ActionContext ctx) {
        String type = evaluateExpression(ctx.acTurn().expression());
        if (type.equals("NUMBER")) {
            return;
        }
        throw new TypeError("Wrong type in turn");
    }

    public String evaluateExpression(ExpressionContext ctx) {
        if (ctx == null) {
            throw new TypeError("context is null");
        }
        if (ctx.NUMBER() != null) {
            return "NUMBER";
        } else if (ctx.POINT() != null) {
            if (ctx.point() != null) {
                return validatePoint(ctx.point());
            }
        } else if (ctx.RANDOM() != null) {
            return validateRandom(ctx);
        } else if (ctx.NEG() != null) {
            return evaluateExpression(ctx.expression(0));
        } else if (ctx.TIMES() != null || ctx.PLUS() != null || ctx.MINUS() != null) {
            String leftType = evaluateExpression(ctx.expression(0));
            String rightType = evaluateExpression(ctx.expression(1));
            if ("NUMBER".equals(leftType) && "NUMBER".equals(rightType)) {
                return "NUMBER";
            }
        }
        throw new TypeError("Type error: " + ctx.getText());
    }

    public String validatePoint(AeroScriptParser.PointContext ctx) {
        String xType = evaluateExpression(ctx.expression(0));
        String yType = evaluateExpression(ctx.expression(1));
        if ("NUMBER".equals(xType) && "NUMBER".equals(yType)) {
            return "POINT";
        }
        throw new TypeError("Invalid point: " + ctx.getText());
    }

    public String validateRandom(AeroScriptParser.ExpressionContext ctx) {
        if (ctx.range() == null) {
            return "NUMBER";
        }
        String firstType = evaluateExpression(ctx.range().expression(0));
        String secondType = evaluateExpression(ctx.range().expression(1));
        if ("NUMBER".equals(firstType) && "NUMBER".equals(secondType)) {
            return "NUMBER";
        }
        throw new TypeError("Invalid random range: " + ctx.getText());
    }
}
