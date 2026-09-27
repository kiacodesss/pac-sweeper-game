package my.minesweeper;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

/**
 *
 * @author Kiana Yeo
 * @date September 27, 2026
 */
public class Pac_Sweeper_GameUI extends javax.swing.JFrame {

    public Pac_Sweeper_GameUI() {

        initComponents();

        getContentPane().setBackground(Color.BLACK);
        jPanel1.setBackground(Color.BLACK);
        jPanel2.setBackground(Color.BLACK);

        // =========================
        // MINE COUNTER
        // =========================
        jLabel1.setBackground(Color.BLACK);
        jLabel1.setForeground(Color.YELLOW);
        jLabel1.setFont(new Font("Monospaced", Font.BOLD, 24));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setOpaque(true);
        jLabel1.setBorder(new LineBorder(new Color(0, 180, 255), 2));

        // =========================
        // TIMER
        // =========================
        jLabel2.setBackground(Color.BLACK);
        jLabel2.setForeground(Color.YELLOW);
        jLabel2.setFont(new Font("Monospaced", Font.BOLD, 24));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setOpaque(true);
        jLabel2.setBorder(new LineBorder(new Color(0, 180, 255), 2));

        // =========================
        // RESET BUTTON
        // =========================
        jButton1.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        jButton1.setBackground(Color.BLACK);
        jButton1.setOpaque(true);
        jButton1.setContentAreaFilled(true);
        jButton1.setFocusPainted(false);
        jButton1.setBorderPainted(true);
        jButton1.setBorder(new LineBorder(new Color(0, 180, 255), 2));
        jButton1.setRolloverEnabled(false);

        setLocationRelativeTo(null);

        createButtons();
    }

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Pac_Sweeper_GameUI.class.getName());
    /**
     * Creates new form Mine_SweeperUI
     */

    // =========================
    // MINESWEEPER GAME SETTINGS
    // =========================
    private static final int ROWS = 9;
    private static final int COLS = 9;
    private static final int MINES = 10;

    private int minesRemaining = MINES;
    private int seconds = 0;

    private boolean gameOver = false;
    private boolean firstClick = true;

    private int[][] symbols = new int[ROWS][COLS];
    private boolean[][] revealed = new boolean[ROWS][COLS];
    private boolean[][] flagged = new boolean[ROWS][COLS];

    private JButton[][] buttons = new JButton[ROWS][COLS];

    private Timer gameTimer;

    Icon iconPacman = new ImageIcon(getClass().getResource("pacman.png"));
    Icon iconGhost = new ImageIcon(getClass().getResource("ghost.png"));
    Icon iconSad = new ImageIcon(getClass().getResource("sad.png"));
    Icon iconFlag = new ImageIcon(getClass().getResource("flag.png"));

    // =========================
    // CREATE THE BOARD
    // =========================
    void createButtons() {

        jPanel2.removeAll();
        jPanel2.setBackground(Color.BLACK);

        jPanel2.setLayout(new GridLayout(ROWS, COLS));

        for (int row = 0; row < ROWS; row++) {

            for (int col = 0; col < COLS; col++) {

                JButton btn = new JButton();

                btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());

                btn.setFont(new Font("Monospaced", Font.BOLD, 16));
                btn.setFocusPainted(false);

                btn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                btn.setVerticalAlignment(javax.swing.SwingConstants.CENTER);

// BLACK TILE
                btn.setBackground(Color.BLACK);
                btn.setOpaque(true);

// BRIGHT BLUE BORDER
                btn.setBorder(new LineBorder(new Color(0, 180, 255), 1));
                btn.setBorderPainted(true);

// KEEP BLACK TILE BACKGROUND
                btn.setContentAreaFilled(true);

                final int r = row;
                final int c = col;

                // LEFT CLICK
                btn.addActionListener(e -> {

                    if (gameOver) {
                        return;
                    }

                    // First click starts the game
                    if (firstClick) {

                        firstClick = false;

                        generateMines(r, c);
                        calculateNumbers();

                        startTimer();
                    }

                    revealCell(r, c);
                });

                // RIGHT CLICK = FLAG
                btn.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mousePressed(MouseEvent e) {

                        if (SwingUtilities.isRightMouseButton(e)) {

                            if (gameOver || firstClick || revealed[r][c]) {
                                return;
                            }

                            toggleFlag(r, c);
                        }
                    }
                });

                buttons[row][col] = btn;

                jPanel2.add(btn);
            }
        }

        jPanel2.revalidate();
        jPanel2.repaint();
    }

    // =========================
    // GENERATE MINES
    // =========================
    void generateMines(int safeRow, int safeCol) {

        int minesPlaced = 0;

        while (minesPlaced < MINES) {

            int row = (int) (Math.random() * ROWS);
            int col = (int) (Math.random() * COLS);

            // First clicked cell cannot be a mine
            if (row == safeRow && col == safeCol) {
                continue;
            }

            // Don't place duplicate mines
            if (symbols[row][col] == -1) {
                continue;
            }

            symbols[row][col] = -1;
            minesPlaced++;
        }
    }

    // =========================
    // CALCULATE NUMBERS
    // =========================
    void calculateNumbers() {

        for (int row = 0; row < ROWS; row++) {

            for (int col = 0; col < COLS; col++) {

                if (symbols[row][col] == -1) {
                    continue;
                }

                int mineCount = countAdjacentMines(row, col);

                symbols[row][col] = mineCount;
            }
        }
    }

    // =========================
    // COUNT ADJACENT MINES
    // =========================
    int countAdjacentMines(int row, int col) {

        int count = 0;

        for (int r = row - 1; r <= row + 1; r++) {

            for (int c = col - 1; c <= col + 1; c++) {

                if (r < 0 || r >= ROWS || c < 0 || c >= COLS) {
                    continue;
                }

                if (r == row && c == col) {
                    continue;
                }

                if (symbols[r][c] == -1) {
                    count++;
                }
            }
        }

        return count;
    }

    // =========================
    // REVEAL CELL
    // =========================
    void revealCell(int row, int col) {

        if (gameOver) {
            return;
        }

        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
            return;
        }

        if (revealed[row][col] || flagged[row][col]) {
            return;
        }

        // Clicked a mine
        if (symbols[row][col] == -1) {

            revealMine(row, col);

            loseGame();

            return;
        }

        revealed[row][col] = true;

        JButton btn = buttons[row][col];

        int number = symbols[row][col];

        // Remove previous icon/text
        btn.setIcon(null);
        btn.setDisabledIcon(null);

        if (number == 0) {

            // Zero = Pac-Man
            btn.setText("");
            btn.setIcon(iconPacman);

        } else {

            // 1-8 = Minesweeper number
            btn.setText(String.valueOf(number));
            btn.setIcon(null);
            btn.setDisabledIcon(null);

            setNumberColor(btn, number);
        }

        // Automatically reveal surrounding cells
        if (number == 0) {

            for (int r = row - 1; r <= row + 1; r++) {

                for (int c = col - 1; c <= col + 1; c++) {

                    if (r == row && c == col) {
                        continue;
                    }

                    revealCell(r, c);
                }
            }
        }

        checkWin();
    }

    // =========================
    // NUMBER COLORS
    // =========================
    void setNumberColor(JButton button, int number) {

        button.setForeground(new Color(255, 0, 0));
        button.setFont(new Font("Monospaced", Font.BOLD, 20));
    }

    // =========================
    // FLAG / UNFLAG
    // =========================
    void toggleFlag(int row, int col) {

        if (revealed[row][col]) {
            return;
        }

        // Don't allow more flags than the number of mines
        if (!flagged[row][col] && minesRemaining == 0) {
            return;
        }

        flagged[row][col] = !flagged[row][col];

        JButton btn = buttons[row][col];

        if (flagged[row][col]) {

            btn.setText("");
            btn.setIcon(iconFlag);

            minesRemaining--;

        } else {

            btn.setText("");
            btn.setIcon(null);

            minesRemaining++;
        }

        jLabel1.setText(String.format("%03d", minesRemaining));
    }

    // =========================
    // REVEAL MINE
    // =========================
    void revealMine(int row, int col) {

        buttons[row][col].setText("");
        buttons[row][col].setIcon(iconGhost);

        revealed[row][col] = true;
    }

    // =========================
    // REVEAL ALL MINES
    // =========================
    void revealAllMines() {

        for (int row = 0; row < ROWS; row++) {

            for (int col = 0; col < COLS; col++) {

                if (symbols[row][col] == -1) {

                    buttons[row][col].setText("");
                    buttons[row][col].setIcon(iconGhost);
                }
            }
        }
    }

    // =========================
    // LOSE GAME
    // =========================
    void loseGame() {

        gameOver = true;

        stopTimer();

        revealAllMines();

        JOptionPane.showMessageDialog(
                this,
                "You hit a mine!",
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE,
                iconSad
        );
    }

    // =========================
    // CHECK WIN
    // =========================
    void checkWin() {

        int revealedCells = 0;

        for (int row = 0; row < ROWS; row++) {

            for (int col = 0; col < COLS; col++) {

                if (revealed[row][col]) {
                    revealedCells++;
                }
            }
        }

        int safeCells = (ROWS * COLS) - MINES;

        if (revealedCells == safeCells) {

            gameOver = true;

            stopTimer();

            // Automatically flag all mines
            for (int row = 0; row < ROWS; row++) {

                for (int col = 0; col < COLS; col++) {

                    if (symbols[row][col] == -1) {

                        buttons[row][col].setText("⚑");
                    }
                }
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Congratulations!\nYou cleared the minefield!\nTime: "
                    + String.format("%03d", seconds) + " seconds.",
                    "You Win!",
                    JOptionPane.INFORMATION_MESSAGE,
                    iconPacman
            );
        }
    }

    // =========================
    // TIMER
    // =========================
    void startTimer() {

        seconds = 0;
        jLabel2.setText(String.format("%03d", seconds));

        if (gameTimer != null) {
            gameTimer.stop();
        }

        gameTimer = new Timer(1000, e -> {

            seconds++;

            jLabel2.setText(String.format("%03d", seconds));

            if (seconds >= 180) {

                gameOver = true;

                stopTimer();

                revealAllMines();

                JOptionPane.showMessageDialog(
                        this,
                        "Time's up!",
                        "Game Over",
                        JOptionPane.INFORMATION_MESSAGE,
                        iconSad
                );
            }
        });

        gameTimer.start();
    }

    void stopTimer() {

        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    // =========================
    // RESET GAME
    // =========================
    void resetGame() {

        stopTimer();

        minesRemaining = MINES;
        seconds = 0;

        gameOver = false;
        firstClick = true;

        symbols = new int[ROWS][COLS];
        revealed = new boolean[ROWS][COLS];
        flagged = new boolean[ROWS][COLS];

        jLabel1.setText(String.format("%03d", minesRemaining));
        jLabel2.setText(String.format("%03d", seconds));

        createButtons();

        jPanel2.revalidate();
        jPanel2.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 180, 255), 2));
        jPanel2.setLayout(new java.awt.GridLayout(9, 9));

        jLabel3.setFont(new java.awt.Font("Monospaced", 1, 10)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 0));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("made by kiacodesss");

        jLabel4.setFont(new java.awt.Font("Monospaced", 1, 26)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 0));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Pac-Sweeper");

        jButton1.setBackground(new java.awt.Color(0, 0, 0));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/minesweeper/smiley.png"))); // NOI18N
        jButton1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 180, 255), 2));
        jButton1.setFocusPainted(false);
        jButton1.setFocusable(false);
        jButton1.setMaximumSize(new java.awt.Dimension(28, 28));
        jButton1.setMinimumSize(new java.awt.Dimension(28, 28));
        jButton1.addActionListener(this::jButton1ActionPerformed);

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Monospaced", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("0");
        jLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 180, 255), 2));
        jLabel1.setOpaque(true);

        jLabel2.setBackground(new java.awt.Color(0, 0, 0));
        jLabel2.setFont(new java.awt.Font("Monospaced", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 0));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("0");
        jLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 180, 255), 2));
        jLabel2.setOpaque(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(86, 86, 86))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 19, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(18, 20, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        resetGame();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        resetGame();
    }//GEN-LAST:event_formWindowOpened

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Pac_Sweeper_GameUI().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
