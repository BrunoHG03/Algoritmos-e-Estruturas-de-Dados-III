// CLASSE CRIPTOGRAFIA VIGENERE
public class Vigenere{
    // CHAVE PARA CRIPTOGRAFIA
    public final static String chave = "piloto";
    // METODO PARA CRIPTOGRAFAR PALAVRA
    public static String criptografar(String palavra){
        // COMPLETANDO CHAVE COM TAMANHO CERTO
        String chaveCompleta = completaChave(palavra, chave);
        String palavraCriptografada = new String();
        // CRIPTOGRAFANDO CARACTERE POR CARACTERE
        for(int i = 0; i < palavra.length(); i++){
            char c = palavra.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
               palavraCriptografada += codifica(c, chaveCompleta.charAt(i));
            else
               palavraCriptografada += c;
        }
        // RETORNA A PALAVRA CRIPTOGRAFADA
        return palavraCriptografada;
    }

    // METODO PARA DESCRIPTOGRAFAR PALAVRA
    public static String descriptografar(String palavra){
        // COMPLETANDO CHAVE COM TAMANHO CERTO
        String chaveCompleta = completaChave(palavra, chave);
        String palavraDescriptografada = new String();
        // DESCRIPTOGRAFANDO CARACTERE POR CARACTERE
        for(int i = 0; i < palavra.length(); i++){
            char c = palavra.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
               palavraDescriptografada += descodifica(c, chaveCompleta.charAt(i));
            else
               palavraDescriptografada += c;
        }
        // RETORNA A PALAVRA DESCRIPTOGRAFADA
        return palavraDescriptografada;
    }

    // METODO PARA CODIFICAR O CARACTERE
    public static char codifica(char x, char y){
        // VERIFICA SE E MAIUSCULA
        boolean isUpper = Character.isUpperCase(x);
        // PADRONIZA PARA MINUSCULO
        x = Character.toLowerCase(x);
        y = Character.toLowerCase(y);
        // PEGA AS POSICOES NO ALFABETO
        int pos1 = x - 'a';
        int pos2 = y - 'a';
        // SOMA ELAS
        int deslocamento = (pos1 + pos2) % 26;
        // E O RESULTADO E O CARACTERE CODIFICADO APOS O DESLOCAMENTO
        char resultado = (char) ('a' + deslocamento);
        // RETORNA PARA MAIUSCULO SE NECESSARIO
        return isUpper ? Character.toUpperCase(resultado) : resultado;
    }

    // METODO PARA DESCODIFICAR O CARACTERE
    public static char descodifica(char x, char y) {
        // VERIFICA SE E MAIUSCULA
        boolean isUpper = Character.isUpperCase(x);
        // PADRONIZA PARA MINUSCULO
        x = Character.toLowerCase(x);
        y = Character.toLowerCase(y);
        // PEGA AS POSICOES NO ALFABETO
        int pos1 = x - 'a';
        int pos2 = y - 'a';
        // SUBTRAI ELAS
        int deslocamento = (pos1 - pos2 + 26) % 26;
        // E O RESULTADO E O CARACTERE DESCODIFICADO APOS O DESLOCAMENTO
        char resultado = (char) ('a' + deslocamento);
        // RETORNA PARA MAIUSCULO SE NECESSARIO
        return isUpper ? Character.toUpperCase(resultado) : resultado;
    }

    // METODO PARA COMPLETAR A CHAVE, EXEMPLO: automobilismo, piloto (retorna: pilotopilotop)
    public static String completaChave(String base, String chave) {
        StringBuilder resultado = new StringBuilder();
        int tamanhoBase = base.length();
        int tamanhoChave = chave.length();
        for (int i = 0; i < tamanhoBase; i++) {
            resultado.append(chave.charAt(i % tamanhoChave));
        }
        return resultado.toString();
    }
}
