/*
CLASSE LZW IMPLEMENTADA COM O MATERIAL DISPONIVEL
DE AEDS III E O CODIGO DISPONIBILIZADO PELO PROFESSOR KUTOVA NO
GITHUB DE AEDS III, FEITO TAMBEM ALGUMAS MODIFICACOES AO LONGO
DE TODO O CODIGO
*/
import java.util.ArrayList;

// CLASSE LZW
public class LZW {
    public static final int BITS_POR_INDICE = 12; // Mínimo de 9 bits por índice (512 itens no dicionário)
    // METODO PARA COMPRESSAO LZW
    public static byte[] compressao(byte[] dados) throws Exception {

        // CRIANDO O DICIONARIO INICIAL COM OS PRIMEIROS 256 VALORES DE BYTES
        ArrayList<ArrayList<Byte>> dicionario = new ArrayList<>();
        ArrayList<Byte> arrayBytes;
        int i, j;
        byte b;
        for (j = -128; j < 128; j++){ 
            b = (byte) j;
            arrayBytes = new ArrayList<>(); // CADA BYTE SERA ADICIONADO NO DICIONARIO COMO UM ARRAY
            arrayBytes.add(b);
            // ADICIONA AO DICIONARIO
            dicionario.add(arrayBytes);
        }
        // ARRAY DE INTEIROS PARA SAIDA
        ArrayList<Integer> saida = new ArrayList<>();
        // CODIFICACAO
        i = 0;
        int indice;
        int ultimoIndice;
        // ENQUANTO HOUVER BYTES
        while (i < dados.length) {
            arrayBytes = new ArrayList<>();
            // ADICIONA O PROXIMO BYTE DOS DADOS AO ARRAY DE BYTES
            b = dados[i];
            arrayBytes.add(b);
            indice = dicionario.indexOf(arrayBytes);
            ultimoIndice = indice;
            // TENTANDO ACRESCENTAR MAIS BYTES AO ARRAY
            while (indice != -1 && i < dados.length - 1) {
                i++;
                b = dados[i];
                arrayBytes.add(b);
                indice = dicionario.indexOf(arrayBytes);
                if (indice != -1)
                    ultimoIndice = indice;
            }
            // ACRESCENTA O ULTIMO INDICE AO ARRAY DE SAIDA
            saida.add(ultimoIndice);
            // ACRESCENTANDO O NOVO ARRAY DE BYTES AO DICIONARIO
            if (dicionario.size() < (Math.pow(2, BITS_POR_INDICE) - 1))
                dicionario.add(arrayBytes);
            // TESTANDO SE OS DADOS ACABARAM
            if (indice != -1 && i == dados.length - 1)
                break;
        }
        // TRANSFORMA O ARRAY DE INDICES COMO UMA SEQUENCIA DE BITS
        VetorDeBits dadosComprimidos = new VetorDeBits(saida.size() * BITS_POR_INDICE);
        int l = saida.size() * BITS_POR_INDICE - 1;
        for (i = saida.size() - 1; i >= 0; i--) {
            int n = saida.get(i);
            for(int k = 0; k < BITS_POR_INDICE; k++){
                if(n % 2 == 0)
                    dadosComprimidos.clear(l);
                else
                    dadosComprimidos.set(l);
                l--;
                n /= 2;
            }
        }
        // RETORNA O VETOR DE BITS
        return dadosComprimidos.toByteArray();
    }

    // METODO PARA DESCOMPRESSAO LZW
    public static byte[] descompressao(byte[] dadosComprimidos) throws Exception {
        // CRIA UM VETOR DE BITS ATRAVES DOS DADOS COMPRIMIDOS
        VetorDeBits bits = new VetorDeBits(dadosComprimidos);
        // TRANSFORMA A SEQUENCIA DE BITS EM UM ARRAY DE INDICES NUMERICOS
        int i, j, k;
        ArrayList<Integer> indices = new ArrayList<>();
        k = 0;
        for (i = 0; i < bits.length()/BITS_POR_INDICE; i++) {
            int n = 0;
            for(j = 0; j < BITS_POR_INDICE; j++) {
                n = n * 2 + (bits.get(k++) ? 1:0);
            }
            indices.add(n);
        }
        ArrayList<Byte> arrayBytes;
        ArrayList<Byte> dados = new ArrayList<>();
        // CRIANDO O DICIONARIO INICIAL COM OS PRIMEIROS 256 VALORES DE BYTES
        ArrayList<ArrayList<Byte>> dicionario = new ArrayList<>();
        byte b;
        for (j = -128, i = 0; j < 128; j++, i++){
            b = (byte) j;
            arrayBytes = new ArrayList<>(); // CADA BYTE SERA ADICIONADO NO DICIONARIO COMO UM ARRAY
            arrayBytes.add(b);
            // ADICIONA AO DICIONARIO
            dicionario.add(arrayBytes);
        }
        // DECODIFICACAO
        ArrayList<Byte> proximoarrayBytes;
        // DECODIFICA OS INDICES
        i = 0;
        while (i < indices.size()) {
            arrayBytes = (ArrayList<Byte>) (dicionario.get(indices.get(i))).clone();
            for (j = 0; j < arrayBytes.size(); j++)
                dados.add(arrayBytes.get(j));
            // ACRESCENTANDO O NOVO ARRAY DE BYTES AO DICIONARIO
            if (dicionario.size() < (Math.pow(2, BITS_POR_INDICE) - 1))
                dicionario.add(arrayBytes);
            i++;
            if (i < indices.size()) {
                proximoarrayBytes = (ArrayList<Byte>) dicionario.get(indices.get(i));
                arrayBytes.add(proximoarrayBytes.get(0));
            }
        }
        // GERA A STRING A PARTIR DO ARRAY DE BYTES
        // CRIA O ARRAY DE BYTES DE SAIDA
        byte[] dadosOriginais = new byte[dados.size()];
        for (i = 0; i < dados.size(); i++)
            dadosOriginais[i] = dados.get(i);
        // RETORNA OS DADOS ORIGINAIS
        return dadosOriginais;
    }
}