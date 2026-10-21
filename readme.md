# MiniComp - Compilador Front-End LL(1) para MiniLang

Projeto didático da disciplina de Compiladores voltado à implementação de um compilador front-end e interpretador para a linguagem hipotética **MiniLang**.

O desenvolvimento adota boas práticas de engenharia de software e está dividido em duas fases. A **Fase 1** (Front-End) está concluída e disponível neste repositório. A **Fase 2** (Interpretador) será desenvolvida em breve.

## 🚀 Funcionalidades (Fase 1)

- **Analisador Léxico (Scanner):** Lê o código-fonte, descarta espaços/comentários e tokeniza a entrada, registrando linha e coluna para tratamento preciso de erros.
- **Analisador Sintático (Parser):** Implementação Preditiva Tabular Descendente Recursiva LL(1). Valida a sintaxe baseada em um token de *lookahead*.
- **Construção da AST:** Gera uma Árvore Sintática Abstrata (Abstract Syntax Tree) em memória utilizando uma hierarquia de classes Orientada a Objetos em Java.
- **Transformações de Gramática:** A gramática original sofreu **Fatoração à Esquerda** (comando `if-then-else`) e **Eliminação de Recursão à Esquerda** (expressões matemáticas) para adequação ao modelo LL(1).

## 🛠️ Tecnologias Utilizadas

- **Java (JDK 8+)**: Linguagem principal para implementação das classes, scanner e parser.
- **JFLAP**: (Modelagem teórica) Utilizado para modelagem de autômatos, verificação da gramática e extração dos conjuntos FIRST/FOLLOW (artefatos a serem incluídos no relatório final).

## 📂 Estrutura do Projeto

* `TokenType.java`: Enumeração dos tokens aceitos pela MiniLang.
* `Token.java`: Estrutura de dados contendo o lexema, tipo, linha e coluna.
* `Scanner.java`: Analisador léxico adaptado para leitura eficiente de arquivos em disco.
* `AST.java`: Hierarquia de nós (`ASTNode`, `ProgramNode`, `CommandNode`, `ExprNode`) para representação da árvore.
* `Parser.java`: Analisador sintático descendente recursivo.
* `Main.java`: Ponto de entrada que orquestra a leitura do arquivo e a análise.
* `src/teste.minilang`: Arquivo de código-fonte MiniLang para testes (ex: cálculo de fatorial).

## ⚙️ Como Executar

1. Clone o repositório:
   ```bash
   git clone https://github.com/el-danieel/MiniLangCompiler.git
   ```
2. Navegue até o diretório do código-fonte e compile os arquivos Java:
   ```bash
   javac *.java
   ```
3. Execute a classe Main:
   ```bash
   java Main
   ```

O programa lerá o arquivo `src/teste.minilang` e imprimirá o status da compilação e a construção da árvore no console. Em caso de erro sintático, o compilador apontará a linha e coluna exatas do problema.

## 🔜 Próximos Passos (Fase 2)

- [ ] Tabela de Símbolos para variáveis globais.
- [ ] Interpretador Semântico percorrendo a AST.
- [ ] Tratamento de erros em tempo de execução (ex: divisão por zero).
- [ ] Relatório Técnico final.