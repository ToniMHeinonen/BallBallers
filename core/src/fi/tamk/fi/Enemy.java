package fi.tamk.fi;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Enemy extends GameObject{

    private int movementTimer = 0;
    private int moveDir;

    Enemy(PlayRoom game, MainGame mainGame, float x, float y) {
        this.game = game;
        this.mainGame = mainGame;
        this.texture = mainGame.getEnemyTexture();
        width = game.TILE_SIZE/100f;
        height = game.TILE_SIZE/100f;
        startX = x + width/2;
        startY = y + height/2;
        weight = 0.5f;
        bounce = 1f;
        health = game.getStageNum();

        body = game.world.createBody(createDynamicBody(false));
        body.setUserData("enemy");
        body.createFixture(createCircleFixture(game.GROUP_SEMI));
        body.setUserData(texture);

        init();
    }

    public void update() {
        controlMovement();
        drawCircle();
    }

    private void controlMovement() {
        if (movementTimer > 0) {
            movementTimer--;
        } else {
            movementTimer = 180;
            int moveDir = MathUtils.random(1, 4);
            float force = 0.05f;

            switch(moveDir) {
                case 1: {
                    body.applyLinearImpulse(new Vector2(0f, force),
                            body.getWorldCenter(),
                            true);
                    break;
                }
                case 2: {
                    body.applyLinearImpulse(new Vector2(0f, -force),
                            body.getWorldCenter(),
                            true);
                    break;
                }
                case 3: {
                    body.applyLinearImpulse(new Vector2(force, 0f),
                            body.getWorldCenter(),
                            true);
                    break;
                }
                case 4: {
                    body.applyLinearImpulse(new Vector2(-force, 0f),
                            body.getWorldCenter(),
                            true);
                    break;
                }
            }
        }
    }

}
