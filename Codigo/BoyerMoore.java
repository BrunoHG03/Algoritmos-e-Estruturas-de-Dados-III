/*
CLASSE BOYER MOORE IMPLEMENTADA COM MATERIA DISPONIVEL DE AEDS III,
FOI USADO COMO BASE E EXEMPLO O CODIGO DISPONIBILIZADO EM
PYTHON PELO PROFESSOR HAYALA NO CANVAS, EM CONJUNTO COM OS
SLIDE E VIDEOS DA MATERIA 
*/
import java.util.*;

// CLASSE BOYER MOORE
public class BoyerMoore {
    // METODO PARA BUSCAR O PADRAO POR BOYER MOORE
    public static boolean buscar(String padrao, String texto) {
        padrao = padrao.toLowerCase();
        texto = texto.toLowerCase();
        int m = padrao.length();
        int n = texto.length();
        // CALCULA O CARACTER RUIM E O SUFIXO BOM
        Map<Character, Integer> caracterRuim = caracterRuim(padrao);
        int[] sufixoBom = sufixoBom(padrao);
        int s = 0;
        // BUSCA O PADRAO
        while (s <= (n - m)) {
            int j = m - 1;
            // BUSCA DA DIREITA PARA ESQUERDA
            while (j >= 0 && padrao.charAt(j) == texto.charAt(s + j)) {
                j--;
            }
            // RETORNA TRUE SE ACHOU O PADRAO
            if (j < 0) {
                return true;
            } else {
                // FAZ O SALTO MAXIMO DO CARACTER RUIM E SUFIXO BOM
                int cRuim = caracterRuim.getOrDefault(texto.charAt(s + j), -1);
                s += Math.max(sufixoBom[j], j - cRuim);
            }
        }
        return false;
    }

    // METODO PARA CALCULAR O CARACTER RUIM
    private static Map<Character, Integer> caracterRuim(String padrao) {
        Map<Character, Integer> caracterRuim = new HashMap<>();
        // PARA CADA CARACTER ARMAZENA SUA ULTIMA OCORRENCIA (EXCETO A ULTIMA POSICAO)
        for (int i = 0; i < padrao.length(); i++) {
            caracterRuim.put(padrao.charAt(i), i);
        }
        return caracterRuim;
    }

    // METODO PARA CALCULAR O SUFIXO BOM
    private static int[] sufixoBom(String padrao) {
        int m = padrao.length();
        int[] borda = new int[m + 1];
        int[] salto = new int[m + 1];
        int i = m, j = m + 1;
        borda[i] = j;
        // CALCULA O SALTO
        while (i > 0) {
            while (j <= m && padrao.charAt(i - 1) != padrao.charAt(j - 1)) {
                if (salto[j] == 0) {
                    salto[j] = j - i;
                }
                j = borda[j];
            }
            i--;
            j--;
            borda[i] = j;
        }
        // PREENCHE OS VALORES DOS SALTOS
        for (int k = 0; k <= m; k++) {
            if (salto[k] == 0) {
                salto[k] = j;
            }
            if (k == j) {
                j = borda[j];
            }
        }
        return Arrays.copyOfRange(salto, 1, salto.length);
    }
}