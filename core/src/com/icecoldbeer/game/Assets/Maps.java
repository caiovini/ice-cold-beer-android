package com.icecoldbeer.game.Assets;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.Locale;

import static com.badlogic.gdx.net.HttpRequestBuilder.json;


public class Maps {

    private static final String MAP_PATH = "map/Map%d.json";
    private static final double IDEAL_HEIGHT = 1080.0 - Gdx.graphics.getHeight();
    private static final double IDEAL_WIDTH = 2220.0 - Gdx.graphics.getWidth();


    public static ArrayList<ModelMap> getMap(int stage){

        ArrayList<ModelMap> modelMaps = json.fromJson(ArrayList.class, ModelMap.class,
                    Gdx.files.internal(String.format(Locale.ENGLISH, MAP_PATH, stage)));

        for( ModelMap m : modelMaps ){

            m.setPosY(IDEAL_HEIGHT <= 0 ? m.getPosY() : (int) (m.getPosY() - IDEAL_HEIGHT));
            m.setPosX(IDEAL_WIDTH <= 0 ? m.getPosX() : (int) (m.getPosX() - IDEAL_WIDTH));
        }

        return  modelMaps;
    }

}
