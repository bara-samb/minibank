import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.Console;

public class Main {

    static DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {

        // prénom, nom, code, solde, numeroCompte
        String[][] utilisateurs = {
                {"Makhtar", "Wade", "1234", "500000", "ACC001"},
                {"Fallou", "Bousso", "2345", "60000", "ACC002"},
                {"Awa", "Sene", "3456", "100000", "ACC003"},
                {"Abdou", "Diouf", "4567", "20000", "ACC004"},
                {"Fatou", "Ndiaye", "5678", "60000", "ACC005"}
        };

        Scanner sc = new Scanner(System.in);

        int index = verifierCode(utilisateurs, sc);

        if (index != -1) {
            afficherMenu(utilisateurs, index, sc);
        }

        System.out.println("\nMerci d'avoir utilisé GAB TOUBA !");
        sc.close();
    }

    // ============================
    // VERIFIER CODE
    // ============================
    public static int verifierCode(String[][] users, Scanner sc) {

        Console con = System.console();
        int tentative = 0;
        String code, numCompte;
        while (tentative < 3) {
            System.out.println("Entrer le numero de compte : ");
            numCompte = sc.next();
            char [] readCodes = con.readPassword("Code secret (4 chiffres) : ");
            code = new String(readCodes);

            // Vérification format
            if (!code.matches("\\d{4}")) {
                tentative++;
                System.out.println("Code invalide (4 chiffres uniquement) "+ (3 - tentative) + " tentatives restantes.");
                continue;
            }

            for (int i = 0; i < users.length; i++) {
                if (code.equals(users[i][2]) && numCompte.equals(users[i][4])) {
                    System.out.println("Bienvenue " +
                            users[i][0].charAt(0) + "." + users[i][1]);
                    return i;
                }
            }

            tentative++;
            System.out.println("Numero de compte ou Code incorrect (" + (3 - tentative) + " tentatives restantes)");
        }

        return -1;
    }

    // ============================
    // AFFICHER MENU
    // ============================
    public static void afficherMenu(String[][] users, int index, Scanner sc) {

        boolean continuer = true;

        while (continuer) {

            System.out.println("\n--- MENU ---");
            System.out.println("1: Consulter solde");
            System.out.println("2: Retrait");
            System.out.println("3: Transfert");
            System.out.println("4: Quitter");
            System.out.print("Votre choix : ");

            String choix = sc.next();

            switch (choix) {
                case "1":
                    // On passe le numéro de compte
                    consulterSolde(users, users[index][4]);
                    break;

                case "2":
                    effectuerRetrait(users, index, sc);
                    break;

                case "3":
                    effectuerTransfert(users, index, sc);
                    break;

                case "4":
                    continuer = false;
                    break;
                default: System.out.println("mauvais choix");
            }
        }
    }

    // ============================
    // CONSULTER SOLDE PAR NUMERO
    // ============================
    public static void consulterSolde(String[][] users, String numeroCompte) {

        for (int i = 0; i < users.length; i++) {

            if (users[i][4].equals(numeroCompte)) {

                double solde = Double.parseDouble(users[i][3]);

                System.out.println("\nCompte : " + numeroCompte);
                System.out.println("Client : " +
                        users[i][0].charAt(0) + "." + users[i][1]);
                System.out.println("Solde : " + solde + " FCFA");

                return;
            }
        }

        System.out.println("Compte introuvable !");
    }

    // ============================
    // RETRAIT
    // ============================
    public static void effectuerRetrait(String[][] users, int index, Scanner sc) {

        double solde = Double.parseDouble(users[index][3]);
        double montant;
        while (true) {
            System.out.print("Entrez le montant : ");
            String saisie = sc.next();

            try {
                // On tente la conversion
                montant = Double.parseDouble(saisie);

                // Optionnel : vérifier que le montant n'est pas négatif
                if (montant > 0) {
                    break; // Saisie correcte et positive, on sort de la boucle !
                } else {
                    System.out.println("Erreur : Le montant doit être supérieur à 0.");
                }

            } catch (NumberFormatException e) {
                // Si l'utilisateur a tapé des lettres ou des symboles invalides
                System.out.println("Saisie invalide ! Veuillez entrer un nombre (ex: 1500).");
            }
        }

        if (montant <= solde) {
            solde -= montant;
            users[index][3] = String.valueOf(solde);

            System.out.println("Retrait réussi !");
            System.out.println("Nouveau solde : " + solde);
            System.out.println("Date : " + LocalDateTime.now().format(formatter));
        } else {
            System.out.println("Solde insuffisant");
        }
    }

    // ============================
    // TRANSFERT
    // ============================
    public static void effectuerTransfert(String[][] users, int index, Scanner sc) {

        double solde = Double.parseDouble(users[index][3]);

        System.out.print("Numéro compte destinataire : ");
        String compte = sc.next();

        // Vérification existence
        int indexDest = -1;
        for (int i = 0; i < users.length; i++) {
            if (users[i][4].equals(compte)) {
                indexDest = i;
                break;
            }
        }

        if (indexDest == -1) {
            System.out.println("Compte destinataire introuvable !");
            return;
        }


        double montant;

        while (true) {
            System.out.print("Entrez le montant : ");
            String saisie = sc.next();

            try {
                // On tente la conversion
                montant = Double.parseDouble(saisie);

                // Optionnel : vérifier que le montant n'est pas négatif
                if (montant > 0) {
                    break; // Saisie correcte et positive, on sort de la boucle !
                } else {
                    System.out.println("Erreur : Le montant doit être supérieur à 0.");
                }

            } catch (NumberFormatException e) {
                // Si l'utilisateur a tapé des lettres ou des symboles invalides
                System.out.println("Saisie invalide ! Veuillez entrer un nombre (ex: 1500).");
            }
        }
        double frais = montant * 0.01;
        double total = montant + frais;

        if (total > solde) {
            System.out.println("Solde insuffisant (montant + frais)");
            return;
        }

        // Débit
        solde -= total;
        users[index][3] = String.valueOf(solde);

        // Crédit destinataire
        double soldeDest = Double.parseDouble(users[indexDest][3]);
        soldeDest += montant;
        users[indexDest][3] = String.valueOf(soldeDest);

        // Confirmation
        System.out.println("\n=== TRANSFERT REUSSI ===");
        System.out.println("Vers : " + compte);
        System.out.println("Montant : " + montant);
        System.out.println("Frais : " + frais);
        System.out.println("Total débité : " + total);
        System.out.println("Solde restant : " + solde);
        System.out.println("Date : " + LocalDateTime.now().format(formatter));
    }
}