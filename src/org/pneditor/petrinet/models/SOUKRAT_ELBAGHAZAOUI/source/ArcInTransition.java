package org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source;


import java.util.LinkedList;

public class ArcInTransition extends Arc {
    private Place source;
    private Transition destination;
   
    
    public ArcInTransition(int weight, Place source, Transition destination) {
    	super(weight);
		this.source = source;
		this.destination = destination;
		
	}
	
	public Place getSource() {
		return this.source;
	}
	
	public Transition getDestination() {
		return this.destination;
	}
    
    
    public boolean isFireable(){
        if(this instanceof ArcZero)    { return true;}
        if(this instanceof ArcDrainer) { return true;}
    	return this.getWeight()<= this.source.getNbrTokens();
    }
    
    public void execute() {
    	
    }
    
    
    
    
}
