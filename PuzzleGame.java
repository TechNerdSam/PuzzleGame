import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * PuzzleGame --- A high-end, professional sliding puzzle game.
 * This class serves as the main frame and controller for the entire application.
 * It manages the different panels (MainMenu, Puzzle, HighScores) using a CardLayout.
 * <p>
 * Jeu de Puzzle --- Un jeu de taquin professionnel et haut de gamme.
 * Cette classe sert de fenêtre principale et de contrôleur pour l'application.
 * Elle gère les différents panneaux (Menu Principal, Jeu, Meilleurs Scores) via un CardLayout.
 */
public class PuzzleGame extends JFrame {

    // --- CONSTANTS ---

    /**
     * The file name for storing high scores.
     * Le nom du fichier pour stocker les meilleurs scores.
     */
    private static final String HIGHSCORE_FILE = "highscores.txt";

    /**
     * Default player name if none is entered.
     * Nom du joueur par défaut si aucun n'est saisi.
     */
    private static final String DEFAULT_PLAYER_NAME = "Guest";

    // --- THEME & STYLING ---
    // Centralized theme colors and fonts for easy customization.
    // Couleurs et polices du thème centralisées pour une personnalisation facile.

    private static final Color COLOR_BACKGROUND = new Color(45, 52, 54);
    private static final Color COLOR_FOREGROUND = Color.WHITE;
    private static final Color COLOR_ACCENT = new Color(99, 110, 114);
    private static final Font FONT_TITLE = new Font("Serif", Font.BOLD, 60);
    private static final Font FONT_BUTTON = new Font("SansSerif", Font.BOLD, 22);
    private static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 18);

    // --- UI COMPONENTS ---
    // Main panels managed by CardLayout.
    // Panneaux principaux gérés par le CardLayout.

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);
    private final MainMenuPanel mainMenuPanel;
    private final PuzzlePanel puzzlePanel;
    private final HighScorePanel highScorePanel;

    // --- GAME STATE ---
    // Fields representing the current state of the game.
    // Champs représentant l'état actuel du jeu.

    private String currentPlayerName = DEFAULT_PLAYER_NAME;
    private BufferedImage originalImage;

    /**
     * Constructor for PuzzleGame.
     * Initializes the main frame and all its sub-panels.
     * <p>
     * Constructeur de PuzzleGame.
     * Initialise la fenêtre principale et tous ses sous-panneaux.
     */
    public PuzzleGame() {
        // --- FRAME INITIALIZATION ---
        setTitle("Puzzle Prestige");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window / Centrer la fenêtre

        // --- PANEL CREATION ---
        mainMenuPanel = new MainMenuPanel();
        puzzlePanel = new PuzzlePanel();
        highScorePanel = new HighScorePanel();

        // --- LAYOUT SETUP ---
        mainPanel.add(mainMenuPanel, "MainMenu");
        mainPanel.add(puzzlePanel, "PuzzleGame");
        mainPanel.add(highScorePanel, "HighScores");
        add(mainPanel);

        // --- INITIAL DISPLAY ---
        showMainMenu();
    }

    // --- NAVIGATION METHODS ---

    /**
     * Displays the main menu panel.
     * Affiche le panneau du menu principal.
     */
    private void showMainMenu() {
        mainMenuPanel.updateGreeting(currentPlayerName);
        cardLayout.show(mainPanel, "MainMenu");
    }

    /**
     * Initiates the process of starting a new game by first asking the user to select an image.
     * Lance le processus de démarrage d'une nouvelle partie en demandant d'abord à l'utilisateur de sélectionner une image.
     */
    private void prepareGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Sélectionnez une image pour le puzzle");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images", "jpg", "png"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                // Read the selected image file.
                // Lire le fichier image sélectionné.
                File selectedFile = fileChooser.getSelectedFile();
                originalImage = ImageIO.read(selectedFile);

                // Ensure the file is a valid image.
                // S'assurer que le fichier est une image valide.
                if (originalImage == null) {
                    throw new IOException("Le fichier sélectionné n'est pas une image valide ou ne peut pas être lu.");
                }

                // Ask for player's name.
                // Demander le nom du joueur.
                promptForPlayerName();

                // Proceed to difficulty selection.
                // Passer à la sélection de la difficulté.
                chooseDifficulty();

            } catch (IOException e) {
                // Display a user-friendly error message if image loading fails.
                // Afficher un message d'erreur clair si le chargement de l'image échoue.
                JOptionPane.showMessageDialog(this,
                        "Erreur de chargement d'image: " + e.getMessage(),
                        "Erreur de Fichier",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Prompts the user to enter their name.
     * Demande à l'utilisateur de saisir son nom.
     */
    private void promptForPlayerName() {
        String name = JOptionPane.showInputDialog(this, "Entrez votre pseudo:", currentPlayerName);
        if (name != null && !name.trim().isEmpty()) {
            // Sanitize the input by trimming whitespace.
            // Nettoyer l'entrée en supprimant les espaces.
            this.currentPlayerName = name.trim();
        } else {
            this.currentPlayerName = DEFAULT_PLAYER_NAME;
        }
    }

    /**
     * Displays a dialog to let the user choose the puzzle's grid size (difficulty).
     * Affiche une boîte de dialogue pour que l'utilisateur choisisse la taille de la grille (difficulté).
     */
    private void chooseDifficulty() {
        Object[] options = {"Facile (3x3)", "Moyen (4x4)", "Difficile (5x5)"};
        int choice = JOptionPane.showOptionDialog(this,
                "Choisissez la difficulté",
                "Difficulté",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[1]);

        int gridSize = 4; // Default to medium / Par défaut sur moyen
        if (choice == 0) {
            gridSize = 3; // Easy / Facile
        } else if (choice == 2) {
            gridSize = 5; // Hard / Difficile
        }

        startGame(gridSize);
    }

    /**
     * Starts the puzzle game with the selected grid size.
     * Démarre le jeu de puzzle avec la taille de grille sélectionnée.
     *
     * @param gridSize The dimension of the puzzle grid (e.g., 3 for a 3x3 grid).
     * La dimension de la grille du puzzle (ex: 3 pour une grille 3x3).
     */
    private void startGame(int gridSize) {
        puzzlePanel.startGame(gridSize, originalImage);
        cardLayout.show(mainPanel, "PuzzleGame");
    }

    /**
     * Saves the player's score to the high score file.
     * This method is synchronized to prevent concurrent file access issues, though unlikely in a single-threaded Swing app.
     * <p>
     * Enregistre le score du joueur dans le fichier des meilleurs scores.
     * Cette méthode est synchronisée pour éviter les problèmes d'accès concurrents, bien que peu probables dans une application Swing.
     *
     * @param name  The player's name. / Le nom du joueur.
     * @param score The player's final score. / Le score final du joueur.
     */
    private synchronized void saveHighScore(String name, int score) {
        // Do not save zero scores. / Ne pas enregistrer les scores de zéro.
        if (score <= 0) return;

        // Use try-with-resources for automatic resource management (safer file handling).
        // Utiliser try-with-resources pour la gestion automatique des ressources (gestion de fichiers plus sûre).
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGHSCORE_FILE, true))) {
            writer.write(name + "," + score);
            writer.newLine();
        } catch (IOException e) {
            // Log the error and inform the user if saving fails.
            // Enregistrer l'erreur et informer l'utilisateur si la sauvegarde échoue.
            System.err.println("Error saving high score: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Impossible d'enregistrer le score.",
                    "Erreur Fichier",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- INNER CLASSES (PANELS) ---
    // Each panel is an inner class, tightly coupled with the main PuzzleGame frame.
    // Chaque panneau est une classe interne, étroitement liée à la fenêtre principale PuzzleGame.

    /**
     * MainMenuPanel --- The first screen the user sees.
     * Provides navigation to start a game, view high scores, or exit.
     * <p>
     * MainMenuPanel --- Le premier écran que l'utilisateur voit.
     * Offre la navigation pour démarrer une partie, voir les scores, ou quitter.
     */
    private class MainMenuPanel extends JPanel {
        private JLabel greetingLabel;
        private Timer animationTimer;
        
        private float hue = 0.55f;
        private Color gradientStartColor = COLOR_BACKGROUND;
        private Color gradientEndColor = COLOR_ACCENT;
        
        private JButton newGameButton;
        private JButton highScoresButton;
        private JButton quitButton;


        public MainMenuPanel() {
            this.setLayout(new GridBagLayout());

            animationTimer = new Timer(40, e -> {
                hue += 0.001f;
                if (hue > 1.0f) {
                    hue = 0.0f;
                }
                gradientStartColor = Color.getHSBColor(hue, 0.5f, 0.3f);
                gradientEndColor = Color.getHSBColor(hue + 0.1f, 0.5f, 0.2f);
                
                Color titleColor = Color.getHSBColor((hue + 0.5f) % 1.0f, 0.4f, 1.0f);
                Color buttonBgColor = Color.getHSBColor(hue, 0.4f, 0.4f);
                Color buttonFgColor = Color.getHSBColor(hue, 0.1f, 0.95f);

                greetingLabel.setForeground(titleColor);
                newGameButton.setBackground(buttonBgColor);
                newGameButton.setForeground(buttonFgColor);
                highScoresButton.setBackground(buttonBgColor);
                highScoresButton.setForeground(buttonFgColor);
                quitButton.setBackground(buttonBgColor);
                quitButton.setForeground(buttonFgColor);
                
                repaint(); 
            });
            

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(20, 20, 20, 20);

            greetingLabel = new JLabel("Bienvenue", SwingConstants.CENTER);
            greetingLabel.setFont(FONT_TITLE);
            greetingLabel.setForeground(COLOR_FOREGROUND);
            add(greetingLabel, gbc);

            gbc.gridy++;
            JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 20, 20));
            buttonPanel.setOpaque(false);

            newGameButton = createMenuButton("▶ Nouvelle Partie", e -> prepareGame());
            buttonPanel.add(newGameButton);
            
            highScoresButton = createMenuButton("🏆 Meilleurs Scores", e -> {
                highScorePanel.loadHighScores();
                cardLayout.show(mainPanel, "HighScores");
            });
            buttonPanel.add(highScoresButton);
            
            quitButton = createMenuButton("❌ Quitter", e -> System.exit(0));
            buttonPanel.add(quitButton);
            
            add(buttonPanel, gbc);
            
            animationTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gp = new GradientPaint(
                    0, 0, gradientStartColor,
                    getWidth(), getHeight(), gradientEndColor);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        private JButton createMenuButton(String text, ActionListener listener) {
            JButton button = new JButton(text);
            button.setFont(FONT_BUTTON);
            button.setBackground(COLOR_ACCENT);
            button.setForeground(COLOR_FOREGROUND);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
            button.addActionListener(listener);
            return button;
        }

        public void updateGreeting(String name) {
            greetingLabel.setText(name.equals(DEFAULT_PLAYER_NAME) ? "Puzzle Prestige" : "Bon retour, " + name);
        }
    }

    /**
     * PuzzlePanel --- The main game screen where the puzzle is solved.
     * Contains the puzzle grid and game information like time and moves.
     * <p>
     * PuzzlePanel --- L'écran de jeu principal où le puzzle est résolu.
     * Contient la grille et les informations de jeu comme le temps et les mouvements.
     */
    private class PuzzlePanel extends JPanel {
        private JButton[][] buttons;
        private int emptyRow, emptyCol, gridSize;
        private BufferedImage originalImage;
        private Timer gameTimer;
        private int timeElapsed = 0;
        private int moveCount = 0;
        private final JLabel timeLabel = createSideLabel("Temps: 0s");
        private final JLabel moveCountLabel = createSideLabel("Mouvements: 0");

        // --- AJOUT : Variables pour l'animation de l'arrière-plan du jeu ---
        private Timer backgroundAnimationTimer;
        private float backgroundHue = 0.6f; // Teinte de départ (bleu/violet)
        private Color gameGradientStart = new Color(25, 25, 80);
        private Color gameGradientEnd = new Color(50, 25, 120);

        public PuzzlePanel() {
            setLayout(new BorderLayout(15, 15));
            setBorder(new EmptyBorder(15, 15, 15, 15));
            // Le setBackground est maintenant géré par paintComponent
            
            // --- AJOUT : Initialisation du Timer pour l'animation de fond ---
            backgroundAnimationTimer = new Timer(50, e -> {
                backgroundHue += 0.0005f;
                if (backgroundHue > 1.0f) {
                    backgroundHue = 0.0f;
                }
                // Crée des couleurs dans les tons bleus/violets qui varient lentement
                gameGradientStart = Color.getHSBColor(backgroundHue, 0.7f, 0.4f);
                gameGradientEnd = Color.getHSBColor(backgroundHue + 0.05f, 0.7f, 0.5f);
                
                // --- AJOUT : Adapte la couleur du texte pour la lisibilité ---
                Color textColor = Color.getHSBColor(backgroundHue + 0.5f, 0.2f, 1.0f);
                timeLabel.setForeground(textColor);
                moveCountLabel.setForeground(textColor);
                repaint();
            });
            backgroundAnimationTimer.start();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // --- MODIFICATION : Utilisation des couleurs animées pour le dégradé ---
            GradientPaint gp = new GradientPaint(0, 0, gameGradientStart, getWidth(), getHeight(), gameGradientEnd);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        public void startGame(int gridSize, BufferedImage image) {
            this.gridSize = gridSize;
            this.originalImage = image;
            this.removeAll(); 

            setupSidePanel();
            setupGrid();
            
            revalidate();
            repaint();
            startTimer();
        }

        private void setupSidePanel() {
            JPanel sidePanel = new JPanel();
            sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
            sidePanel.setOpaque(false);
            sidePanel.setPreferredSize(new Dimension(250, 0));
            
            JLabel imagePreviewLabel = new JLabel();
            imagePreviewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            imagePreviewLabel.setIcon(new ImageIcon(originalImage.getScaledInstance(220, 220, Image.SCALE_SMOOTH)));
            imagePreviewLabel.setToolTipText("Cliquez pour agrandir");
            imagePreviewLabel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    JDialog previewDialog = new JDialog(PuzzleGame.this, "Aperçu de l'image", true);
                    previewDialog.add(new JLabel(new ImageIcon(originalImage)));
                    previewDialog.pack();
                    previewDialog.setLocationRelativeTo(PuzzleGame.this);
                    previewDialog.setVisible(true);
                }
            });
            sidePanel.add(imagePreviewLabel);
            sidePanel.add(Box.createRigidArea(new Dimension(0, 20)));

            sidePanel.add(timeLabel);
            sidePanel.add(moveCountLabel);
            sidePanel.add(Box.createVerticalGlue());

            JButton quitButton = new JButton("Retour au Menu");
            quitButton.addActionListener(e -> showMainMenu());
            quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidePanel.add(quitButton);
            
            add(sidePanel, BorderLayout.EAST);
        }

        private void setupGrid() {
            JPanel puzzleGridPanel = new JPanel(new GridLayout(gridSize, gridSize, 2, 2));
            puzzleGridPanel.setOpaque(false);
            buttons = new JButton[gridSize][gridSize];
            
            List<ImageIcon> tiles = createTiles();
            List<Integer> tileIds = new ArrayList<>();
            for(int i=0; i<tiles.size(); i++) tileIds.add(i);
            Collections.shuffle(tileIds);

            for (int i = 0; i < gridSize * gridSize; i++) {
                int r = i / gridSize;
                int c = i % gridSize;
                
                if (i == tiles.size() -1) {
                    buttons[r][c] = new JButton();
                    buttons[r][c].setOpaque(false);
                    buttons[r][c].setContentAreaFilled(false);
                    buttons[r][c].setBorderPainted(false);
                    emptyRow = r;
                    emptyCol = c;
                } else {
                    int tileId = tileIds.get(i);
                    buttons[r][c] = new JButton(tiles.get(tileId));
                    buttons[r][c].putClientProperty("tile_id", tileId);
                    buttons[r][c].setBorder(null);
                }
                
                buttons[r][c].setFocusPainted(false);
                buttons[r][c].addActionListener(new TileClickListener(r, c));
                puzzleGridPanel.add(buttons[r][c]);
            }
            add(puzzleGridPanel, BorderLayout.CENTER);
        }
        
        private List<ImageIcon> createTiles() {
            List<ImageIcon> tiles = new ArrayList<>();
            int tileWidth = originalImage.getWidth() / gridSize;
            int tileHeight = originalImage.getHeight() / gridSize;
            for (int i = 0; i < gridSize * gridSize; i++) {
                int r = i / gridSize;
                int c = i % gridSize;
                BufferedImage tileImage = originalImage.getSubimage(c * tileWidth, r * tileHeight, tileWidth, tileHeight);
                tiles.add(new ImageIcon(tileImage));
            }
            return tiles;
        }

        private void startTimer() {
            timeElapsed = 0;
            moveCount = 0;
            updateInfo();
            if (gameTimer != null && gameTimer.isRunning()) gameTimer.stop();
            gameTimer = new Timer(1000, e -> {
                timeElapsed++;
                updateInfo();
            });
            gameTimer.start();
        }

        private void updateInfo() {
            timeLabel.setText(String.format("Temps: %ds", timeElapsed));
            moveCountLabel.setText(String.format("Mouvements: %d", moveCount));
        }

        private JLabel createSideLabel(String text) {
            JLabel label = new JLabel(text);
            label.setFont(FONT_LABEL);
            label.setForeground(COLOR_FOREGROUND);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setBorder(new EmptyBorder(10, 0, 10, 0));
            return label;
        }

        private void checkWinCondition() {
            boolean isWin = true;
            for(int i = 0; i < gridSize * gridSize - 1; i++){
                JButton button = buttons[i/gridSize][i%gridSize];
                if(button.getIcon() == null || (int)button.getClientProperty("tile_id") != i){
                    isWin = false;
                    break;
                }
            }

            if(isWin) {
                gameTimer.stop();
                buttons[emptyRow][emptyCol].setIcon(createTiles().get(gridSize*gridSize-1));

                int finalScore = Math.max(0, 10000 - (timeElapsed * 10) - (moveCount * 5));
                saveHighScore(currentPlayerName, finalScore);
                JOptionPane.showMessageDialog(this, "Félicitations! Votre score: " + finalScore, "Puzzle Résolu!", JOptionPane.INFORMATION_MESSAGE);
                showMainMenu();
            }
        }

        private class TileClickListener implements ActionListener {
            private final int row, col;
            public TileClickListener(int r, int c) { row = r; col = c; }

            @Override
            public void actionPerformed(ActionEvent e) {
                if ((Math.abs(row - emptyRow) == 1 && col == emptyCol) || (Math.abs(col - emptyCol) == 1 && row == emptyRow)) {
                    moveCount++;
                    updateInfo();
                    
                    Icon clickedIcon = buttons[row][col].getIcon();
                    Object clickedTileId = buttons[row][col].getClientProperty("tile_id");
                    
                    buttons[emptyRow][emptyCol].setIcon(clickedIcon);
                    buttons[emptyRow][emptyCol].putClientProperty("tile_id", clickedTileId);
                    
                    buttons[row][col].setIcon(null);
                    buttons[row][col].putClientProperty("tile_id", null);

                    emptyRow = row;
                    emptyCol = col;
                    checkWinCondition();
                }
            }
        }
    }

    /**
     * HighScorePanel --- Displays the list of top scores.
     * Reads scores from a file and displays them in a sorted list.
     * <p>
     * HighScorePanel --- Affiche la liste des meilleurs scores.
     * Lit les scores depuis un fichier et les affiche dans une liste triée.
     */
    private class HighScorePanel extends JPanel {
        private final JTextArea scoreArea;
        // --- AJOUT : Variables pour l'animation de l'arrière-plan des scores ---
        private Timer scoreAnimationTimer;
        private float scoreHue = 0.12f; // Teinte de départ dorée
        private Color scoreCenterColor = new Color(255, 236, 179);
        private Color scoreEdgeColor = new Color(205, 133, 63);
        private JLabel titleLabel; // Rendu non-final pour changer sa couleur

        public HighScorePanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(new EmptyBorder(20, 20, 20, 20));
            
            titleLabel = new JLabel("Meilleurs Scores", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Serif", Font.BOLD, 40));
            add(titleLabel, BorderLayout.NORTH);

            scoreArea = new JTextArea();
            scoreArea.setEditable(false);
            scoreArea.setFont(new Font("Monospaced", Font.PLAIN, 18));
            scoreArea.setOpaque(false); 

            JScrollPane scrollPane = new JScrollPane(scoreArea);
            scrollPane.setOpaque(false);
            scrollPane.getViewport().setOpaque(false);
            add(scrollPane, BorderLayout.CENTER);

            JButton backButton = new JButton("Retour au Menu");
            backButton.addActionListener(e -> showMainMenu());
            add(backButton, BorderLayout.SOUTH);

            // --- AJOUT : Initialisation et démarrage du Timer pour l'animation ---
            scoreAnimationTimer = new Timer(60, e -> {
                scoreHue += 0.0008f;
                if(scoreHue > 1.0f) scoreHue = 0.0f;
                
                // Maintient une animation dans les tons dorés
                scoreCenterColor = Color.getHSBColor(0.12f, 0.3f, 1.0f - (float) (Math.sin(scoreHue * Math.PI * 2) * 0.1 + 0.1));
                scoreEdgeColor = Color.getHSBColor(0.13f, 0.6f, 0.8f - (float) (Math.sin(scoreHue * Math.PI * 2) * 0.1 + 0.1));

                // Adapte la couleur du texte pour un contraste optimal
                Color textColor = Color.getHSBColor(0.1f, 0.9f, 0.3f + (float) (Math.sin(scoreHue * Math.PI * 2) * 0.1));
                titleLabel.setForeground(textColor);
                scoreArea.setForeground(textColor);
                repaint();
            });
            scoreAnimationTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // --- MODIFICATION : Utilisation des couleurs animées pour le dégradé radial ---
            Point2D center = new Point2D.Float(this.getWidth() / 2f, this.getHeight() / 2f);
            float radius = Math.max(this.getWidth(), this.getHeight());
            float[] dist = {0.0f, 1.0f};
            Color[] colors = {scoreCenterColor, scoreEdgeColor};
            RadialGradientPaint rgp = new RadialGradientPaint(center, radius, dist, colors);
            g2d.setPaint(rgp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        public void loadHighScores() {
            List<ScoreEntry> scores = new ArrayList<>();
            File scoreFile = new File(HIGHSCORE_FILE);

            if (scoreFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(scoreFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length == 2) {
                            try {
                                scores.add(new ScoreEntry(parts[0], Integer.parseInt(parts[1].trim())));
                            } catch (NumberFormatException e) {
                                System.err.println("Skipping malformed score line: " + line);
                            }
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error reading high score file: " + e.getMessage());
                }
            }
            
            scores.sort(Comparator.comparingInt(ScoreEntry::getScore).reversed());

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-25s %s\n", "Pseudo", "Score"));
            sb.append("------------------------------------\n");
            
            scores.stream().limit(10).forEach(entry -> 
                sb.append(String.format("%-25s %d\n", entry.getName(), entry.getScore()))
            );

            scoreArea.setText(scores.isEmpty() ? "\n   Aucun score enregistré." : sb.toString());
        }
        
        private static class ScoreEntry {
            private final String name;
            private final int score;
            public ScoreEntry(String name, int score) { this.name = name; this.score = score; }
            public String getName() { return name; }
            public int getScore() { return score; }
        }
    }

    /**
     * The main entry point of the application.
     * Le point d'entrée principal de l'application.
     *
     * @param args Command line arguments (not used).
     * Arguments de la ligne de commande (non utilisés).
     */
    public static void main(String[] args) {
        // Ensure the UI is created on the Event Dispatch Thread for thread safety.
        // S'assurer que l'UI est créée sur l'Event Dispatch Thread pour la sécurité des threads.
        SwingUtilities.invokeLater(() -> new PuzzleGame().setVisible(true));
    }
}