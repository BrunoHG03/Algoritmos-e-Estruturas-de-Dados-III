import java.io.*;
import java.util.*;

// CLASSE PARA O MENU DE COMPRESSAO
public class MenuCasamento {
    public static Scanner scanner = new Scanner(System.in);
    public static Arquivo arquivo = new Arquivo();
    public static boolean resultado = false;
    public static KMP kmp = new KMP();
    public static BoyerMoore boyerMoore = new BoyerMoore();
    public static List<Piloto> pilotos = new ArrayList<>();
    public static String padrao = new String();
    public static void menuCasamento() throws Exception{
        int opcao = 50;
        // ENQUANTO USUARIO NAO ESCOLHER SAIR
        while(opcao != 0){
           // MENU
           System.out.println("\n== Casamento de Padrões ==");
           System.out.println("0 - Voltar");
           System.out.println("1 - KMP");
           System.out.println("2 - Boyer Moore\n");
           System.out.print("Escolha sua opção: ");
           opcao = scanner.nextInt();
           // SWITCH PARA O MENU
           switch(opcao){
              case 0:
                  break;
              case 1:
                 int opcao2 = 50;
                 // ENQUANTO USUARIO NAO ESCOLHER SAIR
                 while(opcao2 != 0){
                    // MENU
                    System.out.println("\n== KMP ==");
                    System.out.println("0 - Voltar");
                    System.out.println("1 - Casamento por Nome com retorno de pilotos");
                    System.out.println("2 - Casamento por Nome booleano\n");
                    System.out.print("Escolha sua opção: ");
                    opcao2 = scanner.nextInt();
                    // SWITCH PARA O MENU
                    switch(opcao2){
                    case 0:
                       break;
                    case 1:
                       scanner.nextLine();
                       pilotos.clear();
                       boolean b = false;
                       // PERGUNTA QUAL O PADRAO DE BUSCA
                       System.out.println("Digite qual o padrão que você deseja procurar: ");
                       padrao = scanner.nextLine();
                       // LE TODOS OS PILOTOS DO ARQUIVO PARA FAZER A BUSCA
                       pilotos = arquivo.lerTodosPilotos();
                       // FAZ O CASAMENTO COM TODOS OS NOMES DOS PILOTOS
                       for(int i = 0; i < pilotos.size(); i++){
                          resultado = kmp.buscaKMP(padrao, pilotos.get(i).getNome());
                          // SE ENCONTROU O PADRAO MOSTRA O PILOTO
                          if(resultado){
                             pilotos.get(i).mostrar();
                             b = true;
                          }
                       }
                       if(!b)
                           System.out.println("Padrão não encontrado!");
                       break;
                    case 2:
                       scanner.nextLine();
                       pilotos.clear();
                       int x = 0;
                       // PERGUNTA QUAL O PADRAO DE BUSCA
                       System.out.println("Digite qual o padrão que você deseja procurar: ");
                       padrao = scanner.nextLine();
                       // LE TODOS OS PILOTOS DO ARQUIVO PARA FAZER A BUSCA
                       pilotos = arquivo.lerTodosPilotos();
                       // FAZ O CASAMENTO COM TODOS OS NOMES DOS PILOTOS
                       for(int i = 0; i < pilotos.size(); i++){
                          resultado = kmp.buscaKMP(padrao, pilotos.get(i).getNome());
                          // SE ENCONTROU O PADRAO MOSTRA A MENSAGEM
                          if(resultado){
                             System.out.println("Padrão encontrado!");
                             x++;
                          }
                       }
                       if(x == 0)
                           System.out.println("Padrão não encontrado!");
                       else
                           // MOSTRA QUANTAS VEZES O PADRAO FOI ENCONTRADO
                           System.out.println("O padrão foi encontrado " + x + " vezes.");
                       break;
                    default:
                       System.out.println("\nOpção Inválida!!!");
                       break;
                    }
                 }                  
                 break;
              case 2:
                 int opcao3 = 50;
                 // ENQUANTO USUARIO NAO ESCOLHER SAIR
                 while(opcao3 != 0){
                    // MENU
                    System.out.println("\n== Boyer Moore ==");
                    System.out.println("0 - Voltar");
                    System.out.println("1 - Casamento por Nome com retorno de pilotos");
                    System.out.println("2 - Casamento por Nome booleano\n");
                    System.out.print("Escolha sua opção: ");
                    opcao3 = scanner.nextInt();
                    // SWITCH PARA O MENU
                    switch(opcao3){
                    case 0:
                       break;
                    case 1:
                       scanner.nextLine();
                       pilotos.clear();
                       boolean b = false;
                       // PERGUNTA QUAL O PADRAO DE BUSCA
                       System.out.println("Digite qual o padrão que você deseja procurar: ");
                       padrao = scanner.nextLine();
                       // LE TODOS OS PILOTOS DO ARQUIVO PARA FAZER A BUSCA
                       pilotos = arquivo.lerTodosPilotos();
                       // FAZ O CASAMENTO COM TODOS OS NOMES DOS PILOTOS
                       for(int i = 0; i < pilotos.size(); i++){
                          resultado = boyerMoore.buscar(padrao, pilotos.get(i).getNome());
                          // SE ENCONTROU O PADRAO MOSTRA O PILOTO
                          if(resultado){
                             pilotos.get(i).mostrar();
                             b = true;
                          }
                       }
                       if(!b)
                           System.out.println("Padrão não encontrado!");        
                       break;          
                    case 2:
                       scanner.nextLine();
                       pilotos.clear();
                       int x = 0;
                       // PERGUNTA QUAL O PADRAO DE BUSCA
                       System.out.println("Digite qual o padrão que você deseja procurar: ");
                       padrao = scanner.nextLine();
                       // LE TODOS OS PILOTOS DO ARQUIVO PARA FAZER A BUSCA
                       pilotos = arquivo.lerTodosPilotos();
                       // FAZ O CASAMENTO COM TODOS OS NOMES DOS PILOTOS
                       for(int i = 0; i < pilotos.size(); i++){
                          resultado = boyerMoore.buscar(padrao, pilotos.get(i).getNome());
                          // SE ENCONTROU O PADRAO MOSTRA A MENSAGEM
                          if(resultado){
                             System.out.println("Padrão encontrado!");
                             x++;
                          }
                       }
                       if(x == 0)
                           System.out.println("Padrão não encontrado!");
                       else
                           // MOSTRA QUANTAS VEZES O PADRAO FOI ENCONTRADO
                           System.out.println("O padrão foi encontrado " + x + " vezes.");
                       break;
                    default:
                       System.out.println("\nOpção Inválida!!!");
                       break;
                    }
                 }                               
                 break;
              default:
                  System.out.println("\nOpção Inválida!!!");
                  break;
           }
        }
    }
}
