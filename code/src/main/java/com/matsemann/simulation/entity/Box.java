package com.matsemann.simulation.entity;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

public class Box extends Entity {

    public Box(String name, Model model, btRigidBody.btRigidBodyConstructionInfo info) {
        setup(name, model, info);
    }
}
