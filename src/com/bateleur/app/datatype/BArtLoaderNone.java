package com.bateleur.app.datatype;

import com.bateleur.app.App;
import com.therealergo.main.Main;

import javafx.scene.image.Image;

public class BArtLoaderNone extends BArtLoader {
	private static final long serialVersionUID = -4271906009974761079L;
	
	public Image getImagePrimary() throws Exception {
		return new Image(Main.resource.getResourceFileClass("textures>none.png", App.class).getInputStream());
	}
	
	public Image getImageThumbnail() throws Exception {
		return new Image(Main.resource.getResourceFileClass("textures>none.png", App.class).getInputStream());
	}
	
	public Image getImageBlurred() throws Exception {
		return new Image(Main.resource.getResourceFileClass("textures>none.png", App.class).getInputStream());
	}
}
