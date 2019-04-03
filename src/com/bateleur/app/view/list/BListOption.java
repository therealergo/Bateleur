package com.bateleur.app.view.list;

import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.SettingsModel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class BListOption extends Button {
	private BListTab bListTab;
	
	public final BAudio audio;
	
//	private PlaybackModel playback;
//	private SettingsModel settings;

	public BListOption(BListTab bListTab, boolean isEven, BAudio audio, PlaybackModel playback, SettingsModel settings) {
		this.bListTab = bListTab;
		
		this.audio = audio;
		
//		this.playback = playback;
//		this.settings = settings;
		
		/*
		setMaxHeight(107 * 0.8);
		setMaxWidth (107 * 0.8);
		setPadding(Insets.EMPTY);
		try {
			//setBackground(new Background(new BackgroundImage(audio.get(settings.AUDIO_PROP_ART).getImageBlurred(), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
			
			Vector3D cBG_VEC = audio.get(settings.AUDIO_PROP_COLR_BG);
			Color cBG = new Color(cBG_VEC.x, cBG_VEC.y, cBG_VEC.z, 1.0);
			
			setBackground(new Background(new BackgroundFill(cBG, CornerRadii.EMPTY, Insets.EMPTY)));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		ImageView innerImage = new ImageView();
		try {
			innerImage.setImage(audio.get(settings.AUDIO_PROP_ART).getImageThumbnail());
		} catch (Exception e) {
			e.printStackTrace();
		}
		innerImage.setFitWidth (107.0 * 0.8);
		innerImage.setFitHeight(107.0 * 0.8);
		innerImage.setPreserveRatio(true);
		this.setGraphic(innerImage);
		*/
		
		setMaxHeight(30.0);
		setPrefWidth(10000);
		setPadding(Insets.EMPTY);
		setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
		
		AnchorPane innerAnchor = new AnchorPane();
		setGraphic(innerAnchor);
		
		Pane innerBG = new Pane();
		innerBG.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		innerBG.setEffect(isEven?bListTab.musicListController.master.lightingBG:bListTab.musicListController.master.lightingLI);
		innerBG.setOpacity(0.8);
		AnchorPane.setLeftAnchor  (innerBG, 0.0);
		AnchorPane.setRightAnchor (innerBG, 0.0);
		AnchorPane.setBottomAnchor(innerBG, 0.0);
		AnchorPane.setTopAnchor   (innerBG, 0.0);
		innerAnchor.getChildren().add(innerBG);
		
		Label innerLabelFG = new Label();
		innerLabelFG.setEffect(bListTab.musicListController.master.lightingFG);
		innerLabelFG.setText("    " + audio.get(settings.AUDIO_PROP_TITLE) + " - " + audio.get(settings.AUDIO_PROP_ARTIST));
		if (playback.getLoadedAudio().equals(audio)) {
			innerLabelFG.setText(">> " + innerLabelFG.getText());
		}
		innerLabelFG.setTextFill(Color.WHITE);
		innerLabelFG.setTextAlignment(TextAlignment.LEFT);
		innerLabelFG.setAlignment(Pos.CENTER_LEFT);
		AnchorPane.setLeftAnchor  (innerLabelFG, 0.0);
		AnchorPane.setRightAnchor (innerLabelFG, 0.0);
		AnchorPane.setBottomAnchor(innerLabelFG, 0.0);
		AnchorPane.setTopAnchor   (innerLabelFG, 0.0);
		innerAnchor.getChildren().add(innerLabelFG);
		
		try {
//			setBackground(new Background(new BackgroundImage(audio.get(settings.AUDIO_PROP_ART).getImageBlurred(), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, false, true))));
//			Color cLI = cBG.interpolate(cFG, isEven?0.0:0.1);
//			cLI = cLI.deriveColor(0.0, 1.0, 1.0, 0.8);
			
//			setBackground(new Background(new BackgroundFill(cLI, CornerRadii.EMPTY, Insets.EMPTY)));
//			setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		/*ImageView innerImage = new ImageView();
		try {
			innerImage.setImage(audio.get(settings.AUDIO_PROP_ART).getImageThumbnail());
		} catch (Exception e) {
			e.printStackTrace();
		}
		innerImage.setFitHeight(50.0);
		innerImage.setPreserveRatio(true);
		this.setGraphic(innerImage);*/
		
		setOnMouseClicked((MouseEvent mce) -> {
			onClicked();
		});
	}
	
	public void onClicked() {
		bListTab.onOptionSelected(this);
	}
}
