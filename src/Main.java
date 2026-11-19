public class Main {
    public static void main(String[] args) {
        String filePath = "src/teste.minilang";
        boolean trace = false;

        try {
            boolean fileProvided = false;
            for (String arg : args) {
                if ("--trace".equals(arg)) {
                    trace = true;
                } else if (arg.startsWith("--")) {
                    throw new IllegalArgumentException("Opção desconhecida: " + arg);
                } else if (fileProvided) {
                    throw new IllegalArgumentException("Informe apenas um arquivo de entrada");
                } else {
                    filePath = arg;
                    fileProvided = true;
                }
            }

            try (Scanner scanner = new Scanner(filePath)) {
                System.err.println("Iniciando análise sintática do arquivo: " + filePath);

                Parser parser = new Parser(scanner);
                AST.ProgramNode ast = parser.parse();

                System.err.println("Análise sintática concluída com sucesso!");
                System.err.println("Árvore Sintática (AST) gerada para o programa: " + ast.name);

                Interpreter interpreter = new Interpreter(System.in, System.out, trace);
                interpreter.execute(ast);
            }
        } catch (Exception e) {
            System.err.println("Erro durante compilação/execução: " + e.getMessage());
            System.exit(1);
        }
    }
}
