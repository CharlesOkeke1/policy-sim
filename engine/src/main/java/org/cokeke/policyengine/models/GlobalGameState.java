package org.cokeke.policyengine.models;

public class GlobalGameState {
    private World world;
    private SimConfig config;

    public GlobalGameState(World w, SimConfig s) {
        this.world = w;
        this.config = s;
    }

    public World getWorld() { return this.world; }
    public SimConfig getConfig() { return this.config; }

    public void setWorld(World w) { this.world = w; } 
}
