package fi.tamk.fi;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;

public class MainGame extends Game {
	private SpriteBatch batch;
	private Texture bulletTexture;
	private Texture playerUp;
	private Texture playerDown;
	private Texture playerRight;
	private Texture playerLeft;
	private Texture enemyTexture;
	private Texture hpUp;
	private Texture scoreUp;
	private Texture ammoUp;
	private Texture imgBackground;
	private Music music;
	private Sound boing;
	private Sound ouch;
	private Sound enemyHit;
	private Sound sndScore;
	private Sound sndHealth;
	private Sound sndPowerUp;

	public static final float worldWidth = 8f;
	public static final float worldHeight = 4.8f;
	public static final float pixelWidth = 800f;
	public static final float pixelHeight = 480f;

	private OrthographicCamera camera;
	private OrthographicCamera cameraText;

	private FreeTypeFontGenerator generator;
	private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
	private BitmapFont fntAgency;
	private Preferences prefs;

	@Override
	public void create () {
		playerUp = new Texture("texture/playerUp.png");
		playerDown = new Texture("texture/playerDown.png");
		playerRight = new Texture("texture/playerRight.png");
		playerLeft = new Texture("texture/playerLeft.png");
		bulletTexture = new Texture("texture/ball.png");
		enemyTexture = new Texture("texture/enemy.png");
		hpUp = new Texture("texture/hpUp.png");
		scoreUp = new Texture("texture/scoreUp.png");
		ammoUp = new Texture("texture/ammoUp.png");
		imgBackground = new Texture("texture/background.png");

		music = Gdx.audio.newMusic(Gdx.files.internal("audio/music.mp3"));
		music.setLooping(true);
		music.play();
		boing = Gdx.audio.newSound(Gdx.files.internal("audio/boing.wav"));
		ouch = Gdx.audio.newSound(Gdx.files.internal("audio/ouch.wav"));
		enemyHit = Gdx.audio.newSound(Gdx.files.internal("audio/enemyhit.wav"));
		sndHealth = Gdx.audio.newSound(Gdx.files.internal("audio/sndHealth.wav"));
		sndScore = Gdx.audio.newSound(Gdx.files.internal("audio/sndScore.wav"));
		sndPowerUp = Gdx.audio.newSound(Gdx.files.internal("audio/sndPowerUp.wav"));

		batch = new SpriteBatch();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, worldWidth, worldHeight);

		cameraText = new OrthographicCamera();
		cameraText.setToOrtho(false, 800, 480);

		createFont();
		createHighscores();

		MenuRoom room = new MenuRoom(this);
		setScreen(room);
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose () {
		batch.dispose();
		playerUp.dispose();
		playerDown.dispose();
		playerLeft.dispose();
		playerRight.dispose();
		bulletTexture.dispose();
		enemyTexture.dispose();
		hpUp.dispose();
		scoreUp.dispose();
		ammoUp.dispose();
		boing.dispose();
	}

	public void createFont() {
		generator = new FreeTypeFontGenerator(Gdx.files.internal("fntAgency.TTF"));
		parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 30;
		parameter.borderColor = Color.BLACK;
		parameter.borderWidth = 2;
		fntAgency = generator.generateFont(parameter);
	}

	public void createHighscores() {
		prefs = Gdx.app.getPreferences("FreeGamePreferences");
		if (!prefs.contains("name0")) {
			for (int i = 0; i < 5; i++) {
				prefs.putString("name" + String.valueOf(i), "Nobody");
				prefs.putInteger("score" + String.valueOf(i), 0);
			}
		}
		prefs.flush();
	}

	public Texture getBulletTexture() {
		return bulletTexture;
	}

	public Texture getPlayerUp() {
		return playerUp;
	}

	public Texture getPlayerDown() {
		return playerDown;
	}

	public Texture getPlayerRight() {
		return playerRight;
	}

	public Texture getPlayerLeft() {
		return playerLeft;
	}

	public Texture getEnemyTexture() {
		return enemyTexture;
	}

	public Texture getHpUp() {
		return hpUp;
	}

	public Texture getScoreUp() {
		return scoreUp;
	}

	public Texture getAmmoUp() {
		return ammoUp;
	}

	public Texture getImgBackground() {
		return imgBackground;
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public Sound getBoing() {
		return boing;
	}

	public Sound getOuch() {
		return ouch;
	}

	public Sound getEnemyHit() {
		return enemyHit;
	}

	public Sound getSndScore() {
		return sndScore;
	}

	public Sound getSndHealth() {
		return sndHealth;
	}

	public Sound getSndPowerUp() {
		return sndPowerUp;
	}

	public BitmapFont getFntAgency() {
		return fntAgency;
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public OrthographicCamera getCameraText() {
		return cameraText;
	}

	public Preferences getPrefs() {
		return prefs;
	}
}
