/**
 * [1968] - [2022] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */

package edu.up.isgc.raytracer.lights;

/**
 * @author Jafet Rodríguez & Andrés Martínez
 */

import edu.up.isgc.raytracer.Intersection;
import edu.up.isgc.raytracer.Vector3D;

import java.awt.*;

public class PointLight extends Light{

    public PointLight(Vector3D position, Color color, double intensity) {
        super(position, color, intensity);
    }

    @Override
    public double getNDotL(Intersection intersection) {
        Vector3D direction = Vector3D.normalize(Vector3D.substract(getPosition(), intersection.getPosition()));
        return Math.max(Vector3D.dotProduct(intersection.getNormal(), direction), 0.0);
    }
}
