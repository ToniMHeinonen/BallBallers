package fi.tamk.fi;

import com.badlogic.gdx.math.Vector2;

public class Bullet extends GameObject{
    private int aliveTimer = 560;

    Bullet(Player p, PlayRoom game, MainGame mainGame, float dirX, float dirY) {
        this.game = game;
        this.mainGame = mainGame;
        this.texture = mainGame.getBulletTexture();

        width = game.TILE_SIZE/100f;
        height = game.TILE_SIZE/100f;
        startX = p.body.getPosition().x + dirX;
        startY = p.body.getPosition().y + dirY;
        weight = 0.5f;
        bounce = 1f;
        health = 3;

        body = game.world.createBody(createDynamicBody(false));
        body.setUserData("bullet");
        body.createFixture(createCircleFixture(game.GROUP_DEFAULT));
        body.setUserData(texture);

        init();

        body.applyLinearImpulse(new Vector2(dirX, dirY),
                body.getWorldCenter(),
                true);
    }

    public void update() {
        //Delete if no time left
        aliveTimer--;
        if (aliveTimer <= 0) {
            game.removeBullet(this);
        }

        drawCircle();
    }
}
