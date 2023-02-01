package org.pneditor;

import java.io.File;

public final class PNEConstantsConfiguration {
	
	/*
	 * WARNING : resources path have a "/" separator not a File.seprator.
	 * (Whatever the OS is)
	 */

  private PNEConstantsConfiguration() {
    throw new AssertionError();
  }

  /*
   * resources/
   * └── pneditor
   *     ├── canvas
   *     ├── filechooser
   * */
  public static final String RESOURCESDIR = "/"+"resources"+"/";//
  public static final String PNEDITOR_RESOURCES = RESOURCESDIR+"pneditor"+"/";
  public static final String CANVAS_RESOURCES = PNEDITOR_RESOURCES+"canvas"+"/";
  public static final String FILECHOOSER_RESOURCES = PNEDITOR_RESOURCES+"filechooser"+"/";

  /*
   * resources/
   * */
  public static final String ICON16 = RESOURCESDIR+"icon16.png"; 
  public static final String ICON32 = RESOURCESDIR+"icon32.png";
  public static final String ICON48 = RESOURCESDIR+"icon48.png";

  /*
   * resources/
   * └── pneditor
   * */
  public static final String ABOUT16                   = PNEDITOR_RESOURCES+"About16.gif";
  public static final String ARC16                     = PNEDITOR_RESOURCES+"arc16.gif";
  public static final String CONVERTTRANSITIONTOSUBNET = PNEDITOR_RESOURCES+"converttransitiontosubnet.gif";
  public static final String COPY16                    = PNEDITOR_RESOURCES+"Copy16.gif";
  public static final String CUT16                     = PNEDITOR_RESOURCES+"Cut16.gif";
  public static final String DELETE16                  = PNEDITOR_RESOURCES+"Delete16.gif";
  public static final String EXPORT                    = PNEDITOR_RESOURCES+"export.gif";
  public static final String IMPORT                    = PNEDITOR_RESOURCES+"import.gif";
  public static final String LABEL                     = PNEDITOR_RESOURCES+"label.gif";
  public static final String MULTIPLICITY              = PNEDITOR_RESOURCES+"multiplicity.gif";
  public static final String NEW16                     = PNEDITOR_RESOURCES+"New16.gif";
  public static final String OPEN16                    = PNEDITOR_RESOURCES+"Open16.gif";
  public static final String PASTE16                   = PNEDITOR_RESOURCES+"Paste16.gif";
  public static final String PLACE16                   = PNEDITOR_RESOURCES+"place16.gif";
  public static final String REDO16                    = PNEDITOR_RESOURCES+"Redo16.gif";
  public static final String SAVE16                    = PNEDITOR_RESOURCES+"Save16.gif";
  public static final String SAVEAS16                  = PNEDITOR_RESOURCES+"SaveAs16.gif";
  public static final String SELECT                    = PNEDITOR_RESOURCES+"select.gif";
  public static final String SETARCRESETACTION         = PNEDITOR_RESOURCES+"setarcresetaction.gif";
  public static final String TOKENS                    = PNEDITOR_RESOURCES+"tokens.gif";
  public static final String TOKEN_AND_FIRE16          = PNEDITOR_RESOURCES+"token_and_fire16.gif";
  public static final String TRANSITION16              = PNEDITOR_RESOURCES+"transition16.gif";
  public static final String UNDO16                    = PNEDITOR_RESOURCES+"Undo16.gif";

  /*
   * resources/
   * └── pneditor
   *     ├── canvas
   * */
  public static final String ARC           = CANVAS_RESOURCES+"arc.gif";
  public static final String FIRE          = CANVAS_RESOURCES+"fire.gif";
  public static final String PLACE         = CANVAS_RESOURCES+"place.gif";
  public static final String TOKEN         = CANVAS_RESOURCES+"token.gif";
  public static final String TOKEN_OR_FIRE = CANVAS_RESOURCES+"token_or_fire.gif";
  public static final String TRANSITION    = CANVAS_RESOURCES+"transition.gif";
  
  /*
   * resources/
   * └── pneditor
   *     ├── filechooser
   * */
  public static final String EPS   = FILECHOOSER_RESOURCES+"eps.gif";
  public static final String PFLOW = FILECHOOSER_RESOURCES+"pflow.gif";
  public static final String PNG   = FILECHOOSER_RESOURCES+"png.gif";
  public static final String PNML  = FILECHOOSER_RESOURCES+"pnml.gif";

}
