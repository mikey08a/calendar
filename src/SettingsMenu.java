package calendar;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SettingsMenu extends VBox{
    
    public SettingsMenu(){
        init();
    }
    
    private void init(){
        setStyle("-fx-background-color: lightgrey");
        HBox title = new HBox();
        Button back = new Button();
        title.setBorder(new Border(new BorderStroke(Color.BLACK, 
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        title.setBackground(new Background(new BackgroundFill(Color.WHITE,
        CornerRadii.EMPTY,new Insets(0,0,0,0))));
        Label settings = new Label("Settings");
        settings.setGraphicTextGap(40);
        settings.setFont(new Font(30));
        ImageView iv = new ImageView(new Image(getClass()
                .getResourceAsStream("back.png")));
        iv.setFitHeight(50);
        iv.setFitWidth(50);
        back.setGraphic(iv);
        back.setBackground(Background.EMPTY);
        back.setOnMouseClicked(e -> exit());
        title.getChildren().addAll(back,settings);
        HBox panel = new HBox();
        VBox headers = new VBox();
        addHeaders(headers);
        ScrollPane menuList = new ScrollPane();
        VBox menu = new VBox();
        menuList.setContent(menu);
        panel.getChildren().addAll(headers,menuList);
        getChildren().addAll(title,panel);
        
    }
    private void addHeaders(VBox h){
        Label acct = new Label("Account Settings");
        Label general = new Label("General Setings");
        h.getChildren().addAll(acct,general);
    }

    private void exit() {
        Calendar.root.getChildren().remove(this);
    }
}
