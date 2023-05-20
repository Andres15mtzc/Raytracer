/**
 * [1968] - [2022] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package edu.up.isgc.raytracer.lights;

/**
 * @author Jafet Rodríguez & Andrés Martínez
 */

import edu.up.isgc.raytracer.Intersection;
import edu.up.isgc.raytracer.Ray;
import edu.up.isgc.raytracer.Vector3D;
import edu.up.isgc.raytracer.objects.Object3D;

import java.awt.*;

public abstract class Light extends Object3D {

    private double intensity;

    public Light(Vector3D position, Color color, double intensity) {
        super(position, color, 0f, 0f, false);
        setIntensity(intensity);
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public abstract double getNDotL(Intersection intersection);

    public Intersection getIntersection(Ray ray) {
        return new Intersection(Vector3D.ZERO(), -1, Vector3D.ZERO(), null);
    }
}
