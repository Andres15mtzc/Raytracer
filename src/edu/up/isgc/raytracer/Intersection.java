/**
 * [1968] - [2022] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package edu.up.isgc.raytracer;

/**
 * @author Jafet Rodríguez & Andrés Martínez
 */

import edu.up.isgc.raytracer.objects.Camera;
import edu.up.isgc.raytracer.objects.Object3D;

import java.util.List;

import static edu.up.isgc.raytracer.Raytracer.raycast;

public class Intersection {

    private double distance;
    private Vector3D normal;
    private Vector3D position;
    private Object3D object;

    public Intersection(Vector3D position, double distance, Vector3D normal, Object3D object) {
        setDistance(distance);
        setNormal(normal);
        setPosition(position);
        setObject(object);
    }

    public static Intersection calcReflection(Intersection intersection, List<Object3D> objects, Camera camera, int bounces) {
        if (intersection.getObject().getReflective() == true && bounces <= 2){
            Vector3D normal = intersection.getNormal();
            Vector3D viewer = Vector3D.substract(intersection.getPosition(), camera.getPosition());
            Vector3D nx2= Vector3D.scalarMultiplication(normal,-2);
            double nDotV= Vector3D.dotProduct(normal, viewer);
            Vector3D reflection = Vector3D.add(Vector3D.scalarMultiplication(nx2, nDotV), viewer);
            Ray newRay = new Ray(intersection.getPosition(), reflection);

            for (Object3D object : objects) {
                if (!object.equals(intersection.getObject())) {
                    Intersection newIntersection = raycast(newRay, objects, intersection.getObject(), null);
                    if (newIntersection != null) {
                        return calcReflection(newIntersection, objects, camera, bounces++);
                    }
                    return intersection;
                }
            }
        }
        return intersection;
    }

    public static Intersection calcRefraction(Scene scene, Ray ray, Intersection intersection){
        Vector3D incident = Vector3D.normalize(ray.getDirection());
        double n = 1/(intersection.getObject().getRefraction());
        double c1 = Vector3D.dotProduct(intersection.getNormal(), incident);
        double c2 = Math.sqrt(1-Math.pow(n,2)*(1-Math.pow(c1,2)));
        Vector3D refractionVector = Vector3D.add(Vector3D.scalarMultiplication(incident,n), Vector3D.scalarMultiplication(intersection.getNormal(), (n*c1)-c2));
        Ray refractionRay = new Ray(intersection.getPosition(), refractionVector);

        for (Object3D object : scene.getObjects()) {
            if (!object.equals(intersection.getObject())) {
                Intersection refractedIntersection = raycast(refractionRay, scene.getObjects(), intersection.getObject(), null);
                if (refractedIntersection == null) {
                    return intersection;
                } else {
                    return calcRefraction(scene, ray, refractedIntersection);
                }
            }
        }
        return intersection;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Vector3D getNormal() {
        return normal;
    }

    public void setNormal(Vector3D normal) {
        this.normal = normal;
    }

    public Vector3D getPosition() {
        return position;
    }

    public void setPosition(Vector3D position) {
        this.position = position;
    }

    public Object3D getObject() {
        return object;
    }

    public void setObject(Object3D object) {
        this.object = object;
    }
}
