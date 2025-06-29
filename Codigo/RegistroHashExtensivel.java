import java.io.IOException;

// INTERFACE DOS METODOS NECESSARIOS NA TABELA HASH EXTENSIVEL
public interface RegistroHashExtensivel<T> {
  public int hashCode(); // ID QUE E USADO COMO O CODIGO HASH
  public short getTamanho(); // TAMANHO FIXO DO ID + ENDERECO
  public byte[] toByteArray() throws IOException; // METODO QUE TRANSFORMA O ID E ENDERECO EM UM ARRAY DE BYTES
  public void fromByteArray(byte[] ba) throws IOException; // METODO QUE TRANSFORMA UM ARRAY DE BYTES EM UM ID E ENDERECO
}