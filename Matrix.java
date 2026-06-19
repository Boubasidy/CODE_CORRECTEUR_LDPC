import java.util.*;
import java.io.*;

public class Matrix {
    private byte[][] data = null;
    private int rows = 0, cols = 0;
    
    public Matrix(int r, int c) {
        data = new byte[r][c];
        rows = r;
        cols = c;
    }
    
    public Matrix(byte[][] tab) {
        rows = tab.length;
        cols = tab[0].length;
        data = new byte[rows][cols];
        for (int i = 0 ; i < rows ; i ++)
            for (int j = 0 ; j < cols ; j ++) 
                data[i][j] = tab[i][j];
    }
    
    public int getRows() {
        return rows;
    }
    
    public int getCols() {
        return cols;
    }
    
    public byte getElem(int i, int j) {
        return data[i][j];
    }
    
    public void setElem(int i, int j, byte b) {
        data[i][j] = b;
    }
    
    public boolean isEqualTo(Matrix m){
        if ((rows != m.rows) || (cols != m.cols))
            return false;
        for (int i = 0; i < rows; i++) 
            for (int j = 0; j < cols; j++) 
                if (data[i][j] != m.data[i][j])
                    return false;
                return true;
    }
    
    public void shiftRow(int a, int b){
        byte tmp = 0;
        for (int i = 0; i < cols; i++){
            tmp = data[a][i];
            data[a][i] = data[b][i];
            data[b][i] = tmp;
        }
    }
    
    public void shiftCol(int a, int b){
        byte tmp = 0;
        for (int i = 0; i < rows; i++){
            tmp = data[i][a];
            data[i][a] = data[i][b];
            data[i][b] = tmp;
        }
    }
     
    public void display() {
        System.out.print("[");
        for (int i = 0; i < rows; i++) {
            if (i != 0) {
                System.out.print(" ");
            }
            
            System.out.print("[");
            
            for (int j = 0; j < cols; j++) {
                System.out.printf("%d", data[i][j]);
                
                if (j != cols - 1) {
                    System.out.print(" ");
                }
            }
            
            System.out.print("]");
            
            if (i == rows - 1) {
                System.out.print("]");
            }
            
            System.out.println();
        }
        System.out.println();
    }
    
    public Matrix transpose() {
        Matrix result = new Matrix(cols, rows);
        
        for (int i = 0; i < rows; i++) 
            for (int j = 0; j < cols; j++) 
                result.data[j][i] = data[i][j];
    
        return result;
    }
    
    public Matrix add(Matrix m){
        Matrix r = new Matrix(rows,m.cols);
        
        if ((m.rows != rows) || (m.cols != cols))
            System.out.printf("Erreur d'addition\n");
        
        for (int i = 0; i < rows; i++) 
            for (int j = 0; j < cols; j++) 
                r.data[i][j] = (byte) ((data[i][j] + m.data[i][j]) % 2);
        return r;
    }
    
    public Matrix multiply(Matrix m){
        Matrix r = new Matrix(rows,m.cols);
        
        if (m.rows != cols)
            System.out.printf("Erreur de multiplication\n");
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < m.cols; j++) {
                r.data[i][j] = 0;
                for (int k = 0; k < cols; k++){
                    r.data[i][j] =  (byte) ((r.data[i][j] + data[i][k] * m.data[k][j]) % 2);
                }
            }
        }
        
        return r;
    }
    
    public void addRow(int a, int b) {
        for (int j = 0; j < cols; j++) {
            data[b][j] = (byte) ((data[b][j] + data[a][j]) % 2);
        }
    }
    
    public void addCol(int a, int b) {
        for (int i = 0; i < rows; i++) {
            data[i][b] = (byte) ((data[i][b] + data[i][a]) % 2);
        }
    }
    
    public Matrix sysTransform() {
        Matrix H = this;
        int r = H.rows;
        int c = H.cols;
        
        // Effectuer le pivot de Gauss pour transformer H en (M|id)
        for (int i = 0; i < r; i++) {
            // Chercher un pivot sur la colonne c-r+i en commençant à la ligne i
            int pivot = -1;
            for (int j = i; j < r; j++) {
                if (H.data[j][c - r + i] == 1) {
                    pivot = j;
                    break;
                }
            }
            
            if (pivot == -1) {
                System.out.println("Erreur: pas de pivot trouvé");
                return null;
            }
            
            // Échanger les lignes i et pivot
            H.shiftRow(i, pivot);
            
            // Éliminer les 1 sur la colonne c-r+i (sauf à la ligne i)
            for (int j = 0; j < r; j++) {
                if (j != i && H.data[j][c - r + i] == 1) {
                    H.addRow(i, j);
                }
            }
        }
        
        return H;
    }
    
    public Matrix genG() {
        // Cette méthode suppose que la matrice est sous la forme H' = (M|id)
        // Elle retourne G = (id|M^T)
        int r = rows; // nombre de lignes de H' = nombre de lignes de M
        int c = cols; // nombre de colonnes de H' (c = r + k où k = nombre de colonnes de id)
        int k = c - r; // nombre de colonnes de la matrice identité = nombre de bits d'information
        
        // G a k lignes et c colonnes
        Matrix G = new Matrix(k, c);
        
        // Première partie: matrice identité de taille k x k
        for (int i = 0; i < k; i++) {
            G.data[i][i] = 1;
        }
        
        // Deuxième partie: M^T (transposée de la partie M de H')
        // M est les r premières colonnes de H'
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < r; j++) {
                G.data[i][k + j] = data[j][i];
            }
        }
        
        return G;
    }
    
    public Matrix errGen(int w) {
        // Générer un vecteur ligne de poids w
        Matrix err = new Matrix(1, cols);
        Random rand = new Random();
        int count = 0;
        while (count < w) {
            int idx = rand.nextInt(cols);
            if (err.data[0][idx] == 0) {
                err.data[0][idx] = 1;
                count++;
            }
        }
        return err;
    }
}

