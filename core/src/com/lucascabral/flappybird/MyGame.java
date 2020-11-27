package com.lucascabral.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class MyGame extends ApplicationAdapter {

    // Texturas
    private SpriteBatch batch;
    private Texture[] passaros;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoTopo;
    private Texture gameOver;

    //Formas para colisão
    private ShapeRenderer shapeRenderer;
    private Circle circlePassaro;
    private Rectangle retanguloCanoTopo;
    private Rectangle retanguloCanoBaixo;

    // Configuração dos sons
    Sound somVoando;
    Sound somColisao;
    Sound somPontuacao;

    //Atributos de configurações
    private float larguraDispositivo;
    private float alturaDispositivo;
    private float variacao = 0;
    private float gravidade = 0;
    private float posicaoInicialYPassaro = 0;
    private float posicaoXCano;
    private float posicaoYCano;
    private float espacoEntreCanos;
    private Random random;
    private int pontuacao = 0;
    private int pontuacaoRecord = 0;
    private boolean passouCano = false;
    private int statusJogo = 0;

    // Exibição de textos
    BitmapFont textoPontuacao;
    BitmapFont textoReiniciar;
    BitmapFont textoMelhorPontuacao;

    //Objeto para salvar pontuação
    Preferences preferencias;

    // Objetos para câmera
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 720;
    private final float VIRTUAL_HEIGHT = 1280;

    @Override
    public void create() {

        inicializarTexturas();
        inicializarObjetos();
    }

    @Override
    public void render() {

        // Limpar frames anteriores (recomendação da biblioteca para economizar recursos)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        verificaEstadoJogo();
        validarPontos();
        desenharObjetosTela();
        detectarColisoes();
    }

    /* Status do jogo
     * 0 - Jogo iniciado - pássaro permanece parado
     * 1 - Jogo em execução - pássaro voando
     * 2 - Jogo parado - pássaro colidiu
     * */

    private void verificaEstadoJogo() {

        boolean toqueTela = Gdx.input.justTouched();

        if (statusJogo == 0) {
            variacao = 0;
            // Aplica evento de click na tela
            if (toqueTela) {
                gravidade = -15;
                statusJogo = 1;
                somVoando.play();
            }
        } else if (statusJogo == 1) {

            // Aplica evento de click na tela
            if (toqueTela) {
                gravidade = -17;
                somVoando.play();
            }

            // Aplica movimento aos canos
            posicaoXCano -= Gdx.graphics.getDeltaTime() * 250;
            if (posicaoXCano < -canoBaixo.getWidth()) {
                posicaoXCano = larguraDispositivo;
                posicaoYCano = random.nextInt(1000) - 500;
                passouCano = false;
            }

            // Aplicar gravidade
            if (posicaoInicialYPassaro > 0 || toqueTela)
                posicaoInicialYPassaro = posicaoInicialYPassaro - gravidade;
            gravidade++;

        } else if (statusJogo == 2) {
            variacao = 0;

            if (pontuacao > pontuacaoRecord) {
                pontuacaoRecord = pontuacao;
                preferencias.putInteger("pontuacaoMaxima", pontuacaoRecord);
            }

            // Aplicar gravidade
            if (posicaoInicialYPassaro > 0 || toqueTela)
                posicaoInicialYPassaro = posicaoInicialYPassaro - gravidade;
            gravidade++;

            if (toqueTela) {
                statusJogo = 0;
                pontuacao = 0;
                gravidade = 0;
                posicaoInicialYPassaro = alturaDispositivo / 2;
                posicaoXCano = larguraDispositivo;
            }
        }
    }

    private void detectarColisoes() {


        circlePassaro.set(
                45 + passaros[0].getWidth() / 2,
                posicaoInicialYPassaro + passaros[0].getHeight() / 2,
                passaros[0].getWidth() / 2
        );

        retanguloCanoBaixo.set(
                posicaoXCano,
                alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoYCano,
                canoBaixo.getWidth(), canoBaixo.getHeight());

        retanguloCanoTopo.set(
                posicaoXCano,
                alturaDispositivo / 2 + espacoEntreCanos / 2 + posicaoYCano,
                canoTopo.getWidth(), canoTopo.getHeight()
        );

        boolean colidiuCanoCima = Intersector.overlaps(circlePassaro, retanguloCanoTopo);
        boolean colidiuCanoBaixo = Intersector.overlaps(circlePassaro, retanguloCanoBaixo);

        if (colidiuCanoBaixo || colidiuCanoCima) {

            if (statusJogo == 1) {
                somColisao.play();
                statusJogo = 2;
            }
            //Gdx.app.log("Log", "colidiu");
        }

        /* shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);

        // Circulo Pássaro
        shapeRenderer.circle(45 + passaros[0].getWidth() / 2,
                posicaoInicialYPassaro + passaros[0].getHeight()/2,
                passaros[0].getWidth() / 2);

        // Cano Topo
        shapeRenderer.rect(
                posicaoXCano,
                alturaDispositivo / 2 + espacoEntreCanos / 2 + posicaoYCano,
                canoTopo.getWidth(), canoTopo.getHeight()
                );
        // Cano Baixo
        shapeRenderer.rect(
                posicaoXCano,
                alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoYCano,
                canoBaixo.getWidth(), canoBaixo.getHeight()
        );

        shapeRenderer.end(); */
    }

    private void desenharObjetosTela() {

        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
        batch.draw(passaros[(int) variacao], 45, posicaoInicialYPassaro);
        batch.draw(canoBaixo, posicaoXCano, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoYCano);
        batch.draw(canoTopo, posicaoXCano, alturaDispositivo / 2 + espacoEntreCanos / 2 + posicaoYCano);
        textoPontuacao.draw(batch, String.valueOf(pontuacao), larguraDispositivo / 2, alturaDispositivo - 30);

        if (statusJogo == 2) {

            batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth() / 2, alturaDispositivo / 2);
            textoReiniciar.draw(batch, "Toque para reiniciar!",
                    larguraDispositivo / 2 - 140, alturaDispositivo / 2 - gameOver.getHeight() / 2);
            textoMelhorPontuacao.draw(batch, "Seu record é: " + pontuacaoRecord + " pontos",
                    larguraDispositivo / 2 - 140, alturaDispositivo / 2 - gameOver.getHeight() - 20);
        }
        batch.end();

        variacao += Gdx.graphics.getDeltaTime() * 10;
        // Variação para bater asas do pássaro
        if (variacao > 3)
            variacao = 0;
    }

    private void validarPontos() {

        if (posicaoXCano < 45 - passaros[0].getWidth()) { // Cano já passou do pássaro

            if (!passouCano) {
                pontuacao++;
                passouCano = true;
                somPontuacao.play();
            }
        }
    }

    private void inicializarTexturas() {

        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");
        fundo = new Texture("fundo.png");
        canoBaixo = new Texture("cano_baixo_maior.png");
        canoTopo = new Texture("cano_topo_maior.png");
        gameOver = new Texture("game_over.png");
    }

    private void inicializarObjetos() {

        batch = new SpriteBatch();
        random = new Random();
        larguraDispositivo = VIRTUAL_WIDTH;
        alturaDispositivo = VIRTUAL_HEIGHT;
        posicaoInicialYPassaro = alturaDispositivo / 2;
        posicaoXCano = larguraDispositivo;
        espacoEntreCanos = 280;

        // Configura texto
        textoPontuacao = new BitmapFont();
        textoPontuacao.setColor(Color.WHITE);
        textoPontuacao.getData().setScale(10);

        textoReiniciar = new BitmapFont();
        textoReiniciar.setColor(Color.GREEN);
        textoReiniciar.getData().setScale(2);

        textoMelhorPontuacao = new BitmapFont();
        textoMelhorPontuacao.setColor(Color.RED);
        textoMelhorPontuacao.getData().setScale(2);

        // Formas geométricas para colisões
        circlePassaro = new Circle();
        retanguloCanoBaixo = new Rectangle();
        retanguloCanoTopo = new Rectangle();
        shapeRenderer = new ShapeRenderer();

        // Inicializa Sons
        somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
        somColisao = Gdx.audio.newSound(Gdx.files.internal("som_colisao.wav"));
        somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));

        // Configura Preferências do objeto
        preferencias = Gdx.app.getPreferences("flappyBird");
        pontuacaoRecord = preferencias.getInteger("pontuacaoMaxima", 0);

        // Configuração da câmera
        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
    }

    @Override
    public void resize(int width, int height) {

        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        //Gdx.app.log("dispose", "descarte de conteúdos");
    }
}
