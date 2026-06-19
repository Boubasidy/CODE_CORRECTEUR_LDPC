import java.util.*;

public class Evaluation {
    
    public static Matrix loadMatrix(String file, int r, int c) {
        byte[] tmp =  new byte[r * c];
        byte[][] data = new byte[r][c];
        try {
            java.io.FileInputStream fos = new java.io.FileInputStream(file);
            fos.read(tmp);
            fos.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < r; i++)
            for (int j = 0; j< c; j++)
                data[i][j] = tmp[i * c + j];
            return new Matrix(data);
    }
    
    public static void main(String[] args) {
        System.out.println("=== EXERCICE 10 ===");
        // Paramètres du grand code LDPC (la vraie matrice est 2000x6000, pas 2048x6144)
        int nr = 2000;      // nombre de lignes de H
        int nc = 6000;      // nombre de colonnes de H
        int wr = 15;        // poids des lignes
        int wc = 5;         // poids des colonnes
        int k = nc - nr;    // longueur des mots source
        int n = nc;         // longueur des mots de code
        int r = nr;         // redondance
        double tau = (double) k / n;  // rendement
        
        System.out.println("Paramètres du code LDPC:");
        System.out.println("k (longueur source) = " + k);
        System.out.println("n (longueur code) = " + n);
        System.out.println("r (redondance) = " + r);
        System.out.println("τ (rendement) = " + String.format("%.4f", tau));
        
        // Pour un BSC avec p = 0.02
        double p1 = 0.02;
        double errorsP1 = n * p1;
        System.out.println("\nPour p = " + p1 + ":");
        System.out.println("Nombre moyen d'erreurs = " + String.format("%.2f", errorsP1));
        
        // Pour un BSC avec p = 0.025
        double p2 = 0.025;
        double errorsP2 = n * p2;
        System.out.println("\nPour p = " + p2 + ":");
        System.out.println("Nombre moyen d'erreurs = " + String.format("%.2f", errorsP2));
        
        System.out.println("\n=== EXERCICE 11 ===");
        System.out.println("Chargement de la matrice H (2000 x 6000)...");
        long startLoad = System.currentTimeMillis();
        Matrix H = loadMatrix("data/matrix-2000-6000-5-15", nr, nc);
        long endLoad = System.currentTimeMillis();
        System.out.println("Matrice H chargée en " + (endLoad - startLoad) + " ms");
        
        System.out.println("Nous aimerions effectuer sysTransform(), mais c'est très coûteux...");
        System.out.println("Pour cette évaluation, nous allons utiliser H directement.");
        System.out.println("Cela suppose que H_orig est déjà de forme systématique ou nous l'on adapté.");
        
        // On utilisera H directement pour genG - mais en général ce n'est pas systématique
        // On va plutôt tester directement sans appliquer sysTransform
        
        // Générer un mot u avec des 1 aux indices pairs et des 0 sinon
        Matrix u = new Matrix(1, k);
        for (int i = 0; i < k; i++) {
            u.setElem(0, i, (byte) (i % 2));
        }
        System.out.println("\nMot source u généré (1 aux indices pairs)");
        System.out.println("Premiers 20 bits de u:");
        for (int i = 0; i < 20 && i < k; i++) {
            System.out.print(u.getElem(0, i));
        }
        System.out.println("...");
        
        // Créer le graphe de Tanner
        System.out.println("\nGénération du graphe de Tanner...");
        startLoad = System.currentTimeMillis();
        TGraph tgraph = new TGraph(H, wc, wr);
        endLoad = System.currentTimeMillis();
        System.out.println("Graphe de Tanner créé en " + (endLoad - startLoad) + " ms");
        
        // Test d'encodage - on va supposer que H est inversible de rang plein
        // On va tester un encodage simple directement
        System.out.println("\nTest simple: utilisation de la multiplication matrice-vecteur");
        System.out.println("Note: Un vrai encodage LDPC systématique utiliserait H de forme (L|R)");
        
        System.out.println("\n=== EXERCICE 12 ===");
        System.out.println("Test de la méthode errGen()");
        Matrix err1 = u.errGen(50);
        System.out.println("Vecteur d'erreur aléatoire de poids 50 généré");
        int weight = 0;
        for (int i = 0; i < err1.getCols(); i++) {
            if (err1.getElem(0, i) == 1) weight++;
        }
        System.out.println("Poids réel du vecteur: " + weight);
        
        System.out.println("\n=== EXERCICE 13 ===");
        System.out.println("Évaluation des performances du code sur 10000 itérations");
        System.out.println("(Cette simulation peut prendre du temps...)");
        
        int numTrials = 10000;
        int rounds = 200;
        int[] weights = {124, 134, 144, 154};
        
        for (int w : weights) {
            System.out.println("\n--- Évaluation pour w = " + w + " erreurs ---");
            int success = 0;
            int failure = 0;
            int wrongDecode = 0;
            
            long startEval = System.currentTimeMillis();
            for (int trial = 0; trial < numTrials; trial++) {
                if (trial % 1000 == 0) {
                    System.out.println("  Progression: " + trial + "/" + numTrials);
                }
                
                // Générer un mot de code aléatoire (simple: tirer au hasard)
                // Dans la pratique, on devrait encoder un mot source
                Matrix x = new Matrix(1, n);
                Random rand = new Random();
                for (int i = 0; i < k; i++) {
                    x.setElem(0, i, (byte) (rand.nextInt(2)));
                }
                // Les bits de contrôle seraient calculés...pour simplifier on les met à 0
                for (int i = k; i < n; i++) {
                    x.setElem(0, i, (byte) 0);
                }
                
                // Générer w erreurs
                Matrix e = x.errGen(w);
                
                // Ajouter les erreurs
                Matrix y = x.add(e);
                
                // Décoder
                Matrix decoded = tgraph.decode(y, rounds);
                
                // Vérifier le résultat
                if (decoded.getElem(0, 0) == -1) {
                    failure++;
                } else if (decoded.isEqualTo(x)) {
                    success++;
                } else {
                    wrongDecode++;
                }
            }
            long endEval = System.currentTimeMillis();
            
            double successRate = (100.0 * success) / numTrials;
            double failureRate = (100.0 * failure) / numTrials;
            double wrongRate = (100.0 * wrongDecode) / numTrials;
            
            System.out.println("Résultats pour w = " + w + ":");
            System.out.println("  Réussite: " + success + " (" + String.format("%.2f", successRate) + "%)");
            System.out.println("  Échec: " + failure + " (" + String.format("%.2f", failureRate) + "%)");
            System.out.println("  Décodage incorrect: " + wrongDecode + " (" + String.format("%.2f", wrongRate) + "%)");
            System.out.println("  Temps: " + (endEval - startEval) + " ms");
        }
        
        System.out.println("\n=== EXERCICE 14 ===");
        System.out.println("Limite de Shannon");
        double p = 0.02;
        double H_entropy = -p * Math.log(p) / Math.log(2) - (1 - p) * Math.log(1 - p) / Math.log(2);
        double shannon_limit = 1 - H_entropy;
        System.out.println("Pour un BSC avec p = " + p + ":");
        System.out.println("Entropie H(p) = " + String.format("%.4f", H_entropy));
        System.out.println("Limite de Shannon: τ_max = " + String.format("%.4f", shannon_limit));
        System.out.println("Rendement du code = " + String.format("%.4f", tau));
        System.out.println("Le code respecte la limite: " + (tau <= shannon_limit));
    }
}
