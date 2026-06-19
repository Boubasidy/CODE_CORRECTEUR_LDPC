#!/bin/bash

# Script de compilation et d'exécution du projet LDPC

echo "=== Projet LDPC - Codes correcteurs d'erreurs ==="
echo ""

# Déterminer le répertoire du script
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$DIR"

# Menu
echo "Sélectionnez une option:"
echo "1. Compiler le projet"
echo "2. Exécuter les tests (petite matrice 15×20)"
echo "3. Exécuter l'évaluation complète (grande matrice 2000×6000)"
echo "4. Exécuter l'évaluation rapide (démo)"
echo "5. Compiler et exécuter les tests"
echo "6. Afficher le README"
echo "7. Afficher le RAPPORT"
echo "8. Nettoyer les fichiers .class"
echo ""
read -p "Entrée (1-8): " choice

case $choice in
    1)
        echo "Compilation..."
        javac *.java
        if [ $? -eq 0 ]; then
            echo "✓ Compilation réussie"
        else
            echo "✗ Erreur de compilation"
        fi
        ;;
    2)
        echo "Compilation..."
        javac *.java 2>/dev/null
        echo "Exécution des tests..."
        java Main
        ;;
    3)
        echo "Compilation..."
        javac *.java 2>/dev/null
        echo "Exécution de l'évaluation complète (peut prendre plusieurs minutes)..."
        java Evaluation
        ;;
    4)
        echo "Compilation..."
        javac *.java 2>/dev/null
        echo "Exécution de l'évaluation rapide..."
        java EvaluationFast
        ;;
    5)
        echo "Compilation et exécution des tests..."
        javac *.java && java Main
        ;;
    6)
        if [ -f "README.md" ]; then
            cat README.md
        else
            echo "Fichier README.md non trouvé"
        fi
        ;;
    7)
        if [ -f "RAPPORT.md" ]; then
            cat RAPPORT.md
        else
            echo "Fichier RAPPORT.md non trouvé"
        fi
        ;;
    8)
        echo "Suppression des fichiers .class..."
        rm -f *.class
        echo "✓ Nettoyage terminé"
        ;;
    *)
        echo "Option invalide"
        ;;
esac
