/**
 * [1968] - [2022] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package edu.up.isgc.raytracer;

/**
 * @author Jafet Rodríguez & Andrés Martínez
 */

import edu.up.isgc.raytracer.lights.DirectionalLight;
import edu.up.isgc.raytracer.lights.Light;
import edu.up.isgc.raytracer.lights.PointLight;
import edu.up.isgc.raytracer.objects.*;
import edu.up.isgc.raytracer.tools.OBJReader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Raytracer {

    public static void main(String[] args) {
        System.out.println(new Date());

        Scene scene01 = new Scene();
        scene01.setCamera(new Camera(new Vector3D(0, 1f, -8), 144f, 160f, 1920, 1080, 0.5f, 50f));
        scene01.addLight(new PointLight(new Vector3D(0f, 4f, -3f), Color.WHITE, 1.2));
        scene01.addLight(new PointLight(new Vector3D(3f, 2f, -3f), Color.YELLOW, 0.8));
        scene01.addObject(new Model3D(new Vector3D(0, 0, 0),
                new Triangle[]{
                        new Triangle(new Vector3D(-600,0,-600), new Vector3D(600, 0, -600), new Vector3D(600, 0, 600)),
                        new Triangle(new Vector3D(-600,0,-600), new Vector3D(600, 0, 600), new Vector3D(-600, 0, 600)),},
                new Color(52, 43, 5), 45f, 0f, false));
        scene01.addObject(new Model3D(new Vector3D(0, 0, 0),
                new Triangle[]{
                        new Triangle(new Vector3D(-400,-200,4), new Vector3D(400, -200, 4), new Vector3D(400, 200, 4)),
                        new Triangle(new Vector3D(-400,-200,4), new Vector3D(400, 200, 4), new Vector3D(-400, 200, 4)),},
                Color.WHITE, 45f, 0f, false));
        scene01.addObject(new Model3D(new Vector3D(0, 2.8f, -0.00001f),
                new Triangle[]{
                        new Triangle(new Vector3D(-5,-2,4), new Vector3D(5, -2, 4), new Vector3D(5, 3, 4)),
                        new Triangle(new Vector3D(-5,-2,4), new Vector3D(5, 3, 4), new Vector3D(-5, 3, 4)),},
                Color.CYAN, 10f, 0f, true));
        scene01.addObject(OBJReader.GetModel3D("Plato.obj", new Vector3D(-0.5f, 0.2f, -1f), Color.WHITE, 45f, 0f, true));
        scene01.addObject(OBJReader.GetModel3D("Apple.obj", new Vector3D(-0.5f, 0.2f, -1f), Color.RED, 45f, 0f, false));
        scene01.addObject(OBJReader.GetModel3D("Botella.obj", new Vector3D(2, 0, 0f), new Color(11, 6, 45), 45f, 0.8f, false));

        BufferedImage image = raytrace(scene01);
        File outputImage = new File("image.png");
        try {
            ImageIO.write(image, "png", outputImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(new Date());
    }

    public static BufferedImage raytrace(Scene scene) {
        Camera mainCamera = scene.getCamera();
        float[] nearFarPlanes = mainCamera.getNearFarPlanes();
        float cameraZ = (float) mainCamera.getPosition().getZ();
        BufferedImage image = new BufferedImage(mainCamera.getResolutionWidth(), mainCamera.getResolutionHeight(), BufferedImage.TYPE_INT_RGB);
        List<Object3D> objects = scene.getObjects();
        List<Light> lights = scene.getLights();

        Vector3D[][] positionsToRaytrace = mainCamera.calculatePositionsToRay();
        for (int i = 0; i < positionsToRaytrace.length; i++) {
            for (int j = 0; j < positionsToRaytrace[i].length; j++) {
                if (j==0){
                    //System.out.println(i);
                }
                double x = positionsToRaytrace[i][j].getX() + mainCamera.getPosition().getX();
                double y = positionsToRaytrace[i][j].getY() + mainCamera.getPosition().getY();
                double z = positionsToRaytrace[i][j].getZ() + mainCamera.getPosition().getZ();

                Ray ray = new Ray(mainCamera.getPosition(), new Vector3D(x, y, z));
                Intersection closestIntersection = raycast(ray, objects, null, new float[]{cameraZ + nearFarPlanes[0], cameraZ + nearFarPlanes[1]});

                Color pixelColor = Color.BLACK;
                if (closestIntersection != null) {
                    Color objColor = closestIntersection.getObject().getColor();

                    //Refraction
                    if(closestIntersection.getObject().getRefraction() > 0.0f){
                        Intersection refraction = Intersection.calcRefraction(scene, ray, closestIntersection);
                        if(refraction != null){
                            closestIntersection = refraction;
                        }
                        Color lastObjColor = closestIntersection.getObject().getColor();
                        objColor = combineColor(objColor, lastObjColor, (float)closestIntersection.getObject().getRefraction());
                    }
                    //Reflection
                    if(closestIntersection.getObject().getReflective()){
                        Intersection reflection = Intersection.calcReflection(closestIntersection, objects, mainCamera, 0);
                        if(reflection != null){
                            closestIntersection = reflection;
                        }
                    }

                    for (Light light : lights) {
                        //Check shadows
                        Ray rayToLight = new Ray(closestIntersection.getPosition(), light.getPosition());
                        Intersection collision = raycast(rayToLight, objects, closestIntersection.getObject(), null);

                        if (collision == null){
                            double nDotL = light.getNDotL(closestIntersection);
                            double intensity = light.getIntensity() * nDotL;
                            double distance = Vector3D.magnitude(Vector3D.substract(closestIntersection.getPosition(), light.getPosition()));
                            //double distance = Vector3D.calculateDistance(light.getPosition(), closestIntersection.getPosition());
                            //Light decay
                            double Li = intensity / Math.pow(distance, 1f);
                            Color lightColor = light.getColor();
                            //Specular Light
                            Vector3D V = Vector3D.normalize(mainCamera.getPosition());
                            Vector3D L = Vector3D.normalize(light.getPosition());
                            Vector3D VplusL = Vector3D.add(V, L);
                            Vector3D H = Vector3D.normalize(Vector3D.scalarMultiplication(VplusL, 1/Vector3D.magnitude(VplusL)));
                            double a = closestIntersection.getObject().getShininess();
                            double specularLight = Math.pow((Vector3D.dotProduct(closestIntersection.getNormal(), H)), a);
                            //Add color
                            float[] lightColors = new float[]{lightColor.getRed() / 255.0f, lightColor.getGreen() / 255.0f, lightColor.getBlue() / 255.0f};
                            float[] objColors = new float[]{objColor.getRed() / 255.0f, objColor.getGreen() / 255.0f, objColor.getBlue() / 255.0f};
                            for (int colorIndex = 0; colorIndex < objColors.length; colorIndex++) {
                                objColors[colorIndex] *= (Li+0.04f+specularLight) * lightColors[colorIndex];
                            }
                            Color diffuse = new Color(clamp(objColors[0], 0, 1), clamp(objColors[1], 0, 1), clamp(objColors[2], 0, 1));
                            pixelColor = addColor(pixelColor, diffuse);
                        }else{
                            pixelColor = addColor(pixelColor, Color.BLACK);
                        }
                    }
                }
                image.setRGB(i, j, pixelColor.getRGB());
            }
        }
        return image;
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static Color addColor(Color original, Color otherColor) {
        float red = clamp((original.getRed() / 255f) + (otherColor.getRed() / 255f), 0, 1);
        float green = clamp((original.getGreen() / 255f) + (otherColor.getGreen() / 255f), 0, 1);
        float blue = clamp((original.getBlue() / 255f) + (otherColor.getBlue() / 255f), 0, 1);
        return new Color(red, green, blue);
    }

    public static Color combineColor(Color original, Color otherColor, float percentage) {
        float red = (original.getRed() * (1f-percentage) / 255f) + (otherColor.getRed() * percentage / 255f);
        float green = (original.getGreen() * (1f-percentage) / 255f) + (otherColor.getGreen() * percentage / 255f);
        float blue = (original.getBlue() * (1f-percentage) / 255f) + (otherColor.getBlue() * percentage / 255f);
        return new Color(red, green, blue);
    }

    public static Intersection raycast(Ray ray, List<Object3D> objects, Object3D caster, float[] clippingPlanes) {
        Intersection closestIntersection = null;

        for (int k = 0; k < objects.size(); k++) {
            Object3D currentObj = objects.get(k);
            if (caster == null || !currentObj.equals(caster)) {
                Intersection intersection = currentObj.getIntersection(ray);
                if (intersection != null) {
                    double distance = intersection.getDistance();
                    double intersectionZ = intersection.getPosition().getZ();
                    if (distance >= 0 &&
                            (closestIntersection == null || distance < closestIntersection.getDistance()) &&
                            (clippingPlanes == null || (intersectionZ >= clippingPlanes[0] && intersectionZ <= clippingPlanes[1]))) {
                        closestIntersection = intersection;
                    }
                }
            }
        }

        return closestIntersection;
    }


}
