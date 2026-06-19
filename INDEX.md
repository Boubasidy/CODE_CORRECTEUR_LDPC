# INDEX - Guide de navigation du projet LDPC

Bienvenue dans le projet d'implémentation d'un code correcteur d'erreurs LDPC (Low-Density Parity-Check).

## 📚 Documentation

### Pour commencer rapidement
- **[QUICKSTART.md](QUICKSTART.md)** ← **COMMENCER ICI** 
  - 3 commandes pour exécuter le code
  - Résultats attendus
  - FAQ rapide

### Pour comprendre le projet
- **[README.md](README.md)** 
  - Vue d'ensemble des classes
  - Paramètres du code LDPC
  - Résultats des tests

### Pour l'analyse approfondie
- **[RAPPORT.md](RAPPORT.md)**
  - Objectifs du projet
  - Choix d'implémentation
  - Analyse théorique (limite Shannon)
  - Conclusions et améliorations

### Pour la structure du projet
- **[VISION.md](VISION.md)**
  - Structure des fichiers
  - Métriques du code
  - Cas d'usage
  - Limitations

### Checklist d'accomplissement
- **[COMPLETION.md](COMPLETION.md)**
  - Status de chaque exercice (1-14)
  - Résultats détaillés
  - Vérifications

---

## 💻 Code Source

### Classes principales

#### 1. **Matrix.java** - Fondation
Classe pour manipuler les matrices binaires.

```java
// Opérations de base
Matrix m1 = new Matrix(5, 20);
Matrix m2 = m1.add(other);      // Addition mod 2
Matrix m3 = m1.multiply(G);     // Multiplication matricielle
Matrix m4 = m1.transpose();     // Transposition

// Encodage LDPC
Matrix H_sys = H.sysTransform();  // Transformation systématique
Matrix G = H_sys.genG();           // Génération de matrice génératrice
Matrix x = u.multiply(G);         // Encodage

// Outils
Matrix errors = x.errGen(50);     // Génération d'erreurs aléatoires
```

#### 2. **TGraph.java** - Décodage
Classe pour le graphe de Tanner et décodage LDPC.

```java
// Créer le graphe
TGraph tgraph = new TGraph(H, wc, wr);

// Décoder
Matrix decoded = tgraph.decode(received_word, num_iterations);

// Résultat
if (decoded.getElem(0,0) == -1) {
    // Échec du décodage
} else if (decoded.isEqualTo(original)) {
    // Succès!
} else {
    // Décodage incorrect
}
```

#### 3. **Main.java** - Tests (Exercices 1-9)
Tests complets sur la petite matrice 15×20.

**Ce qu'il affiche**:
- Exercice 1: Chargement de matrice
- Exercices 2-3: Opérations matricielles
- Exercice 4: Transformation systématique
- Exercice 5: Génération de G
- Exercice 6: Encodage d'un mot
- Exercice 7: Graphe de Tanner
- Exercices 8-9: Tests de décodage

#### 4. **Evaluation*.java** - Évaluations (Exercices 10-14)
- `Evaluation.java` - Évaluation complète (30+ minutes)
- `EvaluationFast.java` - Évaluation rapide (2 minutes)
- `EvaluationCorrect.java` - Variante améliorée

---

## 🚀 Démarrage rapide

### Étape 1: Compiler
```bash
cd LDPC-starter
javac *.java
```

### Étape 2: Exécuter
```bash
java Main              # Tests exercices 1-9
java EvaluationFast    # Évaluation rapide
```

### Étape 3: Lire les résultats
Consultez [QUICKSTART.md](QUICKSTART.md) pour résultats attendus.

---

## 📊 Paramètres du code

### Petite matrice (utilisée pour tests)
```
Fichier: matrix-15-20-3-4
Dimensions: 15 lignes × 20 colonnes
Code LDPC:
  - k = 5 bits (information)
  - n = 20 bits (codeword)
  - τ = 1/4 (rendement)
```

### Grande matrice (utilisée pour évaluation)
```
Fichier: matrix-2000-6000-5-15
Dimensions: 2000 lignes × 6000 colonnes
Code LDPC:
  - k = 4000 bits (information)
  - n = 6000 bits (codeword)
  - τ = 2/3 (rendement)
  - Pour p=2%: 120 erreurs en moyenne
  - Pour p=2.5%: 150 erreurs en moyenne
```

---

## ✅ Checklist d'implémentation

- [x] Exercice 1: Ouverture des fichiers
- [x] Exercice 2: Opérations matricielles
- [x] Exercice 3: Implémentation addRow/addCol
- [x] Exercice 4: Transformation systématique
- [x] Exercice 5: Génération de matrice génératrice
- [x] Exercice 6: Encodage
- [x] Exercice 7: Graphe de Tanner
- [x] Exercice 8: Décodage
- [x] Exercice 9: Tests de décodage
- [x] Exercice 10: Paramètres du code
- [x] Exercice 11: Encodage grand code
- [x] Exercice 12: Génération d'erreurs
- [x] Exercice 13: Évaluation de performance
- [x] Exercice 14: Analyse limite Shannon

---

## 🎯 Cas d'usage

| Besoin | Solution |
|--------|----------|
| Voir les tests exercices 1-9 | `java Main` |
| Évaluation rapide | `java EvaluationFast` |
| Évaluation complète | `java Evaluation` |
| Comprendre le code | Lire Matrix.java et TGraph.java |
| Rapport technique | Lire RAPPORT.md |
| Améliorations | Voir VISION.md section "Prochaines étapes" |

---

## 🔍 Points clés

### Algorithme de décodage
⚡ **Renversement de bits itératif** (bit-flipping)
1. Calculer le syndrome s = H*x^T
2. Si s=0, fin (converged)
3. Pour chaque bit: score = nombre de parité checks non satisfaits qui le couvre
4. Renverser le bit avec le score maximal
5. Itérer jusqu'à convergence

### Limite de Shannon
Pour un BSC avec probabilité p:
- τ_max = 1 - H(p) où H(p) = -p*log2(p) - (1-p)*log2(1-p)
- Notre code τ = 2/3 < τ_max pour p = 0.02 et p = 0.025 ✓

### Transformation systématique
H → H' = (M|I) via pivot de Gauss
→ G = (I|M^T)
→ Encodage systématique: x = [u | u*M^T]

---

## 📞 Ressources supplémentaires

### Lectures recommandées
- LDPC sur Wikipedia: https://en.wikipedia.org/wiki/Low-density_parity-check_code
- Graphe de Tanner: Tanner, R. M. (1981)
- Canal binaire symétrique: https://en.wikipedia.org/wiki/Binary_symmetric_channel
- Limite Shannon: https://en.wikipedia.org/wiki/Shannon%27s_source_coding_theorem

### Fichiers générés
- `*.class` - Fichiers compilés (générer avec `javac *.java`)
- `*.md` - Documentation du projet

---

## ❓ FAQ

**Q: Comment compiler?**
A: `javac *.java` dans le répertoire LDPC-starter

**Q: Quoi exécuter en premier?**
A: `java Main` pour voir les tests, puis lire [QUICKSTART.md](QUICKSTART.md)

**Q: Pourquoi certains tests échouent?**
A: C'est normal - l'algorithme ne peut pas corriger toutes les configurations d'erreurs

**Q: Le code est-il prêt pour production?**
A: Non - c'est un code académique. Pour production, voir amélioration dans VISION.md

**Q: Combien de temps prend chaque exécution?**
A: `Main` = 2s, `EvaluationFast` = 2min, `Evaluation` = 30+min

**Q: Puis-je modifier le code?**
A: Oui! Consultez les sections "Améliorations" pour idées

---

## 📄 Fichiers du projet

```
LDPC-starter/
├── Matrix.java                    # Classe de base pour matrices
├── TGraph.java                    # Graphe de Tanner + décodage
├── Main.java                      # Tests exercices 1-9
├── Evaluation.java                # Évaluation complète
├── EvaluationFast.java           # Évaluation rapide
├── EvaluationCorrect.java        # Variante
├── data/
│   ├── matrix-15-20-3-4          # Petite matrice (15×20)
│   └── matrix-2000-6000-5-15    # Grande matrice (2000×6000)
├── README.md                      # Vue d'ensemble
├── RAPPORT.md                     # Rapport technique
├── VISION.md                      # Structure du projet
├── QUICKSTART.md                  # Guide de démarrage rapide
├── COMPLETION.md                  # Checklist d'accomplissement
├── INDEX.md                       # Ce fichier
└── run.sh                         # Script interactif bash
```

---

**Prêt à commencer? Consultez [QUICKSTART.md](QUICKSTART.md)!** 🚀
