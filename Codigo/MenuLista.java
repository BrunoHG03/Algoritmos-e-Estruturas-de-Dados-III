import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MenuLista {
   public Scanner scanner = new Scanner(System.in);
   public ListaInvertida lista;
   public ListaInvertida lista2;
   public static ArquivoHash arq; // ARQUIVO HASH
   // ARQUIVO HASH
    static { try { arq = new ArquivoHash("PilotosHash"); } catch (Exception e) { e.printStackTrace();}}
   // public Arquivo arq; // ARQUIVO SEQUENCIAL
   // MENU
   public void menuLista() {
     try {
        File d = new File("/Dados");
        if (!d.exists())
          d.mkdir();
        lista = new ListaInvertida(4, "dicionarioLista.db", "blocosLista.db");
        lista2 = new ListaInvertida(4, "dicionarioLista2.db", "blocosLista2.db");
     int opcao = 50;
         // ENQUANTO USUARIO NAO ESCOLHER SAIR
         while(opcao != 0){
            // MENU
            System.out.println("\n== Lista Invertida ==");
            System.out.println("0 - Voltar");
            System.out.println("1 - Buscar Piloto");
            System.out.println("2 - Excluir Piloto");
            System.out.println("3 - Atualizar Piloto");
            System.out.println("4 - Criptografia");
            System.out.print("Escolha sua opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();
            // SWITCH PARA O MENU
            switch(opcao) {
               case 0:
                   break;
               case 1:
                   buscarPiloto();
                   break;
               case 2:
                   excluirPiloto();
                   break;
               case 3:
                   atualizarPiloto();
                   break;
               case 4:
                  int opcaoCripto = 50;
                  // ENQUANTO USUARIO NAO ESCOLHER SAIR
                  while(opcaoCripto < 0 || opcaoCripto > 3){
                     // MENU
                     System.out.println("\n== Criptografia ==");
                     System.out.println("0 - Voltar");
                     System.out.println("1 - Sim, Vigenere");
                     System.out.println("2 - Sim, RSA");
                     System.out.println("3 - Não\n");
                     System.out.print("Seu arquivo contém criptografia? ");
                     opcaoCripto = scanner.nextInt();
                     // SWITCH PARA O MENU
                     switch(opcaoCripto) {
                       case 0:
                           break;
                       case 1:
                           arq.setCripto(1);
                           break;
                       case 2:
                           arq.setCripto(2);
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
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void buscarPiloto() throws Exception{
     Piloto piloto = new Piloto();
     System.out.print("\nDigite a chave que deseja buscar: ");
     String chave = scanner.nextLine();
     String[] chaves = chave.split(" ");
     boolean b = false;
     boolean b2 = false;
     for(int i = 0; i < chaves.length; i++){
        ElementoLista[] elementos = lista.buscarLista(chaves[i]);
        ElementoLista[] elementos2 = lista2.buscarLista(chaves[i]);
        for(int j = 0; j < elementos.length; j++){
           piloto = arq.buscarPiloto(elementos[j].getId());
           piloto.mostrarLista();
           b = true;
        }
        for(int j = 0; j < elementos2.length; j++){
            piloto = arq.buscarPiloto(elementos2[j].getId());
            // piloto = arq.buscarPilotoId(elementos2[j].getId()); // ARQUIVO SEQUENCIAL
            piloto.mostrarLista(); // ARQUIVO HASH
            b2 = true;
         }
     }
     if(!b && !b2)
       System.out.println("\nSem resultados encontrados!");
  }

  public void excluirPiloto() throws Exception{
    Piloto piloto = new Piloto();
    boolean b = false;
    boolean b2 = false;
    System.out.print("\nDigite a chave que você deseja excluir: ");
    String chave = scanner.nextLine();
    String[] chaves = chave.split(" ");
    ElementoLista[] elementos = null;
    ElementoLista[] elementos2 = null;
    ArrayList<Piloto> pilotos = new ArrayList<>();
    for(int i = 0; i < chaves.length; i++){
       elementos = lista.buscarLista(chaves[i]);
       elementos2 = lista2.buscarLista(chaves[i]);
       for(int j = 0; j < elementos.length; j++){
          b = true;
          piloto = arq.buscarPiloto(elementos[j].getId());
          pilotos.add(piloto);
          piloto.mostrarLista();
        }
        for(int j = 0; j < elementos2.length; j++){
            b2 = true;
            piloto = arq.buscarPiloto(elementos2[j].getId());
            pilotos.add(piloto);
            piloto.mostrarLista();
          }
    }
     if(!b && !b2)
       System.out.println("\nSem resultados encontrados!");
     else{
     int opcao = 10;
     while(opcao != 0 && opcao != 1){
        System.out.print("\nDeseja excluir esses pilotos? [0 - NÃO | 1 - SIM]: ");
        opcao = scanner.nextInt();
     }
     if(opcao == 1){
        for(int i = 0; i < pilotos.size(); i++){
            String[] nomes = pilotos.get(i).nome.split(" ");
            for(int j = 0; j < nomes.length; j++){
                lista.excluirLista(nomes[j], pilotos.get(i).id);
            }
            String[] paises = pilotos.get(i).pais.split(" ");
            for(int j = 0; j < paises.length; j++){
                lista2.excluirLista(paises[j], pilotos.get(i).id);
            }
            arq.excluirPiloto(pilotos.get(i).id);
         }
         System.out.println("\nPilotos excluidos com sucesso!");
     } else {
        System.err.println("\nOperação Cancelada!");
     }
    }
  }

  public void atualizarPiloto() throws Exception{
    Piloto piloto = new Piloto();
    boolean b = false;
    boolean b2 = false;
    System.out.print("\nDigite a chave que você deseja atualizar: ");
    String chave = scanner.nextLine();
    String[] chaves = chave.split(" ");
    ElementoLista[] elementos = null;
    ElementoLista[] elementos2 = null;
    ArrayList<Piloto> pilotos = new ArrayList<>();
    for(int i = 0; i < chaves.length; i++){
       elementos = lista.buscarLista(chaves[i]);
       elementos2 = lista2.buscarLista(chaves[i]);
       for(int j= 0; j < elementos.length; j++){
          b = true;
          piloto = arq.buscarPiloto(elementos[j].getId());
          pilotos.add(piloto);
          piloto.mostrar();
          int opcao = 10;
        while(opcao != 0 && opcao != 1){
          System.out.print("\nDeseja atualizar esse piloto? [0 - NÃO | 1 - SIM]: ");
          opcao = scanner.nextInt();
        }
        if(opcao == 1)
          atualizarPiloto(piloto);
        else 
          System.err.println("\nOperação Cancelada!");
        }
        for(int j= 0; j < elementos2.length; j++){
            b2 = true;
            piloto = arq.buscarPiloto(elementos2[j].getId());
            pilotos.add(piloto);
            piloto.mostrar();
            int opcao = 10;
          while(opcao != 0 && opcao != 1){
            System.out.print("\nDeseja atualizar esse piloto? [0 - NÃO | 1 - SIM]: ");
            opcao = scanner.nextInt();
          }
          if(opcao == 1)
            atualizarPiloto(piloto);
          else 
            System.err.println("\nOperação Cancelada!");
          }
    }
     if(!b && !b2)
       System.out.println("\nSem resultados encontrados!");
  }

    // METODO PARA ATUALIZAR UM PILOTO
    public void atualizarPiloto(Piloto atualizar) throws Exception{
        String equipe = new String();
        String siglaStr = new String();
        // FORMATO DA DATA
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        boolean confereData = false;
        String data = new String();
        int opcao = 3;
        int opcaoB = 3;
        boolean atualizouNome = false;
        boolean atualizouPais = false;
        String nomeAntigo = new String();
        String paisAntigo = new String();
        // EM SEGUIDA PERGUNTANDO ATRIBUTO POR ATRIBUTO SE DESEJA ATUALIZAR
        // NOME
        System.out.print("\nEsse é o nome atual: "+ atualizar.nome + "\nDeseja atualizar? [0 - NÃO | 1 - SIM]: ");
        opcao = scanner.nextInt();
        scanner.nextLine();
        if(opcao == 1){
            atualizouNome = true;
            nomeAntigo = atualizar.nome;
            String primeiroNome = new String();
            String sobrenome = new String();
            System.out.print("Digite o novo Primeiro nome: ");
            primeiroNome = scanner.nextLine();
            System.out.print("Digite o novo Sobrenome: ");
            sobrenome = scanner.nextLine();
            atualizar.setNome(primeiroNome + " " + sobrenome);
            // SIGLA COM NOVO NOME
            siglaStr = ("" + sobrenome.charAt(0) + sobrenome.charAt(1) + sobrenome.charAt(2)).toUpperCase();
            System.out.print("\nEssa é a sigla atual: " + atualizar.sigla[0] + atualizar.sigla[1] + atualizar.sigla[2] + "\nEssa é a sigla nova gerada automáticamente: " + siglaStr + "\nQual você deseja utilizar? [0 - ATUAL | 1 - NOVA GERADA | 2 - FAZER UMA NOVA]: ");
            opcaoB = scanner.nextInt();
            if(opcaoB == 1){
                atualizar.setSigla(new char[]{siglaStr.charAt(0), siglaStr.charAt(1), siglaStr.charAt(2)});
            } else if(opcaoB == 2){
                System.out.print("Digite a nova sigla: ");
                scanner.nextLine();
                siglaStr = scanner.nextLine().toUpperCase();
                atualizar.setSigla(new char[]{siglaStr.charAt(0), siglaStr.charAt(1), siglaStr.charAt(2)});
            } else if(opcaoB > 2 || opcaoB < 0){
                atualizar.setSigla(new char[]{siglaStr.charAt(0), siglaStr.charAt(1), siglaStr.charAt(2)});
                System.out.println("Opção Inválida!!! Definindo nova sigla com a sigla gerada");
            }
        } else {
            if(opcao > 1 || opcao < 0)
               System.out.println("Opção Inválida!!! Nome mantido");
            // SIGLA COM ANTIGO NOME
            System.out.print("\nEssa é a sigla atual: " + atualizar.sigla[0] + atualizar.sigla[1] + atualizar.sigla[2] + "\nDeseja atualizar? [0 - NÃO | 1 - SIM]: ");
            opcao = scanner.nextInt();
            if(opcao == 1){
                scanner.nextLine();
                System.out.print("Digite a nova sigla: ");
                siglaStr = scanner.nextLine().toUpperCase();
                atualizar.setSigla(new char[]{siglaStr.charAt(0), siglaStr.charAt(1), siglaStr.charAt(2)});
            } else if(opcao > 1 || opcao < 0){
                System.out.println("Opção Inválida!!! Sigla mantida");
            }
        }
        // PAIS
        System.out.print("\nEsse é o país atual do piloto: " + atualizar.pais + "\nDeseja atualizar? [0 - NÃO | 1 - SIM]: ");
        opcao = scanner.nextInt();
        scanner.nextLine();
        if(opcao == 1){
            atualizouPais = true;
            paisAntigo = atualizar.pais;
            System.out.print("Digite o novo país: ");
            atualizar.setPais(scanner.nextLine());
        } else if(opcao > 1 || opcao < 0){
            System.out.println("Opção Inválida!!! País mantido");
        }
        // TITULOS
        System.out.print("\nEsse é o número de títulos atual do piloto: " + atualizar.titulos + "\nDeseja atualizar? [0 - NÃO | 1 - SIM]: ");
        opcao = scanner.nextInt();
        if(opcao == 1){
            System.out.print("Digite o novo número de títulos: ");
            atualizar.setTitulos(scanner.nextInt());
        } else if(opcao > 1 || opcao < 0){
            System.out.println("Opção Inválida!!! Número de títulos mantido");
        }
        // CORRIDAS
        System.out.print("\nEsse é o número de corridas do piloto: " + atualizar.corridas + "\nDeseja atualizar? [0 - NÃO | 1 - SIM]: ");
        opcao = scanner.nextInt();
        if(opcao == 1){
            System.out.print("Digite o novo número de corridas: ");
            atualizar.setCorridas(scanner.nextInt());
        } else if(opcao > 1 || opcao < 0){
            System.out.println("Opção Inválida!!! Número de corridas mantido");
        }
        // POLE POSITIONS
        System.out.print("\nEsse é o número de poles do piloto: " + atualizar.poles + "\nDeseja atualizar? [0 - NÃO | 1 - SIM]: ");
        opcao = scanner.nextInt();
        if(opcao == 1){
            System.out.print("Digite o novo número de poles: ");
            atualizar.setPoles(scanner.nextInt());
        } else if(opcao > 1 || opcao < 0){
            System.out.println("Opção Inválida!!! Número de poles mantido");
        }
        // VITORIAS
        System.out.print("\nEsse é o número de vitórias do piloto: " + atualizar.vitorias + "\nDeseja atualizar? [0 - NÃO | 1 - SIM]: ");
        opcao = scanner.nextInt();
        if(opcao == 1){
            System.out.print("Digite o novo número de vitórias: ");
            atualizar.setVitorias(scanner.nextInt());
        } else if(opcao > 1 || opcao < 0){
            System.out.println("Opção Inválida!!! Número de vitórias mantido");
        }
        // PODIOS
        System.out.print("\nEsse é o número de pódios do piloto: " + atualizar.podios + "\nDeseja atualizar? [0 - NÃO | 1 - SIM]: ");
        opcao = scanner.nextInt();
        if(opcao == 1){
            System.out.print("Digite o novo número de pódios: ");
            atualizar.setPodios(scanner.nextInt());
        } else if(opcao > 1 || opcao < 0){
            System.out.println("Opção Inválida!!! Número de pódios mantido");
        }
        // VOLTAS MAIS RAPIDAS
        System.out.print("\nEsse é o número de voltas mais rápidas do piloto: " + atualizar.voltasRapidas + "\nDeseja atualizar? [0 - NÃO | 1 - SIM]: ");
        opcao = scanner.nextInt();
        if(opcao == 1){
            System.out.print("Digite o novo número de voltas mais rápidas: ");
            atualizar.setVoltasRapidas(scanner.nextInt());
        } else if(opcao > 1 || opcao < 0){
            System.out.println("Opção Inválida!!! Número de voltas mais rápidas mantido");
        }
        // PONTOS
        System.out.print("\nEsse é o número de pontos do piloto: " + atualizar.pontos + "\nDeseja atualizar? [0 - NÃO | 1 - SIM]: ");
        opcao = scanner.nextInt();
        if(opcao == 1){
            System.out.print("Digite o novo número de pontos: ");
            atualizar.setPontos(scanner.nextDouble());
        } else if(opcao > 1 || opcao < 0){
            System.out.println("Opção Inválida!!! Número de pontos mantido");
        }
        // EQUIPES
        System.out.print("\nEssas são as equipes do piloto: " + atualizar.equipes + "\nDeseja atualizar? [0 - NÃO | 1 - ATUALIZAR TODAS | 2 - SOMENTE ADICIONAR]: ");
        opcao = scanner.nextInt();
        if(opcao == 1){
            atualizar.equipes.clear();
            System.out.print("Digite as novas equipes:\n[DIGITE UMA POR UMA E QUANDO FINALIZAR DIGITE FIM]: ");
            scanner.nextLine();
            equipe = scanner.nextLine();
            while(!(equipe.equals("FIM"))){
                atualizar.addEquipe(equipe);
                System.out.print("Próxima equipe: ");
                equipe = scanner.nextLine();
            }
            if(atualizar.equipes.isEmpty()){
                atualizar.addEquipe("Não Disponível");
            }
        } else if(opcao == 2){
            System.out.print("Digite as equipes que você deseja adicionar:\n[DIGITE UMA POR UMA E QUANDO FINALIZAR DIGITE FIM]: ");
            scanner.nextLine();
            equipe = scanner.nextLine();
            while(!(equipe.equals("FIM"))){
                atualizar.addEquipe(equipe);
                System.out.print("Próxima equipe: ");
                equipe = scanner.nextLine();
            }
        } else if(opcao > 2 || opcao < 0){
            System.out.println("Opção Inválida!!! Equipes mantidas");
        }
        // DATA DE NASCIMENTO
        System.out.print("\nEssa é a data de nascimento do piloto: "+ atualizar.nascimento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\nDeseja atualizar? [0 - NÃO | 1 - SIM]: ");
        opcao = scanner.nextInt();
        if(opcao == 1){
           scanner.nextLine();
           do{
              System.out.print("Nova Data de nascimento [DD/MM/AAAA]: ");
              data = scanner.nextLine();
              try {
                  atualizar.setNascimento(LocalDate.parse(data, formato));
                  confereData = true;
              } catch (Exception e) {
                  System.err.println("Data inválida! Use o formato DD/MM/AAAA");
              }
           } while(!confereData);
        } else if(opcao > 1 || opcao < 0){
           System.out.println("Opção Inválida!!! Data de nascimento mantida");
        }
        // FUNCAO
        System.out.print("\nEssa é a função do piloto: " + atualizar.funcao + "\nDeseja atualizar? [0 - NÃO | 1 - SIM]: ");
        opcao = scanner.nextInt();
        if(opcao == 1){
            System.out.print("Escolha a nova função do piloto:\n[1 - Titular | 2 - Treino Livre | 3 - Teste / Reserva / Desenvolvimento | 4 - Não é piloto da F1]: ");
            opcaoB = scanner.nextInt();
            // SWITCH DA FUNCAO DO PILOTO
            switch (opcaoB) {
              case 1:
                  atualizar.setFuncao("Titular");
                  break;
              case 2:
                  atualizar.setFuncao("Treino Livre");
                  break;
              case 3:
                  atualizar.setFuncao("Teste / Reserva / Desenvolvimento");
                  break;
              case 4:
                  atualizar.setFuncao("Não é piloto de F1");
                  break;
              default:
                  // SE NAO FOR ESCOLHIDO NENHUMA DAS OPCOES ACIMA, SEMPRE SERA CONSIDERADA A OPCAO 4
                  atualizar.setFuncao("Não é piloto de F1");
                  System.out.println("Opção Inválida, opção considerada: 4 - Não é piloto de F1");
                  break;
            }       
        } else if(opcao > 1 || opcao < 0){
            System.out.println("Opção Inválida!!! Função mantida");
        }
        // MOSTRANDO DADOS ATUALIZADO E PERGUNTANDO SE DESEJA ATUALIZAR
        atualizar.mostrar();
        System.out.print("Deseja realmente atualizar o piloto? [0 - NÃO | 1 - SIM]: ");
        opcao = scanner.nextInt(); 
        while(opcao != 1 && opcao != 0){
            System.out.print("Opção Inválida!!! \nDeseja realmente atualizar o piloto? [0 - NÃO | 1 - SIM]: ");
            opcao = scanner.nextInt(); 
        }
        if(opcao == 1){
            boolean b = arq.atualizarPiloto(atualizar); // CHAMANDO PARA ATUALIZAR NO ARQUIVO
            if(atualizouNome){
                String[] palavrasAnt = nomeAntigo.split(" ");
                for(int i = 0; i < palavrasAnt.length; i++){
                  lista.excluirLista(palavrasAnt[i], atualizar.id);
                }
                String[] palavras = atualizar.nome.split(" ");
                for(int i = 0; i < palavras.length; i++){
                  lista.adicionaLista(palavras[i], new ElementoLista(atualizar.id, 1));
                }
            }
            if(atualizouPais){
                String[] palavrasAnt = paisAntigo.split(" ");
                for(int i = 0; i < palavrasAnt.length; i++){
                  lista.excluirLista(palavrasAnt[i], atualizar.id);
                }
                String[] palavras = atualizar.pais.split(" ");
                for(int i = 0; i < palavras.length; i++){
                  lista.adicionaLista(palavras[i], new ElementoLista(atualizar.id, 1));
                }
            }
            if(b)
               System.out.println("\nPiloto atualizado com sucesso");
            else
               System.out.println("Erro ao atualizar o piloto! Verifique o ID!");
        } else {
            System.out.println("Operação cancelada!");
        }
    }
}
