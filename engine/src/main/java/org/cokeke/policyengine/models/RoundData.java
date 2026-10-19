package org.cokeke.policyengine.models;

import java.util.List;

public class RoundData {
    private List<StateEconomy> states;
    private FederalEconomy federal;
    private int currentMonth;
    private List<StateFlows> statesFlows;

    public RoundData(List<StateEconomy> states, FederalEconomy federal, int c,
        List<StateFlows> s) {
        this.states = states;
        this.federal = federal;
        this.currentMonth = c;
        this.statesFlows = s;
    }

    public List<StateEconomy> getStates() { return this.states; }
    public int getCurrentMonth() { return this.currentMonth; }
    public List<StateFlows> getStateFlows() { return this.statesFlows; }
    public FederalEconomy getFederal() { return this.federal; }

    public void setStates(List<StateEconomy> states) { this.states = states; }
    public void setCurrentMonth(int currentMonth) { this.currentMonth = currentMonth; }
    public void setFederal(FederalEconomy federal) { this.federal = federal; }
    public void setStateFlows(List<StateFlows> s) { this.statesFlows = s; }
}
