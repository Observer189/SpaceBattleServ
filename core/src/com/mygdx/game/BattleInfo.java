package com.mygdx.game;

import model.Player;

public class BattleInfo {
    private String message;
    private RequestType requestType;
    private int queueTurn;
    private String status;
    private int connectionID;
    private boolean isHost;
    private int queueSize;
    private int enemyId;
    private Player player;
    private Player enemy;
    public String getMessage() {
        return message;
    }

    RequestType getRequestType() {
        return requestType;
    }

    void setMessage(String message) {
        this.message = message;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public enum RequestType
    {
        Adding,Info
    }

    public int getQueueTurn() {
        return queueTurn;
    }

    void setQueueTurn(int queueTurn) {
        this.queueTurn = queueTurn;
    }

    public String getStatus() {
        return status;
    }

    void setStatus(String status) {
        this.status = status;
    }

    void setConnectionID(int connectionID) {
        this.connectionID = connectionID;
    }

    int getConnectionID() {
        return connectionID;
    }

    public boolean isHost() {
        return isHost;
    }

    void setHost(boolean host) {
        isHost = host;
    }

    public int getQueueSize() {
        return queueSize;
    }

    void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getEnemyId() {
        return enemyId;
    }

    void setEnemyId(int enemyId) {
        this.enemyId = enemyId;
    }

    Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getEnemy() {
        return enemy;
    }

    void setEnemy(Player enemy) {
        this.enemy = enemy;
    }
}
