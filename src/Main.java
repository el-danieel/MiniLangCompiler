public class Main {
    public static void main(String[] args) {
        // Caminho do arquivo de teste
        String filePath = "src\\teste.minilang";

        try (Scanner scanner = new Scanner(filePath)) {
            System.out.println("Iniciando análise sintática do arquivo: " + filePath);

            // Instancia o parser e inicia a construção da AST
            Parser parser = new Parser(scanner);
            AST.ProgramNode ast = parser.parse();

            System.out.println("Análise sintática concluída com sucesso!");
            System.out.println("Árvore Sintática (AST) gerada para o programa: " + ast.name);

        } catch (Exception e) {
            System.err.println("Erro durante a compilação: " + e.getMessage());
            // e.printStackTrace(); // Descomente para ver o rastreio completo da pilha em caso de erro
        }
    }
}