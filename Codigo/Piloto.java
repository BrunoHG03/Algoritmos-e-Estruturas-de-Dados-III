import java.util.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;

// CLASSE PILOTO
public class Piloto {
    public int id;
    public String nome;
    public char[] sigla;
    public String pais;
    public int titulos;
    public int corridas;
    public int poles;
    public int vitorias;
    public int podios;
    public int voltasRapidas;
    public double pontos;
    public List<String> equipes;
    public LocalDate nascimento;
    public String funcao;

    // CONSTRUTOR 01
    public Piloto(){
        id = 0;
        nome = new String();
        sigla = new char[3];
        pais = new String();
        titulos = 0;
        corridas = 0;
        poles = 0;
        vitorias = 0;
        podios = 0;
        voltasRapidas = 0;
        pontos = 0.0;
        equipes = new ArrayList<String>();
        nascimento = null;
        funcao = new String();
    }

    // CONSTRUTOR 02
    public Piloto(int id, String nome, char[] sigla, String pais, int titulos, int corridas, int poles, int vitorias, int podios, int voltasRapidas, double pontos, List<String> equipes, LocalDate nascimento, String funcao){
        this.id = id;
        this.nome = nome;
        this.sigla = sigla;
        this.pais = pais;
        this.titulos = titulos;
        this.corridas = corridas;
        this.poles = poles;
        this.vitorias = vitorias;
        this.podios = podios;
        this.voltasRapidas = voltasRapidas;
        this.pontos = pontos;
        this.equipes = equipes;
        this.nascimento = nascimento;
        this.funcao = funcao;
    }

    // METODOS SETs
    public void setId(int id){this.id = id;}
    public void setNome(String nome){this.nome = nome;}
    public void setSigla(char[] sigla){this.sigla = sigla;}
    public void setPais(String pais){this.pais = pais;}
    public void setTitulos(int titulos){this.titulos = titulos;}
    public void setCorridas(int corridas){this.corridas = corridas;}
    public void setPoles(int poles){this.poles = poles;}
    public void setVitorias(int vitorias){this.vitorias = vitorias;}
    public void setPodios(int podios){this.podios = podios;}
    public void setVoltasRapidas(int voltasRapidas){this.voltasRapidas = voltasRapidas;}
    public void setPontos(double pontos){this.pontos = pontos;}
    public void setEquipes(List<String> equipes){this.equipes = equipes;}
    public void addEquipe(String equipe){equipes.add(equipe);} // ADICIONAR 1 EQUIPE NAS EQUIPES
    public void setNascimento(LocalDate nascimento){this.nascimento = nascimento;}
    public void setFuncao(String funcao){this.funcao = funcao;}

    // METODOS GETs
    public int getId(){return id;}
    public String getNome(){return nome;}
    public char[] getSigla(){return sigla;}
    public String getPais(){return pais;}
    public int getTitulos(){return titulos;}
    public int getCorridas(){return corridas;}
    public int getPoles(){return poles;}
    public int getVitorias(){return vitorias;}
    public int getPodios(){return podios;}
    public int getVoltasRapidas(){return voltasRapidas;}
    public double getPontos(){return pontos;}
    public List<String> getEquipes(){return equipes;}
    public LocalDate getNascimento(){return nascimento;}
    public String getFuncao(){return funcao;}

    // METODO PARA IMPRIMIR O PILOTO NA TELA
    public void mostrar(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nPiloto #"+ id);
        System.out.println("Nome: " + nome + " - " + sigla[0] + sigla[1] + sigla[2] + " - " + pais);
        System.out.println("Data de Nascimento: " + nascimento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        System.out.println("Função: " + funcao);
        System.out.println("Títulos: " + titulos + " - Corridas: " + corridas);
        System.out.println("Vitórias: " + vitorias + " - Pódios: " + podios);
        System.out.println("Pole Positions: " + poles + " - Voltas Mais Rápidas: " + voltasRapidas);
        System.out.println("Pontos: " + pontos);
        System.out.println("Equipes: " + equipes + "\n");
        System.out.print("Aperte enter para continuar");
        scanner.nextLine();
        System.out.println();
    }

    // METODO PARA IMPRIMIR O PILOTO NA TELA
    public void mostrarLista(){
        System.out.println("\nPiloto #"+ id);
        System.out.println("Nome: " + nome + " - " + sigla[0] + sigla[1] + sigla[2] + " - " + pais);
        System.out.println("Data de Nascimento: " + nascimento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        System.out.println("Função: " + funcao);
        System.out.println("Títulos: " + titulos + " - Corridas: " + corridas);
        System.out.println("Vitórias: " + vitorias + " - Pódios: " + podios);
        System.out.println("Pole Positions: " + poles + " - Voltas Mais Rápidas: " + voltasRapidas);
        System.out.println("Pontos: " + pontos);
        System.out.println("Equipes: " + equipes + "\n");
    }

    // METODO PARA CONVERTER UM PILOTO EM UM ARRAY DE BYTES
    public byte[] toByteArray() throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id);
        dos.writeUTF(nome);
        for(int i = 0; i < 3; i++){
           dos.writeChar(sigla[i]);
        }
        dos.writeUTF(pais);
        dos.writeInt(titulos);
        dos.writeInt(corridas);
        dos.writeInt(poles);
        dos.writeInt(vitorias);
        dos.writeInt(podios);
        dos.writeInt(voltasRapidas);
        dos.writeDouble(pontos);
        dos.writeInt(equipes.size());
        for(int i = 0; i < equipes.size(); i++){
            dos.writeUTF(equipes.get(i));
        }
        dos.writeInt((int) this.nascimento.toEpochDay());
        dos.writeUTF(funcao);
        return baos.toByteArray();
    }

    // METODO PARA CONVERTER UM ARRAY DE BYTES PARA UM PILOTO
    public void fromByteArray(byte ba[]) throws IOException{
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        id = dis.readInt();
        nome = dis.readUTF();
        for(int i = 0; i < 3; i++){
           sigla[i] = dis.readChar();
        }
        pais = dis.readUTF();
        titulos = dis.readInt();
        corridas = dis.readInt();
        poles = dis.readInt();
        vitorias = dis.readInt();
        podios = dis.readInt();
        voltasRapidas = dis.readInt();
        pontos = dis.readDouble();
        int j = dis.readInt();
        equipes.clear();
        for(int i = 0; i < j; i++){
            equipes.add(dis.readUTF());
        }
        nascimento = LocalDate.ofEpochDay(dis.readInt());
        funcao = dis.readUTF();
    }
}
