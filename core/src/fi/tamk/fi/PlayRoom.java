package fi.tamk.fi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class PlayRoom extends ParentRoom {
    private Player p;
    private Chest c;
    private Bullet b;
    private Enemy e;
    private int changeMapTimer = -1;
    private int prevRoom = -1;
    private int score;
    private int stageNum;
    private int playerHealth;
    private int maxHealth;
    private int ammo;
    private int maxAmmo;
    private Box2DDebugRenderer debugRenderer;
    private Array<Body> bodies;
    private Array<Body> bodiesToBeDestroyed;
    public static Array<Bullet> bullets;
    public static Array<Enemy> enemies;
    public static Array<Chest> chests;

    private double accumulator = 0;
    private float TIME_STEP = 1 / 60f;
    public static World world;

    private OrthographicCamera camera;
    private OrthographicCamera cameraText;

    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    int TILE_SIZE = 32;
    int TILES_AMOUNT_WIDTH  = 25;
    int TILES_AMOUNT_HEIGHT = 15;

    final short GROUP_DEFAULT = 0;
    final short GROUP_SEMI = -1;

    int buttonCounter;
    float[][] buttons;
    boolean movePlayer;
    float moveX;
    float moveY;
    float shootX;
    float shootY;

    PlayRoom(MainGame game) {
        super(game);

        world = new World(new Vector2(0, 0f), true);
        bodies = new Array<Body>();
        bodiesToBeDestroyed = new Array<Body>();
        score = 0;
        stageNum = 1;
        maxHealth = 3;
        playerHealth = maxHealth;
        maxAmmo = 2;
        createButtons();
        debugRenderer = new Box2DDebugRenderer();

        this.camera = game.getCamera();
        this.cameraText = game.getCameraText();

        create();
    }

    public void create () {

        createMap();
        bullets = new Array<Bullet>();
        enemies = new Array<Enemy>();
        chests = new Array<Chest>();

        transformObjectsToBodies("wall", GROUP_DEFAULT);
        transformObjectsToBodies("semi", GROUP_SEMI);
        transformObjectsToBodies("boundary", GROUP_DEFAULT);
        spawnEnemies();
        createCollisionChecking();
        p = new Player(this, game);
        ammo = maxAmmo;

        checkToSpawnChest();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        doPhysicsStep(Gdx.graphics.getDeltaTime());
        checkBodiesToRemove();

        if (changeMapTimer > 0) {
            changeMapTimer--;
            drawLoading();
        } else if (changeMapTimer == 0) {
            changeMapTimer = -1;
            create();
        } else {
            tiledMapRenderer.setView(camera);
            batch.setProjectionMatrix(camera.combined);

            world.getBodies(bodies);

            tiledMapRenderer.render();

            batch.begin();
            //Update bullets actions
            for (Bullet b : bullets) {
                b.update();
            }
            //Update enemies actions
            for (Enemy e : enemies) {
                e.update();
            }
            //Update chests actions
            for (Chest c : chests) {
                c.update();
            }
            //Update player's actions
            if (movePlayer) movePlayer();
            p.update();
            batch.end();

            //debugRenderer.render(world, camera.combined);

            drawTexts();

            stage.act(Gdx.graphics.getDeltaTime());
            stage.draw();

            checkRoomSwap();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        tiledMap.dispose();
    }

    private void doPhysicsStep(float deltaTime) {
        float frameTime = deltaTime;
        // If it took ages (over 4 fps, then use 4 fps)
        // Avoid of "spiral of death"
        if(deltaTime > 1 / 4f) {
            frameTime = 1 / 4f;
        }
        accumulator += frameTime;
        while (accumulator >= TIME_STEP) {
            // It's fixed time step!
            world.step(TIME_STEP, 8, 3);
            accumulator -= TIME_STEP;
        }
    }

    /*
    COLLISIONS
     */
    public void createCollisionChecking() {
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Body body1 = contact.getFixtureA().getBody();
                Body body2 = contact.getFixtureB().getBody();

                collisionBulletEnemy(body1, body2);
                collisionBulletPlayer(body1, body2);
                collisionPlayerEnemy(body1, body2);
                collisionPlayerChest(body1, body2);
            }
            @Override
            public void endContact(Contact contact) {
            }
            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }
            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });
    }
    private void collisionBulletPlayer(Body b1, Body b2) {
        for (Bullet b : bullets) {
            if (b1 == b.getBody() || b2 == b.getBody()) {
                if (b1 == p.getBody() || b2 == p.getBody()) {
                    game.getBoing().play();
                }
            }
        }
    }
    private void collisionPlayerEnemy(Body b1, Body b2) {
        for (Enemy e : enemies) {
            if (b1 == e.getBody() || b2 == e.getBody()) {
                if (b1 == p.getBody() || b2 == p.getBody()) {
                    playerHealth--;
                    game.getOuch().play();

                    if (playerHealth <= 0) {
                        HighscoreRoom hs = new HighscoreRoom(game, score);
                        game.setScreen(hs);
                    }
                }
            }
        }
    }
    private void collisionPlayerChest(Body b1, Body b2) {
        for (Chest c : chests) {
            if (b1 == c.getBody() || b2 == c.getBody()) {
                if (b1 == p.getBody() || b2 == p.getBody()) {
                    int type = c.getType();

                    switch (type) {
                        case 1: {
                            maxHealth++;
                            playerHealth = maxHealth;
                            game.getSndHealth().play();
                            break;
                        }
                        case 2: {
                            score += 10;
                            game.getSndScore().play();
                            break;
                        }
                        case 3: {
                            maxAmmo++;
                            ammo++;
                            game.getSndPowerUp().play();
                            break;
                        }
                    }
                    chests.removeValue(c, true);
                    bodiesToBeDestroyed.add(c.getBody());
                }
            }
        }
    }
    private void collisionBulletEnemy(Body b1, Body b2) {
        for (Bullet b : bullets) {
            if (b1 == b.getBody() || b2 == b.getBody()) {
                for (int i = 0; i < enemies.size; i++) {
                    e = enemies.get(i);
                    if (b1 == e.getBody() || b2 == e.getBody()) {
                        score++;
                        game.getEnemyHit().play();
                        boolean dead = e.takeHit();
                        if (dead) {
                            enemies.removeIndex(i);
                            bodiesToBeDestroyed.add(e.getBody());
                            i--;
                        }
                    }
                }
            }
        }
    }

    private void checkBodiesToRemove() {
        // Destroy needed bodies
        for (int i = 0; i < bodiesToBeDestroyed.size; i++) {
            Body body = bodiesToBeDestroyed.get(i);
            world.destroyBody(body);
            bodiesToBeDestroyed.removeIndex(i);
            i--;
        }
    }

    private void spawnEnemies() {
        // Let's get the collectable rectangles layer
        MapLayer collisionObjectLayer = (MapLayer)tiledMap.getLayers().get("enemy");
        // All the rectangles of the layer
        MapObjects mapObjects = collisionObjectLayer.getObjects();
        // Cast it to RectangleObjects array
        Array<RectangleMapObject> rectangleObjects = mapObjects.getByType(RectangleMapObject.class);
        // Iterate all the rectangles
        for (RectangleMapObject rectangleObject : rectangleObjects) {
            Rectangle tmp = rectangleObject.getRectangle();

            // SCALE given rectangle down if using world dimensions!
            Rectangle rectangle = scaleRect(tmp, 1 / 100f);

            enemies.add(new Enemy(this, game, rectangle.x, rectangle.y));
        }
    }

    private Rectangle scaleRect(Rectangle r, float scale) {
        Rectangle rectangle = new Rectangle();
        rectangle.x      = r.x * scale;
        rectangle.y      = r.y * scale;
        rectangle.width  = r.width * scale;
        rectangle.height = r.height * scale;
        return rectangle;
    }

    private void checkToSpawnChest() {
        boolean spawn = MathUtils.randomBoolean();

        if (spawn) {
            int type = MathUtils.random(1, 3);
            spawnChest(type);
        }
    }

    private void spawnChest(int type) {
        // Let's get the collectable rectangles layer
        MapLayer collisionObjectLayer = (MapLayer)tiledMap.getLayers().get("chest");
        // All the rectangles of the layer
        MapObjects mapObjects = collisionObjectLayer.getObjects();
        // Cast it to RectangleObjects array
        Array<RectangleMapObject> rectangleObjects = mapObjects.getByType(RectangleMapObject.class);
        // Iterate all the rectangles
        for (RectangleMapObject rectangleObject : rectangleObjects) {
            Rectangle tmp = rectangleObject.getRectangle();

            // SCALE given rectangle down if using world dimensions!
            Rectangle rectangle = scaleRect(tmp, 1 / 100f);

            chests.add(new Chest(this, game, rectangle.x, rectangle.y, type));
        }
    }

    private void transformObjectsToBodies(String layer, short group) {
        // Let's get the collectable rectangles layer
        MapLayer collisionObjectLayer = tiledMap.getLayers().get(layer);

        // All the rectangles of the layer
        MapObjects mapObjects = collisionObjectLayer.getObjects();

        // Cast it to RectangleObjects array
        Array<RectangleMapObject> rectangleObjects = mapObjects.getByType(RectangleMapObject.class);

        // Iterate all the rectangles
        for (RectangleMapObject rectangleObject : rectangleObjects) {
            Rectangle tmp = rectangleObject.getRectangle();

            // SCALE given rectangle down if using world dimensions!
            Rectangle rectangle = scaleRect(tmp, 1 / 100f);

            createStaticBody(rectangle, layer, group);
        }
    }

    public void createStaticBody(Rectangle rect, String type, short group) {
        BodyDef myBodyDef = new BodyDef();
        myBodyDef.type = BodyDef.BodyType.StaticBody;

        float x = rect.getX();
        float y = rect.getY();
        float width = rect.getWidth();
        float height = rect.getHeight();

        float centerX = width/2 + x;
        float centerY = height/2 + y;

        myBodyDef.position.set(centerX, centerY);

        Body wall = world.createBody(myBodyDef);

        wall.setUserData(type);

        // Create shape
        PolygonShape groundBox = new PolygonShape();

        // Real width and height is 2 X this!
        groundBox.setAsBox(width / 2 , height / 2 );

        wall.createFixture(createPolygonFixture(group, groundBox));
    }

    public FixtureDef createPolygonFixture(short group, PolygonShape shape) {
        FixtureDef fixtureDef = new FixtureDef();
        // Mass per square meter (kg^m2)
        fixtureDef.density = 0f;
        if (group != GROUP_DEFAULT) {
            // Set collision group if not default
            fixtureDef.filter.groupIndex = group;
        }
        // Add the shape to the fixture
        fixtureDef.shape = shape;
        return fixtureDef;
    }

    public void checkRoomSwap() {
        if (enemies.isEmpty()) {
            stageNum++;
            for (Body body : bodies) {
                bodiesToBeDestroyed.add(body);
            }
            dispose();
            changeMapTimer = 60;
        }
    }

    public void touchCreateBullet() {
        if (ammo > 0) {
            Bullet b = new Bullet(p, this, game, shootX, shootY);
            createBullet(b);
        }
    }

    public void createBullet(Bullet b) {
        bullets.add(b);
        ammo--;
    }

    public void removeBullet(Bullet b) {
        bullets.removeValue(b, true);
        ammo++;
        bodiesToBeDestroyed.add(b.getBody());
    }

    public void drawTexts() {
        batch.setProjectionMatrix(cameraText.combined);
        batch.begin();
        fntAgency.draw(batch, "HP " + String.valueOf(playerHealth),
                TILE_SIZE, TILE_SIZE*TILES_AMOUNT_HEIGHT-4);
        fntAgency.draw(batch, "Ammo " + String.valueOf(ammo),
                TILE_SIZE*3, TILE_SIZE*TILES_AMOUNT_HEIGHT-4);
        fntAgency.draw(batch, "Stage " + String.valueOf(stageNum),
                TILE_SIZE*18, TILE_SIZE*TILES_AMOUNT_HEIGHT-4);
        fntAgency.draw(batch, "Score " + String.valueOf(score),
                TILE_SIZE*21, TILE_SIZE*TILES_AMOUNT_HEIGHT-4);
        batch.end();
    }

    public void drawLoading() {
        batch.setProjectionMatrix(cameraText.combined);
        batch.begin();
        fntAgency.draw(batch, "Loading...",
                game.pixelWidth/2 - 50f, game.pixelHeight/2);
        batch.end();
    }

    public void createMap() {
        int randomRoom;
        randomRoom = MathUtils.random(0,14);
        while (randomRoom == prevRoom) {
            randomRoom = MathUtils.random(0,14);
        }
        tiledMap = new TmxMapLoader().load("map/map" + String.valueOf(randomRoom) + ".tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1/ 100f);
        prevRoom = randomRoom;
    }

    public void createButtons() {
        float force = 0.2f;
        float buttonSize = 60f;
        float xOffSet = game.pixelWidth - buttonSize;
        // 0 = x, 1 = y, 2 = forceX, 3 = forceY
        buttons = new float[][]{
                {buttonSize, buttonSize*2, 0f, force},
                {buttonSize, 0f, 0f, -force},
                {buttonSize*2, buttonSize, force, 0f},
                {0f, buttonSize, -force, 0f},
                {xOffSet - buttonSize, buttonSize*2, 0f, force},
                {xOffSet - buttonSize, 0f, 0f, -force},
                {xOffSet - buttonSize*2, buttonSize, -force, 0f},
                {xOffSet, buttonSize, force, 0f}
        };
        //i <= 4 movement buttons, else shooting buttons
        for (int i = 0; i < 8; i++) {
            buttonCounter = i;
            final TextButton playB = new TextButton("", skin);
            playB.setWidth(buttonSize);
            playB.setHeight(buttonSize);
            playB.setPosition(buttons[i][0], buttons[i][1]);
            stage.addActor(playB);

            if (i < 4) {
                playB.addListener(new ClickListener() {
                    int i = buttonCounter;

                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        movePlayer = true;
                        moveX = buttons[i][2];
                        moveY = buttons[i][3];
                        return true;
                    }

                    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                        movePlayer = false;
                    }
                });
            } else {
                playB.addListener(new ClickListener() {
                    int i = buttonCounter;

                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        shootX = buttons[i][2];
                        shootY = buttons[i][3];
                        touchCreateBullet();
                    }
                });
            }
        }
    }

    public void movePlayer() {
        p.getBody().applyLinearImpulse(new Vector2(moveX, moveY),
                p.getBody().getWorldCenter(),
                true);
    }

    public int getStageNum() {
        return stageNum;
    }

    public int getAmmo() {
        return ammo;
    }
}
