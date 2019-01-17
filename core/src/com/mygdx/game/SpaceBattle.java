package com.mygdx.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import ServModels.ServAsteroid;
import ServModels.ServAsteroidField;
import ServModels.ServObject;
import ServModels.ServPlayer;
import ServModels.ServShip;
import model.Asteroid;
import model.AsteroidField;
import model.PhysicObject;
import model.PhysicShip;
import model.Player;
import model.Asteroids.Asteroid1;
import model.PhysicShips.Fury;
import model.PhysicShips.StarFighter;
import requests.ClientStartInfo;
import requests.HostStartInfo;
import requests.PlayerActions;
import requests.ServBattleInfo;
import requests.ServStartInfo;

public class SpaceBattle extends ApplicationAdapter {
	
	
	static Server server;
	Kryo kryo;
	static int udpPort = 27960, tcpPort = 27960;
	ServerListener listener;
	ArrayList<ServPlayer> players;
	
	ArrayList<BattleInfo> waitQueue;
	ArrayList<Battle> battles;
	public Battle battle;
	static int battleID=100000;
	
	@Override
	public void create() {
		//������� ������
		server=new Server(32768,32768);//� ��������� ������ ������� ��������� � �������
		listener=new ServerListener();
		players=new ArrayList<ServPlayer>();
		waitQueue=new ArrayList<BattleInfo>();
		
		World world=new World(new Vector2(0,0), false);
		//������������ �����
		kryo=server.getKryo();
		kryo.register(BattleInfo.class);
        kryo.register(BattleInfo.RequestType.class);
        kryo.register(Player.class);
        kryo.register(HostStartInfo.class);
        kryo.register(ServObject.class);
        kryo.register(ServAsteroid.class);
        kryo.register(ServAsteroidField.class);
        kryo.register(ServAsteroid[].class);
        kryo.register(ServShip.class);
        kryo.register(ClientStartInfo.class);
        kryo.register(ServPlayer.class);
        kryo.register(ServStartInfo.class);
        kryo.register(ServPlayer[].class);
        kryo.register(PlayerActions.class);
        kryo.register(ServBattleInfo.class);
		//������������ ����
		try {
			server.bind(tcpPort, udpPort);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		//��������� ������
		server.start();
		
		server.addListener(new Listener()
				{
			public void connected(Connection c){
				System.out.println("�� ������ ����������� "+c.getRemoteAddressTCP().getHostString());
				
				
			}
		    public void received(Connection c, Object p){
		    	if(p instanceof BattleInfo)
		    	{
		    		BattleInfo info =(BattleInfo)p;
		    		if(info.getRequestType().equals(BattleInfo.RequestType.Adding))
		    		{
		    			System.out.println("������ ������ ���������� � �������");
		    			info.setConnectionID(c.getID());
		    			info.setStatus("inQueue");
		    			waitQueue.add(info);
		    			System.out.println("������ �������� � �������");
		    			info.setQueueTurn(waitQueue.size());
		    			info.setQueueSize(waitQueue.size());
		    			c.sendTCP(info);
		    			System.out.println("���������� �����");
		    		}
		    		else if(info.getRequestType().equals(BattleInfo.RequestType.Info))
		    				{
		    			        System.out.println("������ ����������� ���������� �� �������");
		    			        info.setQueueSize(waitQueue.size());
		    			        c.sendTCP(info);
		    			        System.out.println("���������� ����������");
		    				}
		    					
		    	}
		    	else if(p instanceof HostStartInfo)
		    	{
		    		System.out.println("���� �������� ��������� ������");
		    		HostStartInfo hsi=(HostStartInfo)p;
		    		server.sendToTCP(hsi.getEnemyConID(), hsi);
		    	}
		    	else if(p instanceof ClientStartInfo)
		    	{
		    		System.out.println("������ �������� ��������� ������");
		    		ClientStartInfo csi=(ClientStartInfo)p;
		    		server.sendToTCP(csi.getEnemyConID(),csi);
		    	}
		    	else if(p instanceof ServPlayer)
		    	{
		    		ServPlayer player=(ServPlayer)p;
		    		Player[] players=new Player[1];
		    		players[0]=Player.fromServ(player);
		    		players[0].setCurrentShip(PhysicShip.fromServ(new StarFighter(0,0,0).toServ()));
		    		players[0].setConId(c.getID());
		    		System.out.println("Id: "+player.getCurrentShip().getId());
		    		
		    		battle=new Battle(battleID,server,players);
		    		System.out.println("� ��� �����"+player.getName());
		    		//System.out.println("Id: "+player.getConID());
		    	}
		    	else if(p instanceof PlayerActions)
		    	{
		    	   PlayerActions acts=(PlayerActions)p;	
		    	   battle.PlayerAct(acts);
		    	}
			
			
		}
			
			//������������ ����� ������ �������� ������.
			public void disconnected(Connection c){
				System.out.println("������ ������� ������!");
				for(int i=0;i<waitQueue.size();i++)
				{
					if(waitQueue.get(i).getConnectionID()==c.getID())
					{
						waitQueue.remove(i);
					}
				}
				System.out.println("������ ��� ������ �� �������");
			}
				});
		
	}

	@Override
	public void render () {
	   if(waitQueue.size()>=2)
	   {
		   waitQueue.get(0).setEnemyId(waitQueue.get(1).getConnectionID());
		   waitQueue.get(0).setEnemy(waitQueue.get(1).getPlayer());
		   waitQueue.get(0).setHost(true);
		   waitQueue.get(0).setStatus("inBattle");
		   waitQueue.get(1).setEnemyId(waitQueue.get(0).getConnectionID());
		   waitQueue.get(1).setEnemy(waitQueue.get(0).getPlayer());
		   waitQueue.get(1).setHost(false);
		   waitQueue.get(1).setStatus("inBattle");
		   server.sendToTCP(waitQueue.get(0).getConnectionID(), waitQueue.get(0));
		   server.sendToTCP(waitQueue.get(1).getConnectionID(), waitQueue.get(1));
		   System.out.println("����������� ���� ������� � ���");
		   waitQueue.remove(1);
		   waitQueue.remove(0);
		   System.out.println("������� �� �� �������");
	   }
	   if(battle!=null)
	   {
		   battle.update();
		   //System.out.println("��������� ���");
	   }
	}
	
	@Override
	public void dispose () {
		
	}
}
