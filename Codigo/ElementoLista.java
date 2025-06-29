// CLASSE PARA OS ELEMENTOS DA LISTA INVERTIDA
public class ElementoLista implements Comparable<ElementoLista>{
    
    // ID DO REGISTRO E FREQUENCIA
    private int id;
    private float frequencia;

    // CONSTRUTOR 01
    public ElementoLista(){
        id = 0;
        frequencia = 0;
    }

    // CONSTRUTOR 02
    public ElementoLista(int i, float f){
        this.id = i;
        this.frequencia = f;
    }

    // METODOS GETs E SETs
    public int getId(){ return id; }
    public void setId(int id){ this.id = id; }
    public float getFrequencia(){ return frequencia; }
    public void setFrequencia(float frequencia){ this.frequencia = frequencia; }

    // METODO PARA COMPARAR 02 ELEMENTOS
    public int compareTo(ElementoLista outro){
        return Integer.compare(this.id, outro.id);
    }
}