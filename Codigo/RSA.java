import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

// CLASSE RSA
public class RSA {
    private static BigInteger p, q, n, z, d, e;
    // CONSTRUTOR FAZENDO TODOS OS CALCULOS PARA ATRIBUIR AS VARIAVEIS
    public RSA(){
        p = new BigInteger("116162656538137593016627893688358864692410079113379315568665382947203877185691448704061553879707146781226208085153651228864027539534816267445982841990686917055249622373221170987504403813552372769372145108933285268037688353940602906263922977653864451566417860658164633810807804057718079469621911021671430961947");
        q = new BigInteger("144591686648855260105129025815575127615304788472762170775982292007793431032403101920617646147409127551945451028591782466866813607505016107018454122981882796720156423094232964715698192493091293325891766892906976419570907292900732021430951066940346966234051605207219329216366053926977376626702734840652481436839");
        n = p.multiply(q);
        z = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        d = new BigInteger("65537");
        while (!z.gcd(d).equals(BigInteger.ONE)) {
            d = d.add(BigInteger.TWO);
        }
        e = d.modInverse(z);
    }

    // METODO PARA CRIPTOGRAFAR
    public static String criptografar(String mensagem) {
       BigInteger resultado = powmodPingala(new BigInteger(mensagem.getBytes()), e, n);
       return Base64.getEncoder().encodeToString(resultado.toByteArray()); // CONVERTE PARA STRING
    }

    // METODO PARA DESCRIPTOGRAFAR
    public static String descriptografar(String cifradaBase64) {
       // CONVERTE PARA BIG INTEGER
       byte[] cifradaBytes = Base64.getDecoder().decode(cifradaBase64);
       BigInteger cifrada = new BigInteger(cifradaBytes);
       BigInteger resultado = powmodPingala(cifrada, d, n);
       byte[] resultadoBytes = resultado.toByteArray();
       if (resultadoBytes.length > 1 && resultadoBytes[0] == 0) {
          byte[] tmp = new byte[resultadoBytes.length - 1];
          System.arraycopy(resultadoBytes, 1, tmp, 0, tmp.length);
          resultadoBytes = tmp;
       }
       return new String(resultadoBytes, StandardCharsets.UTF_8);
    }

    // METODO PARA CALCULAR A POTENCIA MODULAR
    public static BigInteger powmodPingala(BigInteger base, BigInteger exponent, BigInteger modulus) {
       BigInteger result = BigInteger.ONE;
       String bitstring = exponent.toString(2);
       for (char bit : bitstring.toCharArray()) {
          result = result.multiply(result).mod(modulus);
          if (bit == '1') {
             result = result.multiply(base).mod(modulus);
          }
       }
       return result;
    }
}