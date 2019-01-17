package model.Modules;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.physics.box2d.World;
import model.Module;
import utils.ModuleType;
import utils.Size;

/**
 * Created by Sash on 17.10.2018.
 */

public class EnergyModule extends Module {
    private float energyStorage;
    private float energyGeneration;

    public EnergyModule(String spriteName, float x, float y,float rotation, Size size, float density,float energyStorage,float energyGeneration) {
        super(spriteName, x, y,rotation, size, ModuleType.EnergyModule, density);
        this.energyGeneration=energyGeneration;
        this.energyStorage=energyStorage;

    }

    public float getEnergyGeneration() {
        return energyGeneration;
    }

    public float getEnergyStorage() {
        return energyStorage;
    }


}
