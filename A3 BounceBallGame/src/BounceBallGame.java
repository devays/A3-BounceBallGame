import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

public class BounceBallGame extends JFrame {

    private static final int LARGURA = 1920;
    private static final int ALTURA = 1080;
    private static final int VIDAS_INICIAIS = 5;
    private static final int TAMANHO_CORACAO = 20;
    private static final int TAMANHO_LOSANGO = 143;
    private static final int TAMANHO_BOLA = 12;

    private float bolaX = LARGURA / 2.0f;
    private float bolaY = ALTURA / 2.0f;
    private float velocidadeInicialBolaX = 5.0f;
    private float velocidadeInicialBolaY = 3.0f;
    private float velocidadeBolaX = velocidadeInicialBolaX;
    private float velocidadeBolaY = velocidadeInicialBolaY;

    private Color corFundo = Color.BLACK;

    private float raqueteX = LARGURA / 2.0f - 50.0f;
    private static final float RAQUETE_Y = ALTURA - 20.0f;
    private static final float RAQUETE_LARGURA = 100.0f;
    private static final float RAQUETE_ALTURA = 10.0f;

    private boolean jogoRodando = false;
    private boolean jogoPausado = false;
    private boolean mostrarMensagemInicial = true;
    private boolean segundaFase = false;

    private int pontuacao = 0;
    private int vidas = VIDAS_INICIAIS;

    private String mensagemTentarNovamente = "Tente novamente";
    private String mensagemInicio = "Aperte ENTER para começar a jogar";
    private String mensagemPausa = "Jogo pausado, aperte ESC para retornar ao jogo";
    private String mensagemFimDeJogo = "Game Over! Suas vidas acabaram. Pressione ENTER para jogar novamente.";

    private List<Rectangle> coracoes;
    private int vidasRestantes;

    private Losango losango;

    public BounceBallGame() {
        setTitle("BounceBall Game");
        setSize(LARGURA, ALTURA);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        coracoes = new ArrayList<>();
        resetarCoracoes();

        losango = new Losango(LARGURA / 2.0f - TAMANHO_LOSANGO / 2.0f, ALTURA / 2.0f - TAMANHO_LOSANGO / 2.0f, TAMANHO_LOSANGO, TAMANHO_LOSANGO);

        Timer timer = new Timer(16, e -> {
            if (jogoRodando && !jogoPausado) {
                atualizar();
                repaint();
            }
        });
        timer.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                teclaPressionada(evt);
            }
        });

        setFocusable(true);
        mostrarMensagemInicial();
    }

    public void iniciarJogo() {
        SwingUtilities.invokeLater(() -> {
            BounceBallGame game = new BounceBallGame();
            game.setUndecorated(true);
            game.setExtendedState(JFrame.MAXIMIZED_BOTH);
            game.setVisible(true);
        });
    }

    private void mostrarMensagemInicial() {
        if (mostrarMensagemInicial) {
            repaint();
        }
    }

    private void teclaPressionada(KeyEvent evt) {
        int tecla = evt.getKeyCode();
        if (!jogoRodando) {
            if (tecla == KeyEvent.VK_ENTER) {
                iniciarNovoJogo();
            }
        } else {
            if (tecla == KeyEvent.VK_LEFT && raqueteX > 0) {
                raqueteX -= 10;
            } else if (tecla == KeyEvent.VK_RIGHT && raqueteX < LARGURA - RAQUETE_LARGURA) {
                raqueteX += 10;
            } else if (tecla == KeyEvent.VK_ESCAPE) {
                if (!jogoPausado) {
                    pausarJogo();
                } else {
                    continuarJogo();
                }
            } else if (tecla == KeyEvent.VK_0) {
                pararJogo();
            }
        }
    }

    private void iniciarNovoJogo() {
        jogoRodando = true;
        bolaX = LARGURA / 2.0f;
        bolaY = ALTURA / 2.0f;
        velocidadeBolaX = velocidadeInicialBolaX;
        velocidadeBolaY = velocidadeInicialBolaY;
        raqueteX = LARGURA / 2.0f - 50.0f;
        pontuacao = 0;
        vidas = VIDAS_INICIAIS;
        vidasRestantes = VIDAS_INICIAIS;
        resetarCoracoes();
        resetarBola();
        mostrarMensagemInicial = false;
        segundaFase = false;
        repaint();
    }

    private void pausarJogo() {
        jogoPausado = true;
        repaint();
    }

    private void continuarJogo() {
        jogoPausado = false;
        repaint();
    }

    private void pararJogo() {
        jogoRodando = false;
        jogoPausado = false;
        mostrarMensagemInicial = true;
        repaint();
    }

    private void atualizar() {
        bolaX += velocidadeBolaX;
        bolaY += velocidadeBolaY;

        if (bolaX < 0 || bolaX > LARGURA) {
            velocidadeBolaX *= -1;
        }

        if (bolaY < getInsets().top) {
            velocidadeBolaY *= -1;
            bolaY = getInsets().top;
        } else if (bolaY > ALTURA) {
            perderVida();
        }

        if (bolaY > RAQUETE_Y && bolaY < RAQUETE_Y + RAQUETE_ALTURA &&
                bolaX > raqueteX && bolaX < raqueteX + RAQUETE_LARGURA) {
            velocidadeBolaY *= -1;
            pontuacao += 35;

            if (pontuacao > 200 && !segundaFase) {
                iniciarSegundaFase();
            }

            if (pontuacao > 200) {
                aumentarVelocidadeBola();
            }
        }

        if (segundaFase && losango.intersects(bolaX - TAMANHO_BOLA / 2, bolaY - TAMANHO_BOLA / 2, TAMANHO_BOLA, TAMANHO_BOLA)) {
            rebaterLosango();
        }
    }

    private void iniciarSegundaFase() {
        segundaFase = true;
        velocidadeBolaX *= 3.2f;
        velocidadeBolaY *= 3.2f;
        resetarJogo();
    }

    private void aumentarVelocidadeBola() {
        velocidadeBolaX *= 1.12f;
        velocidadeBolaY *= 1.12f;
    }

    private void perderVida() {
        if (vidas > 0) {
            vidas--;
            coracoes.remove(coracoes.size() - 1);
            resetarBola();
        }

        if (vidas == 0) {
            fimDoJogo();
        }
    }

    private void resetarJogo() {
        vidasRestantes = VIDAS_INICIAIS;
        resetarCoracoes();
        resetarBola();
    }

    private void resetarBola() {
        bolaX = LARGURA / 2.0f;
        bolaY = ALTURA / 2.0f;
        velocidadeBolaX = velocidadeInicialBolaX;
        velocidadeBolaY = velocidadeInicialBolaY;
    }

    private void fimDoJogo() {
        jogoRodando = false;
        mostrarMensagemInicial = true;
        segundaFase = false;
        repaint();
    }

    private void resetarCoracoes() {
        coracoes.clear();
        for (int i = 0; i < VIDAS_INICIAIS; i++) {
            coracoes.add(new Rectangle(0, 0, TAMANHO_CORACAO, TAMANHO_CORACAO));
        }
    }

    private void desenharCoracao(Graphics2D g, int x, int y, int tamanho) {
        GeneralPath coracao = new GeneralPath();
        coracao.moveTo(x, y + tamanho / 2);
        coracao.curveTo(x, y, x - tamanho / 2, y, x - tamanho / 2, y + tamanho / 2);
        coracao.curveTo(x - tamanho / 2, y + tamanho, x, y + tamanho * 2 / 3, x, y + tamanho);
        coracao.curveTo(x, y + tamanho * 2 / 3, x + tamanho / 2, y + tamanho, x + tamanho / 2, y + tamanho / 2);
        coracao.curveTo(x + tamanho / 2, y, x, y, x, y + tamanho / 2);
        g.fill(coracao);
    }

    private void rebaterLosango() {
        velocidadeBolaX *= -1;
        velocidadeBolaY *= -1;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        Paint fundoGradiente = new GradientPaint(
                0, 0, corFundo,
                0, ALTURA, Color.DARK_GRAY);
        g2d.setPaint(fundoGradiente);
        g2d.fillRect(0, 0, LARGURA, ALTURA);

        if (jogoRodando) {
            GradientPaint bolaGradiente = new GradientPaint(
                    bolaX - TAMANHO_BOLA / 2, bolaY - TAMANHO_BOLA / 2, Color.WHITE,
                    bolaX + TAMANHO_BOLA / 2, bolaY + TAMANHO_BOLA / 2, Color.BLUE);
            g2d.setPaint(bolaGradiente);
            g2d.fillOval((int) (bolaX - TAMANHO_BOLA / 2), (int) (bolaY - TAMANHO_BOLA / 2), TAMANHO_BOLA, TAMANHO_BOLA);

            GradientPaint raqueteGradiente = new GradientPaint(
                    raqueteX, RAQUETE_Y, Color.GRAY,
                    raqueteX + RAQUETE_LARGURA, RAQUETE_Y + RAQUETE_ALTURA, Color.DARK_GRAY);
            g2d.setPaint(raqueteGradiente);
            g2d.fillRect((int) raqueteX, (int) RAQUETE_Y, (int) RAQUETE_LARGURA, (int) RAQUETE_ALTURA);

            if (segundaFase) {
                g2d.setColor(Color.RED);  // Definir cor rosa
                g2d.fillPolygon(losango);
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 22));
            String textoPontuacao = "Pontuação: " + pontuacao;
            int larguraPontuacao = g.getFontMetrics().stringWidth(textoPontuacao);
            g.drawString(textoPontuacao, LARGURA - larguraPontuacao - 10, 50);

            String textoVidas = "Vidas: " + vidasRestantes;
            int larguraTextoVidas = g.getFontMetrics().stringWidth(textoVidas);

            int coracoesX = LARGURA / 20 - larguraTextoVidas / 2;
            for (Rectangle coracao : coracoes) {
                GradientPaint coracaoGradiente = new GradientPaint(
                        coracoesX, 50, Color.RED,
                        coracoesX + TAMANHO_CORACAO, 50 + TAMANHO_CORACAO, Color.PINK);
                g2d.setPaint(coracaoGradiente);
                desenharCoracao(g2d, coracoesX, 50, TAMANHO_CORACAO);
                coracoesX += TAMANHO_CORACAO + 5;
            }

            GradientPaint baseGradiente = new GradientPaint(
                    raqueteX, RAQUETE_Y, Color.LIGHT_GRAY,
                    raqueteX + RAQUETE_LARGURA, RAQUETE_Y + RAQUETE_ALTURA, Color.DARK_GRAY);
            g2d.setPaint(baseGradiente);
            g2d.fillRect((int) raqueteX, (int) RAQUETE_Y, (int) RAQUETE_LARGURA, (int) RAQUETE_ALTURA);

            g2d.setColor(Color.BLACK);
            g2d.drawRect((int) raqueteX, (int) RAQUETE_Y, (int) RAQUETE_LARGURA, (int) RAQUETE_ALTURA);
        }

        if (jogoPausado) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            int larguraMensagemPausa = g.getFontMetrics().stringWidth(mensagemPausa);
            g.drawString(mensagemPausa, LARGURA / 2 - larguraMensagemPausa / 2, ALTURA / 2);
        }

        if (mostrarMensagemInicial && !jogoRodando) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            int larguraMensagemInicio = g.getFontMetrics().stringWidth(mensagemInicio);
            g.drawString(mensagemInicio, LARGURA / 2 - larguraMensagemInicio / 2, ALTURA / 2);
        }

        if (!mostrarMensagemInicial && vidasRestantes > 0 && !jogoRodando) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            int larguraMensagemTentarNovamente = g.getFontMetrics().stringWidth(mensagemTentarNovamente);
            g.drawString(mensagemTentarNovamente, LARGURA / 2 - larguraMensagemTentarNovamente / 2, ALTURA / 2);
        }

        if (!mostrarMensagemInicial && vidasRestantes == 0 && !jogoRodando) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            int larguraMensagemFimDeJogo = g.getFontMetrics().stringWidth(mensagemFimDeJogo);
            g.drawString(mensagemFimDeJogo, LARGURA / 2 - larguraMensagemFimDeJogo / 2, ALTURA / 2);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BounceBallGame game = new BounceBallGame();
            game.setUndecorated(true);
            game.setExtendedState(JFrame.MAXIMIZED_BOTH);
            game.setVisible(true);
        });
    }
}