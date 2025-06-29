import java.util.BitSet;
// CLASSE VETOR DE BITS QUE USA UM BITSET
public class VetorDeBits {
    private BitSet vetor;
    
    // CONSTRUTOR QUE CRIA UM BIT VAZIO
    public VetorDeBits() {
        vetor = new BitSet();
        vetor.set(0);
    }

    // CONSTRUTOR QUE CRIA UM BITSET DE CERTO TAMANHO
    public VetorDeBits(int tam) {
        vetor = new BitSet(tam);
        vetor.set(tam);
    }

    // CONSTRUTOR QUE JA CONSTROI UM BITSET ATRAVES DE UM ARRAY DE BYTES
    public VetorDeBits(byte[] dados) { vetor = BitSet.valueOf(dados); }

    // METODO QUE TRANSFORMA UM BITSET EM UM ARRAY DE BYTES
    public byte[] toByteArray() { return vetor.toByteArray(); }

    // METODO PARA ATIVAR O BIT DE CERTA POSICAO
    public void set(int pos) {
        if(pos >= vetor.length() - 1){
            vetor.clear(vetor.length() - 1);
            vetor.set(pos + 1);
        }
        vetor.set(pos);
    }

    // METODO PARA DESATIVAR O BIT DE CERTA POSICAO
    public void clear(int pos) {
        if(pos >= vetor.length() - 1) {
            vetor.clear(vetor.length() - 1);
            vetor.set(pos + 1);
        }
        vetor.clear(pos);
    }

    // METODO PARA VERIFICAR SE O BIT DE CERTA POSICAO ESTA ATIVO
    public boolean get(int pos) { return vetor.get(pos); }

    // RETORNA O TAMANHO DO VETOR
    public int length() { return vetor.length() - 1; }

    // RETORNA O TAMANHO FISICO DO BITSET
    public int size() { return vetor.size(); }

    // METODO PARA TRANSFORMAR O BITSET EM UMA STRING
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < vetor.length() - 1; i++)
            if(vetor.get(i))
                sb.append('1');
            else
                sb.append('0');
        return sb.toString();
    }
}