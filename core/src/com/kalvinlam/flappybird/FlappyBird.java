package com.kalvinlam.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

import javafx.scene.media.MediaPlayer;


public class FlappyBird extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
//    ShapeRenderer shapeRenderer;

    Texture gameover;

    Texture[] birds;
    int flapState = 0;
    float birdY = 0;
    float velocity = 0;
    Circle birdCircle;
    int score = 0;
    int scoringTube = 0;
    BitmapFont font;

    int gameState = 0;
    float gravity = 2;

    Texture topTube;
    Texture bottomTube;
    float gap = 700;
    float maxTubeOffset;
    Random randomGenerator;
    float tubeVelocity = 4;
    int numberOfTubes = 4;
    float[] tubeX = new float[numberOfTubes];
    float[] tubeOffset = new float[numberOfTubes];
    float distanceBetweenTubes;
    Rectangle[] topTubeRectangles;
    Rectangle[] bottomTubeRectangles;

    MediaPlayer mp;

    @Override
    public void create () {
        batch = new SpriteBatch();
        background = new Texture("mexicansrioting1.png");
        gameover = new Texture("gameovermexico.png");
//        shapeRenderer = new ShapeRenderer();
        birdCircle = new Circle();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);

        birds = new Texture[2];
        birds[0] = new Texture("trump2.png");
        birds[1] = new Texture("trump2.png");

        topTube = new Texture("topcactus.png");
        bottomTube = new Texture("bottomcactus.png");
        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100; //the 100 comes from the thicker section of tube
        randomGenerator = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4;
        topTubeRectangles = new Rectangle[numberOfTubes];
        bottomTubeRectangles = new Rectangle[numberOfTubes];

        MediaPlayer.create(getApplicationContext(), R.raw.combo);

        startGame();

    }

    public void startGame() {
        birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2; //use birds[0] b/c both birds have same height

        for(int i = 0; i < numberOfTubes; i++) {

            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;

            topTubeRectangles[i] = new Rectangle();
            bottomTubeRectangles[i] = new Rectangle();
        }
    }

    @Override
    public void render () {

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); //drawing background

        if(gameState == 1) { //if the screen has been touched, then gameState = 1

            if(tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {

                score++;

                Gdx.app.log("Score", String.valueOf(score));

                if(scoringTube < numberOfTubes - 1) { //we do not have a 4th tube - comment from rob

                    scoringTube++;

                } else {

                    scoringTube = 0;

                }
            }

            if(Gdx.input.justTouched()) { //check if screen has been touched

                velocity = -30;

            }

            for(int i = 0; i < numberOfTubes; i++) {

                if(tubeX[i] < - topTube.getWidth()) {

                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

                } else {

                    tubeX[i] -= tubeVelocity;

                }

                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

                topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
                bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

            }
            if(birdY > 0) {
                velocity += gravity; //velocity = velocity + gravity; velocity = -18, -16, -14...0, 2
                birdY -= velocity; //birdY = birdY - velocity (the bird is falling); birdY + 18, birdY + 16...birdY - 2
            } else {
                gameState = 2;
            }
        } else if(gameState == 0) { //if gameState = 0 (AKA screen has not been touched)
            if(Gdx.input.justTouched()) { //check if screen has been touched
                gameState = 1; //screen has been touched!
            }
        } else if (gameState == 2) {
            batch.draw(gameover, Gdx.graphics.getWidth() / 2 - gameover.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameover.getHeight() / 2);

            if(Gdx.input.justTouched()) { //check if screen has been touched
                gameState = 1; //screen has been touched!
                startGame();
                score = 0;
                scoringTube = 0;
                velocity = 0;
            }
        }
        if (flapState == 0) {
            flapState = 1;
        } else {
            flapState = 0;
        }
        batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY); //draw bird in center of page

        font.draw(batch, String.valueOf(score), 100, 200);

        batch.end();

        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);

//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(Color.RED);
//        shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

        for(int i = 0; i < numberOfTubes; i++) {

//            shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
//            shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

            if(Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {

                gameState = 2;

            }
        }

//        shapeRenderer.end();

    }
}
