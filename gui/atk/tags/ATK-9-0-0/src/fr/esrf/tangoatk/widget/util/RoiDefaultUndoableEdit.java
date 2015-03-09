/*
 *  Copyright (C) :	2002,2003,2004,2005,2006,2007,2008,2009
 *			European Synchrotron Radiation Facility
 *			BP 220, Grenoble 38043
 *			FRANCE
 * 
 *  This file is part of Tango.
 * 
 *  Tango is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Tango is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Tango.  If not, see <http://www.gnu.org/licenses/>.
 */
 
package fr.esrf.tangoatk.widget.util;

import ij.gui.Roi;

import java.util.Vector;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * class used to undo/redo a <code>Roi</code> operation (like combination,
 * deletion, etc.). To undo a <code>Roi</code> operation, you need :
 * <ul>
 * <li>The concerned <code>AdvancedImagePlus</code></li>
 * <li>The <code>Roi Vector</code>s before operation</li>
 * <li>The validated <code>Roi</code> before operation</li>
 * <li>The handled <code>Roi</code> before operation</li>
 * <li>The <code>Roi Vector</code>s after operation, which are, in fact, the
 * current <code>Roi Vector</code>s of the <code>AdvancedImagePlus</code>
 * </li>
 * <li>The current validated <code>Roi</code></li>
 * <li>The current handled <code>Roi</code></li>
 * </ul>
 * 
 * @author GIRARDOT
 */
public class RoiDefaultUndoableEdit extends AbstractUndoableEdit {

    private AdvancedImagePlus concernedImage;
    private Vector<Roi>       undoRoiVector;
    private Vector<Roi>       undoSelectedRois;
    private Vector<Roi>       undoInnerRois;
    private Vector<Roi>       undoOuterRois;
    private Roi               undoValidatedRoi;
    private Roi               undoHandledRoi;
    private Vector<Roi>       redoRoiVector;
    private Vector<Roi>       redoSelectedRois;
    private Vector<Roi>       redoInnerRois;
    private Vector<Roi>       redoOuterRois;
    private Roi               redoValidatedRoi;
    private Roi               redoHandledRoi;
    private String            presentation = "Roi operation";

    public RoiDefaultUndoableEdit (AdvancedImagePlus concernedImage,
            Vector<Roi> undoRoiVector, Vector<Roi> undoSelectedRois,
            Vector<Roi> undoInnerRois, Vector<Roi> undoOuterRois,
            Roi undoValidatedRoi, Roi undoHandledRoi) {
        this( concernedImage, undoRoiVector, undoSelectedRois, undoInnerRois,
                undoOuterRois, undoValidatedRoi, undoHandledRoi, null );
    }

    public RoiDefaultUndoableEdit (AdvancedImagePlus concernedImage,
            Vector<Roi> undoRoiVector, Vector<Roi> undoSelectedRois,
            Vector<Roi> undoInnerRois, Vector<Roi> undoOuterRois,
            Roi undoValidatedRoi, Roi undoHandledRoi, String presentation) {
        super();
        this.concernedImage = concernedImage;
        this.undoRoiVector = undoRoiVector;
        if (this.undoRoiVector == null) {
            this.undoRoiVector = new Vector<Roi>();
        }
        this.undoSelectedRois = undoSelectedRois;
        if (this.undoSelectedRois == null) {
            this.undoSelectedRois = new Vector<Roi>();
        }
        this.undoInnerRois = undoInnerRois;
        if (this.undoInnerRois == null) {
            this.undoInnerRois = new Vector<Roi>();
        }
        this.undoOuterRois = undoOuterRois;
        if (this.undoOuterRois == null) {
            this.undoOuterRois = new Vector<Roi>();
        }
        this.undoValidatedRoi = undoValidatedRoi;
        this.undoHandledRoi = undoHandledRoi;
        this.redoRoiVector = new Vector<Roi>();;
        this.redoSelectedRois = new Vector<Roi>();
        this.redoInnerRois = new Vector<Roi>();
        this.redoOuterRois = new Vector<Roi>();
        if (this.concernedImage != null) {
            if ( this.concernedImage.getAllRois() != null) {
                this.redoRoiVector.addAll(
                        this.concernedImage.getAllRois()
                );
            }
            if ( this.concernedImage.getSelectedRois() != null) {
                this.redoSelectedRois.addAll(
                        this.concernedImage.getSelectedRois()
                );
            }
            if ( this.concernedImage.getInnerRois() != null) {
                this.redoInnerRois.addAll(
                        this.concernedImage.getInnerRois()
                );
            }
            if ( this.concernedImage.getOuterRois() != null) {
                this.redoOuterRois.addAll(
                        this.concernedImage.getOuterRois()
                );
            }
            this.redoValidatedRoi = this.concernedImage.getValidatedRoi();
            this.redoHandledRoi = this.concernedImage.getRoi();
        }
        if ( presentation != null && !"".equals( presentation.trim() ) ) {
            this.presentation = presentation;
        }
    }

    @Override
    public boolean canUndo () {
        return ( super.canUndo() && concernedImage != null
                && undoRoiVector != null && undoSelectedRois != null
                && undoInnerRois != null && undoOuterRois != null );
    }

    @Override
    public boolean canRedo () {
        return ( super.canRedo() && concernedImage != null
                && redoRoiVector != null && redoSelectedRois != null
                && redoInnerRois != null && redoOuterRois != null );
    }

    @Override
    public void undo () throws CannotUndoException {
        super.undo();
        redoRoiVector.clear();
        if ( concernedImage.getAllRois() != null) {
            redoRoiVector.addAll(
                    concernedImage.getAllRois()
            );
            concernedImage.getAllRois().clear();
            concernedImage.getAllRois().addAll(undoRoiVector);
        }
        redoSelectedRois.clear();
        if ( this.concernedImage.getSelectedRois() != null) {
            redoSelectedRois.addAll(
                    concernedImage.getSelectedRois()
            );
            concernedImage.getSelectedRois().clear();
            concernedImage.getSelectedRois().addAll(undoSelectedRois);
        }
        redoInnerRois.clear();
        if ( concernedImage.getInnerRois() != null) {
            redoInnerRois.addAll(
                    concernedImage.getInnerRois()
            );
            concernedImage.getInnerRois().clear();
            concernedImage.getInnerRois().addAll(undoInnerRois);
        }
        redoOuterRois.clear();
        if ( concernedImage.getOuterRois() != null) {
            redoOuterRois.addAll(
                    concernedImage.getOuterRois()
            );
            concernedImage.getOuterRois().clear();
            concernedImage.getOuterRois().addAll(undoOuterRois);
        }
        redoValidatedRoi = concernedImage.getValidatedRoi();
        concernedImage.setValidatedRoi(undoValidatedRoi);
        redoHandledRoi = concernedImage.getRoi();
        concernedImage.setHandledRoi(undoHandledRoi);
    }

    @Override
    public void redo () throws CannotRedoException {
        super.redo();
        if ( concernedImage.getAllRois() != null) {
            concernedImage.getAllRois().clear();
            concernedImage.getAllRois().addAll(redoRoiVector);
        }
        if ( this.concernedImage.getSelectedRois() != null) {
            concernedImage.getSelectedRois().clear();
            concernedImage.getSelectedRois().addAll(redoSelectedRois);
        }
        if ( concernedImage.getInnerRois() != null) {
            concernedImage.getInnerRois().clear();
            concernedImage.getInnerRois().addAll(redoInnerRois);
        }
        redoOuterRois.clear();
        if ( concernedImage.getOuterRois() != null) {
            concernedImage.getOuterRois().clear();
            concernedImage.getOuterRois().addAll(redoOuterRois);
        }
        concernedImage.setValidatedRoi(redoValidatedRoi);
        concernedImage.setHandledRoi(redoHandledRoi);
    }

    @Override
    public String getPresentationName () {
        return presentation;
    }

    @Override
    public void die () {
        super.die();
        undoRoiVector.clear();
        undoSelectedRois.clear();
        undoInnerRois.clear();
        undoOuterRois.clear();
        undoValidatedRoi = null;
        undoHandledRoi = null;
        undoRoiVector = null;
        undoSelectedRois = null;
        undoInnerRois = null;
        undoOuterRois = null;
        redoRoiVector.clear();
        redoSelectedRois.clear();
        redoInnerRois.clear();
        redoOuterRois.clear();
        redoValidatedRoi = null;
        redoHandledRoi = null;
        redoRoiVector = null;
        redoSelectedRois = null;
        redoInnerRois = null;
        redoOuterRois = null;
        concernedImage = null;
    }

}
