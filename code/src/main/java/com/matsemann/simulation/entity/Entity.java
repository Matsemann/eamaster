package com.matsemann.simulation.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Disposable;
import org.slf4j.Logger;
import com.matsemann.common.LoggerUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Entity implements Disposable {

//    Logger logger = LoggerUtil.getLogger(getClass());

    public String name;
    public ModelInstance modelInstance;
    public MotionState motionState;
    public btRigidBody rigidBody;

    public List<Disposable> disposables = new ArrayList<>();
    private final Map<String, Color> originalColors = new HashMap<>();

    protected void setup(String name, Model model, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
        this.name = name;

        modelInstance = new ModelInstance(model);

        rigidBody = new btRigidBody(constructionInfo);
        rigidBody.userData = this;
        rigidBody.setActivationState(Collision.DISABLE_DEACTIVATION);

        motionState = new MotionState(modelInstance.transform);
        rigidBody.setMotionState(motionState);



        getDefaultColors();
    }

    public void getDefaultColors() {
        for (Material m : modelInstance.materials) {
            ColorAttribute attribute = (ColorAttribute) m.get(ColorAttribute.Diffuse);
            if (attribute != null) {
                originalColors.put(m.id, new Color(attribute.color));
            }
        }
    }

    public void applyColorFunc(Consumer<Color> function) {
//        logger.debug("Color function for '{}'", name);

        for (Material m : modelInstance.materials) {
            ColorAttribute attribute = (ColorAttribute) m.get(ColorAttribute.Diffuse);
            if (attribute != null) {
                function.accept(attribute.color);
            }
        }
    }

    public void setColor(Color color, boolean newDefaults) {
        setColor(color.r, color.g, color.b, color.a, newDefaults);
    }

    public void setColor(float r, float g, float b, float a, boolean newDefaults) {
//        logger.debug("New color for '{}'", name);

        for (Material m : modelInstance.materials) {
            ColorAttribute attribute = (ColorAttribute) m.get(ColorAttribute.Diffuse);
            if (attribute != null) {
                attribute.color.set(r, g, b, a);
                if (newDefaults) {
                    originalColors.get(m.id).set(r, g, b, a);
                }
            }
        }
    }

    public void restoreColors() {
        for (Material m : modelInstance.materials) {
            ColorAttribute attribute = (ColorAttribute) m.get(ColorAttribute.Diffuse);
            if (attribute != null) {
                attribute.color.set(originalColors.get(m.id));
            }
        }
    }

    public void update() {
        rigidBody.setWorldTransform(modelInstance.transform);
    }

    @Override
    public void dispose() {
        rigidBody.dispose();
        motionState.dispose();

        disposables.forEach(Disposable::dispose);
    }


    /**
     * Keeps the Bullet transform and the ModelInstance transform in sync
     */
    static class MotionState extends btMotionState {
        private final Matrix4 transform;
        private final Vector3 tmp = new Vector3();

        MotionState(Matrix4 transform) {
            this.transform = transform;
        }

        @Override
        public void getWorldTransform(Matrix4 worldTrans) {
            worldTrans.set(transform);
        }

        @Override
        public void setWorldTransform(Matrix4 worldTrans) {
            transform.set(worldTrans);
        }
    }
}
