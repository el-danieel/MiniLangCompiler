import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class Scanner implements AutoCloseable {
    private final Reader reader;
    private int currentChar = -1; // -1 indica EOF
    private int line = 1;
    private int column = 0; // Inicia em 0 pois o primeiro advance() vai para 1
    private static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        keywords.put("program", TokenType.PROGRAM);
        keywords.put("begin", TokenType.BEGIN);
        keywords.put("end", TokenType.END);
        keywords.put("read", TokenType.READ);
        keywords.put("write", TokenType.WRITE);
        keywords.put("if", TokenType.IF);
        keywords.put("then", TokenType.THEN);
        keywords.put("else", TokenType.ELSE);
        keywords.put("while", TokenType.WHILE);
        keywords.put("do", TokenType.DO);
    }

    public Scanner(String filePath) throws IOException {
        this.reader = new BufferedReader(new FileReader(filePath));
        advance(); // Carrega o primeiro caractere
    }

    public Token nextToken() {
        skipWhitespaceAndComments();

        if (currentChar == -1) {
            return new Token(TokenType.EOF, "", line, column);
        }

        int startColumn = column;
        char c = (char) currentChar;

        // Identificadores e Palavras-chave
        if (Character.isLetter(c)) {
            StringBuilder sb = new StringBuilder();
            while (currentChar != -1 && Character.isLetterOrDigit((char) currentChar)) {
                sb.append((char) currentChar);
                advance();
            }
            String lexeme = sb.toString();
            TokenType type = keywords.getOrDefault(lexeme, TokenType.ID);
            return new Token(type, lexeme, line, startColumn);
        }

        // Números
        if (Character.isDigit(c)) {
            StringBuilder sb = new StringBuilder();
            while (currentChar != -1 && Character.isDigit((char) currentChar)) {
                sb.append((char) currentChar);
                advance();
            }
            return new Token(TokenType.NUMBER, sb.toString(), line, startColumn);
        }

        // Operadores Relacionais (==) e Atribuição (=)
        if (c == '=') {
            advance();
            if (currentChar == '=') {
                advance();
                return new Token(TokenType.EQ, "==", line, startColumn);
            }
            return new Token(TokenType.ASSIGN, "=", line, startColumn);
        }

        // Processa caractere único e avança
        advance();
        switch (c) {
            case '+': return new Token(TokenType.PLUS, "+", line, startColumn);
            case '-': return new Token(TokenType.MINUS, "-", line, startColumn);
            case '*': return new Token(TokenType.TIMES, "*", line, startColumn);
            case '/': return new Token(TokenType.DIVIDE, "/", line, startColumn);
            case '<': return new Token(TokenType.LT, "<", line, startColumn);
            case '>': return new Token(TokenType.GT, ">", line, startColumn);
            case '(': return new Token(TokenType.LPAREN, "(", line, startColumn);
            case ')': return new Token(TokenType.RPAREN, ")", line, startColumn);
            case '{': return new Token(TokenType.LBRACE, "{", line, startColumn);
            case '}': return new Token(TokenType.RBRACE, "}", line, startColumn);
            case ';': return new Token(TokenType.SEMICOLON, ";", line, startColumn);
            case ':': return new Token(TokenType.COLON, ":", line, startColumn);
            case ',': return new Token(TokenType.COMMA, ",", line, startColumn);
            case '.': return new Token(TokenType.DOT, ".", line, startColumn);
            default:
                throw new RuntimeException("Caractere inválido: '" + c + "' na linha " + line + ", coluna " + startColumn);
        }
    }

    private void skipWhitespaceAndComments() {
        while (currentChar != -1) {
            char c = (char) currentChar;
            if (c == ' ' || c == '\r' || c == '\t') {
                advance();
            } else if (c == '\n') {
                line++;
                column = 0;
                advance();
            } else if (c == '/') {
                // Precisamos verificar o próximo caractere para ver se é comentário
                // Usar mark/reset permite olhar o próximo caractere sem consumi-lo permanentemente
                try {
                    reader.mark(1);
                    int next = reader.read();
                    if (next == '/') {
                        // É comentário, consome até o fim da linha
                        advance(); // atualiza a coluna internamente
                        while (currentChar != -1 && currentChar != '\n') {
                            advance();
                        }
                    } else {
                        // Não é comentário, volta atrás
                        reader.reset();
                        break; // Sai do loop, pois é o operador de divisão
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Erro ao processar lookahead para comentário", e);
                }
            } else {
                break;
            }
        }
    }

    private void advance() {
        try {
            currentChar = reader.read();
            column++;
        } catch (IOException e) {
            currentChar = -1;
        }
    }

    @Override
    public void close() throws Exception {
        if (reader != null) {
            reader.close();
        }
    }
}