package fi.tamk.fi;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class GameObject {
    protected Texture texture;
    protected Body body;
    protected PlayRoom game;
    protected MainGame mainGame;
    protected SpriteBatch batch;
    protected float width;
    protected float height;
    protected float startX;
    protected float startY;
    protected float weight;
    protected float bounce;
    protected int health;

    public void init() {
        batch = mainGame.getBatch();
    }

    public boolean takeHit() {
        boolean dead = false;
        health--;
        if (health <= 0) {
            dead = true;
        }

        return dead;
    }

    public BodyDef createDynamicBody(boolean notRotate) {
        // Body Definition
        BodyDef myBodyDef = new BodyDef();
        // It's a body that moves
        myBodyDef.type = BodyDef.BodyType.DynamicBody;
        // Initial position is centered up
        // This position is the CENTER of the shape!
        myBodyDef.position.set(startX, startY);
        myBodyDef.fixedRotation = notRotate;
        return myBodyDef;
    }

    public FixtureDef createPolygonFixture(short group) {
        FixtureDef fixtureDef = new FixtureDef();
        // Mass per square meter (kg^m2)
        fixtureDef.density = weight;
        // How bouncy object? Very bouncy [0,1]
        fixtureDef.restitution = bounce;
        if (group != game.GROUP_DEFAULT) {
            // Set collision group
            fixtureDef.filter.groupIndex = group;
        }
        // Create polygon shape.
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(game.TILE_SIZE/150f / 2, game.TILE_SIZE/150f / 2);
        // Add the shape to the fixture
        fixtureDef.shape = shape;
        return fixtureDef;
    }

    public FixtureDef createCircleFixture(short group) {
        FixtureDef fixtureDef = new FixtureDef();
        // Mass per square meter (kg^m2)
        fixtureDef.density = weight;
        // How bouncy object? Very bouncy [0,1]
        fixtureDef.restitution = bounce;
        if (group != game.GROUP_DEFAULT) {
            // Set collision group if not default
            fixtureDef.filter.groupIndex = group;
        }
        // Create circle shape.
        CircleShape shape = new CircleShape();
        shape.setRadius(game.TILE_SIZE/110f / 2);
        // Add the shape to the fixture
        fixtureDef.shape = shape;
        return fixtureDef;
    }

    public void drawCircle() {
        batch.draw(texture,
                body.getPosition().x - width/2,
                body.getPosition().y - height/2,
                width,
                height);

        batch.draw(texture,
                body.getPosition().x - width/2,
                body.getPosition().y - height/2,
                width/2,
                height/2,
                width,
                height,
                1.0f,
                1.0f,
                body.getTransform().getRotation() * MathUtils.radiansToDegrees,
                0,
                0,
                texture.getWidth(),
                texture.getHeight(),
                false,
                false);
    }

    public Body getBody() {
        return body;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
