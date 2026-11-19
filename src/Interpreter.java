import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Interpreter {
    private final BufferedReader input;
    private final PrintStream output;
    private final PrintStream traceOutput;
    private final boolean traceEnabled;
    private final Map<String, Integer> symbols = new LinkedHashMap<>();
    private StringTokenizer inputTokens = new StringTokenizer("");

    public Interpreter(InputStream input, PrintStream output, boolean traceEnabled) {
        this(input, output, System.err, traceEnabled);
    }

    public Interpreter(
            InputStream input,
            PrintStream output,
            PrintStream traceOutput,
            boolean traceEnabled
    ) {
        this.input = new BufferedReader(new InputStreamReader(input));
        this.output = output;
        this.traceOutput = traceOutput;
        this.traceEnabled = traceEnabled;
    }

    public void execute(AST.ProgramNode program) {
        trace("Iniciando programa '%s'", program.name);
        executeBlock(program.block);
        trace("Programa finalizado. Símbolos: %s", symbols);
    }

    public Map<String, Integer> getSymbols() {
        return Collections.unmodifiableMap(symbols);
    }

    private void executeBlock(AST.BlockNode block) {
        for (AST.CommandNode command : block.statements) {
            executeCommand(command);
        }
    }

    private void executeCommand(AST.CommandNode command) {
        if (command instanceof AST.AssignNode) {
            executeAssign((AST.AssignNode) command);
        } else if (command instanceof AST.ReadNode) {
            executeRead((AST.ReadNode) command);
        } else if (command instanceof AST.WriteNode) {
            executeWrite((AST.WriteNode) command);
        } else if (command instanceof AST.IfNode) {
            executeIf((AST.IfNode) command);
        } else if (command instanceof AST.WhileNode) {
            executeWhile((AST.WhileNode) command);
        } else {
            throw new RuntimeException("Comando desconhecido na AST");
        }
    }

    private void executeAssign(AST.AssignNode node) {
        int value = evaluate(node.expr);
        symbols.put(node.id, value);
        trace("%s = %d; estado: %s", node.id, value, symbols);
    }

    private void executeRead(AST.ReadNode node) {
        int value = readInputValue();
        symbols.put(node.id, value);
        trace("read(%s) => %d; estado: %s", node.id, value, symbols);
    }

    private void executeWrite(AST.WriteNode node) {
        int value = evaluate(node.expr);
        output.println(value);
        trace("write(%d)", value);
    }

    private void executeIf(AST.IfNode node) {
        boolean condition = evaluateRelational(node.condition);
        trace("if (%s) => %s", describe(node.condition), condition);

        if (condition) {
            executeBlock(node.thenBlock);
        } else if (node.elseBlock != null) {
            executeBlock(node.elseBlock);
        }
    }

    private void executeWhile(AST.WhileNode node) {
        int iteration = 0;
        while (true) {
            boolean condition = evaluateRelational(node.condition);
            trace("while (%s) [iteração %d] => %s",
                    describe(node.condition), iteration, condition);

            if (!condition) {
                return;
            }

            executeBlock(node.body);
            iteration++;
        }
    }

    private int evaluate(AST.ExprNode expression) {
        if (expression instanceof AST.NumberNode) {
            return ((AST.NumberNode) expression).value;
        }

        if (expression instanceof AST.IdNode) {
            String name = ((AST.IdNode) expression).name;
            symbols.putIfAbsent(name, 0);
            return symbols.get(name);
        }

        if (expression instanceof AST.BinOpNode) {
            AST.BinOpNode operation = (AST.BinOpNode) expression;
            int left = evaluate(operation.left);
            int right = evaluate(operation.right);

            switch (operation.operator) {
                case "+":
                    return left + right;
                case "-":
                    return left - right;
                case "*":
                    return left * right;
                case "/":
                    if (right == 0) {
                        throw new RuntimeException(
                                "Divisão por zero ao avaliar expressão aritmética"
                        );
                    }
                    return left / right;
                default:
                    throw new RuntimeException(
                            "Operador aritmético desconhecido: " + operation.operator
                    );
            }
        }

        if (expression instanceof AST.RelOpNode) {
            throw new RuntimeException("Expressão relacional usada como expressão aritmética");
        }

        throw new RuntimeException("Expressão desconhecida na AST");
    }

    private boolean evaluateRelational(AST.RelOpNode relation) {
        int left = evaluate(relation.left);
        int right = evaluate(relation.right);

        switch (relation.operator) {
            case "==":
                return left == right;
            case "<":
                return left < right;
            case ">":
                return left > right;
            default:
                throw new RuntimeException(
                        "Operador relacional desconhecido: " + relation.operator
                );
        }
    }

    private int readInputValue() {
        try {
            while (!inputTokens.hasMoreTokens()) {
                String line = input.readLine();
                if (line == null) {
                    throw new RuntimeException("Entrada insuficiente para o comando read");
                }
                inputTokens = new StringTokenizer(line);
            }

            String token = inputTokens.nextToken();
            try {
                return Integer.parseInt(token);
            } catch (NumberFormatException exception) {
                throw new RuntimeException("Valor de entrada inválido: '" + token + "'");
            }
        } catch (IOException exception) {
            throw new RuntimeException("Erro ao ler a entrada", exception);
        }
    }

    private String describe(AST.RelOpNode relation) {
        return relation.operator;
    }

    private void trace(String format, Object... arguments) {
        if (traceEnabled) {
            traceOutput.printf("[TRACE] " + format + "%n", arguments);
        }
    }
}
