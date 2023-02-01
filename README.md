PNEditor (Petri Net editor)
========

The original PNE can be downloaded from [www.pneditor.org](http://www.pneditor.org/)

This instance is the result of a student project by Joris Thaveau for teaching purpose.

It is a simplified Petri net editor that allows the editing of many PetriNet models.

To use:

1. Run org.pneditor.editor.Main as a Java application
2. Select the model used (the menu scans the org.pneditor.petrinet.adapters folder to build a list of available models and adapters). initial and imta are available. Places and transitions are displayed in different ways.
3. Edit the PetriNet and fire transitions.

You may experiment some unexpected exceptions. Especially if you mix models.

The pedagogical approach consists in:

1. Develop your own PetriNet model in an independent project/environment - with no GUI, just the ''business'' view
2. Pack it as a jar, and let it be visible in the path
3. Develop an Adapter in the org.pneditor.petrinet.adapters folder of PNE to make your model editable

The adapter may be simple or complex depending on the "distance" between your model and the one expected by PNE.

Code license: [GNU GPL v3](http://www.gnu.org/licenses/gpl.html)

Requirements: Java SE 8+
