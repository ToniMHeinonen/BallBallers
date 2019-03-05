package fi.tamk.fi;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MenuRoom extends ParentRoom {
    private boolean moveToPlaY;
    private boolean moveToHighscore;

    MenuRoom(MainGame game) {
        super(game);
        createPlay();
        createHighscore();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        checkPresses();
    }

    public void createPlay() {
        final TextButton playB = new TextButton("Play", skin);
        playB.setWidth(300f);
        playB.setHeight(100f);
        playB.setPosition(game.pixelWidth /2 - playB.getWidth() /2,
                (game.pixelHeight/3) *2 - playB.getHeight() /2);
        playB.getLabel().setFontScale(2f);
        stage.addActor(playB);

        playB.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                moveToPlaY = true;
            }
        });
    }

    public void createHighscore() {
        final TextButton hs = new TextButton("Highscores", skin);
        hs.setWidth(300f);
        hs.setHeight(100f);
        hs.setPosition(game.pixelWidth /2 - hs.getWidth() /2,
                game.pixelHeight/3 - hs.getHeight() /2);
        hs.getLabel().setFontScale(2);
        stage.addActor(hs);

        hs.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                moveToHighscore = true;
            }
        });
    }

    public void checkPresses() {
        if (moveToPlaY) {
            PlayRoom playRoom = new PlayRoom(game);
            game.setScreen(playRoom);
        } else if (moveToHighscore) {
            HighscoreRoom hs = new HighscoreRoom(game);
            game.setScreen(hs);
        }
    }
}
