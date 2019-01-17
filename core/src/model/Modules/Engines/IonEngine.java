package model.Modules.Engines;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import com.badlogic.gdx.physics.box2d.World;
import model.Modules.Engine;
import utils.Size;

/**
 * Created by Sash on 09.10.2018.
 */

public class IonEngine extends Engine {
    public IonEngine( float x, float y,float rotation) {
        super("IonEngine", x, y,rotation, Size.Small, 1f, 5000, 1000f, 10,1f, Type.Cruising);
    }
}

