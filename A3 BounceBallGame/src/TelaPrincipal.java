import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TelaPrincipal extends JFrame {

    private static final int LARGURA = 1920;
    private static final int ALTURA = 1080;

    public TelaPrincipal() {
        setTitle("Tela Principal");
        setSize(LARGURA, ALTURA);
        setUndecorated(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        setFocusable(true);
        exibirRegras();
    }

    private void exibirRegras() {
        getContentPane().setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        // Título "Regras do Jogo"
        JLabel tituloLabel = new JLabel("Regras do Jogo");
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 36));
        tituloLabel.setForeground(Color.WHITE);
        tituloLabel.setHorizontalAlignment(JLabel.CENTER);
        tituloLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(tituloLabel, BorderLayout.NORTH);

        JTextArea regrasTextArea = new JTextArea();
        regrasTextArea.setFont(new Font("Arial", Font.PLAIN, 24));
        regrasTextArea.setForeground(Color.WHITE);
        regrasTextArea.setBackground(Color.BLACK);
        regrasTextArea.setEditable(false);
        regrasTextArea.append("- Mexer a base: Setas do teclado\n");
        regrasTextArea.append("- Interromper o jogo: Número 0\n");
        regrasTextArea.append("- Pausar o jogo: ESC\n");
        regrasTextArea.append("- Começar o jogo: ENTER\n\n");
        regrasTextArea.append("- Após atingir 200 pontos, a bolinha ficará mais rápida e terá um novo obstáculo.\n\n");
        regrasTextArea.append("Pressione a tecla ESPAÇO para fechar esta tela e iniciar o jogo.");

        JScrollPane scrollPane = new JScrollPane(regrasTextArea);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void formKeyPressed(KeyEvent evt) {
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_SPACE) {
            abrirJogo();
        }
    }

    private void abrirJogo() {
        EventQueue.invokeLater(() -> {
            setVisible(false);
            dispose();
            abrirJanelaJogo();
        });
    }

    private void abrirJanelaJogo() {
        EventQueue.invokeLater(() -> {
            BounceBallGame game = new BounceBallGame();
            game.iniciarJogo();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaPrincipal().setVisible(true));
    }
}