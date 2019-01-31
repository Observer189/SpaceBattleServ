package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import ServModels.ServPlayer;
import model.AsteroidField;
import model.Map;
import model.PhysicShip;
import model.Player;
import model.PhysicShips.Fury;
import model.PhysicShips.StarFighter;
import requests.ClientStartInfo;
import requests.PlayerActions;
import requests.ServBattleInfo;
import requests.ServStartInfo;
import utils.BattleContactListener;

public class Battle {
    private int id;
    private Server server;
    private Array<Player> players;
    private Player enemy;
    private long[] lastActTime;//массив времени последних запросов игроков. Соответствует массиву игроков

    private AsteroidField asteroidField;

    private World world;
    private BattleContactListener battleContactListener;
    private float endMapCoef;

    private boolean isBattleStarted;

    private long lrt;
    private ServBattleInfo sbi;

    Battle(int id, Server server, Player[] players) {
        this.id = id;
        this.server = server;
        lastActTime = new long[players.length];
        endMapCoef = 0.05f;
        lrt = 0;
        sbi = new ServBattleInfo();
        world = new World(new Vector2(0, 0), false);
        battleContactListener = new BattleContactListener();
        world.setContactListener(battleContactListener);
        server.addListener(new Listener() {
            public void received(Connection c, Object p) {

            }

        });

        isBattleStarted = false;
        this.players = new Array<Player>();

        //players[0].setCurrentShip(new Fury (50,30,180));
        //players[0].getCurrentShip().setX(11);
        //players[0].getCurrentShip().setY(7);
        //players[0].getCurrentShip().setAngle((float)Math.toRadians(180));
        players[0].getCurrentShip().setTransform(20, 20, 200);
        players[0].getCurrentShip().setTargetRotation(200);

        enemy = new Player();                 //Искусственно создаем противника с именем enemy
        enemy.setName("enemy");// и передаем игроку
        //PhysicShip tempShip=new Fury(55,30,0);
        //tempShip.create(world);
        enemy.setCurrentShip(new StarFighter(60, 30, 180));

        //

        for (int i = 0; i < players.length; i++) {
            players[i].getCurrentShip().setId(i + 1);                  //присваиваем идентификатор каждому кораблю
            System.out.println(players[i].getCurrentShip().getId());
        }

        for (Player player : players) {
            this.players.add(player);
        }
        this.players.add(enemy);
        asteroidField = new AsteroidField(15, 30, 190, 114);
        asteroidField.generate();
        ServStartInfo ssi = new ServStartInfo();
        ssi.setBattleID(id);
        ServPlayer[] servPlayers = new ServPlayer[this.players.size];
        for (int i = 0; i < servPlayers.length; i++) {
            servPlayers[i] = this.players.get(i).toServ();
        }
        ssi.setPlayers(servPlayers);
        ssi.setAsteroidField(asteroidField.toServ());
        this.server.sendToTCP(players[0].getConId(), ssi);
        players[0] = Player.fromServ(players[0].toServ());
    }

    void update() {
        if (isBattleStarted) {
            players.get(0).getCurrentShip().move();
            world.step(1 / 60f, 4, 4);
            //for(Player p:players)
            //{

            //players.get(0).getCurrentShip().move();
            //}

            asteroidField.update();
            if (lrt + 5 < System.currentTimeMillis()) {
                System.out.println("x: " + players.get(0).getCurrentShip().getBody().getPosition().x
                        + " y: " + players.get(0).getCurrentShip().getBody().getPosition().y
                        + " r: " + Math.toDegrees(players.get(0).getCurrentShip().getBody().getAngle()));
                System.out.println(players.get(0).getCurrentShip().getRotationDirection() + " " + players.get(0).getCurrentShip().getMovementPosition());
                lrt = System.currentTimeMillis();
                sbi.setX(players.get(0).getCurrentShip().getX());
                sbi.setY(players.get(0).getCurrentShip().getY());
                sbi.setRotation(players.get(0).getCurrentShip().getRotation());
                sbi.setSaf(asteroidField.toServ());
                sbi.setTime(System.currentTimeMillis());
                server.sendToTCP(players.get(0).getConId(), sbi);
            }

        } else {
            for (Player i : players) {
                i.getCurrentShip().create(world);

            }

            asteroidField.create(world);
            createWall();
            isBattleStarted = true;
        }

    }

    private void createWall() {
        BodyDef bDef = new BodyDef();
        bDef.type = BodyDef.BodyType.StaticBody;
        bDef.position.set(0, 0);
        Body body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        ChainShape shape = new ChainShape();
        shape.createLoop(new float[]{200 * endMapCoef, 120 * endMapCoef,
                200 * (1 - endMapCoef), 120 * endMapCoef,
                200 * (1 - endMapCoef), 120 * (1 - endMapCoef),
                200 * endMapCoef, 120 * (1 - endMapCoef)});
        fDef.shape = shape;
        fDef.friction = 0.2f;
        fDef.restitution = 0.5f;
        body.createFixture(fDef);
        body.setUserData(new Object() {
            @Override
            public String toString() {
                return "EndMap";
            }
        });
    }

    public void connect(ServPlayer player) {
        players.add(Player.fromServ(player));
    }

    void PlayerAct(PlayerActions acts) {
        if (lastActTime[acts.getShipID() - 1] < acts.getTime())//проверяем является ли запрос новее предыдущего
        {
            players.get(acts.getShipID() - 1).getCurrentShip().setMovementPosition(acts.getMovementPosition());
            players.get(acts.getShipID() - 1).getCurrentShip().setTargetRotation(acts.getTargetRotation());
            lastActTime[acts.getShipID() - 1] = acts.getTime();
        }
        //System.out.println("mov: "+acts.getMovementPosition());
        //System.out.println("rot: "+acts.getRotationDirection());
    }
}
