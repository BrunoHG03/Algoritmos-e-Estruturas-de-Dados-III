// COMPILAR javac -encoding UTF-8
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

// CLASSE MENU DA TABELA HASH EXTENSIVEL
public class MenuHash{
    public static Scanner scanner = new Scanner(System.in);
    public static ArquivoHash arquivo;
    static {
        try{
            arquivo = new ArquivoHash("PilotosHash");
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    public static int i;
    public static ListaInvertida lista;
    public static ListaInvertida lista2;

    // METODO MENU
    public static void menuHashExtensivel() throws Exception{
        int opcao = 50;
        // ENQUANTO USUARIO NAO ESCOLHER SAIR
        while(opcao != 0){
           // MENU
           System.out.println("\n== Hash Extensível ==");
           System.out.println("0 - Voltar");
           System.out.println("1 - Fazer Carga com Hash");
           System.out.println("2 - Adicionar Piloto");
           System.out.println("3 - Excluir Piloto");
           System.out.println("4 - Atualizar Piloto");
           System.out.println("5 - Buscar Piloto");
           System.out.println("6 - Criptografia\n");
           System.out.print("Escolha sua opção: ");
           opcao = scanner.nextInt();
           // SWITCH PARA O MENU
           switch(opcao){
              case 0:
                  break;
              case 1:
                  int opcaoCripto = 50;
                  // ENQUANTO USUARIO NAO ESCOLHER SAIR
                  while(opcaoCripto < 0 || opcaoCripto > 3){
                     // MENU
                     System.out.println("\n== Criptografia ==");
                     System.out.println("0 - Voltar");
                     System.out.println("1 - Vigenere");
                     System.out.println("2 - RSA");
                     System.out.println("3 - Nenhum\n");
                     System.out.print("Escolha sua opção: ");
                     opcaoCripto = scanner.nextInt();
                     // SWITCH PARA O MENU
                     switch(opcaoCripto) {
                       case 0:
                           break;
                       case 1:
                           arquivo.setCripto(1);
                           break;
                       case 2:
                           arquivo.setCripto(2);
                           break;
                       default:
                           System.out.println("\nOpção Inválida!!!");
                           break;
                      }
                   }
                  arquivo.criarHash();
                  break;
              case 2:
                  adicionarPiloto();
                  break;
              case 3:
                  excluirPiloto();
                  break;
              case 4:
                  atualizarPiloto();
                  break;
              case 5:
                  buscarPiloto();
                  break;
              case 6:
                  int opcaoCripto1 = 50;
                  // ENQUANTO USUARIO NAO ESCOLHER SAIR
                  while(opcaoCripto1 < 0 || opcaoCripto1 > 3){
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
                           arquivo.setCripto(1);
                           break;
                       case 2:
                           arquivo.setCripto(2);
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

    // METODO PARA ADICIONAR UM PILOTO
    public static void adicionarPiloto() throws Exception{
        scanner.nextLine();
        // NOVO PILOTO
        Piloto novo = new Piloto();
        // VARIAVEIS TEMPORARIAS
        String primeiroNome = new String();
        String sobrenome = new String();
        char[] sigla = new char[3];
        String siglaStr = new String();
        String equipe = new String();
        String data = new String();
        boolean confereData = false;
        int opcao = 3;
        // FORMATO DA DATA
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        // LEITURA DOS DADOS
        System.out.print("\nPrimeiro nome: ");
        primeiroNome = scanner.nextLine();
        System.out.print("Sobrenome: ");
        sobrenome = scanner.nextLine();
        novo.setNome(primeiroNome + ' ' + sobrenome);
        // FAZENDO SIGLA AUTOMATICAMENTE
        for(int i = 0; i < 3; i++){
            sigla[i] = Character.toUpperCase(sobrenome.charAt(i));
        }
        // ESCOLHENDO A SIGLA
        System.out.print("Você deseja que a sigla seja " + sigla[0] + sigla[1] + sigla[2] +" ?\n[0 - NÃO | 1 - SIM]: ");
        opcao = scanner.nextInt();
        scanner.nextLine();
        // DIGITANDO UMA NOVA SIGLA
        if(opcao == 0){
            System.out.print("Digite a nova sigla: ");
            siglaStr = scanner.nextLine();
            for(int i = 0; i < 3; i++){
                sigla[i] = Character.toUpperCase(siglaStr.charAt(i));
            }
        } else if(opcao > 1 || opcao < 0){
            System.out.println("Opção Inválida!!! Sigla Mantida");
        }
        novo.setSigla(sigla);
        // RESTANTE DA LEITURA
        System.out.print("País: ");
        novo.setPais(scanner.nextLine());
        System.out.print("Títulos: ");
        novo.setTitulos(scanner.nextInt());
        System.out.print("Corridas: ");
        novo.setCorridas(scanner.nextInt());
        System.out.print("Poles: ");
        novo.setPoles(scanner.nextInt());
        System.out.print("Vitórias: ");
        novo.setVitorias(scanner.nextInt());
        System.out.print("Pódios: ");
        novo.setPodios(scanner.nextInt());
        System.out.print("Voltas mais rápidas: ");
        novo.setVoltasRapidas(scanner.nextInt());
        System.out.print("Pontos: ");
        novo.setPontos(scanner.nextDouble());
        scanner.nextLine();
        // LENDO EQUIPES ATE "FIM"
        System.out.print("Equipes pelas quais o piloto passou:\n[DIGITE UMA POR UMA E QUANDO FINALIZAR DIGITE FIM]: ");
        equipe = scanner.nextLine();
        while(!(equipe.equals("FIM"))){
            novo.addEquipe(equipe);
            System.out.print("Próxima equipe: ");
            equipe = scanner.nextLine();
        }
        // SE NAO TIVER EQUIPES SERA CONSIDERADA COMO NAO DISPONIVEL
        if(novo.equipes.isEmpty()){
            novo.addEquipe("Não Disponível");
        }
        // LENDO A DATA DE NASCIMENTO
        do{
            System.out.print("Data de nascimento [DD/MM/AAAA]: ");
            data = scanner.nextLine();
            try{
                novo.setNascimento(LocalDate.parse(data, formato));
                confereData = true;
            } catch(Exception e){
                System.err.print("Data inválida! Use o formato DD/MM/AAAA \n");
            }
        } while(!confereData);
        // ESCOLHENDO A FUNCAO DO PILOTO
        opcao = 0;
        System.out.print("Escolha a função do seu piloto:\n[1 - Titular | 2 - Treino Livre | 3 - Teste / Reserva / Desenvolvimento | 4 - Não é piloto da F1]: ");
        opcao = scanner.nextInt();
        // SWITCH DA FUNCAO DO PILOTO
        switch(opcao){
            case 1:
                novo.setFuncao("Titular");
                break;
            case 2:
                novo.setFuncao("Treino Livre");
                break;
            case 3:
                novo.setFuncao("Teste / Reserva / Desenvolvimento");
                break;
            case 4:
                novo.setFuncao("Não é piloto de F1");
                break;
            default:
                // SE NAO FOR ESCOLHIDO NENHUMA DAS OPCOES ACIMA, SEMPRE SERA CONSIDERADA A OPCAO 4
                novo.setFuncao("Não é piloto de F1");
                System.out.println("Opção Inválida, opção considerada: 4 - Não é piloto de F1");
                break;
        }
        // MOSTRANDO O NOVO PILOTO E PERGUNTANDO SE DESEJA ADICIONA-LO
        opcao = 3;
        novo.mostrar();
        System.out.print("Deseja adicionar esse piloto? [0 - NÃO | 1 - SIM]: ");
        opcao = scanner.nextInt();
        while(opcao != 0 && opcao != 1){
            System.out.print("Opção Inválida!!!\nDeseja adicionar esse piloto? [0 - NÃO | 1 - SIM]: ");
            opcao = scanner.nextInt();
        }
        if(opcao == 1){
            arquivo.adicionarPiloto(novo); // CHAMANDO A FUNCAO PARA ADICIONAR O PILOTO NO ARQUIVO
        } else{
            System.out.println("Operação Cancelada!");
        }
    }

    // METODO PARA EXCLUIR O PILOTO
    public static void excluirPiloto() throws Exception{
        lista = new ListaInvertida(4, "dicionarioLista.db", "blocosLista.db");
        lista2 = new ListaInvertida(4, "dicionarioLista2.db", "blocosLista2.db");
        int id = 0;
        boolean b = false;
        int opcao = 3;
        Piloto excluir = new Piloto();
        // ESCOLHENDO O ID DO PILOTO QUE DESEJA EXCLUIR
        System.out.print("Qual o id do piloto que você deseja excluir? ");
        id = scanner.nextInt();
        // BUSCANDO O PILOTO PELO ID E MOSTRANDO NA TELA
        excluir = arquivo.buscarPiloto(id);
        excluir.mostrar();
        // PERGUNTANDO SE DESEJA EXCLUI-LO
        System.out.print("Deseja excluir esse piloto? [0 - NÃO | 1 - SIM]: ");
        opcao = scanner.nextInt();
        while(opcao != 0 && opcao != 1){
            System.out.print("Opção Inválida!!!\nDeseja excluir esse piloto? [0 - NÃO | 1 - SIM]: ");
            opcao = scanner.nextInt();
        }
        if(opcao == 1){
            b = arquivo.excluirPiloto(excluir.id); // CHAMANDO A FUNCAO PARA EXCLUIR O PILOTO NO ARQUIVO
            // ATUALIZANDO AS LISTAS INVERTIDAS
            String[] nomes = excluir.nome.split(" ");
            for(int i = 0; i < nomes.length; i++){
                lista.excluirLista(nomes[i], excluir.id);
            }
            String[] paises = excluir.pais.split(" ");
            for(int i = 0; i < paises.length; i++){
                lista2.excluirLista(paises[i], excluir.id);
            }
            if(b)
              System.out.println("Piloto excluído com sucesso");
            else
              System.out.println("Ocorreu um problema, não foi possível excluir.\nVERIFIQUE O ID!");
        } else{
            System.out.println("Operação Cancelada!");
        }
    }

    // METODO PARA ATUALIZAR UM PILOTO
    public static void atualizarPiloto() throws Exception{
        lista = new ListaInvertida(4, "dicionarioLista.db", "blocosLista.db");
        lista2 = new ListaInvertida(4, "dicionarioLista2.db", "blocosLista2.db");
        Piloto atualizar = new Piloto();
        String nomeAntigo = new String();
        String paisAntigo = new String();
        String equipe = new String();
        String siglaStr = new String();
        // FORMATO DA DATA
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        boolean confereData = false;
        boolean atualizouNome = false;
        boolean atualizouPais = false;
        String data = new String();
        int id = 0;
        int opcao = 3;
        int opcaoB = 3;
        // PERGUNTANDO QUAL ID DESEJA SER ATUALIZADO
        System.out.print("Qual o id do piloto que você deseja atualizar? ");
        id = scanner.nextInt();
        atualizar = arquivo.buscarPiloto(id);
        // EM SEGUIDA PERGUNTANDO ATRIBUTO POR ATRIBUTO SE DESEJA ATUALIZAR
        // NOME
        System.out.print("\nEsse é o nome atual: "+ atualizar.nome + "\nDeseja atualizar? [0 - NÃO | 1 - SIM]: ");
        opcao = scanner.nextInt();
        scanner.nextLine();
        if(opcao == 1){
            String primeiroNome = new String();
            String sobrenome = new String();
            atualizouNome = true;
            nomeAntigo = atualizar.nome;
            System.out.print("Digite o novo Primeiro nome: ");
            primeiroNome = scanner.nextLine();
            System.out.print("Digite o novo Sobrenome: ");
            sobrenome = scanner.nextLine();
            atualizar.setNome(primeiroNome + " " + sobrenome);
            // SIGLA COM NOVO NOME
            siglaStr =("" + sobrenome.charAt(0) + sobrenome.charAt(1) + sobrenome.charAt(2)).toUpperCase();
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
        } else{
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
              try{
                  atualizar.setNascimento(LocalDate.parse(data, formato));
                  confereData = true;
              } catch(Exception e){
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
            switch(opcaoB){
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
            boolean b = arquivo.atualizarPiloto(atualizar); // CHAMANDO PARA ATUALIZAR NO ARQUIVO
            // ATUALIZANDO LISTA INVERTIDA 01
            if(atualizouNome){
               String[] nomesAnt = nomeAntigo.split(" ");
               for(int i = 0; i < nomesAnt.length; i++){
                  lista.excluirLista(nomesAnt[i], atualizar.id);
               }
               String[] nomes = atualizar.nome.split(" ");
               for(int i = 0; i < nomes.length; i++){
                  lista.adicionaLista(nomes[i], new ElementoLista(atualizar.id, 1));
               }
            }
            // ATUALIZANDO LISTA INVERTIDA 02
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
        } else{
            System.out.println("Operação cancelada!");
        }
    }

    // METODO PARA BUSCAR O PILOTO E MOSTRAR NA TELA
    public static void buscarPiloto() throws Exception{
        int id = 0;
        Piloto busca = new Piloto();
        // PERGUNTANDO O ID E BUSCANDO O PILOTO POR ELE
        System.out.print("Digite o id do piloto que você deseja buscar: ");
        id = scanner.nextInt();
        busca = arquivo.buscarPiloto(id);
        if(busca.id == 0)
           System.out.println("Piloto não encontrado!");
        else
           // MOSTRANDO NA TELA
           busca.mostrar();
    }
}