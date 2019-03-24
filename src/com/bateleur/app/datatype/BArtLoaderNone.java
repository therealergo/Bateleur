package com.bateleur.app.datatype;

import com.bateleur.app.App;
import com.therealergo.main.Main;

import javafx.scene.image.Image;

public class BArtLoaderNone extends BArtLoader {
	private static final long serialVersionUID = -4271906009974761078L;
	
	public Image getImage() throws Exception {
		return new Image(Main.resource.getResourceFileClass("none.png", App.class).getInputStream());
	}
}
