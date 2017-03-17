package com.satchlapp;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

import com.squareup.picasso.Transformation;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by Sara on 3/15/2017.
 */
public class PaletteTransformation implements Transformation {

    private static final PaletteTransformation instance = new PaletteTransformation();
    private static final Map<Bitmap, Palette> cache = new WeakHashMap<>();

    public static PaletteTransformation getInstance(){
        return instance;
    }

    public static Palette getPalette(Bitmap bitmap){
        return cache.get(bitmap);
    }

    private PaletteTransformation(){}

    @Override
    public Bitmap transform(Bitmap source){
        Palette palette = Palette.from(source).generate();
        cache.put(source, palette);
        return source;
    }

    @Override
    public String key(){
        return "palette";
    }


}
