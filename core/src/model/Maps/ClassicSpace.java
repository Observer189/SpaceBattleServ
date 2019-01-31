package model.Maps;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import model.Map;

/**
 * Created by Sash on 22.05.2018.
 */

public class ClassicSpace extends Map {
    public ClassicSpace(SpriteBatch batch, TextureAtlas textureAtlas) {
        super(batch, textureAtlas.findRegion("ClassicSpace"), 200, 120);
    }
}
