package no.uio.aeroscript.runtime;

import org.junit.jupiter.api.Test;

import no.uio.aeroscript.antlr.AeroScriptLexer;
import no.uio.aeroscript.antlr.AeroScriptParser;
import no.uio.aeroscript.error.TypeError;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class TypeCheckerTest {

    @Test
    public void testMoveWithoutPoint() {
        String program = "/Users/olaskeie/Documents/3.år/IN2031-1/Documents/3.år/IN2031/aeroscript-3/src/test/java/no/uio/aeroscript/runtime/pointprogram.aero";

        String content;
        try {
            content = new String(Files.readAllBytes(Paths.get(program)));

            AeroScriptLexer lexer = new AeroScriptLexer(CharStreams.fromString(content));
            lexer.removeErrorListeners();

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            AeroScriptParser parser = new AeroScriptParser(tokens);
            parser.removeErrorListeners();

            AeroScriptParser.ProgramContext programContext = parser.program();

            TypeChecker typeChecker = new TypeChecker(programContext);
            typeChecker.check();
            fail("Expected TypeError");
        } catch (TypeError e) {
            assertTrue(e.getMessage().contains("Move requires Point type"));
            System.err.println("Error reading file: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSpeed() {
        String program = "/Users/olaskeie/Documents/3.år/IN2031-1/Documents/3.år/IN2031/aeroscript-3/src/test/java/no/uio/aeroscript/runtime/speedprogram.aero";

        String content;
        try {
            content = new String(Files.readAllBytes(Paths.get(program)));

            AeroScriptLexer lexer = new AeroScriptLexer(CharStreams.fromString(content));
            lexer.removeErrorListeners();

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            AeroScriptParser parser = new AeroScriptParser(tokens);
            parser.removeErrorListeners();

            AeroScriptParser.ProgramContext programContext = parser.program();

            TypeChecker typeChecker = new TypeChecker(programContext);
            typeChecker.check();
            fail("Expected TypeError");
        } catch (TypeError e) {
            assertTrue(e.getMessage().contains("Move speed requires type Num"));
            System.err.println("Error reading file: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPointInPoint() {
        String program = "/Users/olaskeie/Documents/3.år/IN2031-1/Documents/3.år/IN2031/aeroscript-3/src/test/java/no/uio/aeroscript/runtime/pointinpoint.aero";

        String content;
        try {
            content = new String(Files.readAllBytes(Paths.get(program)));

            AeroScriptLexer lexer = new AeroScriptLexer(CharStreams.fromString(content));
            lexer.removeErrorListeners();

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            AeroScriptParser parser = new AeroScriptParser(tokens);
            parser.removeErrorListeners();

            AeroScriptParser.ProgramContext programContext = parser.program();

            TypeChecker typeChecker = new TypeChecker(programContext);
            typeChecker.check();
            fail("Expected TypeError");
        } catch (TypeError e) {
            assertTrue(e.getMessage().contains("Invalid random range"));
            System.err.println("Error reading file: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPointInAscend() {
        String program = "/Users/olaskeie/Documents/3.år/IN2031-1/Documents/3.år/IN2031/aeroscript-3/src/test/java/no/uio/aeroscript/runtime/ascendprogram.aero";

        String content;
        try {
            content = new String(Files.readAllBytes(Paths.get(program)));

            AeroScriptLexer lexer = new AeroScriptLexer(CharStreams.fromString(content));
            lexer.removeErrorListeners();

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            AeroScriptParser parser = new AeroScriptParser(tokens);
            parser.removeErrorListeners();

            AeroScriptParser.ProgramContext programContext = parser.program();

            TypeChecker typeChecker = new TypeChecker(programContext);
            typeChecker.check();
            fail("Expected TypeError");
        } catch (TypeError e) {
            assertTrue(e.getMessage().contains("Wrong type in ascend"));
            System.err.println("Error reading file: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
