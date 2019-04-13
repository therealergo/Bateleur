package com.bateleur.app.view;

import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.SettingsModel;
import com.therealergo.main.math.Vector3D;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class BBackgroundCanvas extends Canvas {
	private SettingsModel settings;
	private PlaybackModel playback;
	
	public final DoubleProperty artAlpha;

	public BBackgroundCanvas(SettingsModel settings, PlaybackModel playback) {
		this.settings = settings;
		this.playback = playback;
		
		this.artAlpha = new SimpleDoubleProperty(this, "artAlpha");
		this.artAlpha.addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
            	redraw(getWidth(), getHeight());
            }
        });
	}
	
	private void drawImageCover(GraphicsContext gc, Image im, double dx, double dy, double dw, double dh) {
    	double imWidth  = im.getWidth ();
    	double imHeight = im.getHeight();
    	
    	double scaledWidth  = dw * Math.min(imWidth/dw, imHeight/dh);
    	double scaledHeight = dh * Math.min(imWidth/dw, imHeight/dh);
    	
		gc.drawImage(im, 
				imWidth /2 - scaledWidth /2, 
				imHeight/2 - scaledHeight/2, 
				scaledWidth , 
				scaledHeight, 
				dx, 
				dy, 
				dw, 
				dh
		);
	}
	
	private void drawImageFit(GraphicsContext gc, Image im, double dx, double dy, double dw, double dh) {
    	double imWidth  = im.getWidth ();
    	double imHeight = im.getHeight();
    	
    	double scaledWidth  = imWidth  * Math.min(dw/imWidth, dh/imHeight);
    	double scaledHeight = imHeight * Math.min(dw/imWidth, dh/imHeight);
    	
		gc.drawImage(im, 
				0, 
				0, 
				imWidth, 
				imHeight, 
				dx + dw/2 - scaledWidth /2, 
				dy + dh/2 - scaledHeight/2, 
				scaledWidth , 
				scaledHeight
		);
	}
	
	public void redraw(double width, double height) {
	    GraphicsContext gc = getGraphicsContext2D();
    	Image im = playback.getLoadedAudio().get(settings.AUDIO_PROP_ART).getImagePrimary();
    	Image im_bl = playback.getLoadedAudio().get(settings.AUDIO_PROP_ART).getImageBlurred();
    	
		gc.setGlobalAlpha(1.0);
    	drawImageCover(
    			gc, 
    			im_bl, 
    			0, 
    			0, 
    			width, 
    			height
    	);
		
		Vector3D cBG_VEC = playback.getLoadedAudio().get(settings.AUDIO_PROP_COLR_BG);
		Color cBG = new Color(cBG_VEC.x, cBG_VEC.y, cBG_VEC.z, 1.0);
		((Pane)getParent()).setBackground(new Background(new BackgroundFill(cBG, CornerRadii.EMPTY, Insets.EMPTY)));
		
		double areaWidth  = width                                                  ;
		double areaHeight = height - 107 - settings.get(settings.UI_TITLEBAR_VSIZE);
		double areaOffX   = 0.0                                                    ;
		double areaOffY   = settings.get(settings.UI_TITLEBAR_VSIZE)               ;
		double scaling = settings.get(settings.UI_ART_SCALING);
		if (areaHeight > 0) {
			gc.setGlobalAlpha(Math.min(Math.max(artAlpha.doubleValue(), 0.0), 1.0));
	    	drawImageFit(
	    			gc, 
	    			im, 
	    			(areaWidth  - areaWidth  * scaling) / 2.0 + areaOffX, 
	    			(areaHeight - areaHeight * scaling) / 2.0 + areaOffY, 
	    			areaWidth  * scaling, 
	    			areaHeight * scaling
	    	);
		}
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
