package org.pneditor.petrinet.models.SOUKRAT_ELBAGHAZAOUI.source;


public class ArcZero extends ArcInTransition {
	
	
	public ArcZero(Place source, Transition destination) {
		super(1,source,destination);
		// TODO Auto-generated constructor stub
	}
        
	public boolean isActive() {
		return(this.getSource().getNbrTokens() == 0);
	}
	
	//do nothing
	public void execute() {
		
	}
	
}

	
	
	


