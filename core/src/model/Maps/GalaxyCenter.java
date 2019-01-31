package model.Maps;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import model.Map;

/**
 * Created by Sash on 22.05.2018.
 */

public class GalaxyCenter extends Map {
    public GalaxyCenter(SpriteBatch batch, TextureAtlas textureAtlas) {
        super(batch, textureAtlas.findRegion("GalaxyCenter"), 200, 120);
    }
}
