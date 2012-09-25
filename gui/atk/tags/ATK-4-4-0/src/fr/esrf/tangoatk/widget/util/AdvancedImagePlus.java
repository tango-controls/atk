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

import java.awt.Image;
import java.util.Vector;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.process.ImageProcessor;

/**
 * An ImagePlus with multi Roi management.
 * 
 * @author GIRARDOT
 */
public class AdvancedImagePlus extends ImagePlus {

    /**
     * Vector containing all Rois
     */
    protected Vector<Roi> roiVector        = new Vector<Roi>();

    /**
     * Vector containing all selected Rois
     */
    protected Vector<Roi> selectedRois     = new Vector<Roi>();

    /**
     * Vector containing all interior Rois
     */
    protected Vector<Roi> innerRois        = new Vector<Roi>();

    /**
     * Vector containing all exterior Rois
     */
    protected Vector<Roi> outerRois        = new Vector<Roi>();

    /**
     * The only selected interior/exterior Roi
     */
    protected Roi         validatedRoi     = null;

    /**
     * A boolean used to know whether a Roi can be created or not. This is a
     * trick used for selection mode.
     */
    protected boolean     allowRoiCreation = true;

    public AdvancedImagePlus () {
        super();
    }

    public AdvancedImagePlus (String title, Image img) {
        super(title, img);
    }

    public AdvancedImagePlus (String title, ImageProcessor ip) {
        super(title, ip);
    }

    public AdvancedImagePlus (String title, ImageStack stack) {
        super(title, stack);
    }

    public AdvancedImagePlus (String pathOrURL) {
        super(pathOrURL);
    }

    @Override
    public void setImage (Image img) {
        super.setImage(img);
    }

    @Override
    public void setRoi (Roi newRoi) {
        if (newRoi != null) {
            addRoi(newRoi);
            newRoi.setImage(this);
        }
        setHandledRoi(newRoi);
        Roi.previousRoi = null;
    }

    @Override
    public void killRoi () {
        super.killRoi();
        Roi.previousRoi = null;
    }

    public void setSelectedRoi(Roi selectedRoi) {
        if ( !selectedRois.contains(selectedRoi) ) {
            selectedRois.add(selectedRoi);
        }
        setHandledRoi(selectedRoi);
    }

    public void setHandledRoi(Roi handledRoi) {
        this.roi = handledRoi;
        if (getProcessor() != null) {
            getProcessor().setRoi(handledRoi);
        }
    }

    @Override
    public void createNewRoi (int sx, int sy) {
        if ( isAllowRoiCreation() ) {
            super.createNewRoi(sx, sy);
            if (roi != null) {
                addRoi(roi);
            }
        }
    }

    public void addRoi(Roi roi) {
        if ( !roiVector.contains(roi) ) {
            roiVector.add(roi);
        }
    }

    public void removeRoi(Roi roi) {
        roiVector.remove(roi);
        selectedRois.remove(roi);
        innerRois.remove(roi);
        outerRois.remove(roi);
        if ( validatedRoi != null && validatedRoi.equals(roi) ) {
            validatedRoi = null;
        }
        if (getRoi() != null && getRoi().equals(roi) ) {
            setRoi( (Roi)null );
        }
    }

    public Vector<Roi> getAllRois() {
        return roiVector;
    }

    /**
     * @return the selectedRois
     */
    public Vector<Roi> getSelectedRois () {
        return selectedRois;
    }

    /**
     * @param selectedRois the selectedRois to set
     */
    public void setSelectedRois (Vector<Roi> selectedRois) {
        this.selectedRois = selectedRois;
    }

    public void deleteSelectedRois() {
        for (int i = 0; i < getSelectedRois().size(); i++) {
            getAllRois().remove( getSelectedRois().get(i) );
            if ( getRoi() != null
                    &&  getRoi().equals( getSelectedRois().get(i) ) ) {
                killRoi();
                roi = null;
            }
            getInnerRois().remove( getSelectedRois().get(i) );
            getOuterRois().remove( getSelectedRois().get(i) );
            if ( validatedRoi == getSelectedRois().get(i) ) {
                validatedRoi = null;
            }
        }
        getSelectedRois().clear();
        if (getAllRois().size() > 0) {
            roi = getAllRois().get( getAllRois().size() - 1 );
        }
    }

    /**
     * @return the allowRoiCreation
     */
    public boolean isAllowRoiCreation () {
        return allowRoiCreation;
    }

    /**
     * @param allowRoiCreation the allowRoiCreation to set
     */
    public void setAllowRoiCreation (boolean allowRoiCreation) {
        this.allowRoiCreation = allowRoiCreation;
    }

    /**
     * @return the innerRois
     */
    public Vector<Roi> getInnerRois () {
        return innerRois;
    }

    /**
     * @return the outerRois
     */
    public Vector<Roi> getOuterRois () {
        return outerRois;
    }

    /**
     * @return the validatedRoi
     */
    public Roi getValidatedRoi () {
        return validatedRoi;
    }

    /**
     * @param validatedRoi the validatedRoi to set
     */
    public void setValidatedRoi (Roi validatedRoi) {
        this.validatedRoi = validatedRoi;
    }

}
