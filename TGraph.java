import java.util.*;

public class TGraph {
    private int nr; // nombre de nœuds fonctionnels (nombre de lignes de H)
    private int wr; // poids des lignes (max)
    private int nc; // nombre de nœuds variables (nombre de colonnes de H)
    private int wc; // poids des colonnes (max)
    private int[][] left; // tableau pour les nœuds fonctionnels
    private int[][] right; // tableau pour les nœuds variables
    
    public TGraph(Matrix H, int wc, int wr) {
        this.nr = H.getRows();
        this.nc = H.getCols();
        
        // Calculer les vrais poids
        int maxRowWeight = 0;
        int maxColWeight = 0;
        
        for (int i = 0; i < nr; i++) {
            int count = 0;
            for (int j = 0; j < nc; j++) {
                if (H.getElem(i, j) == 1) count++;
            }
            maxRowWeight = Math.max(maxRowWeight, count);
        }
        
        for (int j = 0; j < nc; j++) {
            int count = 0;
            for (int i = 0; i < nr; i++) {
                if (H.getElem(i, j) == 1) count++;
            }
            maxColWeight = Math.max(maxColWeight, count);
        }
        
        this.wr = maxRowWeight;
        this.wc = maxColWeight;
        
        // Initialiser les tableaux
        this.left = new int[nr][maxRowWeight + 1];
        this.right = new int[nc][maxColWeight + 1];
        
        // Initialiser les bits associés aux nœuds variables à 0
            // Nœuds variables (colonnes)
        for (int i = 0; i < nc; i++) {
            right[i][0] = 0;
        }
        for (int i = 0; i < nr; i++) {
            left[i][0] = 0;
        }
        // Initialiser à -1 pour indiquer les positions non utilisées
        for (int i = 0; i < nr; i++) {
            for (int j = 1; j <= maxRowWeight; j++) {
                left[i][j] = -1;
            }
        }
        for (int i = 0; i < nc; i++) {
            for (int j = 1; j <= maxColWeight; j++) {
                right[i][j] = -1;
            }
        }
        
        // Construire le graphe à partir de la matrice H
        // Pour chaque colonne j (nœud variable)
        for (int j = 0; j < nc; j++) {
            int idx = 1;
            for (int i = 0; i < nr; i++) {
                if (H.getElem(i, j) == 1) {
                    right[j][idx] = i;
                    idx++;
                }
            }
        }
        
        // Pour chaque ligne i (nœud fonctionnel)
        for (int i = 0; i < nr; i++) {
            int idx = 1;
            for (int j = 0; j < nc; j++) {
                if (H.getElem(i, j) == 1) {
                    left[i][idx] = j;
                    idx++;
                }
            }
        }
    }
    
    public void display() {
        System.out.println("Left (fonction nodes, " + wr + " max weight):");
        for (int i = 0; i < nr; i++) {
            System.out.print("[");
            for (int j = 0; j <= wr; j++) {
                System.out.print(left[i][j]);
                if (j < wr) System.out.print(" ");
            }
            System.out.println("]");
        }
        
        System.out.println("Right (variable nodes, " + wc + " max weight):");
        for (int i = 0; i < nc; i++) {
            System.out.print("[");
            for (int j = 0; j <= wc; j++) {
                System.out.print(right[i][j]);
                if (j < wc) System.out.print(" ");
            }
            System.out.println("]");
        }
    }
    
    public Matrix decode(Matrix code, int rounds) {
        // Initialiser le mot reçu
        byte[] x = new byte[nc];
        for (int i = 0; i < nc; i++) {
            x[i] = code.getElem(0, i);
        }
        
        // Itérations de l'algorithme du renversement de bits
        for (int iter = 0; iter < rounds; iter++) {
            // Étape 1: Calcul du syndrome s = H * x^T
            byte[] syndrome = new byte[nr];
            // Pour chaque nœud fonctionnel, calculer le syndrome
            for (int i = 0; i < nr; i++) {
                syndrome[i] = 0;
                left[i][0] = 0; // Réinitialiser le compteur de bits associés
                // Pour chaque voisin associé à ce nœud fonctionnel
                for (int j = 1; j <= wr; j++) {
                    if (left[i][j] != -1) {
                        syndrome[i] = (byte) ((syndrome[i] + x[left[i][j]]) % 2);
                        int idx_neighbor_variable = left[i][j];
                        left[i][0] = (left[i][0] + right[idx_neighbor_variable][0]) % 2; // Compter les bits associés
                    }
                }
            }
            
            // Vérifier si le syndrome est nul (décodage réussi)
            boolean success = true;
            for (int i = 0; i < nr; i++) {
                if (syndrome[i] != 0) {
                    success = false;
                    break;
                }
            }
            
            if (success) {
                // Le décodage a réussi
                Matrix result = new Matrix(1, nc);
                for (int i = 0; i < nc; i++) {
                    result.setElem(0, i, x[i]);
                }
                return result;
            }
            
            // Étape 2: Calcul du score pour chaque bit
            int[] score = new int[nc];
            for (int j = 0; j < nc; j++) {
                score[j] = 0;
            }
            
            // Pour chaque nœud de contrôle avec syndrome non nul
            for (int i = 0; i < nr; i++) {
                if (syndrome[i] == 1) {
                    // Incrémenter le score de tous les bits associés à ce nœud
                    for (int j = 1; j <= wr; j++) {
                        if (left[i][j] != -1) {
                            score[left[i][j]]++;
                        }
                    }
                }
            }
            
            // Étape 3: Trouver et renverser le bit avec le score maximal
            int maxScore = -1;
            int maxIdx = -1;
            for (int j = 0; j < nc; j++) {
                if (score[j] > maxScore) {
                    maxScore = score[j];
                    maxIdx = j;
                }
            }
            
            if (maxIdx >= 0 && maxScore > 0) {
                x[maxIdx] = (byte) (1 - x[maxIdx]);
            }
        }
        
        // L'algorithme n'a pas convergé après 'rounds' itérations
        // Retourner un vecteur avec tous les éléments à -1 pour signifier un échec
        Matrix result = new Matrix(1, nc);
        for (int i = 0; i < nc; i++) {
            result.setElem(0, i, (byte) -1);
        }
        return result;
    }
}
