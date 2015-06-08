package com.matsemann.simulation.setup;

public class SimulationConstants {

    public final static int MAX_STEPS = 1000;
    public final static float STEP_SIZE = 1f / 60f;

    public final static float HUB_FLANGE_DISTANCE = 5.5f;
    public final static float HUB_FLANGE_RADIUS = 2.25f;
    public final static float HUB_FLANGE_RADIUS_TOTAL = HUB_FLANGE_RADIUS + 0.25f;
    public final static float AXLE_RADIUS = 0.3f;
    public final static float AXLE_LENGTH = 15f;

    public final static float HUB_TO_RIM_LENGTH = 31.25f - HUB_FLANGE_RADIUS;
    public final static float RIM_ERD = 2 * HUB_TO_RIM_LENGTH + 2 * HUB_FLANGE_RADIUS;
    public final static float HUB_CENTER_TO_RIM = RIM_ERD / 2f;
    public final static float ROTATING_BOX_DISTANCE = 5f;
    public final static float CONE_TWIST_LIMIT = 0.785398f;

    public final static int NUM_SPOKES = 32;
    public final static int NUM_SPOKES_ONE_SIDE = NUM_SPOKES / 2;
    public final static float SPOKE_ANGLE_DISTANCE = 360f / NUM_SPOKES_ONE_SIDE;
    public final static float SPOKE_RADIUS = 0.7f;

    public final static float HUB_MASS = 0f;
    public final static float RIM_MASS = 1f;
    public final static float BOX_SIZE = 1f;
    public final static float BOX_MASS = 0.1f;

    public final static float SPRING_STIFFNESS = 1500f;
    public static final float DEFAULT_TENSION_LENGTH = 1f;
    public final static float SPRING_DAMPENING = 0.22f;

    public final static short RAY_GROUP = 64;
    public final static short BOX_GROUP = 128;
    public final static short SPOKE_GROUP = 256;

    public final static float TRANS_FORCE = 10000f;
    public final static float TORQUE_FORCE = 45000f;
}
