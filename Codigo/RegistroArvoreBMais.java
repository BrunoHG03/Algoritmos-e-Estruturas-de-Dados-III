import java.io.IOException;
// INTERFACE PARA OS METODOS NECESSARIOS PARA ARVORE B+
public interface RegistroArvoreBMais<T> {
  public short getTamanho(); // RETORNA O TAMANHO
  public byte[] toByteArray() throws IOException; // TRANSFORMA UM ELEMENTO EM UM ARRAY DE BYTES
  public void fromByteArray(byte[] ba) throws IOException; // TRANSFORMA DE UM ARRAY DE BYTES PARA ELEMENTO
  public int compareTo(T obj); // COMPARA DOIS ELEMENTOS
}