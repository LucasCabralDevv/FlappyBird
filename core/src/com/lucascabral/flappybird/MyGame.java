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
    }

    @Override
    public void render() {

        batch.begin();

        if (variacao > 2)
            variacao = 0;

        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
        batch.draw(passaros[(int) variacao], 30, alturaDispositivo / 2);

        variacao += Gdx.graphics.getDeltaTime() * 10;


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
