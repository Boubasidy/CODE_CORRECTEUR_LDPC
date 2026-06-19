import java.util.*;

public class EvaluationFast {
    
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
        System.out.println("=== Évaluation RAPIDE du code LDPC ===");
        
        // Paramètres du grand code LDPC
        int nr = 2000;      // nombre de lignes de H
        int nc = 6000;      // nombre de colonnes de H
        int k = nc - nr;    // longueur des mots source = 4000
        int n = nc;         // longueur des mots de code = 6000
        double tau = (double) k / n;  // rendement = 2/3
        
        System.out.println("\nParamètres du code LDPC:");
        System.out.println("k = " + k + " bits (information)");
        System.out.println("n = " + n + " bits (code)");
        System.out.println("τ = " + String.format("%.4f", tau) + " (rendement)");
        
        // Analysis théorique
        System.out.println("\nAnalyse théorique:");
        double p = 0.02;
        double H_entropy = -p * Math.log(p) / Math.log(2) - (1 - p) * Math.log(1 - p) / Math.log(2);
        double shannon_limit = 1 - H_entropy;
        System.out.println("Pour p = " + p + ": entropie H = " + String.format("%.4f", H_entropy));
        System.out.println("Limite de Shannon: τ_max = " + String.format("%.4f", shannon_limit));
        if (tau <= shannon_limit) {
            System.out.println("✓ Le code respecte la limite de Shannon");
        } else {
            System.out.println("✗ Le code dépasse la limite de Shannon");
        }
        
        p = 0.025;
        H_entropy = -p * Math.log(p) / Math.log(2) - (1 - p) * Math.log(1 - p) / Math.log(2);
        shannon_limit = 1 - H_entropy;
        System.out.println("\nPour p = " + p + ": entropie H = " + String.format("%.4f", H_entropy));
        System.out.println("Limite de Shannon: τ_max = " + String.format("%.4f", shannon_limit));
        
        System.out.println("\n=== CHARGEMENT DE LA MATRICE ===");
        System.out.println("Chargement de matrix-2000-6000-5-15...");
        long startLoad = System.currentTimeMillis();
        Matrix H = loadMatrix("data/matrix-2000-6000-5-15", nr, nc);
        long endLoad = System.currentTimeMillis();
        System.out.println("Matrice H (2000x6000) chargée en " + (endLoad - startLoad) + " ms");
        
        System.out.println("\n=== GÉNÉRATION DU GRAPHE DE TANNER ===");
        startLoad = System.currentTimeMillis();
        TGraph tgraph = new TGraph(H, 5, 15);  // wc=5, wr=15
        endLoad = System.currentTimeMillis();
        System.out.println("Graphe de Tanner créé en " + (endLoad - startLoad) + " ms");
        
        System.out.println("\n=== TEST DE DÉCODAGE (version rapide) ===");
        System.out.println("Évaluation sur 100 itérations avec 50 rounds de décodage");
        System.out.println("(Pour la simulation complète: 10000 itérations avec 200 rounds)");
        
        int numTrials = 100;
        int rounds = 50;
        int[] weights = {120, 130, 140, 150};  // autour des valeurs critique et 2.5%
        
        for (int w : weights) {
            System.out.println("\n--- Performance pour w = " + w + " erreurs ---");
            System.out.println("(w = " + String.format("%.2f", 100.0*w/n) + "% du code)");
            
            int success = 0;
            int failure = 0;
            int wrongDecode = 0;
            
            long startEval = System.currentTimeMillis();
            Random rand = new Random();
            
            for (int trial = 0; trial < numTrials; trial++) {
                // Générer un mot aléatoire
                Matrix x = new Matrix(1, n);
                for (int i = 0; i < n; i++) {
                    x.setElem(0, i, (byte) (rand.nextInt(2)));
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
            
            System.out.println("Résultats:");
            System.out.println("  Réussite: " + success + " / " + numTrials + " (" + String.format("%.1f", successRate) + "%)");
            System.out.println("  Échec: " + failure + " / " + numTrials + " (" + String.format("%.1f", failureRate) + "%)");
            if (wrongDecode > 0) {
                System.out.println("  ⚠ Décodage incorrect: " + wrongDecode + " / " + numTrials + " (" + String.format("%.1f", wrongRate) + "%)");
            }
            System.out.println("  Temps: " + (endEval - startEval) + " ms");
        }
        
        System.out.println("\n=== RÉSUMÉ ===");
        System.out.println("Le code LDPC (4000,6000) avec rendement 2/3 a été testé.");
        System.out.println("Les performances observées montrent la capacité de correction");
        System.out.println("pour les taux d'erreur critique (2%) et limite (2.5%).");
    }
}
