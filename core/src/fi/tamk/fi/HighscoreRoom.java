package fi.tamk.fi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;

public class HighscoreRoom extends ParentRoom {

    private String[][] scores = new String[5][2];
    private float space = 50f;
    private boolean moveToMenu;

    private String name;
    private int score;
    private boolean ready;

    HighscoreRoom(MainGame game) {
        super(game);
        init();
    }

    HighscoreRoom(MainGame game, int score) {
        super(game);
        this.score = score;
        if (!checkIfScoreHigherThanLowest()) {
            init();
        }
    }

    private void init() {
        getScores();
        createBack();
        ready = true;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (ready) {
            checkPresses();

            batch.setProjectionMatrix(game.getCameraText().combined);
            batch.begin();
            drawScores();
            batch.end();
        }
    }

    public void getScores() {
        for (int i = 0; i < 5; i++) {
            scores[i][0] = prefs.getString("name" + String.valueOf(i));
            scores[i][1] = String.valueOf(prefs.getInteger("score" + String.valueOf(i)));
        }
    }

    public void createBack() {
        final TextButton backB = new TextButton("Back", skin);
        backB.setWidth(200f);
        backB.setHeight(50f);
        backB.setPosition(game.pixelWidth /2 - backB.getWidth() /2,
                game.pixelHeight/4 - backB.getHeight() /2);
        backB.getLabel().setFontScale(2);
        stage.addActor(backB);

        backB.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                moveToMenu = true;
            }
        });
    }

    public void checkPresses() {
        if (moveToMenu) {
            MenuRoom menuRoom = new MenuRoom(game);
            game.setScreen(menuRoom);
        }
    }

    public void drawScores() {
        for (int i = 0; i < 5; i++) {
            fntAgency.draw(batch, scores[i][0], game.pixelWidth / 4,
                    (game.pixelHeight - 50) - (space * i));
            fntAgency.draw(batch, scores[i][1], (game.pixelWidth / 4) * 3,
                    (game.pixelHeight - 50) - (space * i));
        }
    }

    private boolean checkIfScoreHigherThanLowest() {
        boolean higher = false;
        int lowestScore = prefs.getInteger("score4");

        if (score > lowestScore) {
            higher = true;
            askForName();
        }

        return higher;
    }

    public void addNewHighscore() {

        for (int i = 0; i < 5; i++) {
            String ind = String.valueOf(i);
            int highscore = prefs.getInteger("score" + ind);

            if (score > highscore) {
                /*If current points are higher than the spot on the list, create an arraylist
                  and store all current highscore to that.*/
                ArrayList<String> names = new ArrayList<String>();
                ArrayList<Integer> scores = new ArrayList<Integer>();
                for (int c = 0; c < 5; c++) {
                    names.add(c, prefs.getString("name" + String.valueOf(c)));
                    scores.add(c, prefs.getInteger("score" + String.valueOf(c)));
                }

                //Add current score and name to the index spot.
                names.add(i, name);
                scores.add(i, score);

                //Clear previous highscores
                prefs.clear();

                //Add current score and name to prefs, including old highscores minus lowest.
                for (int c = 0; c < 5; c++) {
                    prefs.putString("name" + String.valueOf(c), names.get(c));
                    prefs.putInteger("score" + String.valueOf(c), scores.get(c));
                }

                prefs.flush();

                break;
            }
        }

        init();
    }

    public class MyTextInputListener implements Input.TextInputListener {
        @Override
        public void input (String text) {
            boolean legal = setName(text);
            if (!legal) {
                askForName();
            }
        }

        @Override
        public void canceled () {
            askForName();
        }
    }

    public void askForName() {
        MyTextInputListener listener = new MyTextInputListener();
        Gdx.input.getTextInput(listener, "Enter name", "", "Max 10 characters");
    }

    public boolean setName(String n) {
        boolean legal = true;

        if (n.length() <= 10 && !n.equals("")) {
            this.name = n;
            addNewHighscore();
        } else {
            legal = false;
        }

        return legal;
    }
}
