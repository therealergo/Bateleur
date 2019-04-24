package com.bateleur.app.view.list;

import com.therealergo.main.MainException;
import com.therealergo.main.NilConsumer;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public class BListOptionSetting_Button extends BListOptionSetting {
	private final int tabbing;
	
	private final String name;
	private final String text;
	
	private final NilConsumer action;
	
	public BListOptionSetting_Button(BListTab bListTab, int tabbing, String name, String text, NilConsumer action) {
		super(bListTab);

		if (tabbing < 0) {
			throw new MainException(BListOptionSetting_Button.class, "BListOptionSetting_Button tabbing must be >0!");
		}
		if (name == null) {
			throw new MainException(BListOptionSetting_Button.class, "BListOptionSetting_Button name cannot be null!");
		}
		if (text == null) {
			throw new MainException(BListOptionSetting_Button.class, "BListOptionSetting_Button text cannot be null!");
		}
		if (action == null) {
			throw new MainException(BListOptionSetting_Button.class, "BListOptionSetting_Button action cannot be null!");
		}
		
		this.tabbing = tabbing;
		
		this.name = name;
		this.text = text;
		
		this.action = action;
	}
	
	@Override public int getTabbing() {
		return tabbing;
	}
	
	@Override public String getName() {
		return name;
	}
	
	@Override public Region buildControl() {
		// Create and style the button itself
		AnchorPane baseAnchor = new AnchorPane();
		Button controlButton = new Button();
		controlButton.getStyleClass().add("playbackBarButton");
		controlButton.setPadding(new Insets(2.0, 2.0, 2.0, 2.0));
		controlButton.setText(text);
		controlButton.setTextFill(Color.WHITE);
		controlButton.setOpacity(0.3);
		AnchorPane.setBottomAnchor(controlButton, 4.0);
		AnchorPane.setLeftAnchor  (controlButton, 0.0);
		AnchorPane.setRightAnchor (controlButton, 0.0);
		AnchorPane.setTopAnchor   (controlButton, 4.0);
		baseAnchor.getChildren().add(controlButton);
		
		// Setup the opacity change on button hover
		controlButton.setOnMouseEntered((MouseEvent event) -> {
			controlButton.setOpacity(1.0);
		});
		controlButton.setOnMouseExited ((MouseEvent event) -> {
			controlButton.setOpacity(0.3);
		});
		
		// Setup the action on button press
		controlButton.setOnAction((ActionEvent e) -> {
			action.accept();
		});
		
		return baseAnchor;
	}
}
