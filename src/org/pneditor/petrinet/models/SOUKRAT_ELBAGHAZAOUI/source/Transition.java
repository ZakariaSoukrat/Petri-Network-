package org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source;



import java.util.LinkedList;

public class Transition {
    private LinkedList<ArcInTransition> enteringArc;
    private LinkedList<ArcOutTransition> exitingArc;
	
    // the constructor
    public Transition() {
    	this.enteringArc = new LinkedList<ArcInTransition>();
    	this.exitingArc = new LinkedList<ArcOutTransition>();
    
    }
    
    
    public LinkedList<ArcInTransition> getEnteringArc() {
    	return enteringArc;
    }
    
    
    public LinkedList<ArcOutTransition> getExitingArc() {
    	return exitingArc;
    }
    
    
 
    public void addEnteringArc(ArcInTransition a) {
    	this.enteringArc.add(a);
    }
    
    public void removeEnteringArc(ArcInTransition a) {
    	this.enteringArc.remove(a);
    }
    
    
    public void addExitingArc(ArcOutTransition a) {
    	this.exitingArc.add(a);
    }
    
    public void removeExitingArc(ArcOutTransition a) {
    	this.exitingArc.remove(a);
    }
    //function isFireable
    //verify that all the entringArc are fireable
    public boolean isFireable() {
        for (int i = 0; i < enteringArc.size(); i++) {
        	if( enteringArc.get(i).isFireable() == false) {
        		return false;
        	}
        }
        return true;
    }
    //Function that fire a transition 
    //firstly it verify if the transition is fireable (if not it print a msg)
    //the function execute the ArcZero and ArcDrainer
    //then update the places number of tokens
    public void fire() {
    	if(this.isFireable()) {
    	for (int i = 0; i < enteringArc.size(); i++) {
    		if(enteringArc.get(i) instanceof ArcZero) {
    			enteringArc.get(i).execute();
    		}
    		if(enteringArc.get(i) instanceof ArcDrainer) {
    			enteringArc.get(i).execute();
    		}
    		if(enteringArc.get(i) instanceof ArcZero == false && enteringArc.get(i) instanceof ArcDrainer == false) {
    		enteringArc.get(i).getSource().removeTokens(enteringArc.get(i).getWeight());  	}}
    	
    	for (int i = 0; i < exitingArc.size(); i++) {
    		exitingArc.get(i).getDestination().addTokens(exitingArc.get(i).getWeight());
    	}}
    	else {
    		System.out.println("Transition is not fireable");
    	}
    }
    //toString as described in the test plan
    public String toString() {
 	   String res = "Transition avec " + this.enteringArc.size() + " Arc Entrant " +", " + this.exitingArc.size() + " Arc Sortant" + "\n";
 	   return res;
 	}
    
    
    
    
    
    
    
    
    }