import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final Scanner scanner;
    private Token currentToken;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
        advance(); // Carrega o primeiro token (lookahead)
    }

    private void advance() {
        currentToken = scanner.nextToken();
    }

    private void match(TokenType expected) {
        if (currentToken.type == expected) {
            advance();
        } else {
            throw new RuntimeException(String.format(
                    "Erro Sintático [Linha %d, Col %d]: Esperado %s, mas encontrado %s ('%s')",
                    currentToken.line, currentToken.column, expected, currentToken.type, currentToken.lexeme
            ));
        }
    }

    // Ponto de entrada do Parser
    public AST.ProgramNode parse() {
        return parsePrograma();
    }

    // <programa> -> 'program' <id> ':' <bloco> '.'
    private AST.ProgramNode parsePrograma() {
        match(TokenType.PROGRAM);

        String programName = currentToken.lexeme;
        match(TokenType.ID);

        match(TokenType.COLON);

        AST.BlockNode block = parseBloco();

        match(TokenType.DOT);
        match(TokenType.EOF); // Garante que não há lixo após o fim do programa

        return new AST.ProgramNode(programName, block);
    }

    // <bloco> -> 'begin' <linhas> 'end'
    private AST.BlockNode parseBloco() {
        match(TokenType.BEGIN);

        List<AST.CommandNode> statements = parseLinhas();

        match(TokenType.END);

        return new AST.BlockNode(statements);
    }

    // <linhas> -> <linha> | <linha> <linhas>
    private List<AST.CommandNode> parseLinhas() {
        List<AST.CommandNode> statements = new ArrayList<>();

        while (currentToken.type != TokenType.END && currentToken.type != TokenType.EOF) {
            AST.CommandNode cmd = parseComando();
            if (cmd != null) {
                statements.add(cmd);
            }

            // O último comando de um bloco não exige ';' (Restrição 6.1.3)
            if (currentToken.type == TokenType.SEMICOLON) {
                match(TokenType.SEMICOLON);
            } else if (currentToken.type != TokenType.END) {
                // Se não tem ';' e não é o fim do bloco, forçamos o erro chamando match
                match(TokenType.SEMICOLON);
            }
        }

        return statements;
    }

    // <comando> -> <atrib> | <io> | <controle> | ε
    private AST.CommandNode parseComando() {
        switch (currentToken.type) {
            case ID:
                return parseAtrib();
            case READ:
            case WRITE:
                return parseIO();
            case IF:
                return parseIf();
            case WHILE:
                return parseWhile();
            default:
                // Comando vazio (ε)
                return null;
        }
    }

    // <atrib> -> <id> '=' <expr>
    private AST.AssignNode parseAtrib() {
        String idName = currentToken.lexeme;
        match(TokenType.ID);
        match(TokenType.ASSIGN);

        AST.ExprNode expr = parseExpr();

        return new AST.AssignNode(idName, expr);
    }

    // <io> -> 'read' '(' <id> ')' | 'write' '(' <expr> ')'
    private AST.CommandNode parseIO() {
        if (currentToken.type == TokenType.READ) {
            match(TokenType.READ);
            match(TokenType.LPAREN);
            String idName = currentToken.lexeme;
            match(TokenType.ID);
            match(TokenType.RPAREN);
            return new AST.ReadNode(idName);
        } else {
            match(TokenType.WRITE);
            match(TokenType.LPAREN);
            AST.ExprNode expr = parseExpr();
            match(TokenType.RPAREN);
            return new AST.WriteNode(expr);
        }
    }

    // <condicional> -> 'if' <relacional> 'then' <bloco> <condicional'>
    // <condicional'> -> 'else' <bloco> | ε
    private AST.IfNode parseIf() {
        match(TokenType.IF);

        // Chamará a função de expressões relacionais (a ser implementada)
        AST.RelOpNode condition = parseRelacional();

        match(TokenType.THEN);
        AST.BlockNode thenBlock = parseBloco();

        AST.BlockNode elseBlock = null; // Assume épsilon (vazio) por padrão

        // Avalia <condicional'> verificando o lookahead
        if (currentToken.type == TokenType.ELSE) {
            match(TokenType.ELSE);
            elseBlock = parseBloco();
        }

        return new AST.IfNode(condition, thenBlock, elseBlock);
    }

    // <loop> -> 'while' <relacional> 'do' <bloco>
    private AST.WhileNode parseWhile() {
        match(TokenType.WHILE);

        AST.RelOpNode condition = parseRelacional();

        match(TokenType.DO);
        AST.BlockNode body = parseBloco();

        return new AST.WhileNode(condition, body);
    }

    // <relacional> -> <expr> <relop> <expr>[cite: 2]
    private AST.RelOpNode parseRelacional() {
        AST.ExprNode left = parseExpr();

        // <relop> -> '==' | '<' | '>'[cite: 2]
        String operator = currentToken.lexeme;
        if (currentToken.type == TokenType.EQ || currentToken.type == TokenType.LT || currentToken.type == TokenType.GT) {
            advance();
        } else {
            throw new RuntimeException(String.format(
                    "Erro Sintático [Linha %d, Col %d]: Esperado operador relacional (==, <, >), encontrado %s",
                    currentToken.line, currentToken.column, currentToken.type
            ));
        }

        AST.ExprNode right = parseExpr();
        return new AST.RelOpNode(left, operator, right);
    }

    // <expr> -> <expr_simples> <expr'>[cite: 3]
    private AST.ExprNode parseExpr() {
        AST.ExprNode left = parseExprSimples();

        // <expr'> -> '+' <expr> | '-' <expr> | ε[cite: 3]
        // Substituído por iteração para garantir associatividade à esquerda sem recursão infinita
        while (currentToken.type == TokenType.PLUS || currentToken.type == TokenType.MINUS) {
            String operator = currentToken.lexeme;
            advance();
            AST.ExprNode right = parseExprSimples();
            left = new AST.BinOpNode(left, operator, right);
        }

        return left;
    }

    // <expr_simples> -> <termo> <expr_simples'>[cite: 3]
    private AST.ExprNode parseExprSimples() {
        AST.ExprNode left = parseTermo();

        // <expr_simples'> -> '*' <expr_simples> | '/' <expr_simples> | ε[cite: 3]
        while (currentToken.type == TokenType.TIMES || currentToken.type == TokenType.DIVIDE) {
            String operator = currentToken.lexeme;
            advance();
            AST.ExprNode right = parseTermo();
            left = new AST.BinOpNode(left, operator, right);
        }

        return left;
    }

    // <termo> -> <fator> | '(' <expr> ')'[cite: 2]
    private AST.ExprNode parseTermo() {
        if (currentToken.type == TokenType.LPAREN) {
            match(TokenType.LPAREN);
            AST.ExprNode expr = parseExpr();
            match(TokenType.RPAREN);
            return expr;
        } else {
            return parseFator();
        }
    }

    // <fator> -> <id> | <num>[cite: 2]
    private AST.ExprNode parseFator() {
        if (currentToken.type == TokenType.ID) {
            String name = currentToken.lexeme;
            match(TokenType.ID);
            return new AST.IdNode(name);
        } else if (currentToken.type == TokenType.NUMBER) {
            int value = Integer.parseInt(currentToken.lexeme);
            match(TokenType.NUMBER);
            return new AST.NumberNode(value);
        } else {
            throw new RuntimeException(String.format(
                    "Erro Sintático [Linha %d, Col %d]: Esperado ID ou Número, encontrado %s",
                    currentToken.line, currentToken.column, currentToken.type
            ));
        }
    }
}