package com.lucascabral.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyGame extends ApplicationAdapter {

    private int movimentoX = 0;
    private int movimentoY = 0;
    private SpriteBatch batch;
    private Texture[] passaros;
    private Texture fundo;

    //Atributos de configurações
    private float larguraDispositivo;
    private float alturaDispositivo;
    private float variacao = 0;
    private float gravidade = 0;
    private float posicaoInicialYPassaro = 0;

    @Override
    public void create() {
        //Gdx.app.log("create", "jogo iniciado");
        batch = new SpriteBatch();
        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");
        fundo = new Texture("fundo.png");

        larguraDispositivo = Gdx.graphics.getWidth();
        alturaDispositivo = Gdx.graphics.getHeight();
        posicaoInicialYPassaro = alturaDispositivo / 2;
    }

    @Override
    public void render() {

        batch.begin();

        if (variacao > 3)
            variacao = 0;

        // Aplica evento de click na tela
        boolean toqueTela = Gdx.input.justTouched();
        if (toqueTela){
            gravidade = -20;
        }

        // Aplicar gravidade no pássaro
        if (posicaoInicialYPassaro > 0 || toqueTela)
            posicaoInicialYPassaro = posicaoInicialYPassaro - gravidade;

        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
        batch.draw(passaros[(int) variacao], 30, posicaoInicialYPassaro);

        variacao += Gdx.graphics.getDeltaTime() * 10;

        gravidade++;
        movimentoX++;
        movimentoY++;

        batch.end();

		/*contador++;
		Gdx.app.log("render", "jogo renderizado: " + contador); */
    }

    @Override
    public void dispose() {
        //Gdx.app.log("dispose", "descarte de conteúdos");
    }
}
