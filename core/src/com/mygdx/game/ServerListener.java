package com.mygdx.game;

import java.util.Date;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import model.Player;

public class ServerListener extends Listener {
	public void connected(Connection c){
		System.out.println("�� ������ ����������� "+c.getRemoteAddressTCP().getHostString());
		
		BattleInfo info=new BattleInfo();
		info.setMessage("������ �����: "+new Date().getHours()+":"+new Date().getMinutes());
		c.sendTCP(info);
	}
public void received(Connection c, Object p){
	if(p instanceof Player)
	{
		Player player=(Player)p;
		System.out.println("��� ���:"+player.getName());
		System.out.println("����:"+c.getReturnTripTime());
	}
}
	
	//������������ ����� ������ �������� ������.
	public void disconnected(Connection c){
		System.out.println("������ ������� ������!");
	}
	
}
