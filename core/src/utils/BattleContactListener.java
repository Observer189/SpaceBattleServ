package utils;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import model.Asteroid;
import model.PhysicAmmo;
import model.PhysicShip;

public class BattleContactListener implements ContactListener {
    Object a;
    Object b;
    PhysicShip ship1;
    PhysicShip ship2;
    PhysicAmmo ammo1;
    PhysicAmmo ammo2;
    Asteroid asteroid1;
    Asteroid asteroid2;
    @Override
    public void beginContact(Contact contact) {


    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        if((contact.getFixtureA().getBody().getUserData()!=null)&&(contact.getFixtureB().getBody().getUserData()!=null)) {
            a = contact.getFixtureA().getBody().getUserData();
            b=contact.getFixtureB().getBody().getUserData();
            if (a.toString() != null){
                if(a.toString().equals("Ship")) {
                ship1 = (PhysicShip) a;
                if(b.toString().equals("Ammo"))
                {
                    ammo2=(PhysicAmmo)b;
                    ammo2.setMustDestroyed(true);
                    ship1.hurt(ammo2);
                }
            }
                else if (a.toString().equals("Ammo"))
                {
                    ammo1=(PhysicAmmo)a;
                    if(b.toString().equals("Asteroid"))
                    {
                        asteroid2=(Asteroid)b;
                        ammo1.setMustDestroyed(true);
                        asteroid2.hurt(ammo1);
                    }
                    else if(b.toString().equals("EndMap"))
                    {
                        ammo1.setMustDestroyed(true);
                    }
                    else if(b.toString().equals("Ship"))
                    {
                        ship2=(PhysicShip)b;
                        ship2.hurt(ammo1);
                    }
                }
                else if(a.toString().equals("Asteroid"))
                {
                    asteroid1=(Asteroid)a;
                    if(b.toString().equals("Ammo"))
                    {
                        ammo2=(PhysicAmmo)b;
                        ammo2.setMustDestroyed(true);
                        asteroid1.hurt(ammo2);
                    }
                }
                else if (a.toString().equals("EndMap"))
                {
                    if(b.toString().equals("Ammo"))
                    {
                        ammo2=(PhysicAmmo)b;
                        ammo2.setMustDestroyed(true);
                    }
                }

            }
            //System.out.println(a.toString()+" "+b.toString());
        }

    }
}