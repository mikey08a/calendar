package calendar;

import calendar.Day.Flag;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.beans.binding.Binding;
import java.sql.*;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;

public class Calendar extends Application {
    
    HashMap colors,images,dimens;
    private BorderPane borderPane, menu;
    private Scene scene;
    public static StackPane root;
    private boolean menuExpanded;
    
    @Override
    public void start(Stage stage) {
        
        //Set up window
        root = new StackPane();
        borderPane = new BorderPane();
        root.getChildren().add(borderPane);
        
        scene = new Scene(root, 1000,600);
        //initColors();
        images = new HashMap();
        loadImages();
        setupLayout(borderPane);
        
        stage.setTitle("My Calendar");
        stage.setScene(scene);
        stage.show();
    }
    
    private void initColors(){
        Stop[] stops = new Stop[] { new Stop(0, Color.BLACK), new Stop(1, Color.RED)};
        LinearGradient lg1 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
        colors.put("userGrad",lg1);
    }
    
    private void loadImages(){
        //Load image resources into the project
        InputStream temp = getClass().getResourceAsStream("settings_black.png");
        Image img = new Image(temp,20,20,false,false);
        images.put("settings",img);
        temp = getClass().getResourceAsStream("android.png");
        images.put("icon", new Image(temp));
        temp = getClass().getResourceAsStream("buffer.png");
        images.put("buffer",new Image(temp));
        images.put("profile grad", "-fx-background-color: linear-gradient"
                + "(to bottom right,lightgreen, green)");
        images.put("trans background", "-fx-background-color: TRANSPARENT");
        temp = getClass().getResourceAsStream("month.png");
        images.put("month",new Image(temp,20,20,false,false));
        temp = getClass().getResourceAsStream("week.png");
        images.put("week",new Image(temp,20,20,false,false));
        temp = getClass().getResourceAsStream("day.png");
        images.put("day", new Image(temp,20,20,false,false));
        temp = getClass().getResourceAsStream("meu.png");
        images.put("menu",new Image(temp,20,20,false,false));
    }
    
    private void setupLayout(BorderPane p){
        menuExpanded = true;
        drawMenu(menuExpanded);
        p.setLeft(menu);
        borderPane.setCenter(new MonthView());
        //p.setCenter(iv);
        
    }
    
    private void monthClick(){
        borderPane.setCenter(new MonthView());
        menu.toFront();
    }
    
    private void weekClick(){
        borderPane.setCenter(new WeekView());
    }
    private void dayClick(){
        Date date = new Date();
        borderPane.setCenter(new Day(date, Flag.DAILY));
    }
    
    private Canvas addDivider(int w){
        Canvas canvas = new Canvas(w,3);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.strokeLine(5,0,canvas.getWidth()-5,0);
        return canvas;
    }
    
    public void launchSettings(){
        root.getChildren().add(new SettingsMenu());
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    private void drawMenu(boolean expanded){
        
        if(borderPane.getChildren().contains(menu)){
            borderPane.getChildren().remove(menu);
        }
        menu = new BorderPane();
        menu.getChildren().clear();
        ScrollPane main = new ScrollPane();
        menu.setStyle("-fx-border-color: blue;"
                + "-fx-background-color: white;"
                + "-fx-focus-color: transparent");
        menu.requestFocus();
        menu.toFront();
        main.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        main.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox content = new VBox();
        VBox bottom = new VBox();
        content.setSpacing(10);
        content.setAlignment(Pos.CENTER);
        
        //Add Menu Items
        Label month = new Label();
        month.setOnMouseClicked(e -> monthClick());
        
        Label week = new Label();
        week.setOnMouseClicked(e -> weekClick());
        
        Label day = new Label();
        day.setOnMouseClicked(e -> dayClick());
        
        Label settings = new Label();
        
        InputStream is=getClass().getResourceAsStream("android.png");
        
        Button menuHandle = new Button();
        menuHandle.setGraphic(new ImageView((Image) images.get("menu")));
        menuHandle.setStyle("-fx-background-color: transparent;"
                + "-fx-border-color: transparent;"
                + "-fx-focused-color: transparent");
        menuHandle.setPadding(new Insets(0,0,0,0));
        
        StackPane sPane = new StackPane();
        
        ImageView iv;
        
        //Draw menu based on expanded flag
        if(expanded){
            month.setText("Month");
            month.setPadding(new Insets(0,0,0,15));
            month.setFont(new Font(18));
            month.setGraphic(new ImageView((Image) images.get("month")));

            week.setText("Week");
            week.setPadding(new Insets(0,0,0,15));
            week.setGraphic(new ImageView((Image) images.get("week")));
            week.setFont(new Font(18));

            day.setText("Day");
            day.setGraphic(new ImageView((Image) images.get("day")));
            day.setPadding(new Insets(0,0,0,15));
            day.setFont(new Font(18));

            sPane.setStyle((String) images.get("profile grad"));

            settings.setText("Settings");
            settings.setOnMouseClicked(e -> launchSettings());
            settings.setStyle((String) images.get("trans background"));
            settings.setGraphic(new ImageView((Image) images.get("settings")));
            StackPane.setAlignment(settings,Pos.TOP_RIGHT);
            
            StackPane.setAlignment(menuHandle, Pos.TOP_RIGHT);
            
            IconHolder ih = new IconHolder(is,50);
            iv = new ImageView(ih.getCanvas());
            
            sPane.getChildren().addAll(iv,menuHandle);
            content.getChildren().addAll(sPane,month,week,day);
        }else{
            month.setGraphic(new ImageView((Image) images.get("month")));
            week.setGraphic(new ImageView((Image) images.get("week")));
            day.setGraphic(new ImageView((Image) images.get("day")));
            settings.setStyle((String) images.get("trans background"));
            settings.setGraphic(new ImageView((Image) images.get("settings")));
            settings.setOnMouseClicked(e -> launchSettings());
            IconHolder ih = new IconHolder(is,15);
            iv = new ImageView(ih.getCanvas());
            sPane.getChildren().add(iv);
            content.getChildren().addAll(menuHandle,sPane,month,week,day);
        }
        
        menuHandle.setOnMouseClicked((MouseEvent event) -> {
            menuExpanded = !menuExpanded;
            drawMenu(menuExpanded);
        });
        
        //Show Content
        main.setContent(content);
        bottom.getChildren().add(settings);
        menu.setTop(main);
        menu.setBottom(bottom);
        borderPane.setLeft(menu);
    }
    
}
