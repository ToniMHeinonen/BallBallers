package fi.tamk.fi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class ParentRoom implements Screen {
    protected SpriteBatch batch;
    protected MainGame game;

    protected Stage stage;
    protected Skin skin;

    protected BitmapFont fntAgency;
    protected Preferences prefs;

    protected Texture background;

    ParentRoom(MainGame game) {
        this.batch = game.getBatch();
        this.game = game;
        this.fntAgency = game.getFntAgency();
        this.prefs = game.getPrefs();
        this.background = game.getImgBackground();

        stage = new Stage(new StretchViewport(game.pixelWidth, game.pixelHeight), batch);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin( Gdx.files.internal("uiskin.json") );
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background, 0f,0f, background.getWidth(), background.getHeight());
        batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
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
    public void dispose() {

    }
}
