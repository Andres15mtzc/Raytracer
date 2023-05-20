/**
 * [1968] - [2022] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package edu.up.isgc.raytracer.objects;

/**
 * @author Jafet Rodríguez & Andrés Martínez
 */

import edu.up.isgc.raytracer.IIntersectable;
import edu.up.isgc.raytracer.Vector3D;

import java.awt.*;

public abstract class Object3D implements IIntersectable {

    private Color color;
    private Vector3D position;
    private double shininess;
    private boolean isReflective;
    private double refraction;

    public Object3D(Vector3D position, Color color, double shininess,double refraction, boolean isReflective) {
        setPosition(position);
        setColor(color);
        setShininess(shininess);
        setRefraction(refraction);
        setReflective(isReflective);
    }

    public double getShininess() {
        return shininess;
    }

    public void setShininess(double shininess) {
        this.shininess = shininess;
    }

    public double getRefraction() {
        return refraction;
    }

    public void setRefraction(double refraction) {
        this.refraction = refraction;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Vector3D getPosition() {
        return position;
    }

    public void setPosition(Vector3D position) {
        this.position = position;
    }

    public Boolean getReflective() {
        return isReflective;
    }

    public void setReflective(Boolean reflective) {
        isReflective = reflective;
    }

}
