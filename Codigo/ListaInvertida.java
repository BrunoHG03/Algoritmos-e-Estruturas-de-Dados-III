/*
CLASSE LISTA INVERTIDA IMPLEMENTADA COM O MATERIAL DISPONIVEL
DE AEDS III E O CODIGO DISPONIBILIZADO PELO PROFESSOR KUTOVA NO
GITHUB DE AEDS III, FEITO TAMBEM ALGUMAS MODIFICACOES AO LONGO
DE TODO O CODIGO
*/
import java.io.*;
import java.util.*;

// CLASSE LISTA INVERTIDA
public class ListaInvertida{

  // NOMES DE ARQUIVO, ARQUIVOS E A QUANTIDADE DE DADOS POR BLOCO
  String nomeArqDicionario;
  String nomeArquivoBlocos;
  RandomAccessFile arqDicionario;
  RandomAccessFile arqBlocos;
  int quantidadeDadosPorBloco;

  // CLASSE BLOCO
  class Bloco{
    short quantidade; // QUANTIDADE DE ELEMENTOS NO BLOCO
    short qtdMaxima; // QUANTIDADE MAXIMA
    ElementoLista[] elementos; // LISTA DE ELEMENTOS
    long proximo; // PONTEIRO PARA PROXIMO
    short bytesBloco; // BYTES POR BLOCO

    // CONSTRUTOR
    public Bloco(int qtdmax) throws Exception{
      quantidade = 0;
      qtdMaxima = (short) qtdmax;
      elementos = new ElementoLista[qtdMaxima];
      proximo = -1;
      bytesBloco = (short) (2+(4+4)*qtdMaxima+8);  // CALCULANDO BYTES
    }

    // TRANSFORMA ELEMENTOS PARA UM ARRAY DE BYTES
    public byte[] toByteArray() throws IOException{
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(baos);
      dos.writeShort(quantidade);
      int i = 0;
      while(i < quantidade){
        dos.writeInt(elementos[i].getId());
        dos.writeFloat(elementos[i].getFrequencia());
        i++;
      }
      while(i < qtdMaxima){
        dos.writeInt(-1);
        dos.writeFloat(-1);
        i++;
      }
      dos.writeLong(proximo);
      return baos.toByteArray();
    }

    // TRANSFORMA UM ARRAY DE BYTES EM ELEMENTOS
    public void fromByteArray(byte[] ba) throws IOException{
      ByteArrayInputStream bais = new ByteArrayInputStream(ba);
      DataInputStream dis = new DataInputStream(bais);
      quantidade = dis.readShort();
      int i = 0;
      while(i < qtdMaxima){
        elementos[i] = new ElementoLista(dis.readInt(), dis.readFloat());
        i++;
      }
      proximo = dis.readLong();
    }

    // ADICIONA UM ELEMENTO AO BLOCO
    public boolean adicionaBloco(ElementoLista elemento){
      // SE ESTIVER CHEIA NAO ADICIONA
      if(full())
        return false;
      int i = quantidade - 1;
      // INSERINDO O ELEMENTO NA POSICAO ORDENADA
      while(i >= 0 && elemento.getId() < elementos[i].getId()){
        elementos[i + 1] = elementos[i];
        i--;
      }
      i++;
      elementos[i] = elemento;
      quantidade++;
      return true;
    }

    // LEITURA DE UM VALOR NO BLOCO
    public boolean leituraBloco(int id){
      // CONFERE SE ESTA VAZIA
      if(empty())
        return false;
      int i = 0;
      // PROCURA O ID
      while(i < quantidade && id > elementos[i].getId())
        i++;
      // SE ACHOU O ID
      if(i < quantidade && id == elementos[i].getId())
        return true;
      else
        return false;
    }

    // REMOVE UM VALOR DO BLOCO
    public boolean removeBloco(int id){
      // CONFERE SE ESTA VAZIA
      if(empty())
        return false;
      int i = 0;
      // PROCURA O ID
      while(i < quantidade && id > elementos[i].getId())
        i++;
      // SE ACHOU ID
      if(id == elementos[i].getId()){
        // REALOCA OS ID
        while(i < quantidade - 1){
          elementos[i] = elementos[i + 1];
          i++;
        }
        quantidade--;
        return true;
      }else
        return false;
    }

    // ATRIBUI UMA LISTA DE ELEMENTOS
    public ElementoLista[] lista(){
      ElementoLista[] lista = new ElementoLista[quantidade];
      for(int i = 0; i < quantidade; i++)
        lista[i] = elementos[i];
      return lista;
    }

    // METODOS PARA VER SE O BLOCO ESTA VAZIO OU CHEIO
    public boolean empty(){ return quantidade == 0; }
    public boolean full(){ return quantidade == qtdMaxima; }

    // METODOS GETs E SETs
    public long getProximo(){ return proximo; }
    public void setProximo(long p){ proximo = p; }
    public int getBytesBloco(){ return bytesBloco; }
  }

  // CONSTRUTOR DA LISTA INVERTIDA
  public ListaInvertida(int n, String nomeDicionario, String nomeBlocos) throws Exception{
    quantidadeDadosPorBloco = n;
    nomeArqDicionario = nomeDicionario;
    nomeArquivoBlocos = nomeBlocos;
    arqDicionario = new RandomAccessFile("Dados/" + nomeArqDicionario, "rw");
    if(arqDicionario.length() < 4){
      arqDicionario.seek(0);
      arqDicionario.writeInt(0);
    }
    arqBlocos = new RandomAccessFile("Dados/" + nomeArquivoBlocos, "rw");
  }

  // ADICIONAR A LISTA INVERTIDA
  public boolean adicionaLista(String c, ElementoLista e) throws Exception{
    // PERCORRE A LISTA RETORNADA DA CHAVE PARA VER SE JA NAO EXISTE O ID RELACIONADO A CHAVE
    ElementoLista[] lista = buscarLista(c);
    for(int i = 0; i < lista.length; i++)
      if(lista[i].getId() == e.getId())
        return false;
    String chave = "";
    long endereco = -1;
    boolean existe = false;
    // LOCALIZANDO A CHAVE NO DICIONARIO
    arqDicionario.seek(4);
    while(arqDicionario.getFilePointer() != arqDicionario.length()){
      chave = arqDicionario.readUTF();
      endereco = arqDicionario.readLong();
      if(chave.compareTo(c) == 0){
        existe = true;
        break;
      }
    }
    // SE NAO ENCONTROU CRIA SE UM BLOCO PARA A CHAVE
    if(!existe){
      // CRIANDO BLOCO
      Bloco bloco = new Bloco(quantidadeDadosPorBloco);
      endereco = arqBlocos.length();
      arqBlocos.seek(endereco);
      arqBlocos.write(bloco.toByteArray());
      // INSERE A CHAVE NO DICIONARIO
      arqDicionario.seek(arqDicionario.length());
      arqDicionario.writeUTF(c);
      arqDicionario.writeLong(endereco);
    }
    // PECORRE TODOS OS BLOCOS ENCADEADOS NESSE ENDERECO
    Bloco b = new Bloco(quantidadeDadosPorBloco);
    byte[] bd;
    while(endereco != -1){
      long proximo = -1;
      // CARREGA O BLOCO
      arqBlocos.seek(endereco);
      bd = new byte[b.getBytesBloco()];
      arqBlocos.read(bd);
      b.fromByteArray(bd);
      // VENDO SE O DADO CABE NO BLOCO
      if(!b.full()){
        b.adicionaBloco(e);
      } else{
        // AVANCA BLOCO
        proximo = b.getProximo();
        if(proximo == -1){
          // SE NAO EXISTE BLOCO, CRIA UM NOVO
          Bloco b1 = new Bloco(quantidadeDadosPorBloco);
          proximo = arqBlocos.length();
          arqBlocos.seek(proximo);
          arqBlocos.write(b1.toByteArray());
          // ATUALIZA PONTEIRO
          b.setProximo(proximo);
        }
      }
      // ATUALIZA O BLOCO ATUAL
      arqBlocos.seek(endereco);
      arqBlocos.write(b.toByteArray());
      endereco = proximo;
    }
    return true;
  }

  // RETORNA UMA LISTA DOS DADOS DA BUSCA DE UMA CHAVE
  public ElementoLista[] buscarLista(String c) throws Exception{
    ArrayList<ElementoLista> lista = new ArrayList<>();
    String chave = "";
    long endereco = -1;
    boolean existe = false;
    // LOCALIZA CHAVE NO DICIONARIO
    arqDicionario.seek(4);
    while(arqDicionario.getFilePointer() != arqDicionario.length()){
      chave = arqDicionario.readUTF();
      endereco = arqDicionario.readLong();
      if(chave.compareTo(c) == 0){
        existe = true;
        break;
      }
    }
    // SE CHAVE NAO EXISTIR
    if(!existe)
      return new ElementoLista[0];
    // PECORRE TODOS OS BLOCOS ENCADEADOS NESSE ENDERECO
    Bloco b = new Bloco(quantidadeDadosPorBloco);
    byte[] bd;
    while(endereco != -1){
      // CARREGA O BLOCO
      arqBlocos.seek(endereco);
      bd = new byte[b.getBytesBloco()];
      arqBlocos.read(bd);
      b.fromByteArray(bd);
      // ADICIONA CADA VALOR A LISTA
      ElementoLista[] lb = b.lista();
      for(int i = 0; i < lb.length; i++)
        lista.add(lb[i]);
      // AVANCA PARA O PROXIMO BLOCO
      endereco = b.getProximo();
    }
    // CONSTRUINDO UMA LISTA DE RESPOSTAS ORDENADA PELO ID
    lista.sort(null);
    ElementoLista[] resposta = new ElementoLista[lista.size()];
    for(int j = 0; j < lista.size(); j++)
      resposta[j] = (ElementoLista) lista.get(j);
    // RETORNO DAS RESPOSTAS
    return resposta;
  }

  // REMOVE OS DADOS RELACIONADOS A UMA CHAVE MAS NAO APAGA A CHAVE
  public boolean excluirLista(String c, int id) throws Exception{
    String chave = "";
    long endereco = -1;
    boolean existe = false;
    // LOCALIZANDO A CHAVE NO DICIONARIO
    arqDicionario.seek(4);
    while(arqDicionario.getFilePointer() != arqDicionario.length()){
      chave = arqDicionario.readUTF();
      endereco = arqDicionario.readLong();
      if(chave.compareTo(c) == 0){
        existe = true;
        break;
      }
    }
    // SE A CHAVE NAO EXISTE
    if(!existe)
      return false;
    // PECORRE TODOS OS BLOCOS ENCADEADOS NESSE ENDERECO
    Bloco b = new Bloco(quantidadeDadosPorBloco);
    byte[] bd;
    while(endereco != -1){
      // CARREGA O BLOCO
      arqBlocos.seek(endereco);
      bd = new byte[b.getBytesBloco()];
      arqBlocos.read(bd);
      b.fromByteArray(bd);
      // TESTANDO SE O VALOR ESTA NO BLOCO
      if(b.leituraBloco(id)){
        b.removeBloco(id);
        arqBlocos.seek(endereco);
        arqBlocos.write(b.toByteArray());
        return true;
      }
      // AVANCA O BLOCO
      endereco = b.getProximo();
    }
    // CHAVE NAO ENCONTRADA
    return false;
  }
}