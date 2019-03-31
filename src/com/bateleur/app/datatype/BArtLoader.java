package com.bateleur.app.datatype;

import java.io.Serializable;

import javafx.scene.image.Image;

public abstract class BArtLoader implements Serializable {
	private static final long serialVersionUID = -295078939661918276L;
	
	public abstract Image getImagePrimary() throws Exception;
	
	public abstract Image getImageThumbnail() throws Exception;
	
	public abstract Image getImageBlurred() throws Exception;
}
