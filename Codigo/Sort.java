import java.io.*;
import java.util.*;

// CLASSE SORT
public class Sort{
    public static int NumId;
    // METODO PARA ORDENACAO EXTERNA (FEITO POR MIM)
    public static void ordenar(int caminhos, int registrosPorArq){
        // DIVIDINDO O ARQUIVO
        List<File> arquivosTemporarios = dividirArquivo(caminhos, registrosPorArq);
        // INTERCALANDO OS ARQUIVOS
        File novoArquivo = intercalarArquivos(arquivosTemporarios);
    }

    // METODO PARA DIVIDIR O ARQUIVO (FEITO POR MIM)
    public static List<File> dividirArquivo(int caminhos, int registrosPorArq){
        try{
            List<File> arquivosTemporarios = new ArrayList<>();
            RandomAccessFile arquivo = new RandomAccessFile("Pilotos.db", "r");
            // GUARDANDO CABECALHO
            NumId = arquivo.readInt();
            arquivo.seek(4); // PULANDO O CABECALHO
            // CRIANDO OS ARQUIVOS
            for(int i = 0; i < caminhos; i++) {
                arquivosTemporarios.add(new File("temp" + i + ".db"));
            }
            // LE TODOS OS REGISTROS
            while(arquivo.getFilePointer() < arquivo.length()){
                // FOR PARA O NUMERO DE CAMINHOS
                for(int i = 0; i < caminhos; i++){
                    if(arquivo.getFilePointer() >= arquivo.length()) break;
                    RandomAccessFile temp = new RandomAccessFile(arquivosTemporarios.get(i), "rw");
                    List<Piloto> pilotos = new ArrayList<>();
                    // FOR DO NUMERO DE REGISTROS POR ARQUIVO
                    for(int j = 0; j < registrosPorArq; j++){
                        if (arquivo.getFilePointer() >= arquivo.length()) break;
                        long pos = arquivo.getFilePointer();
                        byte lapide = arquivo.readByte();
                        int tam = arquivo.readInt();
                        // SE NAO TIVER EXCLUIDO
                        if(lapide == 1){
                            if(arquivo.getFilePointer() + tam <= arquivo.length()){ // EVITAR EXCECAO
                                // LENDO PILOTO DO ARQUIVO ORIGINAL
                                byte[] b = new byte[tam];
                                arquivo.readFully(b);
                                Piloto piloto = new Piloto();
                                piloto.fromByteArray(b);
                                pilotos.add(piloto);
                            } else {
                                break;
                            }
                        } else {
                            if(arquivo.getFilePointer() + tam <= arquivo.length()){ 
                                arquivo.seek(arquivo.getFilePointer() + tam); // PULA PARA O PROXIMO REGISTRO
                            } else {
                                break;
                            }
                        }
                    }
                    // ORDENAR ANTES DE COLOCAR NOS ARQUIVOS TEMPORARIOS, POR QUICKSORT
                    quicksort(pilotos, 0, pilotos.size() - 1);
                    // ESCREVENDO NO ARQUIVO TEMPORARIO
                    for(Piloto p : pilotos){
                        byte[] b = p.toByteArray();
                        temp.writeInt(b.length);
                        temp.write(b);
                    }
                    temp.close();
                }
            }
            arquivo.close();
            // RETORNO DOS ARQUIVOS TEMPORARIOS
            return arquivosTemporarios;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // METODO SWAP (REUTILIZADO DO TP02 DE AEDS 02)
    public static void swap(List<Piloto> pilotos, int i, int j){
        Piloto temp = pilotos.get(i);
        pilotos.set(i, pilotos.get(j));
        pilotos.set(j, temp);
    }

    // METODO QUICKSORT POR ID (METODO PIVO NO MEIO) (REUTILIZADO DO TP02 DE AEDS 02)
    public static void quicksort(List<Piloto> pilotos, int esq, int dir) {
        int i = esq, j = dir;
        Piloto pivo = pilotos.get((esq + dir) / 2);
        while (i <= j) {
            while (pilotos.get(i).id < pivo.id) i++;
            while (pilotos.get(j).id > pivo.id) j--;
            if (i <= j) {
                swap(pilotos, i, j);
                i++;
                j--;
            }
        }
        if (esq < j) quicksort(pilotos, esq, j);
        if (i < dir) quicksort(pilotos, i, dir);
    }

    // METODO PARA INTERCALACAO DOS ARQUIVOS (INICIADO POR MIM COM AJUDA DO CHATGPT)
    public static File intercalarArquivos(List<File> arquivosTemporarios) {
        try {
            List<RandomAccessFile> arquivos = new ArrayList<>();
            // ABRINDO ARQUIVOS
            for (File tempFile : arquivosTemporarios) {
                arquivos.add(new RandomAccessFile(tempFile, "r"));
            }
            File arquivoFinal = new File("Pilotos_ordenados.db");
            RandomAccessFile arquivoDeSaida = new RandomAccessFile(arquivoFinal, "rw");
            arquivoDeSaida.writeInt(NumId);
            // FILA PRIORIDADE
            PriorityQueue<Registro> fp = new PriorityQueue<>(Comparator.comparing(r -> r.piloto.getId()));
            // PRIMEIRO REGISTRO DE CADA ARQUIVO
            for(int i = 0; i < arquivos.size(); i++){
                if (arquivos.get(i).getFilePointer() < arquivos.get(i).length()){
                    int tam = arquivos.get(i).readInt();
                    byte[] b = new byte[tam];
                    arquivos.get(i).readFully(b);
                    Piloto piloto = new Piloto();
                    piloto.fromByteArray(b);
                    fp.add(new Registro(piloto, i));
                }
            }
            // INTERCALANDO OS REGISTROS E ESCREVENDO NO ARQUIVO FINAL
            while (!fp.isEmpty()) {
                Registro minRegistro = fp.poll();
                Piloto piloto = minRegistro.piloto;
                byte[] b = piloto.toByteArray();
                arquivoDeSaida.writeInt(b.length);
                arquivoDeSaida.write(b);
                //LE O PROXIMO REGISTRO DE ONDE VEIO O MENOR
                if (arquivos.get(minRegistro.indice).getFilePointer() + 4 <= arquivos.get(minRegistro.indice).length()){
                   int tam = arquivos.get(minRegistro.indice).readInt();
                   if (arquivos.get(minRegistro.indice).getFilePointer() + tam <= arquivos.get(minRegistro.indice).length()){
                      byte[] b2 = new byte[tam];
                      arquivos.get(minRegistro.indice).readFully(b2);
                      Piloto novoPiloto = new Piloto();
                      novoPiloto.fromByteArray(b2);
                      fp.add(new Registro(novoPiloto, minRegistro.indice));
                   }
                }
            }
            // FECHANDO OS ARQUIVOS
            for (RandomAccessFile arq : arquivos) {
                arq.close();
            }
            arquivoDeSaida.close();
            // RETORNA O ARQUIVO FINAL
            return arquivoFinal;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // CLASSE REGISTRO
    public static class Registro{
        Piloto piloto;
        int indice;
        // CONSTRUTOR
        Registro(Piloto piloto, int indice){
            this.piloto = piloto;
            this.indice = indice;
        }
    }
}