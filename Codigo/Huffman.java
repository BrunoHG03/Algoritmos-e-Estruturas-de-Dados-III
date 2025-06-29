/*
CLASSE HUFFMAN IMPLEMENTADA COM O MATERIAL DISPONIVEL
DE AEDS III E O CODIGO DISPONIBILIZADO PELO PROFESSOR KUTOVA NO
GITHUB DE AEDS III, FEITO TAMBEM ALGUMAS MODIFICACOES AO LONGO
DE TODO O CODIGO
*/
import java.io.*;
import java.util.HashMap;
import java.util.PriorityQueue;

// CLASSE NO DA ARVORE DE HUFFMAN
class HuffmanNode implements Comparable<HuffmanNode> {
    byte b;
    int frequencia;
    HuffmanNode esquerdo, direito;

    // CONSTRUTOR 
    public HuffmanNode(byte b, int f) {
        this.b = b;
        this.frequencia = f;
        esquerdo = direito = null;
    }

    // METODO PARA COMPARAR OS NOS ATRAVES DA FREQUENCIA
    @Override
    public int compareTo(HuffmanNode o) { return this.frequencia - o.frequencia; }
}

// CLASSE HUFFMAN
public class Huffman {

    // METODO PARA GERAR OS CODIGOS DOS CARACTERES
    public static HashMap<Byte, String> codifica(byte[] dados) throws IOException {
        // CRIA O MAPA DE FREQUENCIA PARA OS CODIGOS
        HashMap<Byte, Integer> mapaDeFrequencias = new HashMap<>();
        for (byte c : dados) {
            mapaDeFrequencias.put(c, mapaDeFrequencias.getOrDefault(c, 0) + 1);
        }
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        for (Byte b : mapaDeFrequencias.keySet()) {
            pq.add(new HuffmanNode(b, mapaDeFrequencias.get(b)));
        }
        // MONTA A ARVORE DE HUFFMAN
        while (pq.size() > 1) {
            HuffmanNode esquerdo = pq.poll();
            HuffmanNode direito = pq.poll();
            HuffmanNode pai = new HuffmanNode((byte)0, esquerdo.frequencia + direito.frequencia);
            pai.esquerdo = esquerdo;
            pai.direito = direito;
            pq.add(pai);
        }
        HuffmanNode raiz = pq.poll();
        HashMap<Byte, String> codigos = new HashMap<>();
        // CONSTROI OS CODIGOS ATRAVES DA ARVORE CONSTRUIDA
        constroiCodigos(raiz, "", codigos);
        // RETORNA OS CODIGOS DOS CARACTERES
        return codigos;
    }

    // METODO PARA CONSTROI OS CODIGOS ATRAVES DA ARVORE DE HUFFMAN
    private static void constroiCodigos(HuffmanNode no, String codigo, HashMap<Byte, String> codigos) {
        if (no == null) {
            return;
        }
        if (no.esquerdo == null && no.direito == null) {
            codigos.put(no.b, codigo);
        }
        constroiCodigos(no.esquerdo, codigo + "0", codigos);
        constroiCodigos(no.direito, codigo + "1", codigos);
    }

    // METODO PARA FAZER A COMPRESSAO DE HUFFMAN USANDO OS CODIGOS GERADO ANTERIORMENTE
    public static byte[] compressao(byte[] dados, HashMap<Byte, String> codigos) {
       VetorDeBits dadosComprimidos = new VetorDeBits();
       int i = 0;
       // PARA CADA BYTE DE DADOS ORIGINAL, OBTEM O SEU CODIGO
       for (byte b : dados) {
         String codigo = codigos.get(b);
          for (char c : codigo.toCharArray()) {
             if (c == '0') {
                 dadosComprimidos.clear(i++);
             } else {
                 dadosComprimidos.set(i++);
             }
          }
       }
       // RETORNA OS DADOS COMPRIMIDOS
       return dadosComprimidos.toByteArray();
    }

    // METODO PARA DESCOMPRESSAO HUFFMAN
    public static byte[] descompressao(String dadosComprimidos, HashMap<Byte, String> codigos) {
        ByteArrayOutputStream dadosOriginais = new ByteArrayOutputStream();
        StringBuilder codigoAtual = new StringBuilder();
        // VAI MONTANDO OS DADOS ORIGINAIS ATRAVES DOS CODIGOS
        for (int i = 0; i < dadosComprimidos.length(); i++) {
            codigoAtual.append(dadosComprimidos.charAt(i));
            for (byte b : codigos.keySet()) {
                if (codigos.get(b).equals(codigoAtual.toString())) {
                    dadosOriginais.write(b);
                    codigoAtual = new StringBuilder();
                    break;
                }
            }
        }
        // RETORNA A DESCOMPRESSAO
        return dadosOriginais.toByteArray();
    }

    // METODO PARA ESCREVER O MAPA DE CODIGOS EM UM ARQUIVO PARA MANTER GUARDADO
    public static void escreverCodigos(HashMap<Byte, String> mapa, int x) throws IOException {
       try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Dados/CodigosHuffman"+ x +".map"))) {
          oos.writeObject(mapa);
       }
    }

    // METODO PARA LER O MAPA DE CODIGOS DE UM ARQUIVO
    @SuppressWarnings("unchecked")
    public static HashMap<Byte, String> lerCodigos(int numero) throws IOException, ClassNotFoundException {
       try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Dados/CodigosHuffman"+ numero +".map"))) {
          return (HashMap<Byte, String>) ois.readObject();
       }
    }
}