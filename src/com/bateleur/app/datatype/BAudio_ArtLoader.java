package com.bateleur.app.datatype;

import java.io.Serializable;

import com.bateleur.app.App;
import com.therealergo.main.Main;

import javafx.scene.image.Image;

public class BAudio_ArtLoader implements Serializable {
	private static final long serialVersionUID = -295078939661918276L;
	
	/**
	 * Retrieve the primary, full-resolution Image that this BArtLoader represents.
	 * This image is typically only loaded onto disk at this point, 
	 * so the supplied Image instance may well be asynchronously loaded in the background.
	 * By default, this will return a generic image that represents 'no image found'.
	 * This behavior will generally be overridden by subclasses that provide images using their own methods.
	 * @return This BArtLoader's primary Image.
	 */
	public Image getImagePrimary(BReference parentReference) {
		return new Image(Main.resource.getResourceFileClass("textures>none_TH.png", App.class).getInputStream());
	}
	
	/**
	 * Retrieve a low-resolution thumbnail of the Image that this BArtLoader represents.
	 * The vertical resolution of this Image will typically be defined by the height of the Bateleur playback bar.
	 * This is done so that the thumbnail will appear sharp when displayed as a thumbnail in that bar.
	 * By default, this will return a generic image that represents 'no image found'.
	 * This behavior will generally be overridden by subclasses that provide images using their own methods.
	 * @return This BArtLoader's thumbnail Image.
	 */
	public Image getImageThumbnail(BReference parentReference) {
		return new Image(Main.resource.getResourceFileClass("textures>none_TH.png", App.class).getInputStream());
	}
	
	/**
	 * Retrieve a blurred version of the Image that this BArtLoader represents.
	 * No *guarantee* is given regarding the returned Image's width, height, or level of blurriness.
	 * However, this image is generally used as a background, 
	 * and as such should be blurred enough to appear behind UI components without becoming distracting.
	 * By default, this will return a generic image that represents 'no image found'.
	 * This behavior will generally be overridden by subclasses that provide images using their own methods.
	 * @return This BArtLoader's thumbnail Image.
	 */
	public Image getImageBlurred(BReference parentReference) {
		return new Image(Main.resource.getResourceFileClass("textures>none_BL.png", App.class).getInputStream());
	}
}
