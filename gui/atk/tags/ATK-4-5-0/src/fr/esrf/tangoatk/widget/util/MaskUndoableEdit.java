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

import ij.ImagePlus;
import ij.process.ImageProcessor;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * class used to undo/redo a Mask. To undo a mask, you need :
 * <ul>
 * <li>The concerned <code>ImagePlus</code></li>
 * <li>The <code>ImageProcessor</code> before mask was applied</li>
 * <li>The <code>ImageProcessor</code> once mask applied, which is, in fact,
 * the current <code>ImageProcessor</code> of the <code>ImagePlus</code></li>
 * </ul>
 * 
 * @author GIRARDOT
 */
public class MaskUndoableEdit extends AbstractUndoableEdit {

    private ImagePlus concernedImage;
    private ImageProcessor undoProcessor;
    private ImageProcessor redoProcessor;
    private String presentation = "Mask";

    /**
     * Standard Contructor
     * 
     * @param concernedImage
     *            the concerned <code>ImagePlus</code>
     * @param undoProcessor
     *            the <code>ImageProcessor</code> before mask was applied
     * @see #MaskUndoableEdit(ImagePlus, ImageProcessor, ImageProcessor, String)
     */
    public MaskUndoableEdit (ImagePlus concernedImage,
            ImageProcessor undoProcessor) {
        this(concernedImage, undoProcessor, null);
    }

    /**
     * Advanced Contructor
     * 
     * @param concernedImage
     *            the concerned <code>ImagePlus</code>
     * @param undoProcessor
     *            the <code>ImageProcessor</code> before mask was applied
     * @param presentation
     *            the presentation <code>String</code> - default: "Mask"
     */
    public MaskUndoableEdit (ImagePlus concernedImage,
            ImageProcessor undoProcessor, String presentation) {
        super();
        this.concernedImage = concernedImage;
        this.undoProcessor = undoProcessor;
        if (this.concernedImage != null) {
            this.redoProcessor = this.concernedImage.getProcessor();
        }
        if ( presentation != null && !"".equals( presentation.trim() ) ) {
            this.presentation = presentation;
        }
    }

    @Override
    public boolean canUndo () {
        return ( super.canUndo() && concernedImage != null
                && undoProcessor != null );
    }

    @Override
    public boolean canRedo () {
        return ( super.canRedo() && concernedImage != null
                && redoProcessor != null );
    }

    @Override
    public void undo () throws CannotUndoException {
        super.undo();
        redoProcessor = concernedImage.getProcessor();
        concernedImage.setProcessor(null, undoProcessor);
    }

    @Override
    public void redo () throws CannotRedoException {
        super.redo();
        concernedImage.setProcessor(null, redoProcessor);
    }

    @Override
    public String getPresentationName () {
        return presentation;
    }

    @Override
    public void die () {
        boolean canUndo = canUndo();
        boolean canRedo = canRedo();
        super.die();
        if (canUndo) {
            // redoProcessor is used, undoProcessor is not used
            undoProcessor.setPixels(null);
        }
        else if (canRedo) {
            // undoProcessor is used, redoProcessor is not used
            redoProcessor.setPixels(null);
        }
        undoProcessor = null;
        redoProcessor = null;
        concernedImage = null;
    }

}
