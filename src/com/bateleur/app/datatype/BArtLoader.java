package com.bateleur.app.datatype;

import java.io.Serializable;

import javafx.scene.image.Image;

public abstract class BArtLoader implements Serializable {
	private static final long serialVersionUID = -295078939661918275L;
	
	public abstract Image getImage() throws Exception;
}
