/*
CLASSE ÁRVORE B+ IMPLEMENTADA COM O MATERIAL DISPONIVEL
DE AEDS III E O CODIGO DISPONIBILIZADO PELO PROFESSOR KUTOVA NO
GITHUB DE AEDS III, FEITO TAMBEM ALGUMAS MODIFICACOES AO LONGO
DE TODO O CODIGO
*/
import java.io.*;
import java.util.ArrayList;
import java.lang.reflect.Constructor;
// CLASSE ARVORE B+
public class ArvoreBMais<T extends RegistroArvoreBMais<T>>{
    private int ordem;
    private int maxElementos; 
    private int maxFilhos; 
    private RandomAccessFile arquivo;
    private String nomeArquivo;
    private Constructor<T> construtor;
    // VARIAVEIS USADAS NOS METODOS RECURSIVOS
    private T elemAux;
    private long paginaAux;
    private boolean cresceu;
    private boolean diminuiu;

    // CLASSE PAGINA
    private class Pagina{
        protected int ordem; 
        protected Constructor<T> construtor;
        protected int maxElementos;
        protected int maxFilhos; 
        protected int tamElemento; 
        protected int tamPagina; 
        protected ArrayList<T> elementos; 
        protected ArrayList<Long> filhos; 
        protected long proxima; 

        // CONSTRUTOR
        public Pagina(Constructor<T> ct, int o) throws Exception{
            this.construtor = ct;
            this.ordem = o;
            this.maxFilhos = this.ordem;
            this.maxElementos = this.ordem - 1;
            this.elementos = new ArrayList<>(this.maxElementos);
            this.filhos = new ArrayList<>(this.maxFilhos);
            this.proxima = -1;
            this.tamElemento = 12;
            this.tamPagina = 4 + this.maxElementos * this.tamElemento + this.maxFilhos * 8 + 8;
        }

        // TRANSFORMAS OS DADOS NECESSARIOS EM UMA PAGINA EM UM ARRAY DE BYTES
        protected byte[] toByteArray() throws IOException{
            ByteArrayOutputStream ba = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(ba);
            out.writeInt(this.elementos.size());
            int i = 0;
            while(i < this.elementos.size()){
                out.writeLong(this.filhos.get(i).longValue());
                out.write(this.elementos.get(i).toByteArray());
                i++;
            }
            if(this.filhos.size() > 0)
                out.writeLong(this.filhos.get(i).longValue());
            else
                out.writeLong(-1L);
            // COMPLETA O RESTANTE COM REGISTROS VAZIOS
            byte[] registroVazio = new byte[tamElemento];
            while(i < this.maxElementos){
                out.write(registroVazio);
                out.writeLong(-1L);
                i++;
            }
            out.writeLong(this.proxima);
            return ba.toByteArray();
        }

        // TRANDFORMA UM ARRAY DE BYTES EM DADOS DE UMA PAGINA
        public void fromByteArray(byte[] buffer) throws Exception{
            if(buffer.length < this.tamPagina){
                throw new IOException("Tamanho de buffer insuficiente! Esperado: " + tamPagina + ", recebido: " + buffer.length);
            }
            ByteArrayInputStream ba = new ByteArrayInputStream(buffer);
            DataInputStream in = new DataInputStream(ba);
            int n = in.readInt();
            int i = 0;
            this.elementos = new ArrayList<>(this.maxElementos);
            this.filhos = new ArrayList<>(this.maxFilhos);
            T elem;
            while(i < n){
                this.filhos.add(in.readLong());
                byte[] registro = new byte[tamElemento];
                in.read(registro);
                elem = this.construtor.newInstance();
                elem.fromByteArray(registro);
                this.elementos.add(elem);
                i++;
            }
            in.skipBytes((this.maxElementos - i) *(tamElemento + 8));
            this.proxima = in.readLong();
        }
    }

    // CONSTRUTOR DA ARVORE B+
    public ArvoreBMais(Constructor<T> c, int o, String na) throws Exception{
        construtor = c;
        ordem = o;
        maxElementos = o - 1;
        maxFilhos = o;
        nomeArquivo = na;
        arquivo = new RandomAccessFile(nomeArquivo, "rw");
        if(arquivo.length() < 16){
            arquivo.writeLong(-1); 
            arquivo.writeLong(-1);
        }
    }

    // METODO PARA VER SE ARVORE ESTA VAZIA
    public boolean empty() throws IOException{
        long raiz;
        arquivo.seek(0);
        raiz = arquivo.readLong();
        return raiz == -1;
    }

    // METODO PARA BUSCAR ELEMENTO NA ARVORE
    public ArrayList<T> buscarArvore(T elem) throws Exception{
        // PEGA A RAIZ
        long raiz;
        arquivo.seek(0);
        raiz = arquivo.readLong();
        // FAZ A BUSCA RECURSIVA
        if(raiz != -1)
            return buscaArvoreRec(elem, raiz);
        else{
            ArrayList<T> resposta = new ArrayList<>();
            return resposta;
        }
    }

    // METODO DE BUSCA RECURSIVO
    private ArrayList<T> buscaArvoreRec(T elem, long pagina) throws Exception{
        // SE DESCER PARA UM FILHO INEXISTENTE RETORNA VAZIO
        if(pagina == -1){
            ArrayList<T> resposta = new ArrayList<>();
            return resposta;
        }
        // RECONSTROI A PAGINA PASSADA
        arquivo.seek(pagina);
        Pagina pa = new Pagina(construtor, ordem);
        byte[] buffer = new byte[pa.tamPagina];
        arquivo.read(buffer);
        pa.fromByteArray(buffer);
        // ENCONTRA O PONTO ONDE A CHAVE DEVE ESTAR NA PAGINA
        int i = 0;
        while(elem!=null && i < pa.elementos.size() && elem.compareTo(pa.elementos.get(i)) > 0){
            i++;
        }
        // TESTA SE A CHAVE FOI ENCONTRADA OU SE E UMA FOLHA
        if(i < pa.elementos.size() && pa.filhos.get(0) == -1 &&(elem==null || elem.compareTo(pa.elementos.get(i)) == 0)){
            // CRIA A LISTA DE RETORNO E INSERE OS ELEMENTOS
            ArrayList<T> lista = new ArrayList<>();
            while(elem==null || elem.compareTo(pa.elementos.get(i)) <= 0){
                if(elem==null || elem.compareTo(pa.elementos.get(i)) == 0)
                    lista.add(pa.elementos.get(i));
                i++;
                // SE CHEGA NO FIM DA FOLHA, VAI PARA PROXIMA
                if(i == pa.elementos.size()){
                    if(pa.proxima == -1)
                        break;
                    arquivo.seek(pa.proxima);
                    arquivo.read(buffer);
                    pa.fromByteArray(buffer);
                    i = 0;
                }
            }
            return lista;
        }
        // SE NAO ESTIVER NA FOLHA, VERIFICA SE ESTA NA PROXIMA
        else if(i == pa.elementos.size() && pa.filhos.get(0) == -1){
            // TESTA SE HA PROXIMA FOLHA
            if(pa.proxima == -1){
                ArrayList<T> resposta = new ArrayList<>();
                return resposta;
            }
            // LE A PROXIMA FOLHA
            arquivo.seek(pa.proxima);
            arquivo.read(buffer);
            pa.fromByteArray(buffer);
            // TESTA SE A CHAVE E A PRIMEIRA DA FOLHA
            i = 0;
            if(elem.compareTo(pa.elementos.get(i)) <= 0){
                // CRIA LISTA DE RETORNO E SE FOI ENCONTRADA ADICIONA O ELEMENTO
                ArrayList<T> lista = new ArrayList<>();
                while(elem.compareTo(pa.elementos.get(i)) <= 0){
                    if(elem.compareTo(pa.elementos.get(i)) == 0)
                        lista.add(pa.elementos.get(i));
                    i++;
                    if(i == pa.elementos.size()){
                        if(pa.proxima == -1)
                            break;
                        arquivo.seek(pa.proxima);
                        arquivo.read(buffer);
                        pa.fromByteArray(buffer);
                        i = 0;
                    }
                }
                return lista;
            }
            // SE NAO HOUVER ROXIMA PAGINA RETORNA VAZIO
            else{
                ArrayList<T> resposta = new ArrayList<>();
                return resposta;
            }
        }
        // CONTINUA BUSCA RECURSIVA
        if(elem==null || i == pa.elementos.size() || elem.compareTo(pa.elementos.get(i)) <= 0)
            return buscaArvoreRec(elem, pa.filhos.get(i));
        else
            return buscaArvoreRec(elem, pa.filhos.get(i + 1));
    }

    // METODO PARA ADICIONAR ELEMENTO NA PROXIMA PAGINA
    public boolean adicionaArvore(T elem) throws Exception{
        // CARREGA A RAIZ
        arquivo.seek(0);
        long pagina = arquivo.readLong();
        // VERIFICA SE ESTA VAZIA
        if(pagina == -1){
            // CRIA PAGINA RAIZ VAZIA
            Pagina novaRaiz = new Pagina(construtor, ordem);
            novaRaiz.elementos.add(elem);
            novaRaiz.filhos.add(-1L);
            novaRaiz.filhos.add(-1L);
            novaRaiz.proxima = -1;
            // GRAVA NOVA RAIZ NO ARQUIVO
            arquivo.seek(arquivo.length());  
            long novaRaizPosicao = arquivo.getFilePointer();  
            byte[] buffer = novaRaiz.toByteArray();
            arquivo.write(buffer);
            // ATUALIZA O CABECALHO
            arquivo.seek(0);  
            arquivo.writeLong(novaRaizPosicao); 
            
            return true; 
        }
        elemAux = elem;
        paginaAux = -1;
        cresceu = false;
        // CHAMADA RECURSIVA
        boolean inserido = adicionaArvoreRec(pagina);
        // TESTA SE PRECISA DE UMA NOVA RAIZ
        if(cresceu){
            // CRIA A NOVA RAIZ
            Pagina novaPagina = new Pagina(construtor, ordem);
            novaPagina.elementos = new ArrayList<>(this.maxElementos);
            novaPagina.elementos.add(elemAux);
            novaPagina.filhos = new ArrayList<>(this.maxFilhos);
            novaPagina.filhos.add(pagina);
            novaPagina.filhos.add(paginaAux);
            // ACHA ESPACO EM DISCO
            arquivo.seek(8);
            long end = arquivo.readLong();
            if(end == -1){
                end = arquivo.length();
            } else{ // REUTILIZA ENDERECO
                arquivo.seek(end);
                Pagina pa_excluida = new Pagina(construtor, ordem);
                byte[] buffer = new byte[pa_excluida.tamPagina];
                arquivo.read(buffer);
                pa_excluida.fromByteArray(buffer);
                arquivo.seek(8);
                arquivo.writeLong(pa_excluida.proxima);
            }
            arquivo.seek(end);
            long raiz = arquivo.getFilePointer();
            arquivo.write(novaPagina.toByteArray());
            arquivo.seek(0);
            arquivo.writeLong(raiz);
            inserido = true;
        }
        return inserido;
    }
    
    // METODO DE ADICIONAR ELEMENTO RECURSIVO
    private boolean adicionaArvoreRec(long pagina) throws Exception{
        // TESTA SE PASSOU PARA UM FILHO
        if(pagina == -1){
            cresceu = true;
            paginaAux = -1;
            return false;
        }
        // LE PAGINA PASSADA
        arquivo.seek(pagina);
        Pagina pa = new Pagina(construtor, ordem);
        byte[] buffer = new byte[pa.tamPagina];
        arquivo.read(buffer);
        pa.fromByteArray(buffer);
        // BUSCA PONTEIRO DE DESCIDA
        int i = 0;
        while(i < pa.elementos.size() &&(elemAux.compareTo(pa.elementos.get(i)) > 0)){
            i++;
        }
        // TESTA SE REGISTRO JA EXISTE
        if(i < pa.elementos.size() && pa.filhos.get(0) == -1 && elemAux.compareTo(pa.elementos.get(i)) == 0){
            cresceu = false;
            return false;
        }
        // CONTINUA RECURSIVAMENTE
        boolean inserido;
        if(i == pa.elementos.size() || elemAux.compareTo(pa.elementos.get(i)) < 0)
            inserido = adicionaArvoreRec(pa.filhos.get(i));
        else
            inserido = adicionaArvoreRec(pa.filhos.get(i + 1));
        // SE NAO CRESCEU
        if(!cresceu)
            return inserido;
        // SE TIVER ESPACO NA PAGINA, INSERENELA MESMA
        if(pa.elementos.size() < maxElementos){
            // ESCREVE PAGINA ATUALIZADA
            pa.elementos.add(i, elemAux);
            pa.filhos.add(i + 1, paginaAux);
            arquivo.seek(pagina);
            arquivo.write(pa.toByteArray());
            cresceu = false;
            return true;
        }
        // SE NAO CABE CRIA UMA NOVA PAGINA
        Pagina np = new Pagina(construtor, ordem);
        // MOVE METADE DOS ELEMENTOS PARA CIMA
        int meio = maxElementos / 2;
        np.filhos.add(pa.filhos.get(meio));
        for(int j = 0; j <(maxElementos - meio); j++){
            np.elementos.add(pa.elementos.remove(meio));
            np.filhos.add(pa.filhos.remove(meio + 1)); 
        }
        // TESTA O LADO A SER ADICIONADO
        if(i <= meio){
            pa.elementos.add(i, elemAux);
            pa.filhos.add(i + 1, paginaAux);
            // SE PAGINA FOR FOLHA
            if(pa.filhos.get(0) == -1)
                elemAux = np.elementos.get(0);
            // SENAO
            else{
                elemAux = pa.elementos.remove(pa.elementos.size() - 1);
                pa.filhos.remove(pa.filhos.size() - 1);
            }
        }
        // SE FOR ADICIONAR NA DIREITA
        else{
            int j = maxElementos - meio;
            while(elemAux.compareTo(np.elementos.get(j - 1)) < 0)
                j--;
            np.elementos.add(j, elemAux);
            np.filhos.add(j + 1, paginaAux);
            elemAux = np.elementos.get(0);
            // SE NAO FOR FOLHA
            if(pa.filhos.get(0) != -1){
                np.elementos.remove(0);
                np.filhos.remove(0);
            }
        }
        // OBTEM ENDERECO PARA NOVA PAGINA
        arquivo.seek(8);
        long end = arquivo.readLong();
        if(end==-1){
            end = arquivo.length();
        } else{ 
            arquivo.seek(end);
            Pagina pa_excluida = new Pagina(construtor, ordem);
            buffer = new byte[pa_excluida.tamPagina];
            arquivo.read(buffer);
            pa_excluida.fromByteArray(buffer);
            arquivo.seek(8);
            arquivo.writeLong(pa_excluida.proxima);
        }
        // SE PAGINA ERA UMA FOLHA
        if(pa.filhos.get(0) == -1){
            np.proxima = pa.proxima;
            pa.proxima = end;
        }
        // GRAVA AS PAGINAS NO ARQUIVO
        paginaAux = end;
        arquivo.seek(paginaAux);
        arquivo.write(np.toByteArray());
        arquivo.seek(pagina);
        arquivo.write(pa.toByteArray());
        return true;
    }

    // METODO PARA EXCLUIR ELEMENTO DE UMA ARVORE
    public boolean excluirArvore(T elem) throws Exception{
        // ENCONTRA RAIZ
        arquivo.seek(0);
        long pagina;
        pagina = arquivo.readLong();
        diminuiu = false;
        // CHAMADA RECURSIVA
        boolean excluido = excluirArvoreRec(elem, pagina);
        // SE EXCLUI E PAGINA DIMINUIU
        if(excluido && diminuiu){
            // LE A RAIZ
            arquivo.seek(pagina);
            Pagina pa = new Pagina(construtor, ordem);
            byte[] buffer = new byte[pa.tamPagina];
            arquivo.read(buffer);
            pa.fromByteArray(buffer);
            // SE NAO TIVER ELEMENTOS ATUALIZA PONTEIRO DA RAIZ
            if(pa.elementos.size() == 0){
                arquivo.seek(0);
                arquivo.writeLong(pa.filhos.get(0));
                arquivo.seek(8);
                long end = arquivo.readLong();
                pa.proxima = end;
                arquivo.seek(8);
                arquivo.writeLong(pagina);
                arquivo.seek(pagina);
                arquivo.write(pa.toByteArray());
            }
        }
        return excluido;
    }

    // METODO PARA EXCLUIR ELEMENTO RECURSIVO
    private boolean excluirArvoreRec(T elem, long pagina) throws Exception{
        boolean excluido = false;
        int diminuido;
        // TESTA SE NAO FOI ENCONTRADO
        if(pagina == -1){
            diminuiu = false;
            return false;
        }
        // LE O REGISTRO
        arquivo.seek(pagina);
        Pagina pa = new Pagina(construtor, ordem);
        byte[] buffer = new byte[pa.tamPagina];
        arquivo.read(buffer);
        pa.fromByteArray(buffer);
        // ENCONTRA A PAGINA QUE ESTA PRESENTE
        int i = 0;
        while(i < pa.elementos.size() && elem.compareTo(pa.elementos.get(i)) > 0){
            i++;
        }
        // SE ENCONTROU NUMA FOLHA
        if(i < pa.elementos.size() && pa.filhos.get(0) == -1 && elem.compareTo(pa.elementos.get(i)) == 0){
            // AJUSTA POSICAO DOS ELEMENTOS
            pa.elementos.remove(i);
            pa.filhos.remove(i + 1);
            // ATUALIZA PAGINA
            arquivo.seek(pagina);
            arquivo.write(pa.toByteArray());
            // SE TIVER MENOS REGISTROS QUE O MINIMO
            diminuiu = pa.elementos.size() < maxElementos / 2;
            return true;
        }
        // SE NAO ENCONTROU A CHAVE
        if(i == pa.elementos.size() || elem.compareTo(pa.elementos.get(i)) < 0){
            excluido = excluirArvoreRec(elem, pa.filhos.get(i));
            diminuido = i;
        } else{
            excluido = excluirArvoreRec(elem, pa.filhos.get(i + 1));
            diminuido = i + 1;
        }
        // TESTA SE PRECISA DE FUSOES DE PAGINAS
        if(diminuiu){
            // CARREGA A PAGINA QUE FICOU COM MENOS QUE O MINIMO DE ELEMENTOS
            long paginaFilho = pa.filhos.get(diminuido);
            Pagina pFilho = new Pagina(construtor, ordem);
            arquivo.seek(paginaFilho);
            arquivo.read(buffer);
            pFilho.fromByteArray(buffer);
            // CRIA UMA PAGINA PARA O IRMAO
            long paginaIrmaoEsq = -1, paginaIrmaoDir = -1;
            Pagina pIrmaoEsq = null, pIrmaoDir = null; 
            // CARREGA OS IRMAOS
            if(diminuido > 0){ 
                paginaIrmaoEsq = pa.filhos.get(diminuido - 1);
                pIrmaoEsq = new Pagina(construtor, ordem);
                arquivo.seek(paginaIrmaoEsq);
                arquivo.read(buffer);
                pIrmaoEsq.fromByteArray(buffer);
            }
            if(diminuido < pa.elementos.size()){ 
                paginaIrmaoDir = pa.filhos.get(diminuido + 1);
                pIrmaoDir = new Pagina(construtor, ordem);
                arquivo.seek(paginaIrmaoDir);
                arquivo.read(buffer);
                pIrmaoDir.fromByteArray(buffer);
            }
            // VERIFICA SE IRMAO ESQUERDO EXISTE E SE CEDE ELEMENTOS
            if(pIrmaoEsq != null && pIrmaoEsq.elementos.size() > maxElementos / 2){
                // SE FOR FOLHA
                if(pFilho.filhos.get(0) == -1)
                    pFilho.elementos.add(0, pIrmaoEsq.elementos.remove(pIrmaoEsq.elementos.size() - 1));
                // SENAO
                else
                    pFilho.elementos.add(0, pa.elementos.get(diminuido - 1));
                // COPIA ELEMENTO VINDO DO IRMAO
                pa.elementos.set(diminuido - 1, pFilho.elementos.get(0));
                // REDUZ ELEMENTO DO IRMAO
                pFilho.filhos.add(0, pIrmaoEsq.filhos.remove(pIrmaoEsq.filhos.size() - 1));
            }
            // VERIFICA SE IRMAO DIREITO EXISTE E SE CEDE ELEMENTOS
            else if(pIrmaoDir != null && pIrmaoDir.elementos.size() > maxElementos / 2){
                // SE FOR FOLHA
                if(pFilho.filhos.get(0) == -1){
                    pFilho.elementos.add(pIrmaoDir.elementos.remove(0));
                    pFilho.filhos.add(pIrmaoDir.filhos.remove(0));
                    pa.elementos.set(diminuido, pIrmaoDir.elementos.get(0));
                }
                // SENAO
                else{
                    pFilho.elementos.add(pa.elementos.get(diminuido));
                    pFilho.filhos.add(pIrmaoDir.filhos.remove(0));
                    pa.elementos.set(diminuido, pIrmaoDir.elementos.remove(0));
                }
            }
            // SENAO FAZ FUSAO COM IRMAO ESQUERDO
            else if(pIrmaoEsq != null){
                // SE PAGINA REDUZIDA NAO FOR FOLHA
                if(pFilho.filhos.get(0) != -1){
                    pIrmaoEsq.elementos.add(pa.elementos.remove(diminuido - 1));
                    pIrmaoEsq.filhos.add(pFilho.filhos.remove(0));
                }
                // SENAO
                else{
                    pa.elementos.remove(diminuido - 1);
                    pFilho.filhos.remove(0);
                }
                pa.filhos.remove(diminuido);
                // COPIA OS REGISTROS PARA O IRMAO DA ESQUERDA
                pIrmaoEsq.elementos.addAll(pFilho.elementos);
                pIrmaoEsq.filhos.addAll(pFilho.filhos);
                pFilho.elementos.clear(); 
                pFilho.filhos.clear();
                // SE AS PAGINAS FOREM FOLHAS
                if(pIrmaoEsq.filhos.get(0) == -1)
                    pIrmaoEsq.proxima = pFilho.proxima;
                arquivo.seek(8);
                pFilho.proxima = arquivo.readLong();
                arquivo.seek(8);
                arquivo.writeLong(paginaFilho);
            }
            // FAZ FUSAO COM IRMAO DA DIREITA
            else{
                // SE PAGINA REDUZIDA NAO FOR FOLHA
                if(pFilho.filhos.get(0) != -1){
                    pFilho.elementos.add(pa.elementos.remove(diminuido));
                    pFilho.filhos.add(pIrmaoDir.filhos.remove(0));
                }
                // SENAO
                else{
                    pa.elementos.remove(diminuido);
                    pFilho.filhos.remove(0);
                }
                pa.filhos.remove(diminuido + 1);
                // MOVE OS ELEMENTOS PARA FILHO DA DIREITA
                pFilho.elementos.addAll(pIrmaoDir.elementos);
                pFilho.filhos.addAll(pIrmaoDir.filhos);
                pIrmaoDir.elementos.clear(); 
                pIrmaoDir.filhos.clear();
                pFilho.proxima = pIrmaoDir.proxima;
                arquivo.seek(8);
                pIrmaoDir.proxima = arquivo.readLong();
                arquivo.seek(8);
                arquivo.writeLong(paginaIrmaoDir);
            }
            // TESTA SE PAI FICOU COM O MINIMO DE ELEMENTOS
            diminuiu = pa.elementos.size() < maxElementos / 2;
            // ATUALIZA OS REGISTROS
            arquivo.seek(pagina);
            arquivo.write(pa.toByteArray());
            arquivo.seek(paginaFilho);
            arquivo.write(pFilho.toByteArray());
            if(pIrmaoEsq != null){
                arquivo.seek(paginaIrmaoEsq);
                arquivo.write(pIrmaoEsq.toByteArray());
            }
            if(pIrmaoDir != null){
                arquivo.seek(paginaIrmaoDir);
                arquivo.write(pIrmaoDir.toByteArray());
            }
        }
        return excluido;
    }
}