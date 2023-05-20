/**
 * [1968] - [2022] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package edu.up.isgc.raytracer;

/**
 * @author Jafet Rodríguez & Andrés Martínez
 */

import edu.up.isgc.raytracer.lights.Light;
import edu.up.isgc.raytracer.objects.Camera;
import edu.up.isgc.raytracer.objects.Object3D;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    private Camera camera;
    private List<Object3D> objects;
    private List<Light> lights;

    public Scene() {
        setObjects(new ArrayList<>());
        setLights(new ArrayList<>());
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void addObject(Object3D object) {
        getObjects().add(object);
    }

    public List<Object3D> getObjects() {
        if (objects == null) {
            objects = new ArrayList<>();
        }
        return objects;
    }

    public void setObjects(List<Object3D> objects) {
        this.objects = objects;
    }

    public List<Light> getLights() {
        return lights;
    }

    public void setLights(List<Light> lights) {
        this.lights = lights;
    }

    public void addLight(Light light){
        getLights().add(light);
    }
}

