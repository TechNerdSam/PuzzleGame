Voici une documentation complète, professionnelle et sophistiquée pour votre projet **Puzzle Prestige**, conçue pour mettre en valeur la qualité et l'élégance de votre travail.

---

# 📖 Documentation Officielle : Puzzle Prestige 🧩

Bienvenue dans l'univers de **Puzzle Prestige**, une application de jeu de taquin conçue avec une attention méticuleuse portée aux détails, à l'expérience utilisateur et à l'esthétique. Ce document sert de guide complet pour comprendre l'architecture, les fonctionnalités et la vision derrière ce projet.

## ✨ Fonctionnalités Principales

**Puzzle Prestige** n'est pas un simple jeu de puzzle. C'est une expérience complète, offrant une multitude de fonctionnalités pour un plaisir de jeu maximal.

* **🖼️ Puzzles Personnalisables** : Les joueurs peuvent choisir n'importe quelle image de leur ordinateur pour la transformer en un puzzle captivant.
* **🧠 Niveaux de Difficulté Multiples** : Trois niveaux de difficulté sont proposés pour s'adapter à tous les types de joueurs :
    * **Facile** : Grille de 3x3 pour les débutants.
    * **Moyen** : Grille de 4x4 pour un défi équilibré.
    * **Difficile** : Grille de 5x5 pour les experts en quête de challenge.
* **🏆 Système de Meilleurs Scores** : Un tableau des scores persistant enregistre les 10 meilleures performances, incitant les joueurs à améliorer leurs compétences.
* **🎨 Interface Élégante et Animée** : L'ensemble de l'interface utilisateur est sublimé par des dégradés animés et dynamiques, offrant une expérience visuelle cohérente et immersive sur tous les écrans (Menu Principal, Jeu et Meilleurs Scores).
* **👤 Profil Utilisateur** : Le jeu accueille les joueurs avec un message personnalisé et associe les scores à leur pseudo.
* **👀 Aperçu de l'Image** : Pendant le jeu, un aperçu de l'image originale est disponible pour aider le joueur. Il est même possible de l'agrandir pour une meilleure visibilité.
* **⏱️ Suivi en Temps Réel** : Le temps écoulé et le nombre de mouvements sont affichés et mis à jour en direct, ajoutant une dimension compétitive.
* **🎉 Célébration de Victoire** : Une fois le puzzle résolu, l'image complète est affichée et le score final est présenté au joueur.

## 🕹️ Comment Jouer

L'expérience de jeu est conçue pour être intuitive et agréable.

1.  **Démarrage** : Lancez l'application pour arriver sur le menu principal animé.
2.  **Nouvelle Partie** : Cliquez sur "▶ Nouvelle Partie".
3.  **Sélection d'Image** : Une fenêtre s'ouvrira, vous invitant à choisir un fichier image (`.jpg` ou `.png`) sur votre ordinateur.
4.  **Pseudo** : Entrez votre pseudo. Par défaut, il sera "Guest".
5.  **Difficulté** : Choisissez votre niveau de difficulté (3x3, 4x4, ou 5x5).
6.  **Le Jeu** : La partie commence ! Cliquez sur les tuiles adjacentes à la case vide pour les déplacer.
7.  **Objectif** : Reconstituez l'image originale dans le bon ordre.
8.  **Fin de Partie** : Une fois l'image complétée, votre score est calculé et sauvegardé si il est assez élevé. Vous êtes alors redirigé vers le menu principal.

## 🛠️ Architecture Technique

Le projet est développé en **Java** avec la bibliothèque **Swing**, en suivant les meilleures pratiques de la programmation orientée objet.

* **Structure Modulaire** : Le jeu est divisé en panneaux (`JPanel`) gérés par un `CardLayout`, ce qui permet une navigation fluide entre les différentes sections :
    * `MainMenuPanel` : L'écran d'accueil.
    * `PuzzlePanel` : L'écran de jeu principal.
    * `HighScorePanel` : L'écran des meilleurs scores.
* **Gestion d'État** : La classe principale `PuzzleGame` (qui hérite de `JFrame`) agit comme un contrôleur central, gérant l'état du jeu (nom du joueur, image sélectionnée, etc.).
* **Rendu Personnalisé (Custom Painting)** : L'aspect visuel dynamique est obtenu en surchargeant la méthode `paintComponent(Graphics g)` dans chaque panneau. Cela permet de dessiner des dégradés complexes (`GradientPaint`, `RadialGradientPaint`) et de les animer à l'aide de `javax.swing.Timer`.
* **Gestion des Événements** : Le jeu utilise des `ActionListener` et des `MouseAdapter` pour gérer les interactions de l'utilisateur de manière efficace et réactive.
* **Persistance des Données** : Les meilleurs scores sont sauvegardés dans un fichier texte (`highscores.txt`), assurant leur persistance entre les sessions de jeu.

## 🎨 Thème et Style

L'un des points forts de **Puzzle Prestige** est son identité visuelle sophistiquée.

* **Arrière-plans Animés** : Chaque écran possède son propre dégradé animé, créant une expérience visuelle cohérente.
    * **Menu Principal** : Un dégradé sombre et élégant dont la teinte évolue lentement, avec des couleurs de texte et de boutons qui s'adaptent en temps réel pour une harmonie parfaite.
    * **Jeu** : Un fond bleu/violet profond et animé pour une atmosphère de concentration.
    * **Meilleurs Scores** : Un dégradé radial doré et chatoyant, évoquant une salle des trophées prestigieuse.
* **Polices et Couleurs Centralisées** : Les polices (`Font`) et les couleurs (`Color`) sont définies comme des constantes statiques, ce qui facilite la maintenance et la personnalisation du thème.

## 🚀 Améliorations Futures Potentielles

Le projet a été conçu pour être évolutif. Voici quelques idées pour de futures versions :

* **🎵 Ajout d'Effets Sonores et de Musique** : Une musique d'ambiance et des effets sonores lors des déplacements de tuiles ou de la victoire.
* **🌐 Puzzles en Ligne** : La possibilité de télécharger des images depuis une URL ou une banque d'images en ligne.
* **🧩 Formes de Tuiles Personnalisées** : Aller au-delà des carrés avec des formes de pièces plus complexes.
* **✨ Thèmes Visuels Multiples** : Permettre au joueur de choisir entre plusieurs thèmes visuels (sombre, clair, coloré, etc.).
* **🌍 Internationalisation** : Ajouter le support pour plusieurs langues.

---

## 👨‍💻 Auteur

Ce projet a été conçu, développé et perfectionné avec passion par :

**TechNerdSam (Samyn-Antoy ABASSE)**

## 📧 Contact

Pour toute question, suggestion ou opportunité de collaboration, n'hésitez pas à envoyer un email à :

**samynantoy@gmail.com**
