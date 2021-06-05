package com.icecoldbeer.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class TransitionScreen implements Screen {

    private Stage stage;
    private Table fadingTable;
    private Game aGame;
    private Screen nextScreen;

    public TransitionScreen(Game game , Screen nextScreen)
    {
        this.stage = new Stage();
        this.fadingTable = new Table();
        this.aGame = game;
        this.nextScreen = nextScreen;
    }

    @Override
    public void show()
    {
        fadingTable.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getWidth());
        fadingTable.setPosition(0.0f, 0.0f);
        fadingTable.setColor(Color.WHITE);
        fadingTable.getColor().a = 1.0f;

        // Configure the fade in and fade out effect
        fadingTable.addAction(Actions.sequence(Actions.delay(0.03f),
                Actions.fadeOut(0.03f), Actions.delay(0.5f),
                Actions.fadeIn(0.03f), new Action() {
            @Override
            public boolean act(float delta) {
                aGame.setScreen(nextScreen);
                return true;
            }
        }));

        stage.addActor(fadingTable);
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose()
    {
        stage.dispose();
    }
}
