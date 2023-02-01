package org.pneditor.petrinet;

@SuppressWarnings("serial")
public class ResetArcMultiplicityException extends Exception {

	public ResetArcMultiplicityException() {
        super("A reset arc can not have multiplicity. This exception should never happen");
    }
}
