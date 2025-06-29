import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

// CLASSE DO ARQUIVO DE ARVORE B+
public class ArquivoArvore{
    public static int cripto;
    public static Vigenere vigenere;
    public static RSA rsa = new RSA();
    final int tamCabecalho = 12;
    RandomAccessFile arquivo;
    String nomeArquivo;
    ArvoreBMais<ParIDEndereco> indiceDireto;

    // CONSTRUTOR DO ARQUIVO ARVORE B+
    public ArquivoArvore(String nomeArquivo, int ordem) throws Exception{
        File d = new File("Dados");
        if(!d.exists())
            d.mkdir();
        this.nomeArquivo = "Dados/"+ nomeArquivo +".db";
        arquivo = new RandomAccessFile(this.nomeArquivo, "rw");
        if(arquivo.length() < tamCabecalho){
            // CRIANDO CABECALHO
            arquivo.writeInt(0); 
            arquivo.writeLong(-1); 
        }
        indiceDireto = new ArvoreBMais<>(
            ParIDEndereco.class.getConstructor(), 
            ordem, 
            "Dados/"+ nomeArquivo +".db");
    }

    // METODO PARA FAZER A LEITURA DO CSV DE PILOTOS (ARVORE B+) (REUTILIZADO DO TP02 DE AEDS 02)
    public void criarArvore(){
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
             } else{
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
       } catch (Exception e){
          e.printStackTrace();
       }
    }

    // METODO PARA ADICIONAR PILOTO
    public int adicionarPiloto(Piloto piloto) throws Exception{
        ListaInvertida lista = new ListaInvertida(4, "dicionarioLista.db", "blocosLista.db");
        ListaInvertida lista2 = new ListaInvertida(4, "dicionarioLista2.db", "blocosLista2.db");
        String[] palavras;
        String[] palavras2;
        // VOLTA PARA O INICIO, LE O ULTIMO ID INSERIDO E COLOCA O ID DO PILOTO A SER ADICIONADO
        arquivo.seek(0);
        int proximoID = arquivo.readInt() + 1;
        // VOLTA PARA O INICIO E ESCREVE O ID DO NOVO PILOTO
        arquivo.seek(0);
        arquivo.writeInt(proximoID);
        piloto.setId(proximoID);
        // PEGA AS PALAVRAS PARA LISTA
        palavras = piloto.nome.split(" ");
        palavras2 = piloto.pais.split(" ");
        // CRIPTOGRAFA NOME SE NECESSARIO
        if(cripto == 1)
           piloto.setNome(vigenere.criptografar(piloto.getNome()));
        else if(cripto == 2)
           piloto.setNome(rsa.criptografar(piloto.getNome()));
        // CONVERTE O PILOTO PARA UM ARRAY DE BYTES
        byte[] b = piloto.toByteArray();
        // VERIFICA SE HA ESPACO DISPONIVEL PARA SER REUTILIZADO
        long endereco = getDeleted(b.length); 
        // SE NAO HOUVER ADICIONA PILOTO NO FINAL DO ARQUIVO
        if(endereco == -1){    
            arquivo.seek(arquivo.length());
            endereco = arquivo.getFilePointer();
            arquivo.writeByte(1);     
            arquivo.writeShort(b.length); 
            arquivo.write(b);            
        }
        // SE HOUVER REUTILIZA O ESPACO
        else{
            arquivo.seek(endereco);
            arquivo.writeByte(1);     
            arquivo.skipBytes(2);        
            arquivo.write(b);            
        }           
        // CRIA O INDICE
        indiceDireto.adicionaArvore(new ParIDEndereco(proximoID, endereco));
        // ADICIONA ELEMENTOS AS LISTAS INVERTIDAS
          for(int i = 0; i < palavras.length; i++){
            lista.adicionaLista(palavras[i], new ElementoLista(piloto.id, 1));
          }
          for(int i = 0; i < palavras2.length; i++){
            lista2.adicionaLista(palavras2[i], new ElementoLista(piloto.id, 1));
          }
        return piloto.getId();
    }
    
    // METODO PARA BUSCA DE PILOTO
    public Piloto buscarPiloto(int id) throws Exception{
        Piloto piloto;
        short tam;
        byte[] b;
        byte lapide;
        // PEGA O ENDERECO NO INDICE
        ArrayList<ParIDEndereco> idEndereco = indiceDireto.buscarArvore(new ParIDEndereco(id, -1));
        if(idEndereco != null){
            // PULA PARA O ENDERECO
            arquivo.seek(idEndereco.get(0).getEndereco());
            piloto = new Piloto();
            lapide = arquivo.readByte();
            // CONFERE SE A LAPIDE E VALIDA
            if(lapide == 1){
                // LE O PILOTO
                tam = arquivo.readShort();
                b = new byte[tam];
                arquivo.read(b);
                piloto.fromByteArray(b);
                // RETORNA O PILOTO
                if(piloto.getId() == id){
                    if(cripto == 1)
                        piloto.setNome(vigenere.descriptografar(piloto.getNome()));
                    else if(cripto == 2)
                        piloto.setNome(rsa.descriptografar(piloto.getNome()));
                    return piloto;
                }
            }
        }
        return new Piloto();
    }

    // METODO PARA EXCLUIR O PILOTO DO ARQUIVO
    public boolean excluirPiloto(int id) throws Exception{
        Piloto piloto;
        short tam;
        byte[] b;
        byte lapide;
        // PEGA O ENDERECO NO INDICE
        ArrayList<ParIDEndereco> idEndereco = indiceDireto.buscarArvore(new ParIDEndereco(id, -1));
        // CONFERE SE NAO E NULL
        if(idEndereco != null){
            // PULA PARA O ENDERECO
            arquivo.seek(idEndereco.get(0).getEndereco());
            piloto = new Piloto();
            lapide = arquivo.readByte();
            // CONFERE SE A LAPIDE ESTA VALIDA
            if(lapide == 1){
                tam = arquivo.readShort();
                b = new byte[tam];
                arquivo.read(b);
                piloto.fromByteArray(b);
                // SE FOR O ID A SER EXCLUIDO
                if(piloto.getId() == id){
                    // EXCLUI O INDICE
                    if(indiceDireto.excluirArvore(new ParIDEndereco(id, -1))){
                        arquivo.seek(idEndereco.get(0).getEndereco());
                        // ESCREVE LAPIDE COMO EXCLUIDO
                        arquivo.write(0);
                        addDeleted(tam, idEndereco.get(0).getEndereco());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // METODO PARA ATUALIZAR O PILOTO NO ARQUIVO
    public boolean atualizarPiloto(Piloto novopiloto) throws Exception{
        Piloto piloto;
        short tam;
        byte[] b;
        byte lapide;
        // PEGA O ENDERECO NO INDICE
        ArrayList<ParIDEndereco> idEndereco = indiceDireto.buscarArvore(new ParIDEndereco(novopiloto.getId(), -1));
        // VERIFICA SE NAO E NULL
        if(idEndereco != null){
            // PULA PARA O ENDERECO DO REGISTRO
            arquivo.seek(idEndereco.get(0).getEndereco());
            piloto = new Piloto();
            lapide = arquivo.readByte();
            // CONFERE SE LAPIDE E VALIDA
            if(lapide == 1){
                tam = arquivo.readShort();
                b = new byte[tam];
                arquivo.read(b);
                piloto.fromByteArray(b);
                // CONFERE SE PEGOU O PILOTO CORRETO
                if(piloto.getId() == novopiloto.getId()){
                    byte[] b2 = novopiloto.toByteArray();
                    short tam2 = (short)b2.length;
                    // SE O TAMANHO FOR MENOR SOBRESCREVE O PILOTO
                    if(tam2 <= tam){
                        arquivo.seek(idEndereco.get(0).getEndereco()+3);
                        arquivo.write(b2);
                    }
                    // SE NAO ESCREVE O FIM DO ARQUIVO
                    else{
                        // EXCLUINDO O PILOTO ANTIGO
                        arquivo.seek(idEndereco.get(0).getEndereco());
                        arquivo.write(0);
                        addDeleted(tam, idEndereco.get(0).getEndereco());                        
                        // ESCREVE O NOVO PILOTO
                        // CONFERE SE HA ESPACO PARA SER REUTILIZADO
                        long novoEndereco = getDeleted(b.length); 
                        // SE NAO ESCREVE NO FINAL DO ARQUIVO
                        if(novoEndereco == -1){   
                            arquivo.seek(arquivo.length());
                            novoEndereco = arquivo.getFilePointer();
                            arquivo.writeByte(1);       
                            arquivo.writeShort(tam2);      
                            arquivo.write(b2);             
                        }
                        // SE SIM REUTILIZA O ESPACO
                        else{
                            arquivo.seek(novoEndereco);
                            arquivo.writeByte(1);     
                            arquivo.skipBytes(2);     
                            arquivo.write(b2);             
                        }
                        // ATUALIZA O INDICE
                        indiceDireto.excluirArvore(new ParIDEndereco(novopiloto.getId(), -1));
                        indiceDireto.adicionaArvore(new ParIDEndereco(novopiloto.getId(), novoEndereco));
                    }
                    return true;
                }
            }
        }
        return false;
    }

    // ADICIONA NA LISTA ESPACOS QUE PODEM SER REUTILIZADOS
    public void addDeleted(int tamanhoEspaco, long enderecoEspaco) throws Exception{
        long anterior = 4;
        arquivo.seek(anterior);
        long endereco = arquivo.readLong(); 
        long proximo = -1;
        int tamanho;
        // CONFERE SE LISTA ESTA VAZIA
        if(endereco == -1){ 
            arquivo.seek(4);
            arquivo.writeLong(enderecoEspaco);
            arquivo.seek(enderecoEspaco+3);
            arquivo.writeLong(-1);
        } else{
            // PERCORRE A LISTA DE ESPAÇOS LIVRES
            do{
                arquivo.seek(endereco+1);
                tamanho = arquivo.readShort();
                proximo = arquivo.readLong();
                // VERIFICA O TAMANHO DO ESPACO
                if(tamanho > tamanhoEspaco){ 
                    if(anterior == 4) 
                        arquivo.seek(anterior);
                    else
                        arquivo.seek(anterior+3);
                    arquivo.writeLong(enderecoEspaco);
                    arquivo.seek(enderecoEspaco+3);
                    arquivo.writeLong(endereco);
                    break;
                }
                // SE CHEGOU AO FINAL DA LISTA
                if(proximo == -1){ 
                    arquivo.seek(endereco+3);
                    arquivo.writeLong(enderecoEspaco);
                    arquivo.seek(enderecoEspaco+3);
                    arquivo.writeLong(+1);
                    break;
                }
                anterior = endereco;
                endereco = proximo;
            } while (endereco != -1);
        }
    }
    
    // DELETA UM ESPACO PARA SE REUTILIZADO
    public long getDeleted(int tamanhoNecessario) throws Exception{
        long anterior = 4;
        arquivo.seek(anterior);
        long endereco = arquivo.readLong(); 
        long proximo = -1; 
        int tamanho;
        // PERCORRE A LISTA DE ESPAÇOS LIVRES
        while(endereco != -1){
            arquivo.seek(endereco+1);
            tamanho = arquivo.readShort();
            proximo = arquivo.readLong();
            // VERIFICA SE O TAMANHO DO ESPAÇO É SUFICIENTE
            if(tamanho > tamanhoNecessario){  
                if(anterior == 4) 
                    arquivo.seek(anterior);
                else
                    arquivo.seek(anterior+3);
                arquivo.writeLong(proximo);
                break;
            }
            anterior = endereco;
            endereco = proximo;
        }
        return endereco;
    }

    // FECHANDO ARQUIVO E INDICE
    public void close() throws Exception{
        arquivo.close();
    }

    // METODOS GET E SET DA CRIPTOGRAFIA
    public void setCripto(int cripto){ this.cripto = cripto; }
    public int getCripto(){ return this.cripto; }
}