/*
CLASSE HASH EXTENSIVEL IMPLEMENTADA COM O MATERIAL DISPONIVEL
DE AEDS III E O CODIGO DISPONIBILIZADO PELO PROFESSOR KUTOVA NO
GITHUB DE AEDS III, FEITO TAMBEM ALGUMAS MODIFICACOES AO LONGO
DE TODO O CODIGO
*/
import java.io.*;
import java.util.ArrayList;
import java.lang.reflect.Constructor;

// CLASSE HASH EXTENSIVEL
public class HashExtensivel<T extends RegistroHashExtensivel<T>>{
  String nomeArqDiretorio;
  String nomeArqCestos;
  RandomAccessFile arqDiretorio;
  RandomAccessFile arqCestos;
  int qntdDadosPorCesto;
  Diretorio diretorio;
  Constructor<T> construtor;
  // CLASSE CESTO
  public class Cesto{
    Constructor<T> construtor;
    short qntdMaxima;
    short bytesElemento;
    short bytesCesto;
    byte profundidadeLocal;
    short quantidade;
    ArrayList<T> elementos;

    // CONSTRUTOR 01
    public Cesto(Constructor<T> ct, int qtdmax) throws Exception{
      this(ct, qtdmax, 0);
    }

    // CONSTRUTOR 02
    public Cesto(Constructor<T> ct, int qtdmax, int pl) throws Exception{
      construtor = ct;
      if(qtdmax > 32767)
        throw new Exception("Quantidade máxima de 32.767 elementos");
      if(pl > 127)
        throw new Exception("Profundidade local máxima de 127 bits");
      profundidadeLocal =(byte) pl;
      quantidade = 0;
      qntdMaxima =(short) qtdmax;
      elementos = new ArrayList<>(qntdMaxima);
      bytesElemento = ct.newInstance().getTamanho();
      bytesCesto =(short)(bytesElemento * qntdMaxima + 3);
    }

    // CONVERTE PARA UM ARRAY DE BYTES
    public byte[] toByteArray() throws Exception{
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(baos);
      dos.writeByte(profundidadeLocal);
      dos.writeShort(quantidade);
      int i = 0;
      while(i < quantidade){
        dos.write(elementos.get(i).toByteArray());
        i++;
      }
      byte[] vazio = new byte[bytesElemento];
      while(i < qntdMaxima){
        dos.write(vazio);
        i++;
      }
      return baos.toByteArray();
    }

    // CONVERTE DE UM ARRAY DE BYTES
    public void fromByteArray(byte[] ba) throws Exception{
      ByteArrayInputStream bais = new ByteArrayInputStream(ba);
      DataInputStream dis = new DataInputStream(bais);
      profundidadeLocal = dis.readByte();
      quantidade = dis.readShort();
      int i = 0;
      elementos = new ArrayList<>(qntdMaxima);
      byte[] dados = new byte[bytesElemento];
      T elem;
      while(i < qntdMaxima){
        dis.read(dados);
        elem = construtor.newInstance();
        elem.fromByteArray(dados);
        elementos.add(elem);
        i++;
      }
    }

    // METODO PARA CRIAR UM CESTO
    public boolean criarCesto(T elem){
      // VERIFICA SE ESTA CHEIO
      if(full())
        return false;
      int i = quantidade - 1;
      // ENCONTRA A POSICAO
      while(i >= 0 && elem.hashCode() < elementos.get(i).hashCode())
        i--;
      // ADICIONA O ELEMENTO NA POSIÇÃO CORRETA
      elementos.add(i + 1, elem);
      quantidade++;
      return true;
    }

    // METODO PARA BUSCAR UM ELEMENTO NO CESTO
    public T buscarCesto(int chave){
      // VERIFICA SE ESTA VAZIO
      if(empty())
        return null;
      int i = 0;
      // BUSCA A POSICAO
      while(i < quantidade && chave > elementos.get(i).hashCode())
        i++;
      if(i < quantidade && chave == elementos.get(i).hashCode())
        return elementos.get(i);
      else
        return null;
    }

    // METODO PARA ATUALIZAR UM ELEMENTO DO CESTO
    public boolean atualizarCesto(T elem){
      // VERIFICA SE ESTA VAZIO
      if(empty())
        return false;
      int i = 0;
      // BUSCA O ELEMENTO
      while(i < quantidade && elem.hashCode() > elementos.get(i).hashCode())
        i++;
      if(i < quantidade && elem.hashCode() == elementos.get(i).hashCode()){
        // SUBSTITUI O ELEMENTO ANTIGO PELO NOVO
        elementos.set(i, elem);
        return true;
      } else
        return false;
    }

    // METODO PARA EXCLUIR ELEMENTO DO CESTO
    public boolean excluirCesto(int chave){
      // VERIFICA SE ESTA VAZIO
      if(empty())
        return false;
      int i = 0;
      // BUSCA O ELEMENTO
      while(i < quantidade && chave > elementos.get(i).hashCode())
        i++;
      if(chave == elementos.get(i).hashCode()){
        // REMOVE O ELEMENTO
        elementos.remove(i);
        quantidade--;
        return true;
      } else
        return false;
    }

    // METODOS PARA VER SE O CESTOS ESTA VAZIO OU CHEIO
    public boolean empty(){ return quantidade == 0; }
    public boolean full(){ return quantidade == qntdMaxima; }
    // METODO PARA VER O TAMANHO DO CESTO
    public int tamanho(){ return bytesCesto; }
  }

  // CLASSE DIRETORIO
  protected class Diretorio{
    byte profundidadeGlobal;
    long[] enderecos;
    // CONSTRUTOR
    public Diretorio(){
      profundidadeGlobal = 0;
      enderecos = new long[1];
      enderecos[0] = 0;
    }

    // METODO PARA ATUALIZAR ENDERECO
    public boolean atualizaEndereco(int p, long e){
      if(p > Math.pow(2, profundidadeGlobal))
        return false;
      enderecos[p] = e;
      return true;
    }

    // CONVERTE PARA UM ARRAY DE BYTES
    public byte[] toByteArray() throws IOException{
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(baos);
      dos.writeByte(profundidadeGlobal);
      int quantidade =(int) Math.pow(2, profundidadeGlobal);
      int i = 0;
      while(i < quantidade){
        dos.writeLong(enderecos[i]);
        i++;
      }
      return baos.toByteArray();
    }

    // CONVERTE DE UM ARRAY DE BYTES
    public void fromByteArray(byte[] ba) throws IOException{
      ByteArrayInputStream bais = new ByteArrayInputStream(ba);
      DataInputStream dis = new DataInputStream(bais);
      profundidadeGlobal = dis.readByte();
      int quantidade =(int) Math.pow(2, profundidadeGlobal);
      enderecos = new long[quantidade];
      int i = 0;
      while(i < quantidade){
        enderecos[i] = dis.readLong();
        i++;
      }
    }

    // RETORNA ENDERECO
    protected long endereço(int p){
      if(p > Math.pow(2, profundidadeGlobal))
        return -1;
      return enderecos[p];
    }

    // METODO PARA DUPLICAR DIRETORIO
    protected boolean duplica(){
      if(profundidadeGlobal == 127)
        return false;
      // AUMENTA PROFUNDIDADE GLOBAL
      profundidadeGlobal++;
      int q1 =(int) Math.pow(2, profundidadeGlobal - 1);
      int q2 =(int) Math.pow(2, profundidadeGlobal);
      long[] novosEnderecos = new long[q2];
      int i = 0;
      // COPIA ENDERECOS PARA PRIMEIRA METADE
      while(i < q1){ 
        novosEnderecos[i] = enderecos[i];
        i++;
      }
      // DUPLICA OS ENDERECOS PARA SEGUNDA METADE
      while(i < q2){ 
        novosEnderecos[i] = enderecos[i - q1];
        i++;
      }
      enderecos = novosEnderecos;
      return true;
    }

    // HASH PARA VER ONDE O ENDERECO DEVE SER INSERIDO K % 2 ^ p
    protected int hash(int chave){
      return Math.abs(chave) %(int) Math.pow(2, profundidadeGlobal);
    }

    // HASH AUXILIAR PARA ATUALIZAR ENDERECO AO DUPLICAR DIRETORIO K % 2 ^ p
    protected int hash2(int chave, int pl){ 
      return Math.abs(chave) %(int) Math.pow(2, pl);
    }

  }

  // CONSTRUTOR DO HASH EXTENSIVEL
  public HashExtensivel(Constructor<T> ct, int n, String nd, String nc) throws Exception{
    construtor = ct;
    qntdDadosPorCesto = n;
    nomeArqDiretorio = nd;
    nomeArqCestos = nc;
    arqDiretorio = new RandomAccessFile(nomeArqDiretorio, "rw");
    arqCestos = new RandomAccessFile(nomeArqCestos, "rw");
    // SE DIRETORIO E CESTOS TIVEREM VAZIOS
    if(arqDiretorio.length() == 0 || arqCestos.length() == 0){
      // CRIA UM NOVO DIRETORIO
      diretorio = new Diretorio();
      byte[] bd = diretorio.toByteArray();
      arqDiretorio.write(bd);
      // CRIA UM CESTO VAZIO
      Cesto c = new Cesto(construtor, qntdDadosPorCesto);
      bd = c.toByteArray();
      arqCestos.seek(0);
      arqCestos.write(bd);
    }
  }

  // METODO PARA ADICIONAR ELEMENTO NO HASH EXTENSIVEL
  public boolean adicionaHash(T elem) throws Exception{
    // CARREGA DIRETORIO PARA A MEMORIA
    byte[] bd = new byte[(int) arqDiretorio.length()];
    arqDiretorio.seek(0);
    arqDiretorio.read(bd);
    diretorio = new Diretorio();
    diretorio.fromByteArray(bd);
    // IDENTIFICA O HASH DO DIRETORIO
    int i = diretorio.hash(elem.hashCode());
    // RECUPERA O CESTO
    long enderecoCesto = diretorio.endereço(i);
    Cesto c = new Cesto(construtor, qntdDadosPorCesto);
    byte[] ba = new byte[c.tamanho()];
    arqCestos.seek(enderecoCesto);
    arqCestos.read(ba);
    c.fromByteArray(ba);
    // TESTA PARA VER SE O ID JA NAO EXISTE NO CESTO
    if(c.buscarCesto(elem.hashCode()) != null)
      throw new Exception("Elemento já existe");
    // TESTA SE O CESTO ESTA CHEIO
    if(!c.full()){
      // INSERE CHAVE NO CESTO E O ATUALIZA
      c.criarCesto(elem);
      arqCestos.seek(enderecoCesto);
      arqCestos.write(c.toByteArray());
      return true;
    }
    // DUPLICA DIRETORIO
    byte pl = c.profundidadeLocal;
    if(pl >= diretorio.profundidadeGlobal)
      diretorio.duplica();
    byte pg = diretorio.profundidadeGlobal;
    // CRIA OS NOVOS CESTOS
    Cesto c1 = new Cesto(construtor, qntdDadosPorCesto, pl + 1);
    arqCestos.seek(enderecoCesto);
    arqCestos.write(c1.toByteArray());
    Cesto c2 = new Cesto(construtor, qntdDadosPorCesto, pl + 1);
    long novoEndereco = arqCestos.length();
    arqCestos.seek(novoEndereco);
    arqCestos.write(c2.toByteArray());
    // ATUALIZA OS DADOS NO DIRETORIO
    int inicio = diretorio.hash2(elem.hashCode(), c.profundidadeLocal);
    int deslocamento =(int) Math.pow(2, pl);
    int max =(int) Math.pow(2, pg);
    boolean troca = false;
    for(int j = inicio; j < max; j += deslocamento){
      if(troca)
        diretorio.atualizaEndereco(j, novoEndereco);
      troca = !troca;
    }
    // ATUALIZA O ARQUIVO DO DIRETORIO
    bd = diretorio.toByteArray();
    arqDiretorio.seek(0);
    arqDiretorio.write(bd);
    // INSERE AS CHAVES DO CESTO ANTIGO
    for(int j = 0; j < c.quantidade; j++){
      adicionaHash(c.elementos.get(j));
    }
    adicionaHash(elem);
    return true;
  }

  // METODO PARA BUSCAR NO HASH EXTENSIVEL
  public T buscarHash(int chave) throws Exception{
    // CARREGA O DIRETORIO
    byte[] bd = new byte[(int) arqDiretorio.length()];
    arqDiretorio.seek(0);
    arqDiretorio.read(bd);
    diretorio = new Diretorio();
    diretorio.fromByteArray(bd);
    // IDENTIFICA O HASH DO DIRETORIO
    int i = diretorio.hash(chave);
    // RECUPERA O CESTO
    long enderecoCesto = diretorio.endereço(i);
    Cesto c = new Cesto(construtor, qntdDadosPorCesto);
    byte[] ba = new byte[c.tamanho()];
    arqCestos.seek(enderecoCesto);
    arqCestos.read(ba);
    c.fromByteArray(ba);
    // RETORNA A BUSCA DO CESTO
    return c.buscarCesto(chave);
  }

  // METODO PARA ATUALIZAR NO HASH EXTENSIVEL
  public boolean update(T elem) throws Exception{
    // CARREGA O DIRETORIO
    byte[] bd = new byte[(int) arqDiretorio.length()];
    arqDiretorio.seek(0);
    arqDiretorio.read(bd);
    diretorio = new Diretorio();
    diretorio.fromByteArray(bd);
    // IDENTIFICA O HASH DO DIRETORIO
    int i = diretorio.hash(elem.hashCode());
    // RECUPERA O CESTO
    long enderecoCesto = diretorio.endereço(i);
    Cesto c = new Cesto(construtor, qntdDadosPorCesto);
    byte[] ba = new byte[c.tamanho()];
    arqCestos.seek(enderecoCesto);
    arqCestos.read(ba);
    c.fromByteArray(ba);
    // ATUALIZA OS DADOS DO CESTO
    if(!c.atualizarCesto(elem))
      return false;
    // ATUALIZA O CESTO
    arqCestos.seek(enderecoCesto);
    arqCestos.write(c.toByteArray());
    return true;
  }

  // METODO PARA EXCLUIR NO HASH
  public boolean excluirHash(int chave) throws Exception{
    // CARREGA O DIRETORIO
    byte[] bd = new byte[(int) arqDiretorio.length()];
    arqDiretorio.seek(0);
    arqDiretorio.read(bd);
    diretorio = new Diretorio();
    diretorio.fromByteArray(bd);
    // IDENTIFICA O HASH DO DIRETORIO
    int i = diretorio.hash(chave);
    // RECUPERA O CESTO
    long enderecoCesto = diretorio.endereço(i);
    Cesto c = new Cesto(construtor, qntdDadosPorCesto);
    byte[] ba = new byte[c.tamanho()];
    arqCestos.seek(enderecoCesto);
    arqCestos.read(ba);
    c.fromByteArray(ba);
    // EXCLUI A CHAVE DO CESTO
    if(!c.excluirCesto(chave))
      return false;
    // ATUALIZA O CESTO
    arqCestos.seek(enderecoCesto);
    arqCestos.write(c.toByteArray());
    return true;
  }

  // METODO PARA FECHAR OS ARQUIVOS
  public void close() throws Exception{
    arqDiretorio.close();
    arqCestos.close();
  }
}