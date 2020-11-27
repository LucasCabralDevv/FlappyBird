package com.lucascabral.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

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
    private boolean passouCano = false;
    private int statusJogo = 0;

    // Exibição de textos
    BitmapFont textoPontuacao;
    BitmapFont textoReiniciar;
    BitmapFont textoMelhorPontuacao;

    @Override
    public void create() {

        inicializarTexturas();
        inicializarObjetos();
    }

    @Override
    public void render() {

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

        if (statusJogo == 0){

            // Aplica evento de click na tela
            if (toqueTela) {
                gravidade = -15;
                statusJogo = 1;
            }
        }else if (statusJogo == 1){

            // Aplica evento de click na tela
            if (toqueTela) {
                gravidade = -17;
            }

            // Aplica movimento aos canos
            posicaoXCano -= Gdx.graphics.getDeltaTime() * 250;
            if (posicaoXCano < - canoBaixo.getWidth()){
                posicaoXCano = larguraDispositivo;
                posicaoYCano = random.nextInt(1400) - 700;
                passouCano = false;
            }

            // Aplicar gravidade
            if (posicaoInicialYPassaro > 0 || toqueTela)
                posicaoInicialYPassaro = posicaoInicialYPassaro - gravidade;


            gravidade++;

        }else if (statusJogo == 2){


        }
    }

    private void detectarColisoes(){


        circlePassaro.set(
                45 + passaros[0].getWidth() / 2,
                posicaoInicialYPassaro + passaros[0].getHeight()/2,
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

        if (colidiuCanoBaixo || colidiuCanoCima){

            statusJogo = 2;
            Gdx.app.log("Log", "colidiu");
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

        batch.begin();

        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
        batch.draw(passaros[(int) variacao], 45, posicaoInicialYPassaro);
        batch.draw(canoBaixo, posicaoXCano, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoYCano);
        batch.draw(canoTopo, posicaoXCano, alturaDispositivo / 2 + espacoEntreCanos / 2 + posicaoYCano);
        textoPontuacao.draw(batch, String.valueOf(pontuacao), larguraDispositivo/2, alturaDispositivo - 30);

        if (statusJogo == 2){

            batch.draw(gameOver, larguraDispositivo/2 - gameOver.getWidth()/2, alturaDispositivo/2);
            textoReiniciar.draw(batch, "Toque para reiniciar!",
                    larguraDispositivo/2 - 140, alturaDispositivo/2 - gameOver.getHeight()/2);
            textoMelhorPontuacao.draw(batch, "Seu record é: 0 pontos",
                    larguraDispositivo/2 - 140, alturaDispositivo/2 - gameOver.getHeight() - 20);
        }
        batch.end();

        variacao += Gdx.graphics.getDeltaTime() * 10;
        // Variação para bater asas do pássaro
        if (variacao > 3)
            variacao = 0;
    }

    private void validarPontos (){

        if (posicaoXCano < 45 - passaros[0].getWidth()){ // Cano já passou do pássaro

            if (!passouCano){
                pontuacao++;
                passouCano = true;
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
        larguraDispositivo = Gdx.graphics.getWidth();
        alturaDispositivo = Gdx.graphics.getHeight();
        posicaoInicialYPassaro = alturaDispositivo / 2;
        posicaoXCano = larguraDispositivo;
        espacoEntreCanos = 300;

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
    }

    @Override
    public void dispose() {
        //Gdx.app.log("dispose", "descarte de conteúdos");
    }
}
