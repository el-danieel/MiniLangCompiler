import java.util.List;

public class AST {

    // Classe base
    public static abstract class ASTNode { }

    // --- ESTRUTURA PRINCIPAL ---

    public static class ProgramNode extends ASTNode {
        public final String name;
        public final BlockNode block;

        public ProgramNode(String name, BlockNode block) {
            this.name = name;
            this.block = block;
        }
    }

    public static class BlockNode extends ASTNode {
        public final List<CommandNode> statements;

        public BlockNode(List<CommandNode> statements) {
            this.statements = statements;
        }
    }

    // --- COMANDOS ---
    public static abstract class CommandNode extends ASTNode { }

    public static class AssignNode extends CommandNode {
        public final String id;
        public final ExprNode expr;

        public AssignNode(String id, ExprNode expr) {
            this.id = id;
            this.expr = expr;
        }
    }

    public static class ReadNode extends CommandNode {
        public final String id;

        public ReadNode(String id) {
            this.id = id;
        }
    }

    public static class WriteNode extends CommandNode {
        public final ExprNode expr;

        public WriteNode(ExprNode expr) {
            this.expr = expr;
        }
    }

    public static class IfNode extends CommandNode {
        public final RelOpNode condition;
        public final BlockNode thenBlock;
        public final BlockNode elseBlock; // Pode ser null se não houver 'else'

        public IfNode(RelOpNode condition, BlockNode thenBlock, BlockNode elseBlock) {
            this.condition = condition;
            this.thenBlock = thenBlock;
            this.elseBlock = elseBlock;
        }
    }

    public static class WhileNode extends CommandNode {
        public final RelOpNode condition;
        public final BlockNode body;

        public WhileNode(RelOpNode condition, BlockNode body) {
            this.condition = condition;
            this.body = body;
        }
    }

    // --- EXPRESSÕES ---
    public static abstract class ExprNode extends ASTNode { }

    public static class BinOpNode extends ExprNode {
        public final ExprNode left;
        public final String operator;
        public final ExprNode right;

        public BinOpNode(ExprNode left, String operator, ExprNode right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }
    }

    public static class RelOpNode extends ExprNode {
        public final ExprNode left;
        public final String operator;
        public final ExprNode right;

        public RelOpNode(ExprNode left, String operator, ExprNode right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }
    }

    public static class NumberNode extends ExprNode {
        public final int value;

        public NumberNode(int value) {
            this.value = value;
        }
    }

    public static class IdNode extends ExprNode {
        public final String name;

        public IdNode(String name) {
            this.name = name;
        }
    }
}