package com.bateleur.app.view;

import java.util.Iterator;
import java.util.LinkedList;

import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.model.LibraryModel;
import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.SettingsModel;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class BBackgroundCanvas extends Canvas {
	private SettingsModel settings;
	
	public final DoubleProperty artAlpha;

	private long lastUpdate;
	private final AnimationTimer imageAnimation;
	private final LinkedList<BackgroundImage> imageList;

	public BBackgroundCanvas(SettingsModel settings, PlaybackModel playback, LibraryModel library) {
		this.settings = settings;
		
		this.artAlpha = new SimpleDoubleProperty(this, "artAlpha");
		this.artAlpha.addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
            	redraw(getWidth(), getHeight());
            }
        });
		
		imageAnimation = new AnimationTimer() {
			@Override public void handle(long now) {
				Iterator<BackgroundImage> imageIterator;
				
				// Compute an exponential decay base dependent on how long it has been since the last animation frame
				long dt = now - lastUpdate;
				lastUpdate = System.nanoTime();
				double baseMul = Math.pow(0.9999, dt * 0.0001 / settings.get(settings.UI_ANIM_TIME_MUL));
				
				// Fade in the frontmost image
		    	imageList.getLast().fade = imageList.getLast().fade * baseMul + (1.0-baseMul);
		    	
		    	// Grow in the frontmost image
		    	imageList.getLast().size = imageList.getLast().size * baseMul + (1.0-baseMul);
		    	
		    	// Shrink/grow out the back images
		    	imageIterator = imageList.descendingIterator();
		    	imageIterator.next();
		    	while (imageIterator.hasNext()) {
		    		BackgroundImage img = imageIterator.next();
		    		img.size = img.size * baseMul + (1.0-baseMul) * settings.get(settings.UI_SONG_ANIM_OSIZE);
		    	}
		    	
		    	// Remove any images that are more than 99% obscured
		    	double attenuation = 1.0;
		    	imageIterator = imageList.descendingIterator();
		    	while (imageIterator.hasNext()) {
		    		BackgroundImage img = imageIterator.next();
		    		if (attenuation < 0.01) {
		    			imageIterator.remove();
		    		} else {
			    		attenuation = attenuation * (1.0-img.fade);
		    		}
		    	}
		    	
		    	// If there's only 1 image left, we can stop the animation
		    	if (imageList.size() == 1) {
		    		imageList.getLast().fade = 1.0;
		    		imageList.getLast().size = 1.0;
					imageAnimation.stop();
		    	}
		    	
		    	// Redraw the entire canvas
				redraw(getWidth(), getHeight());
			}
		};
		
		this.imageList = new LinkedList<BackgroundImage>();
		playback.onSongChangeEvent.addListener(() -> {
			new Thread("Image Load Thread") {
				public void run() {
					BAudio newLoadedAudio = library.getByReference(playback.getLoadedAudio());
			    	Image image         = newLoadedAudio.get(settings.AUDIO_META_ARTLOAD).getImagePrimary(settings, newLoadedAudio);
			    	Image image_blurred = newLoadedAudio.get(settings.AUDIO_META_ARTLOAD).getImageBlurred(settings, newLoadedAudio);
			    	Platform.runLater(() -> {
						imageList.add(new BackgroundImage(image, image_blurred));
						lastUpdate = System.nanoTime();
						imageAnimation.start();
			    	});
				}
			}.start();
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
    	
    	imageList.forEach((BackgroundImage image) -> {
    		image.draw(gc, width, height);
    	});
	}
	
	@Override public boolean isResizable() {
	    return true;
	}
	
	@Override public void resize(double width, double height) {
	    super.setWidth(width);
	    super.setHeight(height);
	    
	    redraw(width, height);
	}
	
	private class BackgroundImage {
		private Image image;
		private Image image_blurred;
		private double fade;
		private double size;
		
		BackgroundImage(Image image, Image image_blurred) {
			this.image = image;
			this.image_blurred = image_blurred;
			this.fade = 0.0;
			this.size = settings.get(settings.UI_SONG_ANIM_ISIZE);
		}
		
		private void draw(GraphicsContext gc, double width, double height) {
			gc.setGlobalAlpha(fade);
	    	drawImageCover(
	    			gc, 
	    			image_blurred, 
	    			0, 
	    			0, 
	    			width, 
	    			height
	    	);
	    	
			double areaWidth  = width                                                  ;
			double areaHeight = height - 107 - settings.get(settings.UI_TITLEBAR_VSIZE);
			double areaOffX   = 0.0                                                    ;
			double areaOffY   = settings.get(settings.UI_TITLEBAR_VSIZE)               ;
			double scaling    = settings.get(settings.UI_ART_SCALING) * size           ;
			if (areaHeight > 0) {
				gc.setGlobalAlpha(fade * Math.min(Math.max(artAlpha.doubleValue(), 0.0), 1.0));
		    	drawImageFit(
		    			gc, 
		    			image, 
		    			(areaWidth  - areaWidth  * scaling) / 2.0 + areaOffX, 
		    			(areaHeight - areaHeight * scaling) / 2.0 + areaOffY, 
		    			areaWidth  * scaling, 
		    			areaHeight * scaling
		    	);
			}
		}
	}
}
