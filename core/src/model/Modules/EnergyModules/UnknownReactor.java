package model.Modules.EnergyModules;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import com.badlogic.gdx.physics.box2d.World;
import model.Modules.EnergyModule;
import utils.Size;

/**
 * Created by Sash on 17.10.2018.
 */

public class UnknownReactor extends EnergyModule {
    public UnknownReactor( float x, float y,float rotation) {
        super("Reactor", x, y,rotation, Size.Small, 1,1000,10);
    }
}

