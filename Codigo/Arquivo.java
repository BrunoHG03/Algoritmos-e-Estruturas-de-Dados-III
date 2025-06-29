import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// CLASSE ARQUIVO
public class Arquivo {
    public static int cripto;
    public static Vigenere vigenere;
    public static RSA rsa = new RSA();
    // METODO PARA FAZER A LEITURA DO CSV DE PILOTOS (REUTILIZADO DO TP02 DE AEDS 02)
    public void fazerCarga(){
    // ACESSANDO ARQUIVO CSV
    try{
       FileInputStream fis = new FileInputStream("pilotos.csv");
       InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
       BufferedReader br = new BufferedReader(isr);
       br.readLine();
       String linha = new String();
       // LENDO LINHAS ATE O FIM DO ARQUIVO
       while((linha = br.readLine()) != null)
       {
           if(linha != null)
           {
         /*  - FAZENDO SPLIT NAS VIRGULAS PARA DIVIDIR OS DADOS
             - TIRANDO OS ESPAÇOS EM BRANCO COM .trim()
             - CONVERTENDO OS VALORES DE STRING PARA SEUS RESPECTIVOS TIPOS  */
             Piloto piloto = new Piloto();
             String[] dados = linha.split(";");
             // ID
             piloto.setId(Integer.parseInt(dados[0].trim()));
             // NOME
             piloto.setNome(dados[1].trim());
             // SIGLA
             String sigla = dados[2].trim();
             piloto.setSigla(new char[]{sigla.charAt(0), sigla.charAt(1), sigla.charAt(2)});
             // PAIS
             piloto.setPais(dados[3].trim());
             // TITULOS
             piloto.setTitulos(Integer.parseInt(dados[4].trim()));
             // CORRIDAS
             piloto.setCorridas(Integer.parseInt(dados[5].trim()));
             // POLES
             piloto.setPoles(Integer.parseInt(dados[6].trim()));
             // VITORIAS
             piloto.setVitorias(Integer.parseInt(dados[7].trim()));
             // PODIOS
             piloto.setPodios(Integer.parseInt(dados[8].trim()));
             // VOLTAS MAIS RAPIDAS
             piloto.setVoltasRapidas(Integer.parseInt(dados[9].trim()));
             // PONTOS
             piloto.setPontos(Double.parseDouble(dados[10].trim()));
             // EQUIPES
             String[] equipes = dados[11].trim().split(",");
             // SE RECEBER UMA STRING VAZIA PARA EQUIPES
             if(equipes[0].isEmpty()){
                piloto.addEquipe("Não Disponível");
             } else {
                // ADICIONANDO TODAS AS EQUIPES
                for(int i = 0; i < equipes.length; i++){
                  piloto.addEquipe(equipes[i].trim());
                  equipes[i] = null;
                }
             }
             // DATA DE NASCIMENTO
             DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
             piloto.setNascimento(LocalDate.parse(dados[12].trim(), formato));
             // FUNCAO
             piloto.setFuncao(dados[13].trim());
             // ADICIONANDO OS PILOTOS
             adicionarPiloto(piloto);
           }
        }
        // FECHANDO ARQUIVO
        br.close();
       } catch (Exception e) {
          e.printStackTrace();
       }
    }

    // METODOS A SEGUIR FEITOS A PARTIR DOS VIDEOS DE AEDS III NO CANVAS
    // METODO PARA ADICIONAR UM PILOTO
    public void adicionarPiloto(Piloto piloto){
       try{
          // ABRINDO ARQUIVO
          RandomAccessFile arquivo = new RandomAccessFile("Pilotos.db", "rw");
          byte[] b;
          ListaInvertida lista = new ListaInvertida(4, "dicionarioLista.db", "blocosLista.db");
          ListaInvertida lista2 = new ListaInvertida(4, "dicionarioLista2.db", "blocosLista2.db");
          String[] palavras;
          String[] palavras2;
          // SE O ARQUIVO TIVER VAZIO ESCREVE 0 COMO ULTIMO ID INSERIDO
          if(arquivo.length() == 0)
            arquivo.writeInt(0);
          // VOLTA PARA O INICIO, LE O ULTIMO ID INSERIDO E COLOCA O ID DO PILOTO A SER ADICIONADO
          arquivo.seek(0);
          piloto.setId(arquivo.readInt() + 1);
          // VOLTA PARA O INICIO E ESCREVE O ID DO NOVO PILOTO
          arquivo.seek(0);
          arquivo.writeInt(piloto.id);
          // PEGA AS PALAVRAS PARA LISTA
          palavras = piloto.nome.split(" ");
          palavras2 = piloto.pais.split(" ");
          // CRIPTOGRAFA NOME SE NECESSARIO
          if(cripto == 1)
             piloto.setNome(vigenere.criptografar(piloto.getNome()));
          else if(cripto == 2)
             piloto.setNome(rsa.criptografar(piloto.getNome()));
          // CONVERTE O PILOTO PARA UM ARRAY DE BYTES
          b = piloto.toByteArray();
          // PONTEIRO VAI PARA O FIM DO ARQUIVO
          arquivo.seek(arquivo.length());
          // ESCREVE A LAPIDE, O TAMANHO DO REGISTRO E POR ULTIMO O REGISTRO
          arquivo.writeByte(1);
          arquivo.writeInt(b.length);
          arquivo.write(b);
          // ADICIONANDO DADOS AS LISTAS INVERTIDAS
          for(int i = 0; i < palavras.length; i++){
            lista.adicionaLista(palavras[i], new ElementoLista(piloto.id, 1));
          }
          for(int i = 0; i < palavras2.length; i++){
            lista2.adicionaLista(palavras2[i], new ElementoLista(piloto.id, 1));
          }
          // FECHA O ARQUIVO
          arquivo.close();
       } catch (Exception e) {
         e.printStackTrace();
       }
    }

    // METODO PARA BUSCAR UM PILOTO POR ID
    public Piloto buscarPilotoId(int id){
       try{
          Piloto piloto = new Piloto();
          // ABRINDO ARQUIVO
          RandomAccessFile arquivo = new RandomAccessFile("Pilotos.db", "r");
          int tam;
          long pos;
          byte[] b;
          byte lapide;
          // PONTEIRO VAI PARA UMA POSIÇÃO APÓS O CABEÇALHO
          arquivo.seek(4);
          // LE TODOS OS REGISTROS
          while(arquivo.getFilePointer() < arquivo.length()){
              // GUARDA A POSICAO DO INICIO DO REGISTRO E LE A LAPIDE
              pos = arquivo.getFilePointer();
              lapide = arquivo.readByte();
              if(arquivo.getFilePointer() + 4 > arquivo.length()) break; // EVITAR EXCECAO
                 // LE O TAMANHO DO REGISTRO
                 tam = arquivo.readInt();
              // SE LAPIDE NAO ESTIVER COMO EXCLUIDA
              if(lapide == 1) {
                  // LE O CONTEUDO DO REGISTRO
                  if(arquivo.getFilePointer() + tam > arquivo.length()) break; // EVITAR EXCECAO
                     b = new byte[tam];
                  arquivo.readFully(b);
                  piloto.fromByteArray(b);
                  // SE O ID FOR O ID BUSCADO
                  if(piloto.id == id) {
                      arquivo.close();
                      System.out.println();
                      if(cripto == 1)
                         piloto.setNome(vigenere.descriptografar(piloto.getNome()));
                      else if(cripto == 2)
                         piloto.setNome(rsa.descriptografar(piloto.getNome()));
                      return piloto; // RETORNA O PILOTO BUSCADO
                  }
               } else {
                  // PULA A LAPIDE, O TAMANHO DO REGISTRO E O REGISTRO
                  arquivo.seek(pos + 1 + 4 + tam);
               }
          }
          // FECHA ARQUIVO
          arquivo.close();
       } catch(Exception e) {
          e.printStackTrace();
       }
       // SE PILOTO NÃO FOR ENCONTRADO RETORNA UM PILOTO DE ID = 0
       return new Piloto();
    }

    // METODO PARA BUSCAR UM PILOTO POR ID
    public Piloto buscarPilotoNome(String nome){
      try{
         Piloto piloto = new Piloto();
         // ABRINDO ARQUIVO
         RandomAccessFile arquivo = new RandomAccessFile("Pilotos.db", "r");
         int tam;
         long pos;
         byte[] b;
         byte lapide;
         // PONTEIRO VAI PARA UMA POSIÇÃO APÓS O CABEÇALHO
         arquivo.seek(4);
         // LE TODOS OS REGISTROS
         while(arquivo.getFilePointer() < arquivo.length()){
             // GUARDA A POSICAO DO INICIO DO REGISTRO E LE A LAPIDE
             pos = arquivo.getFilePointer();
             lapide = arquivo.readByte();
             if(arquivo.getFilePointer() + 4 > arquivo.length()) break; // EVITAR EXCECAO
                // LE O TAMANHO DO REGISTRO
                tam = arquivo.readInt();
             // SE LAPIDE NAO ESTIVER COMO EXCLUIDA
             if(lapide == 1) {
                 // LE O CONTEUDO DO REGISTRO
                 if(arquivo.getFilePointer() + tam > arquivo.length()) break; // EVITAR EXCECAO
                    b = new byte[tam];
                 arquivo.readFully(b);
                 piloto.fromByteArray(b);
                 if(cripto == 1)
                     piloto.setNome(vigenere.descriptografar(piloto.getNome()));
                 else if(cripto == 2)
                     piloto.setNome(rsa.descriptografar(piloto.getNome()));
                 // SE O ID FOR O ID BUSCADO
                 if(piloto.nome.equals(nome)) {
                     arquivo.close();
                     return piloto; // RETORNA O PILOTO BUSCADO
                 }
              } else {
                 // PULA A LAPIDE, O TAMANHO DO REGISTRO E O REGISTRO
                 arquivo.seek(pos + 1 + 4 + tam);
              }
         }
         // FECHA ARQUIVO
         arquivo.close();
      } catch(Exception e) {
         e.printStackTrace();
      }
      // SE PILOTO NÃO FOR ENCONTRADO RETORNA UM PILOTO DE ID = 0
      return new Piloto();
   }

   // METODO PARA BUSCAR UM PILOTO POR ID
   public static ArrayList<Piloto> buscarPilotosEquipe(String equipe){
    try{
       // ABRINDO ARQUIVO
       RandomAccessFile arquivo = new RandomAccessFile("Pilotos.db", "r");
       ArrayList<Piloto> pilotos = new ArrayList<>();
       int tam;
       long pos;
       byte[] b;
       byte lapide;
       // PONTEIRO VAI PARA UMA POSIÇÃO APÓS O CABEÇALHO
       arquivo.seek(4);
       // LE TODOS OS REGISTROS
       while(arquivo.getFilePointer() < arquivo.length()){
           // GUARDA A POSICAO DO INICIO DO REGISTRO E LE A LAPIDE
           pos = arquivo.getFilePointer();
           lapide = arquivo.readByte();
           if(arquivo.getFilePointer() + 4 > arquivo.length()) break; // EVITAR EXCECAO
              // LE O TAMANHO DO REGISTRO
              tam = arquivo.readInt();
           // SE LAPIDE NAO ESTIVER COMO EXCLUIDA
           if(lapide == 1) {
               // LE O CONTEUDO DO REGISTRO
               if(arquivo.getFilePointer() + tam > arquivo.length()) break; // EVITAR EXCECAO
                  b = new byte[tam];
               arquivo.readFully(b);
               Piloto piloto = new Piloto();
               piloto.fromByteArray(b);
               // SE O ID FOR O ID BUSCADO
               for(int i = 0; i < piloto.equipes.size(); i++){
                 if(piloto.equipes.get(i).trim().equals(equipe)) {
                     if(cripto == 1)
                         piloto.setNome(vigenere.descriptografar(piloto.getNome()));
                      else if(cripto == 2)
                         piloto.setNome(rsa.descriptografar(piloto.getNome()));
                     pilotos.add(piloto);
                     i = 500;
                 }
               }
            } else {
               // PULA A LAPIDE, O TAMANHO DO REGISTRO E O REGISTRO
               arquivo.seek(pos + 1 + 4 + tam);
            }
       }
       // FECHA ARQUIVO
       arquivo.close();
       return pilotos;
    } catch(Exception e) {
       e.printStackTrace();
    }
    // SE PILOTO NÃO FOR ENCONTRADO RETORNA UM PILOTO DE ID = 0
    return new ArrayList<>();
 }

 // METODO PARA BUSCAR PILOTOS POR SIGLA
 public ArrayList<Piloto> buscarPilotosSigla(String sigla){
   try{
      // ABRINDO ARQUIVO
      RandomAccessFile arquivo = new RandomAccessFile("Pilotos.db", "r");
      ArrayList<Piloto> pilotos = new ArrayList<>();
      int tam;
      long pos;
      byte[] b;
      byte lapide;
      // PONTEIRO VAI PARA UMA POSIÇÃO APÓS O CABEÇALHO
      arquivo.seek(4);
      // LE TODOS OS REGISTROS
      while(arquivo.getFilePointer() < arquivo.length()){
          // GUARDA A POSICAO DO INICIO DO REGISTRO E LE A LAPIDE
          pos = arquivo.getFilePointer();
          lapide = arquivo.readByte();
          if(arquivo.getFilePointer() + 4 > arquivo.length()) break; // EVITAR EXCECAO
             // LE O TAMANHO DO REGISTRO
             tam = arquivo.readInt();
          // SE LAPIDE NAO ESTIVER COMO EXCLUIDA
          if(lapide == 1) {
              // LE O CONTEUDO DO REGISTRO
              if(arquivo.getFilePointer() + tam > arquivo.length()) break; // EVITAR EXCECAO
                 b = new byte[tam];
              arquivo.readFully(b);
              Piloto piloto = new Piloto();
              piloto.fromByteArray(b);
              // SE ACHOU A SIGLA BUSCADA ADICIONA NA LISTA
              if(piloto.sigla[0] == sigla.charAt(0) && piloto.sigla[1] == sigla.charAt(1) && piloto.sigla[2] == sigla.charAt(2)){
                  if(cripto == 1)
                     piloto.setNome(vigenere.descriptografar(piloto.getNome()));
                  else if(cripto == 2)
                     piloto.setNome(rsa.descriptografar(piloto.getNome()));
                  pilotos.add(piloto);
              }
           } else {
              // PULA A LAPIDE, O TAMANHO DO REGISTRO E O REGISTRO
              arquivo.seek(pos + 1 + 4 + tam);
           }
      }
      // FECHA ARQUIVO
      arquivo.close();
      // RETORNO DA LISTA DE PILOTOS
      return pilotos;
   } catch(Exception e) {
      e.printStackTrace();
   }
   // SE PILOTO NÃO FOR ENCONTRADO RETORNA UMA LISTA COM PILOTO DE ID = 0
   return new ArrayList<>();
}

    // METODO PARA EXCLUIR UM PILOTO POR ID
    public boolean excluirPiloto(int id){
      try{
         // ABRINDO ARQUIVO
         RandomAccessFile arquivo = new RandomAccessFile("Pilotos.db", "rw");
         Piloto piloto = new Piloto();
         long pos = 0;
         int tam = 0;
         byte[] b;
         byte lapide;
         // PONTEIRO VAI PARA UMA POSICAO APOS O CABECALHO
         arquivo.seek(4);
         // LE TODOS OS REGISTROS
         while(arquivo.getFilePointer() < arquivo.length()){
            // GUARDA A POSICAO DO PONTEIRO
            pos = arquivo.getFilePointer();
            // LE A LAPIDE E O TAMANHO DO REGISTRO
            lapide = arquivo.readByte();
            tam = arquivo.readInt();
            // SE LAPIDE MARCAR COMO NAO EXCLUIDO
            if(lapide == 1){
               // LE O REGISTRO E TRANSFORMA EM PILOTO
               b = new byte[tam];
               arquivo.readFully(b);
               piloto.fromByteArray(b);
               // SE O ID FOR O ID A SER EXCLUIDO
               if(piloto.id == id){
                  // VOLTA PARA O INICIO DO REGISTRO, MARCA A LAPIDE E FECHA O ARQUIVO
                  arquivo.seek(pos);
                  arquivo.writeByte(0);
                  arquivo.close();
                  // RETORNA TRUE PARA A EXCLUSAO
                  return true;
               }
            } else {
               // PULA A LAPIDE, O TAMANHO DO REGISTRO E O REGISTRO
               arquivo.seek(pos + 1 + 4 + tam);
            }
         }
         // FECHA O ARQUIVO
         arquivo.close();
      } catch(Exception e) {
         e.printStackTrace();
      }
      // RETORNA FALSE PARA EXCLUSAO
      return false;
    }

    // METODO PARA ATUALIZAR O PILOTO
    public boolean atualizarPiloto(Piloto piloto){
      try{
         // ABRINDO ARQUIVO
         RandomAccessFile arquivo = new RandomAccessFile("Pilotos.db", "rw");
         Piloto piloto2 = new Piloto();
         long pos = 0;
         int tam = 0;
         byte[] b;
         byte[] b2;
         byte lapide;
         // PONTEIRO VAI PARA UMA POSICAO APOS O CABECALHO
         arquivo.seek(4);
         // LE TODOS OS REGISTROS
         while(arquivo.getFilePointer() < arquivo.length()){
            // GUARDA A POSICAO DO PONTEIRO, LE A LAPIDE E O TAMANHO DO REGISTRO
            pos = arquivo.getFilePointer();
            lapide = arquivo.readByte();
            tam = arquivo.readInt();
            // SE LAPIDE MARCAR COMO NAO EXCLUIDO
            if(lapide == 1){
               // LE O REGISTRO E TRANSFORMA EM PILOTO
               b = new byte[tam];
               arquivo.readFully(b);
               piloto2.fromByteArray(b);
               // SE O ID DO REGISTRO E DO NOVO OBJETO FOREM IGUAIS
               if(piloto2.id == piloto.id){
                  // CONVERTE O PILOTO PARA UM ARRAY DE BYTES
                  b2 = piloto.toByteArray();
                  /* SE O TAMANHO DO NOVO OBJETO FOR MENOR, VOLTA PARA A POSICAO GUARDADA, 
                  PULA A LAPIDE E ESCREVE O TAMANHO DO REGISTRO E O REGISTRO */
                  if(tam > b2.length){
                     arquivo.seek(pos+1);
                     arquivo.writeInt(b2.length);
                     arquivo.write(b2);
                  /* SE O TAMANHO DO NOVO OBJETO FOR O MESMO DO ANTIGO, VOLTA PARA A POSICAO GUARDADA,
                  PULA A LAPIDE E O INDICADOR DO TAMANHO DO REGISTRO E ESCREVE O REGISTRO NOVO */
                  } else if(tam == b2.length){
                     arquivo.seek(pos+5);
                     arquivo.write(b2);
                  /* SE O TAMANHO DO NOVO OBJETO FOR MAIOR, VOLTA PARA A POSICAO GUARDADA, 
                  MARCA A LAPIDE COMO EXCLUIDA, VAI PARA O FIM DO ARQUIVO E ESCREVE O REGISTRO COM SUA
                  LAPIDE E SEU TAMANHO NO INICIO */
                  } else {
                     arquivo.seek(pos);
                     arquivo.writeByte(0);
                     arquivo.seek(arquivo.length());
                     arquivo.writeByte(1);
                     arquivo.writeInt(b2.length);
                     arquivo.write(b2);
                  }
                  // RETORNA TRUE SE A ATUALIZACAO FOR CONCLUIDA
                  arquivo.close();
                  return true;
               }
            } else {
               // PULA A LAPIDE, O TAMANHO DO REGISTRO E O REGISTRO
               arquivo.seek(pos + 1 + 4 + tam);
            }
         }
         // FECHA O ARQUIVO
         arquivo.close();
      } catch (Exception e) {
         e.printStackTrace();
      }
      // RETORNA FALSE SE A ATUALIZACAO NAO FOR CONCLUIDA
      return false;
    }

    // METODO PARA LER TODOS OS PILOTOS DO ARQUIVO
    public List<Piloto> lerTodosPilotos(){
       try{
          List <Piloto> pilotos = new ArrayList<>();
          // ABRINDO ARQUIVO
          RandomAccessFile arquivo = new RandomAccessFile("Pilotos.db", "r");
          int tam;
          long pos;
          byte[] b;
          byte lapide;
          // PONTEIRO VAI PARA UMA POSIÇÃO APÓS O CABEÇALHO
          arquivo.seek(4);
          // LE TODOS OS REGISTROS
          while(arquivo.getFilePointer() < arquivo.length()){
              // GUARDA A POSICAO DO INICIO DO REGISTRO E LE A LAPIDE
              pos = arquivo.getFilePointer();
              lapide = arquivo.readByte();
              if(arquivo.getFilePointer() + 4 > arquivo.length()) break; // EVITAR EXCECAO
                 // LE O TAMANHO DO REGISTRO
                 tam = arquivo.readInt();
              // SE LAPIDE NAO ESTIVER COMO EXCLUIDA
              if(lapide == 1) {
                  // LE O CONTEUDO DO REGISTRO
                  if(arquivo.getFilePointer() + tam > arquivo.length()) break; // EVITAR EXCECAO
                     b = new byte[tam];
                  arquivo.readFully(b);
                  Piloto piloto = new Piloto();
                  piloto.fromByteArray(b);
                  // ADICIONA O PILOTO NA LISTA DE RETORNO
                  pilotos.add(piloto);
               } else {
                  // PULA A LAPIDE, O TAMANHO DO REGISTRO E O REGISTRO
                  arquivo.seek(pos + 1 + 4 + tam);
               }
          }
          // FECHA ARQUIVO
          arquivo.close();
          // RETORNA A LISTA
          return pilotos;
       } catch(Exception e) {
          e.printStackTrace();
       }
       // SE NÃO HOUVER PILOTOS RETORNA UMA LISTA VAZIA
       return new ArrayList<>();
    }

    // METODOS GET E SET DA CRIPTOGRAFIA
    public void setCripto(int cripto){ this.cripto = cripto; }
    public int getCripto(){ return this.cripto; }
}

