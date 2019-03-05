package fi.tamk.fi;

public class Chest extends GameObject {
    int type;

    Chest(PlayRoom game, MainGame mainGame, float x, float y, int type) {
        this.game = game;
        this.mainGame = mainGame;
        this.type = type;
        getCorrectTexture();
        width = game.TILE_SIZE/100f;
        height = game.TILE_SIZE/100f;
        startX = x + width/2;
        startY = y + height/2;
        weight = 0.5f;
        bounce = 1f;

        body = game.world.createBody(createDynamicBody(false));
        body.createFixture(createCircleFixture(game.GROUP_SEMI));
        body.setUserData(texture);

        init();
    }

    public void getCorrectTexture() {
        switch (type) {
            case 1: {
                this.texture = mainGame.getHpUp();
                break;
            }
            case 2: {
                this.texture = mainGame.getScoreUp();
                break;
            }
            case 3: {
                this.texture = mainGame.getAmmoUp();
                break;
            }
        }
    }

    public void update() {
        draw();
    }

    public void draw() {
        batch.draw(texture,
                body.getPosition().x - width/2,
                body.getPosition().y - height/2,
                width,
                height);
    }

    public int getType() {
        return type;
    }
}
