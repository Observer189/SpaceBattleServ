package requests;

import ServModels.ServAsteroid;
import ServModels.ServAsteroidField;
import ServModels.ServShip;


public class HostStartInfo {
    private int enemyConID;
    private ServShip ship;
    private ServShip enemyShip;
    private ServAsteroidField field;

    public HostStartInfo() {

    }

    public HostStartInfo(int enemyConID) {
        this.enemyConID = enemyConID;
    }

    public int getEnemyConID() {
        return enemyConID;
    }

    public void setEnemyConID(int enemyConID) {
        this.enemyConID = enemyConID;
    }

    public ServAsteroidField getField() {
        return field;
    }

    public void setField(ServAsteroidField field) {
        this.field = field;
    }

    public void setShip(ServShip ship) {
        this.ship = ship;
    }

    public void setEnemyShip(ServShip enemyShip) {
        this.enemyShip = enemyShip;
    }

    public ServShip getShip() {
        return ship;
    }

    public ServShip getEnemyShip() {
        return enemyShip;
    }
}

