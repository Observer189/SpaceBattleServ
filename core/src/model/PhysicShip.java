package model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import ServModels.ServShip;
import model.PhysicShips.Fury;
import model.PhysicShips.StarFighter;

/**
 * Created by Sash on 15.06.2018.
 */

public class PhysicShip extends PhysicObject {
    private int movementPosition;
    private float rotationSpeed;
    private float targetRotation;
    private float linearDamping;//�� ���� 0.01
    private int rotationDirection;//-1-����� 1-������ 0-��� ��������
    //private float enginePower;
    private WeaponPoint[] weapons;
    private EnginePoint[] engines;
    private EnergyPoint[] energyPoints;
    private float maxSpeed;
    private float speed;
    private int weaponNumbers;
    private int engineNumbers;
    Vector2 movementVector;
    Vector2 tempVelocity;

    private float maxHP;
    private float hp;

    private float energy;
    private float maxEnergy;
    private long energyConsLastTime;
    private int consumptionReload;
    private boolean isShooting;

    private int id;

    public PhysicShip(String spriteName, float x, float y, float rotation,float width, float height,
                      float density,int bodyNumber,int weaponNumbers,int engineNumbers,int energyNumbers,float linearDamping,float maxHP,
                      float[][] shape) {
        super(spriteName, x, y,rotation, width, height,density,bodyNumber,shape);
        this.rotationSpeed=0;
        movementPosition=0;
        //enginePower=0;
        movementVector = new Vector2(0, 0);
        tempVelocity=new Vector2(0,0);
        speed=0;
        this.weaponNumbers=weaponNumbers;
        this.engineNumbers=engineNumbers;
        weapons=new WeaponPoint[weaponNumbers];
        engines= new EnginePoint[engineNumbers];
        energyPoints=new EnergyPoint[energyNumbers];
        rotationDirection=0;
        this.maxHP=maxHP;
        this.hp=maxHP;

        this.linearDamping=linearDamping;
        targetRotation=rotation;

        /*for (Body i:bodies)
        {
            i.setLinearDamping(this.linearDamping);
        }*/

    }
    @Override
    public void create(TextureAtlas textureAtlas, World world)
    {
        super.create(textureAtlas,world);
        for(WeaponPoint i:weapons)
        {
            i.create(textureAtlas,world,getBody());
        }
        for(EnginePoint i:engines)
        {
            i.create(textureAtlas,world,getBody());
        }
        for(EnergyPoint i:energyPoints)
        {
            i.create(textureAtlas,world,getBody());
        }
        rotationSpeed=findRotationSpeed();
        maxEnergy=0;
        for(int i=0;i<energyPoints.length;i++)
        {
            maxEnergy+=energyPoints[i].getEnergyModule().getEnergyStorage();
        }

        energy=maxEnergy;
        energyConsLastTime=System.currentTimeMillis();
        consumptionReload=100;
        body.setUserData(this);
    }
    public void create(World world)
    {
        super.create(world);
        for(WeaponPoint i:weapons)
        {
            i.create(world,getBody());
        }
        for(EnginePoint i:engines)
        {
            i.create(world,getBody());
        }
        for(EnergyPoint i:energyPoints)
        {
            i.create(world,getBody());
        }
        rotationSpeed=findRotationSpeed();
        maxEnergy=0;
        for(int i=0;i<energyPoints.length;i++)
        {
            maxEnergy+=energyPoints[i].getEnergyModule().getEnergyStorage();
        }

        energy=maxEnergy;
        energyConsLastTime=System.currentTimeMillis();
        consumptionReload=100;
        body.setUserData(this);
    }

    public void move()
    {
        findRotDir();
        maxSpeed=findMaxSpeed();
        if(movementPosition==0)
            movementVector.set(0,0);
        else if(movementPosition==1)
        {
            movementVector.set((float)(-Math.sin(body.getAngle())),(float)(Math.cos(body.getAngle())));
        }
        else if(movementPosition==-1)
        {
            movementVector.set((float)(Math.sin(body.getAngle()))*0.15f,(float)(-Math.cos(body.getAngle()))*0.15f);
        }
        if(isShooting) {
            shot();
        }
        if(energy>0) {
            for (int i = 0; i < engines.length; i++) {
                if (!(speed >= maxSpeed)) {
                    //for (int j = 0; i < bodies.length; i++) {
                    //bodies[j].setLinearDamping(0.f);
                    //}
                    //bodies[i].applyForceToCenter(new Vector2(enginePower * movementVector.x, enginePower * movementVector.y), true);
                    engines[i].move(movementVector);
                } else {
                    tempVelocity.set(body.getLinearVelocity());
                    tempVelocity.nor();
                    tempVelocity.x*=maxSpeed*0.99f;
                    tempVelocity.y*=maxSpeed*0.99f;

                    //bodies[j].setLinearDamping(0.1f);
                    body.setLinearVelocity(tempVelocity);

                    //bodies[i].setLinearVelocity((float)-Math.sin(getRotation())*maxSpeed*0.98f,(float)Math.cos(getRotation())*maxSpeed*0.98f);
                }
            }

            if (rotationDirection == -1)

                //bodies[i].setTransform(bodies[i].getPosition().x, bodies[i].getPosition().y, bodies[i].getAngle() + rotationSpeed);
                body.setAngularVelocity(-rotationSpeed);
            else if (rotationDirection == 1) {
                //bodies[i].setTransform(bodies[i].getPosition().x, bodies[i].getPosition().y, bodies[i].getAngle() - rotationSpeed);
                body.setAngularVelocity(rotationSpeed);
                //body.setAngularVelocity(2);
            }
            else if (rotationDirection == 0) {
                body.setAngularVelocity(0);


            }

        }
        speed = body.getLinearVelocity().len();
        if(System.currentTimeMillis()-energyConsLastTime>consumptionReload) {
            for (int i = 0; i < engines.length; i++) {
                if(engines[i].getEngine()!=null) {
                    energy -= engines[i].getEngine().getEnergyConsumption();
                }

            }
            for (int i = 0; i < energyPoints.length; i++) {
                energy += energyPoints[i].getEnergyModule().getEnergyGeneration();

            }
            energyConsLastTime=System.currentTimeMillis();
        }
        if(energy<0)
            energy=0;
        else if(energy>maxEnergy)
            energy=maxEnergy;

        //System.out.println(body.getFixtureList().size);
        //System.out.println(Gdx.graphics.getFramesPerSecond());
        for(WeaponPoint i:weapons)
        {
            i.destroyAmmo();
        }
    }
    public void findRotDir()
    {
        //ship.getBodies()[0].setTransform(ship.getBodies()[0].getPosition().x,ship.getBodies()[0].getPosition().y,(float)Math.toRadians(sprite.getRotation()));

        //ship.getBodies()[1].setTransform(ship.getBodies()[1].getPosition().x,ship.getBodies()[1].getPosition().y,(float)Math.toRadians(sprite.getRotation()));

        int shipRotation=(int) getRotation();

        while (shipRotation>=360) {                 //
            shipRotation -= 360;                    //
        }                                            //������������ ��������� ���� ������� � ����� 0 �� 360
        while (shipRotation<=0) {                     //
            shipRotation += 360;                      //
        }                                            //
        int spriteRotation=(int)targetRotation;   // �������� �������� �� ������ � ������� �������������� �� Int
        while (spriteRotation>=360) {
            spriteRotation -= 360;
        }
        while (spriteRotation<=0) {
            spriteRotation += 360;
        }
        //shipRotation=(Math.toDegrees(ship.getRotation())>360)?(float)Math.toDegrees(ship.getRotation())-360:(float)Math.toDegrees(ship.getRotation());
        //spriteRotation=(sprite.getRotation()>360)?sprite.getRotation()-360:sprite.getRotation();
        int internalArc=Math.abs(shipRotation-spriteRotation);     //������� ������� � ��������� ��������� � �������� �����
        int externalArc=360-internalArc;                           // ����� ������� � ��������
         /*if(internalArc<externalArc)
         {
             ship.setRotationDirection(1);
         }
         else if(internalArc>externalArc)
         {
             ship.setRotationDirection(-1);
         }
         else ship.setRotationDirection(0);*/
        int tempShip=(shipRotation<180)?shipRotation+360:shipRotation;// ����������� �������� ��������� ���������� �� 360 ��� ���� ����� �������� �������� ��� ��������� �������� ����� 0
        int tempSprite=(spriteRotation<180)?spriteRotation+360:spriteRotation;

        if(Math.abs(tempShip-tempSprite)>rotationSpeed) {
            if (internalArc < externalArc) {
                if (shipRotation > spriteRotation) {
                    setRotationDirection(-1);

                    //ship.getBodies()[0].setAngularVelocity(-2f);
                    //ship.getBodies()[1].setAngularVelocity(-2f);
                } else if (shipRotation < spriteRotation) {
                    setRotationDirection(1);

                    //ship.getBodies()[0].setAngularVelocity(2f);
                    //ship.getBodies()[1].setAngularVelocity(2f);
                } else {
                    setRotationDirection(0);

                    //ship.getBodies()[0].setAngularVelocity(0f);
                    //ship.getBodies()[1].setAngularVelocity(0f);

                }

            } else {
                if (shipRotation > spriteRotation) {
                    setRotationDirection(1);

                    //ship.getBodies()[0].setAngularVelocity(2f);
                    //ship.getBodies()[1].setAngularVelocity(2f);
                } else if (shipRotation < spriteRotation) {
                    setRotationDirection(-1);

                    //ship.getBodies()[0].setAngularVelocity(-2f);
                    //ship.getBodies()[1].setAngularVelocity(-2f);
                } else {
                    setRotationDirection(0);

                    //ship.getBodies()[0].setAngularVelocity(0f);
                    //ship.getBodies()[1].setAngularVelocity(0f);

                }
            }
        }
        else
        {
            setRotationDirection(0);
            setAngularVelocity(0f);
            setAngle((float) Math.toRadians(targetRotation));

        }


    }
    public void shot()
    {
        for (WeaponPoint i:weapons) {
            if(energy>i.weapon.getEnergyCost()) {

                if(i.shot(getHeight() / 2 - i.getLocalAnchor().y, body.getLinearVelocity())) {
                    energy -= i.weapon.getEnergyCost();
                }
            }
        }
    }
    public void hurt(PhysicAmmo ammo)
    {
        hp-=ammo.getDamage();
    }

    @Override
    public void destroy() {
        super.destroy();
        for(WeaponPoint i:weapons)
        {
            i.destroy();
        }
        for(EnginePoint i:engines)
        {
            i.destroy();
        }
        for(EnergyPoint i:energyPoints)
        {
            i.destroy();
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        for (int i=0;i<engines.length;i++) {
            engines[i].draw(batch);
        }
        super.draw(batch);
        for (int i=0;i<weapons.length;i++) {
            weapons[i].draw(batch);
        }
        for (int i=0;i<energyPoints.length;i++) {
            if(energyPoints[i]!=null)
                energyPoints[i].draw(batch);
        }

    }

    public void setMovementPosition(int movementPosition) {
        this.movementPosition = movementPosition;
    }

    public int getMovementPosition() {
        return movementPosition;
    }

    public int getRotationDirection() {
        return rotationDirection;
    }

    public void setRotationDirection(int rotationDirection) {
        this.rotationDirection = rotationDirection;
    }

    public WeaponPoint[] getWeapons() {
        return weapons;
    }

    public EnginePoint[] getEngines() {
        return engines;
    }
    public float findMaxSpeed()
    {
        float amountSpeed=0;
        int engineNumber=0;
        for(int i=0;i<engines.length;i++)
        {
            if(engines[i].getEngine()!=null) {
                amountSpeed += engines[i].getEngine().getMaxSpeed();
                engineNumber++;
            }

        }
        return amountSpeed/engineNumber;
    }
    public float findRotationSpeed()
    {
        float rotationPower=0;
        for(int i=0;i<engines.length;i++)
        {
            if(engines[i].getEngine()!=null) {
                rotationPower += engines[i].getAngularPower();
            }
        }
        return rotationPower/getMass();
    }
    public void setAngle(float angle)
    {
        if(body!=null) {
            body.setTransform(body.getPosition().x, body.getPosition().y, angle);
        }
        else bDef.angle=angle;

    }
    public void setAngularVelocity(float angularVelocity)
    {

        body.setAngularVelocity(angularVelocity);

    }

    @Override
    public void setTransform(float x, float y, float rotation) {
        super.setTransform(x, y, rotation);
        for (int i=0;i<engines.length;i++) {
            engines[i].toShip(getX(),getY(),getRotation());
        }

        for (int i=0;i<weapons.length;i++) {
            weapons[i].toShip(getX(),getY(),getRotation());
        }
        for (int i=0;i<energyPoints.length;i++) {
            if(energyPoints[i]!=null)
                energyPoints[i].toShip(getX(),getY(),getRotation());
        }
        
    }

    public int getWeaponNumbers() {
        return weaponNumbers;
    }

    public int getEngineNumbers() {
        return engineNumbers;
    }

    public EnergyPoint[] getEnergyPoints() {
        return energyPoints;
    }
    public float getMass()
    {
        float mass=0;

        return body.getMass();
    }

    public float getMaxEnergy() {
        return maxEnergy;
    }

    public float getEnergy() {
        return energy;
    }
    public void setShooting(boolean a)
    {
        isShooting=a;
    }

    @Override
    public String toString() {
        return "Ship";
    }

    public float getMaxHP() {
        return maxHP;
    }

    public float getHp() {
        return hp;
    }

    public float getTargetRotation() {
        return targetRotation;
    }

    public void setTargetRotation(float targetRotation) {
        this.targetRotation = targetRotation;
    }

    public static PhysicShip fromServ(ServShip servShip)
    {
        PhysicShip ship;
        if (servShip.getName().equals("Dashing"))
        {
            ship=new Fury(servShip.getX(),servShip.getY(),servShip.getRotation());
        }
        else if(servShip.getName().equals("StarFighter"))
        {
            ship=new  StarFighter(servShip.getX(),servShip.getY(),servShip.getRotation());
        }
        else{
            System.out.println("������� � ������ ������ �� ����������");
            return null;
        }

        ship.setId(servShip.getId());

        return  ship;


    }
    public ServShip toServ()
    {
        ServShip servShip=new ServShip();
        servShip.setName(spriteName);
        servShip.setX(getX());
        servShip.setY(getY());
        servShip.setWidth(getWidth());
        servShip.setHeight(getHeight());
        servShip.setRotation(getRotation());
        servShip.setId(getId());
        return servShip;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

}






