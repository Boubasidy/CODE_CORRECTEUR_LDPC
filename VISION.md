# Structure du projet LDPC-starter

## Fichiers Java

### Core Implementation
- **Matrix.java** (340 lignes)
  - Classe pour représenter et manipuler les matrices binaires
  - Opérations: addition, multiplication, transposition
  - Transformation systématique: sysTransform()
  - Génération de matrice génératrice: genG()
  - Génération d'erreurs: errGen(w)
  - Accesseurs: getElem(), setElem(), getRows(), getCols()
  - Affichage: display()

- **TGraph.java** (200 lignes)
  - Représentation du graphe de Tanner pour décodage LDPC
  - Structure: tableaux left[][] et right[][]
  - Constructeur: TGraph(Matrix H, int wc, int wr)
  - Décodage: decode(Matrix code, int rounds)
  - Affichage: display()
  - Calcul automatique des poids de la matrice

### Tests et Évaluation
- **Main.java** (160 lignes)
  - Tests complets des exercices 1-9
  - Chargement de matrix-15-20-3-4
  - Tests d'encodage et décodage sur petite matrice
  - Tests avec différents vecteurs d'erreur

- **Evaluation.java** (200 lignes)
  - Évaluation théorique et pratique sur grande matrice
  - Calcul des paramètres du code
  - Analyse Shannon
  - Tests 10000 itérations × 200 rounds (très lent)

- **EvaluationFast.java** (180 lignes)
  - Évaluation rapide pour démonstration
  - 100 itérations × 50 rounds
  - Résumé des performances

- **EvaluationCorrect.java** (190 lignes)
  - Évaluation avec mots aléatoires
  - 50 itérations × 100 rounds
  - Analyse plus détaillée

## Fichiers de données

### Matrices de contrôle
- **data/matrix-15-20-3-4**
  - Matrice 15×20
  - Poids lignes: 4, poids colonnes: 3
  - Utilisée pour tests exercices 1-9
  
- **data/matrix-2000-6000-5-15**
  - Matrice 2000×6000
  - Poids lignes: 15, poids colonnes: 5
  - Utilisée pour évaluations complètes

## Documentation

- **README.md** (200 lignes)
  - Vue d'ensemble du projet
  - Description des classes
  - Paramètres des codes LDPC
  - Résultats des tests
  - Instructions d'utilisation

- **RAPPORT.md** (350 lignes)
  - Rapport complet du projet
  - Objectifs et contexte applicatif
  - Détails d'implémentation
  - Analyse théorique (limite Shannon)
  - Résultats et conclusions
  - Améliorations proposées

- **VISION.md** (ce fichier)
  - Structure et vue d'ensemble du projet

## Scripts

- **run.sh** (100 lignes)
  - Script interactif bash
  - Menu pour compiler et exécuter
  - Accès aux résultats et documentation

## Métriques du projet

### Lignes de code
- Total: ~1500 lignes de code Java
- Classes: 5 (Matrix, TGraph, Main, Evaluation, EvaluationFast, EvaluationCorrect)
- Méthodes: 30+ méthodes implémentées

### Complexité
- Transformation systématique: O(r²n) où r=nombre lignes, n=nombre colonnes
- Génération graphe Tanner: O(nw) où w=poids moyen
- Décodage (par itération): O(nr*wr + nc*wc)
- Génération erreurs: O(w + n) pour hachage/collision

### Espace mémoire
- Matrice H (2000×6000): ~18 MB (brut), ~3 MB (~1.7 bps) compressée
- Graphe Tanner: ~48 MB pour left[][] + right[][]
- Stockage temporaire: ~50 KB/itération de décodage

## Cas d'usage

### 1. Tests académiques
```bash
java Main          # Affiche tous les tests exercices 1-9
```

### 2. Évaluation complète
```bash
java Evaluation    # 10000 itérations (lent, pour résultats finaux)
```

### 3. Démonstration rapide
```bash
java EvaluationFast  # 100 itérations (rapide, pour démo)
```

### 4. Utilisation depuis autre code
```java
Matrix H = loadMatrix("data/matrix-2000-6000-5-15", 2000, 6000);
TGraph tgraph = new TGraph(H, 5, 15);
Matrix decoded = tgraph.decode(received_codeword, 200);
```

## Limitations connues

1. **Encodage incomplet**: genG() génère la matrice génératrice mais le vrai encodage systématique n'est pas implémenté (nécessite résolution H*x^T=0)

2. **Performance limite**: Trop lent pour applications temps réel

3. **Algorithme basique**: Seulement renversement de bits; pas de sum-product ou belief-propagation

4. **Matrices denses**: Stockele toutes les connexions même si la matrice est creuse

## Prochaines étapes pour amélioration

1. Implémenter encodage systématique complet
2. Utiliser structures creuses pour optimiser mémoire/vitesse
3. Ajouter algorithmes de décodage avancés
4. Parraleliser les calculs (Java multithreading)
5. Optimiser critères de convergence
