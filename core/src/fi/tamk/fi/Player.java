package fi.tamk.fi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Player extends GameObject{

    private float maxSpeed = 1.5f;
    private float fricSpeed = 0.1f;

    //Animation
    private TextureRegion[][] tmp;
    private TextureRegion[] frames;
    private Animation<TextureRegion> animation;
    private TextureRegion currentFrame;
    private int frameCols;
    private int frameRows;
    private int frameSpeed;
    private float stateTime = 0.0f;
    private float delta;

    Player(PlayRoom game, MainGame mainGame) {
        this.game = game;
        this.mainGame = mainGame;
        this.texture = mainGame.getPlayerDown();
        width = game.TILE_SIZE/100f;
        height = game.TILE_SIZE/100f * 2;
        startX = mainGame.worldWidth / game.TILES_AMOUNT_WIDTH * 2 - width/2;
        startY = mainGame.worldHeight / 2;
        weight = 1f;
        bounce = 0f;

        body = game.world.createBody(createDynamicBody(true));
        body.setUserData("player");
        body.createFixture(createPolygonFixture(game.GROUP_DEFAULT));
        body.setUserData(texture);

        init();

        frameCols = 4;
        frameRows = 1;
        frameSpeed = 10;
        createAnimation(texture);
    }

    public void update() {
        //Animation
        stateTime += Gdx.graphics.getDeltaTime() / frameSpeed;
        delta = Gdx.graphics.getDeltaTime();
        if (body.getLinearVelocity().len() > 0.05) {
            currentFrame = animation.getKeyFrame(stateTime, true);
        }

        checkInput();
        controlMaxSpeed();
        addFriction();
        handleAnimation();
        draw();
    }

    public void checkInput() {
        float force = 0.2f;
        //Movement
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            body.applyLinearImpulse(new Vector2(0f, force),
                    body.getWorldCenter(),
                    true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            body.applyLinearImpulse(new Vector2(0f, -force),
                    body.getWorldCenter(),
                    true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            body.applyLinearImpulse(new Vector2(force, 0f),
                    body.getWorldCenter(),
                    true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            body.applyLinearImpulse(new Vector2(-force, 0f),
                    body.getWorldCenter(),
                    true);
        }
        //Shooting
        float shootSpd = 0.2f;
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (game.getAmmo() > 0) {
                Bullet b = new Bullet(this, game, mainGame, 0f, shootSpd);
                game.createBullet(b);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            if (game.getAmmo() > 0) {
                Bullet b = new Bullet(this, game, mainGame, 0f, -shootSpd);
                game.createBullet(b);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            if (game.getAmmo() > 0) {
                Bullet b = new Bullet(this, game, mainGame, shootSpd, 0f);
                game.createBullet(b);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            if (game.getAmmo() > 0) {
                Bullet b = new Bullet(this, game, mainGame, -shootSpd, 0f);
                game.createBullet(b);
            }
        }
    }

    public void createAnimation(Texture image) {
        stateTime = 0.0f;

        tmp = TextureRegion.split(image, image.getWidth() / frameCols,
                image.getHeight() / frameRows);
        frames = toTextureArray(tmp);
        animation = new Animation(1 / 60f, frames);
        currentFrame = animation.getKeyFrame(stateTime, true);
    }

    public TextureRegion[] toTextureArray(TextureRegion[][] tr) {
        int fc = this.frameCols;
        int fr = this.frameRows;
        TextureRegion [] frames = new TextureRegion[fc * fr];

        int index = 0;
        for (int i = 0; i < fr; i++) {
            for (int j = 0; j < fc; j++) {
                frames[index++] = tr[i][j];
            }
        }

        return frames;
    }

    public void controlMaxSpeed() {
        if(body.getLinearVelocity().x >= maxSpeed)
            body.setLinearVelocity(maxSpeed,body.getLinearVelocity().y);
        if(body.getLinearVelocity().x <= -maxSpeed)
            body.setLinearVelocity(-maxSpeed,body.getLinearVelocity().y);
        if(body.getLinearVelocity().y >= maxSpeed)
            body.setLinearVelocity(body.getLinearVelocity().x,maxSpeed);
        if(body.getLinearVelocity().y <= -maxSpeed)
            body.setLinearVelocity(body.getLinearVelocity().x,-maxSpeed);
    }

    public void addFriction() {
        if(body.getLinearVelocity().x > 0)
            body.setLinearVelocity(body.getLinearVelocity().x - fricSpeed,body.getLinearVelocity().y);
        if(body.getLinearVelocity().x < -0)
            body.setLinearVelocity(body.getLinearVelocity().x + fricSpeed,body.getLinearVelocity().y);
        if(body.getLinearVelocity().y > 0)
            body.setLinearVelocity(body.getLinearVelocity().x,body.getLinearVelocity().y - fricSpeed);
        if(body.getLinearVelocity().y < -0)
            body.setLinearVelocity(body.getLinearVelocity().x,body.getLinearVelocity().y + fricSpeed);

        //if under fricSpeed then make speed 0f
        if(body.getLinearVelocity().x < fricSpeed*2 && body.getLinearVelocity().x > -fricSpeed*2) {
            body.setLinearVelocity(0f,body.getLinearVelocity().y);
        }
        if(body.getLinearVelocity().y < fricSpeed*2 && body.getLinearVelocity().y > -fricSpeed*2) {
            body.setLinearVelocity(body.getLinearVelocity().x,0f);
        }
    }

    public void draw() {
        batch.draw(currentFrame,
                body.getPosition().x - width/2,
                body.getPosition().y - height/5,
                width,
                height);
    }

    public void handleAnimation() {
        if (body.getLinearVelocity().y > 0) {
            if (this.texture != mainGame.getPlayerUp()) {
                this.texture = mainGame.getPlayerUp();
                createAnimation(texture);
            }
        } else if (body.getLinearVelocity().y < 0) {
            if (this.texture != mainGame.getPlayerDown()) {
                this.texture = mainGame.getPlayerDown();
                createAnimation(texture);
            }
        } else if (body.getLinearVelocity().x > 0) {
            if (this.texture != mainGame.getPlayerRight()) {
                this.texture = mainGame.getPlayerRight();
                createAnimation(texture);
            }
        } else if (body.getLinearVelocity().x < 0) {
            if (this.texture != mainGame.getPlayerLeft()) {
                this.texture = mainGame.getPlayerLeft();
                createAnimation(texture);
            }
        }
    }
}
