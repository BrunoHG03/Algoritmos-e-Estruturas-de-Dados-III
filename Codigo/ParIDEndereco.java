import java.io.*;

// CLASSE QUE JUNTA O ID E O ENDERECO USADA PARA HASH E ARVORE
public class ParIDEndereco implements RegistroHashExtensivel<ParIDEndereco>, RegistroArvoreBMais<ParIDEndereco>{
    
    private int id;   // ID
    private long endereco;    // ENDERECO
    private final short tamanho = 12;  // TAMANHO EM BYTES

    // CONSTRUTOR 01
    public ParIDEndereco(){
        this.id = -1;
        this.endereco = -1;
    }

    // CONSTRUTOR 02
    public ParIDEndereco(int id, long end){
        this.id = id;
        this.endereco = end;
    }

    // METODOS GETs
    public int getId(){ return id; }
    public long getEndereco(){ return endereco; }
    public short getTamanho(){ return this.tamanho; }

    // METODO QUE RETORNA O HASH CODE
    @Override
    public int hashCode(){ return this.id; }

    // METODO QUE TRANSFORMA O ID E ENDERECO EM UM ARRAY DE BYTES
    public byte[] toByteArray() throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeLong(this.endereco);
        return baos.toByteArray();
    }

    // METODO QUE TRANSFORMA UM ARRAY DE BYTES EM UM ID E ENDERECO
    public void fromByteArray(byte[] ba) throws IOException{
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.endereco = dis.readLong();
    }

    // METODO PARA COMPARAR IDs
    public int compareTo(ParIDEndereco a) {
        if (this.id != a.id)
          return this.id - a.id;
        else
          return -1;
    }

}