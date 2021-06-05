package com.icecoldbeer.game.screens;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.icecoldbeer.game.Assets.Buttons;
import com.icecoldbeer.game.Assets.Maps;
import com.icecoldbeer.game.Assets.ModelMap;

import java.util.ArrayList;
import java.util.Locale;

public class GameScreen extends ApplicationAdapter implements Screen {

    private Stage stage;
    private Texture ballImage;
    private Sprite ballSprite;

    private World world;
    private Body bodyBall;
    private Body bodyBottom;

    private float w;
    private float h;
    private long ballHeight;
    private long ballWidth;
    private ShapeRenderer ballLine;
    private ShapeRenderer holes;

    private final int PAD_SPEED = 10;
    private final float GRAVITY = -108f;

    private final Image leftPad;
    private final Image rightPad;

    private boolean isGameOver;
    private boolean isGameWin;

    private ArrayList<ModelMap> level;
    private final Game game;

    private float seconds;
    private float timer;
    BitmapFont stopWatchFont;


    public GameScreen(Game game) {

        this.game = game;
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();

        ballWidth = Math.round(w / 22.2);
        ballHeight = Math.round(h / 10.8);

        level = Maps.getMap(1);
        isGameOver = false;
        isGameWin = false;

        stopWatchFont = new BitmapFont();
        stopWatchFont.setColor(Color.BROWN);
        stopWatchFont.getData().setScale(4);

        stage = new Stage(new ScreenViewport());
        ballImage = new Texture("ball1.png");
        ballSprite = new Sprite(ballImage);
        ballSprite.setSize(ballWidth, ballHeight);
        ballLine = new ShapeRenderer();
        holes = new ShapeRenderer();

        world = new World(new Vector2(0, GRAVITY), true);
        BodyDef bodyDefBall = createBodyDef(200, 250, BodyDef.BodyType.DynamicBody);
        bodyBall = world.createBody(bodyDefBall);

        CircleShape shapeBall = new CircleShape();
        shapeBall.setRadius(ballImage.getWidth() / 5f);

        FixtureDef fixtureDefBall = new FixtureDef();
        fixtureDefBall.shape = shapeBall;
        fixtureDefBall.density = 1f;
        fixtureDefBall.friction = 0f;
        fixtureDefBall.restitution = .5f;
        bodyBall.createFixture(fixtureDefBall);
        shapeBall.dispose();


        final BodyDef bodyBottomDef = createBodyDef(0, 0, BodyDef.BodyType.KinematicBody);
        bodyBottom = createFixtureForEdges(0, bodyDefBall.position.y/2,
                 w, bodyDefBall.position.y/2, bodyBottomDef, world);

        BodyDef bodyLeftDef = createBodyDef(0, 0, BodyDef.BodyType.StaticBody);
        createFixtureForEdges(10, 0, 10, h, bodyLeftDef, world);

        BodyDef bodyRightDef = createBodyDef(0, 0, BodyDef.BodyType.StaticBody);
        createFixtureForEdges(w, 0, w, h, bodyRightDef, world);

        BodyDef bodyTopDef = createBodyDef(0, 0, BodyDef.BodyType.StaticBody);
        createFixtureForEdges(0, h, w, h, bodyTopDef, world);


        final Skin skinPad = new Skin();
        skinPad.add("default", new Label.LabelStyle(new BitmapFont(), Color.RED));
        skinPad.add("pad", new Texture("white_board.jpg"));

        leftPad = new Image(skinPad, "pad");
        leftPad.setBounds(0, 100, 50, 100);

        rightPad = new Image(skinPad, "pad");
        rightPad.setBounds(w - 50, 100, 50, 100);


        leftPad.addListener(new DragListener() {
            @Override
            synchronized public void drag(InputEvent event, float x, float y, int pointer) {

                int dist = y < 0 ? -PAD_SPEED : PAD_SPEED;

                if (leftPad.getY() + dist < h - 150) {
                    leftPad.moveBy(0, y < 0 ? dist : dist);
                    world.destroyBody(bodyBottom);
                    bodyBottom = createFixtureForEdges(0, 40 + leftPad.getY(), w,
                            40 + rightPad.getY(), bodyBottomDef, world);
                }
            }

        });
        stage.addActor(leftPad);

        rightPad.addListener(new DragListener() {
            @Override
            synchronized public void drag(InputEvent event, float x, float y, int pointer) {

                int dist = y < 0 ? -PAD_SPEED : PAD_SPEED;

                if (rightPad.getY() + dist < h - 150) {
                    rightPad.moveBy(0, y < 0 ? -PAD_SPEED : PAD_SPEED);
                    world.destroyBody(bodyBottom);
                    bodyBottom = createFixtureForEdges(0, 40 + leftPad.getY(), w,
                            40 + rightPad.getY(), bodyBottomDef, world);
                }
            }

        });
        stage.addActor(rightPad);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(215 / 255f, 215 / 255f, 100 / 255f, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if( !isGameWin && !isGameOver) {
            world.step(Gdx.graphics.getDeltaTime(), 10, 2);
            timer += Gdx.graphics.getDeltaTime();
            seconds = (timer / 60.0f) * 60.0f;
        }
        ballLine.setColor(Color.BLACK);
        ballLine.begin(ShapeRenderer.ShapeType.Filled);
        ballLine.rectLine(leftPad.getImageX(), leftPad.getY() + 40,
                rightPad.getX(), rightPad.getY() + 40, 10);
        ballLine.end();

        Circle ballCircle = new Circle();
        ballCircle.set(bodyBall.getPosition().x, bodyBall.getPosition().y, ballHeight/2f);

        Circle holeCircle = new Circle();
        for (ModelMap m : level) {

            boolean isScore = m.isScore() == 1;

            holes.setColor(isScore ?
                    Color.BLUE : Color.BLACK);
            holes.begin(ShapeRenderer.ShapeType.Filled);
            holes.circle(m.getPosX(), m.getPosY(), ballHeight/2f);
            holes.end();
            holeCircle.set(m.getPosX(), m.getPosY(), ballHeight/4f);

            if (Intersector.overlaps(ballCircle, holeCircle)) {

                if (isScore && !isGameWin) {
                    showFinalScreen(true);
                    isGameWin = true;
                } else if (!isScore && !isGameOver){
                    showFinalScreen(false);
                    isGameOver = true;
                }
            }
        }

        stage.getBatch().begin();

        ballSprite.setOriginCenter();
        ballSprite.setPosition(bodyBall.getPosition().x - ballWidth / 2f,
                bodyBall.getPosition().y - ballHeight / 2f);
        ballSprite.rotate((float) Math.toDegrees(bodyBall.getAngle()));
        ballSprite.draw(stage.getBatch());
        stopWatchFont.draw(stage.getBatch(), String.format(Locale.ENGLISH,
                                                    "%.3fs",  seconds), 150, h - 50);

        stage.getBatch().end();
        stage.act();
        stage.draw();
    }

    private BodyDef createBodyDef(int x, int y, BodyDef.BodyType bodyType) {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.set(x, y);
        return bodyDef;
    }

    private Body createFixtureForEdges(float x0, float y0, float x1, float y1,
                                       BodyDef bodyDef, World world) {

        FixtureDef fixtureDefBottom = new FixtureDef();
        EdgeShape edgeShapeBottom = new EdgeShape();
        edgeShapeBottom.set(x0, y0, x1, y1);
        fixtureDefBottom.friction = .1f;
        fixtureDefBottom.density = .1f;
        fixtureDefBottom.restitution = .5f;
        fixtureDefBottom.shape = edgeShapeBottom;
        Body bodyEdgeScreen = world.createBody(bodyDef);

        bodyEdgeScreen.createFixture(fixtureDefBottom);

        edgeShapeBottom.dispose();
        return bodyEdgeScreen;
    }

    private void showFinalScreen(boolean isVictory) {

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0, 0, 0, .6f));

        pixmap.fillRectangle(0, 0, 1, 1);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        Image semiTL = new Image(texture);
        semiTL.setSize(w, h);
        semiTL.getColor().a = .8f;
        stage.addActor(semiTL);


        Image button = isVictory ? new Image(Buttons.BUTTON_VICTORY) :
                                                new Image(Buttons.BUTTON_TRY_AGAIN);
        button.setSize(w / 5.55f, h / 5.4f);
        button.setPosition(w / 2 - button.getWidth() / 2, h / 2);
        button.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new TransitionScreen(game, new GameScreen(game)));
                return false;
            }
        });
        stage.addActor(button);


        Image buttonQuit = new Image(Buttons.BUTTON_QUIT);
        buttonQuit.setSize(w / 5.55f, h / 5.4f);
        buttonQuit.setPosition(w / 2 - buttonQuit.getWidth() / 2, h / 4);
        buttonQuit.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                dispose();
                return false;
            }
        });

        stage.addActor(buttonQuit);
    }


    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
        world.dispose();
        Gdx.app.exit();
    }

    @Override
    public void hide() {

    }
}
