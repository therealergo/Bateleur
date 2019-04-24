package com.bateleur.app.view.list;

import com.bateleur.app.datatype.BFile;
import com.bateleur.app.datatype.BFile.Entry;
import com.bateleur.app.view.BSliderCanvas;
import com.therealergo.main.MainException;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class BListOptionSetting_DoubleSlider extends BListOptionSetting {
	private final Entry<Double> setting;
	private final double minVal;
	private final double maxVal;
	
	private Label valueLabel;
	
	public BListOptionSetting_DoubleSlider(BListTab bListTab, BFile.Entry<Double> setting, double minVal, double maxVal) {
		super(bListTab);
		
		if (setting == null) {
			throw new MainException(BListOptionSetting_DoubleSlider.class, "Supplied setting cannot be null!");
		}
		
		this.setting = setting;
		this.minVal = minVal;
		this.maxVal = maxVal;
	}
	
	@Override public int getTabbing() {
		return 2;
	}
	
	@Override public String getName() {
		// Start with the setting's own name
		String internalName = setting.key;
		
		// Add a space before each capital letter
		for (int i = 0; i<internalName.length(); i++) {
			if (Character.isUpperCase(internalName.charAt(i))) {
				internalName = internalName.substring(0, i) + ' ' + internalName.substring(i);
				i++;
			}
		}
		
		// Remove text before the first capital letter
		for (int i = 0; i<internalName.length(); i++) {
			if (Character.isUpperCase(internalName.charAt(i))) {
				internalName = internalName.substring(i);
				break;
			}
		}
		
		// Return the result
		return internalName;
	}

	@Override public Region buildControl() {
		HBox baseSplit = new HBox();
		
		valueLabel = new Label();
		valueLabel.setTextFill(Color.WHITE);
		valueLabel.setText(String.format("%3.1f   ", bListTab.musicListController.master.settings.get(setting)));
		valueLabel.setPrefWidth(50.0);
		valueLabel.prefHeightProperty().bind(baseSplit.heightProperty());
		valueLabel.setTextAlignment(TextAlignment.RIGHT);
		valueLabel.setAlignment(Pos.CENTER_RIGHT);
		baseSplit.getChildren().add(valueLabel);
		
		AnchorPane controlAnchor = new AnchorPane();
		controlAnchor.prefWidthProperty().bind(baseSplit.widthProperty().subtract(50.0));
		baseSplit.getChildren().add(controlAnchor);
		
		Slider internalSlider = new Slider();
		internalSlider.setMin(minVal);
		internalSlider.setMax(maxVal);
		internalSlider.setValue(bListTab.musicListController.master.settings.get(setting));
		internalSlider.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
			bListTab.musicListController.master.settings.set(setting.to(new_val.doubleValue()));
			valueLabel.setText(String.format("%3.1f   ", bListTab.musicListController.master.settings.get(setting)));
		});
		internalSlider.setOpacity(0.0);
		AnchorPane.setBottomAnchor(internalSlider, 0.0);
		AnchorPane.setLeftAnchor  (internalSlider, 0.0);
		AnchorPane.setRightAnchor (internalSlider, 0.0);
		AnchorPane.setTopAnchor   (internalSlider, 0.0);
		controlAnchor.getChildren().add(internalSlider);
		
		BSliderCanvas internalSliderCanvas = new BSliderCanvas();
		internalSliderCanvas.setMouseTransparent(true);
		AnchorPane.setBottomAnchor(internalSliderCanvas, 0.0);
		AnchorPane.setLeftAnchor  (internalSliderCanvas, 0.0);
		AnchorPane.setRightAnchor (internalSliderCanvas, 0.0);
		AnchorPane.setTopAnchor   (internalSliderCanvas, 0.0);
		controlAnchor.getChildren().add(internalSliderCanvas);
		
		return baseSplit;
	}
}
