import java.util.*;
import java.io.*;

public class Main {
    
    public static Matrix loadMatrix(String file, int r, int c) {
        byte[] tmp =  new byte[r * c];
        byte[][] data = new byte[r][c];
        try {
            FileInputStream fos = new FileInputStream(file);
            fos.read(tmp);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < r; i++)
            for (int j = 0; j< c; j++)
                data[i][j] = tmp[i * c + j];
            return new Matrix(data);
    }
    
    public static void main(String[] arg){
        // Exercice 1: Charger et afficher la matrice
        System.out.println("=== EXERCICE 1 ===");
        Matrix hbase = loadMatrix("data/matrix-15-20-3-4", 15, 20);
        System.out.println("Matrice chargée (15x20):");
        hbase.display();
        
        // Exercice 2: Tester les opérations de base
        System.out.println("=== EXERCICE 2 ===");
        byte[][] tab1 = {{1,0},{0,1}};
        byte[][] tab2 = {{1,1},{0,1}};
        Matrix m1 = new Matrix(tab1);
        Matrix m2 = new Matrix(tab2);
        System.out.println("Matrice 1:");
        m1.display();
        System.out.println("Matrice 2:");
        m2.display();
        System.out.println("Addition m1+m2:");
        m1.add(m2).display();
        System.out.println("Multiplication m1*m2:");
        m1.multiply(m2).display();
        System.out.println("Transposée m2:");
        m2.transpose().display();
        
        // Exercice 3: Tester addRow et addCol
        System.out.println("=== EXERCICE 3 ===");
        byte[][] testTab = {{1,0,1},{1,1,0},{0,1,1}};
        Matrix testMatrix = new Matrix(testTab);
        System.out.println("Matrice originale:");
        testMatrix.display();
        testMatrix.addRow(0, 1);
        System.out.println("Après addRow(0, 1):");
        testMatrix.display();
        
        byte[][] testTab2 = {{1,0,1},{1,1,0},{0,1,1}};
        Matrix testMatrix2 = new Matrix(testTab2);
        testMatrix2.addCol(0, 1);
        System.out.println("Après addCol(0, 1):");
        testMatrix2.display();
        
        // Exercice 4: Tester sysTransform
        System.out.println("=== EXERCICE 4 ===");
        Matrix H = loadMatrix("data/matrix-15-20-3-4", 15, 20);
        System.out.println("Matrice H originale:");
        H.display();
        Matrix H_sys = H.sysTransform();
        System.out.println("Matrice H sous forme systématique (M|id):");
        H_sys.display();
        
        // Exercice 5: Tester genG
        System.out.println("=== EXERCICE 5 ===");
        Matrix G = H_sys.genG();
        System.out.println("Matrice génératrice G (id|M^T):");
        G.display();
        
        // Exercice 6: Encoder un mot
        System.out.println("=== EXERCICE 6 ===");
        byte[][] u_arr = {{1,0,1,0,1}};  // k = 5 bits d'information
        Matrix u = new Matrix(u_arr);
        System.out.println("Mot à encoder u (k=5 bits):");
        u.display();
        System.out.println("Dimensions de G: " + G.getRows() + " x " + G.getCols());
        Matrix x = u.multiply(G);
        System.out.println("Mot encodé x = u * G (n=20 bits):");
        x.display();
        
        // Exercice 7: Créer et afficher TGraph
        System.out.println("=== EXERCICE 7 ===");
        Matrix H_orig = loadMatrix("data/matrix-15-20-3-4", 15, 20);
        TGraph tgraph = new TGraph(H_orig, 3, 4);  // wc=3, wr=4 (poids des colonnes et lignes)
        System.out.println("Graphe de Tanner:");
        tgraph.display();
        
        // Exercice 8-9: Tester le décodage
        System.out.println("=== EXERCICE 8-9 ===");
        int rounds = 100;
        System.out.println("Test décodage de x:");
        Matrix decoded_x = tgraph.decode(x, rounds);
        System.out.println("Mot décodé:");
        decoded_x.display();
        System.out.println("Correspond à x: " + decoded_x.isEqualTo(x));
        
        // Créer des erreurs
        byte[][] e1_arr = {{0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0}};
        byte[][] e2_arr = {{0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};
        byte[][] e3_arr = {{0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0}};
        byte[][] e4_arr = {{0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0}};
        
        Matrix e1 = new Matrix(e1_arr);
        Matrix e2 = new Matrix(e2_arr);
        Matrix e3 = new Matrix(e3_arr);
        Matrix e4 = new Matrix(e4_arr);
        
        Matrix[] errors = {e1, e2, e3, e4};
        String[] errorNames = {"e1", "e2", "e3", "e4"};
        
        for (int i = 0; i < errors.length; i++) {
            System.out.println("\n--- Test avec " + errorNames[i] + " ---");
            Matrix y = x.add(errors[i]);
            System.out.println("Mot altéré y = x + " + errorNames[i] + ":");
            y.display();
            
            Matrix decoded_y = tgraph.decode(y, rounds);
            if (decoded_y.getElem(0, 0) == -1) {
                System.out.println("Résultat: ÉCHEC du décodage");
            } else if (decoded_y.isEqualTo(x)) {
                System.out.println("Résultat: Décodage correct");
            } else {
                System.out.println("Résultat: Décodage incorrect (mot décodé different de x)");
                decoded_y.display();
            }
        }
    }
}
