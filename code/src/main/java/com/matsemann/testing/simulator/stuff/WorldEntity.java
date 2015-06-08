package com.matsemann.testing.simulator.stuff;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class WorldEntity implements Disposable {

    Logger logger = LoggerUtil.getLogger(getClass());

    public final ModelInstance instance;
    public final MotionState motionState;
    public final btRigidBody btRigidBody;
    
    private final String name;
    private final Map<String, Color> originalColors = new HashMap<>();


    public WorldEntity(String name, ModelInstance instance, btRigidBody btRigidBody, boolean disableDeactivation) {
        this.name = name;
        this.instance = instance;
        this.btRigidBody = btRigidBody;
        btRigidBody.userData = this;

        motionState = new MotionState(instance.transform);
        btRigidBody.setMotionState(motionState);

        logger.debug("Creating object '{}'", name);

        if (disableDeactivation) {
            logger.debug("Disabling deactivation for '{}'", name);
            btRigidBody.setActivationState(Collision.DISABLE_DEACTIVATION);
        }

        getDefaultColors();
    }

    private void getDefaultColors() {
        for (Material m : instance.materials) {
            ColorAttribute attribute = (ColorAttribute) m.get(ColorAttribute.Diffuse);
            if (attribute != null) {
                originalColors.put(m.id, new Color(attribute.color));
            }
        }
    }

    public void restoreColors() {
        for (Material m : instance.materials) {
            ColorAttribute attribute = (ColorAttribute) m.get(ColorAttribute.Diffuse);
            if (attribute != null) {
                attribute.color.set(originalColors.get(m.id));
            }
        }
    }

    public void update() {
        logger.debug("Updating Bullet's transform for '{}'", name);
        btRigidBody.setWorldTransform(instance.transform);
    }

    public void setColor(float r, float g, float b, float a) {
        setColor(r, g, b, a, false);
    }

    public void applyColorFunc(Consumer<Color> function) {
        logger.debug("Color function for '{}'", name);

        for (Material m : instance.materials) {
            ColorAttribute attribute = (ColorAttribute) m.get(ColorAttribute.Diffuse);
            if (attribute != null) {
                function.accept(attribute.color);
            }
        }
    }

    public void setColor(float r, float g, float b, float a, boolean newDefaults) {
        logger.debug("New color for '{}'", name);

        for (Material m : instance.materials) {
            ColorAttribute attribute = (ColorAttribute) m.get(ColorAttribute.Diffuse);
            if (attribute != null) {
                attribute.color.set(r, g, b, a);
                if (newDefaults) {
                    originalColors.get(m.id).set(r, g, b, a);
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public void dispose() {
        logger.debug("Disposing '{}'", name);

        btRigidBody.dispose();
        motionState.dispose();
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
//            transform.getScale(tmp);

            transform.set(worldTrans);

//            transform.setToScaling(tmp);
        }
    }

}
