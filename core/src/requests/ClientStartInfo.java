package requests;

import ServModels.ServShip;

public class ClientStartInfo {
    private ServShip ship;
    private int enemyConID;

    public ClientStartInfo(int enemyConID) {
        this.enemyConID = enemyConID;
    }

    public ClientStartInfo() {
    }

    public ClientStartInfo(ServShip ship) {
        this.ship = ship;
    }

    public ServShip getShip() {
        return ship;
    }

    public void setShip(ServShip ship) {
        this.ship = ship;
    }

    public int getEnemyConID() {
        return enemyConID;
    }

    public void setEnemyConID(int enemyConID) {
        this.enemyConID = enemyConID;
    }
}