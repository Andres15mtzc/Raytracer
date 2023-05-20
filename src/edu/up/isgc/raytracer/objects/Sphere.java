/**
 * [1968] - [2022] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package edu.up.isgc.raytracer.objects;

/**
 * @author Jafet Rodríguez & Andrés Martínez
 */

import edu.up.isgc.raytracer.Intersection;
import edu.up.isgc.raytracer.Ray;
import edu.up.isgc.raytracer.Vector3D;

import java.awt.*;

public class Sphere extends Object3D {
    private double radius;

    public Sphere(Vector3D position, double radius, Color color, double shininess, double refraction, boolean isReflective) {
        super(position, color, shininess, refraction, isReflective);
        setRadius(radius);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public Intersection getIntersection(Ray ray) {
        Vector3D L = Vector3D.substract(ray.getOrigin(), getPosition());
        double tca = Vector3D.dotProduct(ray.getDirection(), L);
        double L2 = Math.pow(Vector3D.magnitude(L), 2);
        //Intersection
        double d2 = Math.pow(tca, 2) - L2 + Math.pow(getRadius(), 2);
        if(d2 >= 0){
            double d = Math.sqrt(d2);
            double t0 = -tca + d;
            double t1 = -tca - d;

            double distance = Math.min(t0, t1);
            Vector3D position = Vector3D.add(ray.getOrigin(), Vector3D.scalarMultiplication(ray.getDirection(), distance));
            Vector3D normal = Vector3D.normalize(Vector3D.substract(position, getPosition()));
            return new Intersection(position, distance, normal, this);
        }

        return null;
    }
}
