package com.lucascabral.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

public class MyGame extends ApplicationAdapter {

    // Texturas
    private SpriteBatch batch;
    private Texture[] passaros;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoTopo;

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

    // Exibição de textos
    BitmapFont textoPontuacao;

    @Override
    public void create() {

        inicializarTexturas();
        inicializarObjetos();
    }

    @Override
    public void render() {

        verificaEstadoJogo();
        validarPontos();
        desenharTexturas();
    }

    private void verificaEstadoJogo() {

        // Aplica movimento aos canos
        posicaoXCano -= Gdx.graphics.getDeltaTime() * 150;
        if (posicaoXCano < - canoBaixo.getWidth()){
            posicaoXCano = larguraDispositivo;
            posicaoYCano = random.nextInt(1400) - 700;
            passouCano = false;
        }

        // Aplica evento de click na tela
        boolean toqueTela = Gdx.input.justTouched();
        if (toqueTela) {
            gravidade = -18;
        }

        // Aplicar gravidade
        if (posicaoInicialYPassaro > 0 || toqueTela)
            posicaoInicialYPassaro = posicaoInicialYPassaro - gravidade;

        variacao += Gdx.graphics.getDeltaTime() * 10;

        // Variação para bater asas do pássaro
        if (variacao > 3)
            variacao = 0;

        gravidade++;
    }

    private void desenharTexturas() {

        batch.begin();

        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
        batch.draw(passaros[(int) variacao], 45, posicaoInicialYPassaro);
        batch.draw(canoBaixo, posicaoXCano, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoYCano);
        batch.draw(canoTopo, posicaoXCano, alturaDispositivo / 2 + espacoEntreCanos / 2 + posicaoYCano);
        textoPontuacao.draw(batch, String.valueOf(pontuacao), larguraDispositivo/2, alturaDispositivo - 30);
        batch.end();
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
    }

    @Override
    public void dispose() {
        //Gdx.app.log("dispose", "descarte de conteúdos");
    }
}
