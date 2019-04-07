package com.bateleur.app.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;

public class BSliderCanvas extends Canvas {
	public final ObjectProperty<Color> drawColor;
	
	public BSliderCanvas() {
		drawColor = new SimpleObjectProperty<Color>();
	}
	
	public void redraw(double width, double height) {
	    GraphicsContext gc = getGraphicsContext2D();
	    
	    Slider slider = (Slider) getParent().getChildrenUnmodifiable().get(0);
	    double sliderVal = (slider.getValue() - slider.getMin()) / (slider.getMax() - slider.getMin());
	    if (Double.isNaN(sliderVal)) {
	    	sliderVal = 0.0;
	    }
	    
	    double seekHeadEdging = 5;
	    double seekHeadRadius = 7;
	    double seekHeadDrawRadius = 5;
	    double sliderPos = seekHeadRadius + sliderVal * (width - 2 * seekHeadRadius);
	    double lineHeight = (int) (height/2);
	    
	    gc.clearRect(0, 0, width, height);
	    gc.setLineWidth(2);
	    gc.setStroke(drawColor.getValue());
	    if (sliderPos - seekHeadRadius - seekHeadEdging > 0) {
		    gc.strokeLine(
		    		0, 
		    		lineHeight, 
		    		sliderPos - seekHeadRadius - seekHeadEdging, 
		    		lineHeight
		    );
	    }
	    if (sliderPos + seekHeadRadius + seekHeadEdging < width) {
		    gc.strokeLine(
		    		sliderPos + seekHeadRadius + seekHeadEdging, 
		    		lineHeight, 
		    		width, 
		    		lineHeight
		    );
	    }
	    gc.strokeOval(sliderPos-seekHeadDrawRadius, height/2-seekHeadDrawRadius, seekHeadDrawRadius*2, seekHeadDrawRadius*2);
	}
	
	@Override
	public boolean isResizable() {
	    return true;
	}
	
	@Override
	public void resize(double width, double height) {
	    super.setWidth(width);
	    super.setHeight(height);
	    
	    redraw(width, height);
	}
}
