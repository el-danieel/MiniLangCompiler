public enum TokenType {
    PROGRAM, BEGIN, END, READ, WRITE, IF, THEN, ELSE, WHILE, DO, INT, // Palavras-chave
    ID, NUMBER, // Identificadores e Números
    ASSIGN, PLUS, MINUS, TIMES, DIVIDE, EQ, LT, GT, // Operadores
    LPAREN, RPAREN, LBRACE, RBRACE, SEMICOLON, COLON, COMMA, DOT, // Pontuação
    EOF // Fim de Arquivo
}