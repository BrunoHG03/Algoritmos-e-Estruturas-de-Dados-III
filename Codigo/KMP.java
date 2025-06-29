/*
CLASSE KMP IMPLEMENTADA COM MATERIA DISPONIVEL DE AEDS III,
FOI USADO COMO BASE E EXEMPLO O CODIGO DISPONIBILIZADO EM
PYTHON PELO PROFESSOR HAYALA NO CANVAS, EM CONJUNTO COM OS
SLIDE E VIDEOS DA MATERIA 
*/

// CLASSE KMP
public class KMP {

    // METODO PARA FAZER O CASAMENTO KMP
    public static boolean buscaKMP(String padrao, String texto) {
        padrao = padrao.toLowerCase();
        texto = texto.toLowerCase();
        int M = padrao.length();
        int N = texto.length();
        int[] vetorFalha = new int[M];
        // CRIA O VETOR DE FALHAS
        criarVetorFalha(padrao, M, vetorFalha);
        // PONTEIROS PARA TEXTO E PADRAO
        int i = 0;
        int j = 0;
        while ((N - i) >= (M - j)) {
            // SE AMBOS OS CARACTERES FOREM IGUAIS AVANCA AMBOS OS PONTEIROS
            if (padrao.charAt(j) == texto.charAt(i)) {
                i++;
                j++;
            }
            // SE CHEGOU AO FIM DO PADRAO RETORNA TRUE
            if (j == M) {
                return true;
            } else if (i < N && padrao.charAt(j) != texto.charAt(i)) { // SE OS CARACTERES NAO FOREM IGUAIS VOLTA O PONTEIRO DO PADRAO PARA POSICAO NECESSARIA
                if (j != 0) {
                    j = vetorFalha[j - 1];
                } else {
                    i++;
                }
            }
        }
        // SE NAO ENCONTROU O PADRAO RETORNA FALSE
        return false;
    }

    // METODO QUE CRIA O VETOR DE FALHAS
    public static void criarVetorFalha(String padrao, int M, int[] vetorFalha) {
        int len = 0;
        vetorFalha[0] = 0; 
        int i = 1;
        // PREENCHE O VETOR DE FALHAS
        while (i < M) {
            if (padrao.charAt(i) == padrao.charAt(len)) {
                len++;
                vetorFalha[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = vetorFalha[len - 1];
                } else {
                    vetorFalha[i] = 0;
                    i++;
                }
            }
        }
        /* SE QUISER MOSTRAR O VETOR DE FALHAS
        System.out.print("Vetor de falhas: ");
        for (int val : vetorFalha) {
            System.out.print(val + " ");
        }
        System.out.println(); */
    }
}