import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class PuzzleGame extends JFrame {

    private static final String HIGHSCORE_FILE = "highscores.txt";
    private static final int MAX_LEVEL = 20;

    private JPanel mainPanel;
    private CardLayout cardLayout;

    private PuzzlePanel puzzlePanel;
    private HighScorePanel highScorePanel;
    private MainMenuPanel mainMenuPanel;

    private String currentPlayerName = "Guest";
    private int currentScore = 0;
    private int currentLevel = 1;

    // Image principale du puzzle
    private BufferedImage originalImage; // Cette image sera chargée APRES le démarrage, via le menu
    private List<BufferedImage> tileImages; // Liste des sous-images pour les tuiles

    public PuzzleGame() {
        setTitle("Jeu de Puzzle Simplifié");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrer la fenêtre

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainMenuPanel = new MainMenuPanel();
        puzzlePanel = new PuzzlePanel();
        highScorePanel = new HighScorePanel();

        mainPanel.add(mainMenuPanel, "MainMenu");
        mainPanel.add(puzzlePanel, "PuzzleGame");
        mainPanel.add(highScorePanel, "HighScores");

        add(mainPanel);
        showMainMenu();
    }

    // Méthode pour découper l'image en tuiles
    private List<BufferedImage> createTileImages(BufferedImage sourceImage, int gridSize) {
        List<BufferedImage> images = new ArrayList<>();
        int tileWidth = sourceImage.getWidth() / gridSize;
        int tileHeight = sourceImage.getHeight() / gridSize;

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                // Pour la dernière tuile (qui sera vide), nous n'ajoutons pas d'image
                if (!(i == gridSize - 1 && j == gridSize - 1)) {
                    images.add(sourceImage.getSubimage(j * tileWidth, i * tileHeight, tileWidth, tileHeight));
                }
            }
        }
        return images;
    }

    public void showMainMenu() {
        cardLayout.show(mainPanel, "MainMenu");
    }

    public void startGame() {
        // Demander à l'utilisateur de choisir une image avant de démarrer le jeu
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Sélectionnez l'image du puzzle");
        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                BufferedImage chosenImage = ImageIO.read(selectedFile);
                if (chosenImage == null) {
                    throw new IOException("Le fichier sélectionné n'est pas une image valide.");
                }

                // Redimensionner l'image choisie
                int maxGridSize = 5; // Correspond à la grille 5x5 pour level 3+
                int preferredSize = Math.min(chosenImage.getWidth(), chosenImage.getHeight());
                if (preferredSize > 500) preferredSize = 500; // Limite la taille pour l'affichage
                if (preferredSize < 300) preferredSize = 300; // Taille minimale pour la visibilité

                BufferedImage resizedImage = new BufferedImage(preferredSize, preferredSize, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = resizedImage.createGraphics();
                g2d.drawImage(chosenImage, 0, 0, preferredSize, preferredSize, null);
                g2d.dispose();
                this.originalImage = resizedImage; // Mettre à jour l'image principale

                // Créer les tuiles à partir de l'image redimensionnée
                this.tileImages = createTileImages(this.originalImage, maxGridSize);

                // Maintenant que l'image est chargée, procéder au démarrage du jeu
                String name = JOptionPane.showInputDialog(this, "Entrez votre pseudo:", "Pseudo", JOptionPane.PLAIN_MESSAGE);
                if (name != null && !name.trim().isEmpty()) {
                    currentPlayerName = name.trim();
                } else {
                    currentPlayerName = "Guest";
                }
                currentScore = 0;
                currentLevel = 1;
                puzzlePanel.startGame(currentLevel);
                cardLayout.show(mainPanel, "PuzzleGame");

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors du chargement de l'image: " + e.getMessage() + "\n" +
                        "Veuillez sélectionner un fichier image valide (JPG, PNG, GIF, etc.).",
                        "Erreur de chargement d'image", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            // L'utilisateur a annulé la sélection de fichier
            JOptionPane.showMessageDialog(this, "Aucune image sélectionnée. Retour au menu principal.", "Annulé", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void showHighScores() {
        highScorePanel.loadHighScores();
        cardLayout.show(mainPanel, "HighScores");
    }

    public void exitGame() {
        saveHighScore(currentPlayerName, currentScore); // Sauvegarde le score au moment de quitter
        System.exit(0);
    }

    public void levelCompleted() {
        currentScore += 100 * currentLevel; // Augmenter le score à chaque niveau complété
        currentLevel++;
        if (currentLevel > MAX_LEVEL) {
            currentLevel = 1; // Retour au niveau 1 après le niveau 20
            JOptionPane.showMessageDialog(this, "Félicitations ! Vous avez terminé le cycle de niveaux. Le jeu redémarre au niveau 1 avec votre score actuel.", "Niveau Max Atteint", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Niveau " + (currentLevel - 1) + " terminé ! Score actuel : " + currentScore, "Niveau Terminé", JOptionPane.INFORMATION_MESSAGE);
        }
        puzzlePanel.startGame(currentLevel); // Démarrer le nouveau niveau
    }

    public void gameOver() {
        JOptionPane.showMessageDialog(this, "Partie terminée ! Votre score : " + currentScore, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        saveHighScore(currentPlayerName, currentScore);
        showMainMenu();
    }

    private void saveHighScore(String name, int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGHSCORE_FILE, true))) {
            writer.write(name + "," + score);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement du score: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Panneaux du Jeu ---

    class MainMenuPanel extends JPanel {
        public MainMenuPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridx = 0;
            gbc.gridy = 0;

            JLabel titleLabel = new JLabel("Jeu de Puzzle");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
            add(titleLabel, gbc);

            gbc.gridy++;
            JButton startButton = new JButton("Nouvelle Partie");
            startButton.setFont(new Font("Arial", Font.PLAIN, 20));
            startButton.addActionListener(e -> startGame()); // Cette action appellera JFileChooser
            add(startButton, gbc);

            gbc.gridy++;
            JButton highScoresButton = new JButton("Meilleurs Scores");
            highScoresButton.setFont(new Font("Arial", Font.PLAIN, 20));
            highScoresButton.addActionListener(e -> showHighScores());
            add(highScoresButton, gbc);

            gbc.gridy++;
            JButton instructionsButton = new JButton("Instructions");
            instructionsButton.setFont(new Font("Arial", Font.PLAIN, 20));
            instructionsButton.addActionListener(e -> JOptionPane.showMessageDialog(this,
                    "Pour commencer une partie, cliquez sur 'Nouvelle Partie' et sélectionnez une image.\n" +
                            "Reconstituez l'image en cliquant sur les tuiles adjacentes à la tuile vide.\n" +
                            "Chaque niveau augmente la difficulté (plus de tuiles ou réinitialisation des tuiles).\n" +
                            "Le but est de terminer les niveaux et d'obtenir le meilleur score !",
                    "Instructions", JOptionPane.INFORMATION_MESSAGE));
            add(instructionsButton, gbc);

            gbc.gridy++;
            JButton exitButton = new JButton("Quitter");
            exitButton.setFont(new Font("Arial", Font.PLAIN, 20));
            exitButton.addActionListener(e -> exitGame());
            add(exitButton, gbc);
        }
    }

    class PuzzlePanel extends JPanel {
        private JButton[][] buttons;
        private int emptyRow, emptyCol;
        private int gridSize;
        private JLabel scoreLabel;
        private JLabel levelLabel;

        // Stocke l'ordre actuel des tuiles (indices des images dans tileImages)
        private int[][] tileOrder;


        public PuzzlePanel() {
            setLayout(new BorderLayout());
            scoreLabel = new JLabel("Score: 0");
            levelLabel = new JLabel("Niveau: 1");
        }

        public void startGame(int level) {
            // Vérifier si une image a été chargée
            if (originalImage == null || tileImages == null || tileImages.isEmpty()) {
                JOptionPane.showMessageDialog(PuzzleGame.this,
                        "Veuillez d'abord sélectionner une image via 'Nouvelle Partie' dans le menu principal.",
                        "Image Manquante", JOptionPane.WARNING_MESSAGE);
                showMainMenu(); // Retour au menu si pas d'image
                return;
            }

            this.gridSize = Math.min(level + 2, 5); // Grille de 3x3 à 5x5 max (pour level 3 et au-delà)
            removeAll(); // Supprimer les anciens composants de la grille

            setLayout(new BorderLayout());

            JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            scoreLabel.setText("Score: " + currentScore);
            scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
            levelLabel.setText("Niveau: " + currentLevel);
            levelLabel.setFont(new Font("Arial", Font.BOLD, 18));
            infoPanel.add(scoreLabel);
            infoPanel.add(Box.createHorizontalStrut(50));
            infoPanel.add(levelLabel);

            JButton quitButton = new JButton("Quitter la partie");
            quitButton.addActionListener(e -> gameOver());
            infoPanel.add(Box.createHorizontalStrut(50));
            infoPanel.add(quitButton);
            add(infoPanel, BorderLayout.NORTH);

            JPanel puzzleGridPanel = new JPanel(new GridLayout(gridSize, gridSize));
            buttons = new JButton[gridSize][gridSize];
            tileOrder = new int[gridSize][gridSize]; // Nouvelle matrice pour l'ordre des tuiles

            // Créer une liste d'indices de tuiles de 0 à (gridSize*gridSize - 2)
            List<Integer> tileIndices = new ArrayList<>();
            // Attention: tileImages contient gridSize*gridSize - 1 tuiles
            // Donc les indices vont de 0 à (nombre de tuiles - 1)
            for (int i = 0; i < tileImages.size(); i++) { // Utiliser tileImages.size() pour la bonne borne
                tileIndices.add(i);
            }
            Collections.shuffle(tileIndices); // Mélanger les indices

            int currentTileIndex = 0;
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    if (i == gridSize - 1 && j == gridSize - 1) { // Dernière tuile est vide
                        buttons[i][j] = new JButton();
                        buttons[i][j].setPreferredSize(new Dimension(originalImage.getWidth() / gridSize, originalImage.getHeight() / gridSize)); // Taille pour les tuiles
                        buttons[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                        emptyRow = i;
                        emptyCol = j;
                        tileOrder[i][j] = -1; // -1 pour la tuile vide
                    } else {
                        // S'assurer qu'on ne dépasse pas la taille de tileIndices
                        if (currentTileIndex < tileIndices.size()) {
                            int originalImageIndex = tileIndices.get(currentTileIndex++);
                            buttons[i][j] = new JButton(new ImageIcon(tileImages.get(originalImageIndex)));
                            buttons[i][j].setPreferredSize(new Dimension(originalImage.getWidth() / gridSize, originalImage.getHeight() / gridSize)); // Taille pour les tuiles
                            buttons[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                            buttons[i][j].setFocusPainted(false); // Amélioration visuelle
                            tileOrder[i][j] = originalImageIndex; // Stocke l'indice de l'image originale
                        } else {
                             // Ceci ne devrait pas arriver si le calcul de tileImages.size() est correct
                             // Mais c'est une sécurité.
                            buttons[i][j] = new JButton("?"); // Fallback pour tuile manquante
                            buttons[i][j].setPreferredSize(new Dimension(originalImage.getWidth() / gridSize, originalImage.getHeight() / gridSize));
                        }
                    }
                    buttons[i][j].addActionListener(new TileClickListener(i, j));
                    puzzleGridPanel.add(buttons[i][j]);
                }
            }
            add(puzzleGridPanel, BorderLayout.CENTER);
            revalidate();
            repaint();
        }

        private class TileClickListener implements ActionListener {
            private int row, col;

            public TileClickListener(int row, int col) {
                this.row = row;
                this.col = col;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                // Mettre à jour les labels de score et de niveau avant de vérifier le déplacement
                scoreLabel.setText("Score: " + currentScore);
                levelLabel.setText("Niveau: " + currentLevel);

                if ((Math.abs(row - emptyRow) == 1 && col == emptyCol) ||
                        (Math.abs(col - emptyCol) == 1 && row == emptyRow)) {
                    // Échanger les icônes des boutons
                    Icon tempIcon = buttons[row][col].getIcon();
                    buttons[emptyRow][emptyCol].setIcon(tempIcon);
                    buttons[row][col].setIcon(null); // Rendre la tuile cliquée vide

                    // Échanger les indices dans la matrice tileOrder
                    int tempTileIndex = tileOrder[row][col];
                    tileOrder[emptyRow][emptyCol] = tempTileIndex;
                    tileOrder[row][col] = -1; // La nouvelle tuile vide a l'indice -1

                    emptyRow = row;
                    emptyCol = col;
                    checkWin();
                }
            }
        }

        private void checkWin() {
            boolean win = true;
            int expectedIndex = 0;
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    if (i == gridSize - 1 && j == gridSize - 1) {
                        // La dernière tuile doit être la tuile vide (-1)
                        if (tileOrder[i][j] != -1) {
                            win = false;
                            break;
                        }
                    } else {
                        // Vérifier si l'indice de la tuile correspond à l'ordre attendu
                        if (tileOrder[i][j] != expectedIndex) {
                            win = false;
                            break;
                        }
                    }
                    expectedIndex++;
                }
                if (!win) break;
            }

            if (win) {
                levelCompleted();
            }
        }
    }

    class HighScorePanel extends JPanel {
        private JTextArea scoreArea;

        public HighScorePanel() {
            setLayout(new BorderLayout());
            JLabel titleLabel = new JLabel("Meilleurs Scores", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            add(titleLabel, BorderLayout.NORTH);

            scoreArea = new JTextArea();
            scoreArea.setEditable(false);
            scoreArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
            JScrollPane scrollPane = new JScrollPane(scoreArea);
            add(scrollPane, BorderLayout.CENTER);

            JButton backButton = new JButton("Retour au Menu Principal");
            backButton.addActionListener(e -> showMainMenu());
            add(backButton, BorderLayout.SOUTH);
        }

        public void loadHighScores() {
            List<ScoreEntry> scores = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(HIGHSCORE_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        try {
                            String name = parts[0];
                            int score = Integer.parseInt(parts[1]);
                            scores.add(new ScoreEntry(name, score));
                        } catch (NumberFormatException e) {
                            System.err.println("Erreur de format de score dans le fichier: " + line);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Fichier de highscores non trouvé ou erreur de lecture: " + HIGHSCORE_FILE + " (" + e.getMessage() + ")");
                scoreArea.setText("Aucun score enregistré pour le moment.");
                return;
            }

            Collections.sort(scores, Comparator.comparingInt(ScoreEntry::getScore).reversed());

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-20s %s\n", "Pseudo", "Score"));
            sb.append("------------------------------\n");
            int count = 0;
            for (ScoreEntry entry : scores) {
                if (count >= 10) break; // Afficher seulement les 10 meilleurs
                sb.append(String.format("%-20s %d\n", entry.getName(), entry.getScore()));
                count++;
            }
            scoreArea.setText(sb.toString());
            if (scores.isEmpty()) {
                scoreArea.setText("Aucun score enregistré pour le moment.");
            }
        }

        private class ScoreEntry {
            private String name;
            private int score;

            public ScoreEntry(String name, int score) {
                this.name = name;
                this.score = score;
            }

            public String getName() {
                return name;
            }

            public int getScore() {
                return score;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PuzzleGame().setVisible(true);
        });
    }
}