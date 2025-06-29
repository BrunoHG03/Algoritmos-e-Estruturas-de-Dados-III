import java.io.*;
import java.util.*;

// CLASSE PARA O MENU DE COMPRESSAO
public class MenuCompressao {
    public static Scanner scanner = new Scanner(System.in);
    public static int x = 1;
    public static LZW lzw = new LZW();
    public static Huffman huffman = new Huffman();
    public static void menuCompressao() throws Exception{
        int opcao = 50;
        // ENQUANTO USUARIO NAO ESCOLHER SAIR
        while(opcao != 0){
           // MENU
           System.out.println("\n== Compressão ==");
           System.out.println("0 - Voltar");
           System.out.println("1 - Comprimir");
           System.out.println("2 - Descomprimir\n");
           System.out.print("Escolha sua opção: ");
           opcao = scanner.nextInt();
           // SWITCH PARA O MENU
           switch(opcao){
              case 0:
                  break;
              case 1:
                  // LE O ARQUIVO ORIGINAL TODO
                  byte[] dados = lerArquivoTodo("Pilotos.db");
                  RandomAccessFile raf = new RandomAccessFile("Pilotos.db", "rw");
                  raf.setLength(0);
                  // MARCA O TEMPO DE EXECUCAO E EXECUTA A COMPRESSAO VIA LZW
                  long inicio = System.nanoTime();
                  byte[] dadosComprimidosLZW = lzw.compressao(dados);
                  // ESCREVE O ARQUIVO COMPRIMIDO
                  escreveArquivo("Dados/PilotosLZWCompressao" + x + ".db", dadosComprimidosLZW);
                  long fim = System.nanoTime();
                  double tempoExecucaoLZW = (fim - inicio) / 1_000_000_000.0;
                  double porcentagemLZW = (((double) dados.length - (double) dadosComprimidosLZW.length) / (double) dados.length) * 100;
                  // MOSTRANDO TEMPO E PORCENTAGEM DO LZW PARA O USUÁRIO
                  System.out.println("== LZW ==");
                  System.out.printf("Porcentagem de ganho: %.2f%%\n", porcentagemLZW);
                  System.out.printf("Tempo de Execução: %.2fs\n", tempoExecucaoLZW);
                  // MARCA O TEMPO DE EXECUCAO E EXECUTA A COMPRESSAO VIA HUFFMAN
                  inicio = System.nanoTime();
                  // PEGA OS CODIGOS DOS CARACTERES
                  HashMap<Byte, String> codigos = huffman.codifica(dados);
                  huffman.escreverCodigos(codigos, x);
                  // FAZ A COMPRESSAO
                  byte[] dadosComprimidosHuffman = huffman.compressao(dados, codigos);
                  // ESCREVE O ARQUIVO COMPRIMIDO
                  escreveArquivo("Dados/PilotosHuffmanCompressao" + x + ".db", dadosComprimidosHuffman);
                  fim = System.nanoTime();
                  double tempoExecucaoHuffman = (fim - inicio) / 1_000_000_000.0;
                  double porcentagemHuffman = (((double) dados.length - (double) dadosComprimidosHuffman.length) / (double) dados.length) * 100;
                  // MOSTRANDO TEMPO E PORCENTAGEM DO HUFFMAN PARA O USUÁRIO
                  System.out.println("== Huffman ==");
                  System.out.printf("Porcentagem de ganho: %.2f%%\n", porcentagemHuffman);
                  System.out.printf("Tempo de Execução: %.2fs\n", tempoExecucaoHuffman);
                  // COMPARACOES ENTRE AS EXECUCOES
                  if(porcentagemLZW > porcentagemHuffman || porcentagemHuffman < 0)
                     System.out.println("A compressão LZW teve maior porcentagem de ganho!");
                  else if(porcentagemLZW == porcentagemHuffman)
                     System.out.println("A compressão Huffman e LZW tiveram a mesma porcentagem de ganho!");
                  else
                     System.out.println("A compressão Huffman teve maior porcentagem de ganho!");
                  if(tempoExecucaoLZW < tempoExecucaoHuffman)
                     System.out.println("A compressão LZW teve melhor tempo de execução!");
                  else if(tempoExecucaoLZW == tempoExecucaoHuffman)
                     System.out.println("A compressão Huffman e LZW tiveram o mesmo tempo de execução!");
                  else
                     System.out.println("A compressão Huffman teve melhor tempo de execução!");
                  x++;
                  break;
              case 2:
                  // ESCOLHE O NUMERO DA COMPRESSAO QUE IRA DESCOMPRIMIR
                  int numero = 0;
                  System.out.println("Qual o número da compressão que você deseja descomprimir?");
                  numero = scanner.nextInt();
                  // LE O ARQUIVO LZW COMPRIMIDO
                  byte[] dadosLZW = lerArquivoTodo("Dados/PilotosLZWCompressao" + numero + ".db");
                  // MARCA O TEMPO DE EXECUCAO E EXECUTA A DESCOMPRESSAO VIA LZW
                  inicio = System.nanoTime();
                  byte[] dadosDescomprimidosLZW = lzw.descompressao(dadosLZW);
                  // ESCREVE OS DADOS DESCOMPRIMIDOS NO ARQUIVO ORIGINAL
                  escreveArquivo("Pilotos.db", dadosDescomprimidosLZW);
                  fim = System.nanoTime();
                  tempoExecucaoLZW = (fim - inicio) / 1_000_000_000.0;
                  // MOSTRA O TEMPO DE EXECUCAO DO LZW
                  System.out.println("== LZW ==");
                  System.out.printf("Tempo de Execução: %.2fs\n", tempoExecucaoLZW);
                  // LE O ARQUIVO HUFFMAN COMPRIMIDO
                  byte[] dadosHuffman = lerArquivoTodo("Dados/PilotosHuffmanCompressao" + numero + ".db");
                  VetorDeBits dh = new VetorDeBits(dadosHuffman);
                   // MARCA O TEMPO DE EXECUCAO E EXECUTA A DESCOMPRESSAO VIA HUFFMAN
                  inicio = System.nanoTime();
                  HashMap<Byte, String> codigosDesc = huffman.lerCodigos(numero);
                  byte[] dadosDescomprimidosHuffman = huffman.descompressao(dh.toString(), codigosDesc);
                  // ESCREVE OS DADOS DESCOMPRIMIDOS NO ARQUIVO ORIGINAL
                  escreveArquivo("Pilotos.db", dadosDescomprimidosHuffman);
                  fim = System.nanoTime();
                  tempoExecucaoHuffman = (fim - inicio) / 1_000_000_000.0;
                  // MOSTRA O TEMPO DE EXECUCAO DO HUFFMAN
                  System.out.println("== Huffman ==");
                  System.out.printf("Tempo de Execução: %.2fs\n", tempoExecucaoHuffman);
                  // COMPARACOES ENTRE AS EXECUCOES
                  if(tempoExecucaoLZW < tempoExecucaoHuffman)
                     System.out.println("A descompressão LZW teve melhor tempo de execução!");
                  else if(tempoExecucaoLZW == tempoExecucaoHuffman)
                     System.out.println("A compressão Huffman e LZW tiveram o mesmo tempo de execução!");
                  else
                     System.out.println("A descompressão Huffman teve melhor tempo de execução!");
                  break;
              default:
                  System.out.println("\nOpção Inválida!!!");
                  break;
           }
        }
    }

    // METODO PARA LER O ARQUIVO TODO EM UM ARRAY DE BYTES
    public static byte[] lerArquivoTodo(String nomeArquivo) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(nomeArquivo, "r")) {
            long tamanho = raf.length();
            byte[] dados = new byte[(int) tamanho];
            raf.readFully(dados);
            return dados;
        }
    }

    // METODO PARA ESCREVER UM ARRAY DE BYTES NO ARQUIVO
    public static void escreveArquivo(String nomeArquivo, byte[] dados) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(nomeArquivo, "rw")) {
            raf.setLength(0);
            raf.write(dados);
        }
    }
}