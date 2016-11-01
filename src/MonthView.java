package calendar;

import calendar.Day.Flag;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.MXNodeAlgorithm;
import com.sun.javafx.jmx.MXNodeAlgorithmContext;
import com.sun.javafx.sg.prism.NGNode;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class MonthView extends StackPane{
    
    private GridPane grid;
    private Calendar cal;
    private int numRows;
    public int FLAG_EXPANDED = 0;
    private Label month;
    private double sourceX;
    private boolean dragged,next,prev;
    
    public MonthView(){
        init();
    } 
    private void init(){
        
        //Set up layout
        VBox layout = new VBox();
        GridPane calHolder = new GridPane();
        ColumnConstraints cc1 = new ColumnConstraints();
        RowConstraints rc = new RowConstraints();
        HBox title = new HBox();
        Date date = new Date();
        cal = Calendar.getInstance();
        month = new Label("Month");
        Button prevMonth = new Button();
        Button nextMonth = new Button();
        grid = new GridPane();
        
        //Set up components
        cal.setTime(date);
        
        VBox.setVgrow(calHolder, Priority.ALWAYS);
        rc.setPercentHeight(100);
        cc1.setPercentWidth(100);
        calHolder.getColumnConstraints().add(cc1);
        calHolder.getRowConstraints().add(rc);
        calHolder.setId("calHolder");
        touchEvents(calHolder);
        
        setAlignment(Pos.CENTER);
        
        month.setFont(new Font(26));
        month.setStyle("-fx-font-weight: bold");
        
        prevMonth.setGraphic(new ImageView(new Image(
                getClass().getResourceAsStream("prev.png"))));
        prevMonth.setStyle("-fx-background-color: transparent");
        prevMonth.setOnMouseClicked(e -> prevMonth());
        
        nextMonth.setGraphic(new ImageView(new Image(
                getClass().getResourceAsStream("next.png"))));
        nextMonth.setStyle("-fx-background-color: transparent");
        nextMonth.setOnMouseClicked(e -> nextMonth());
        
        title.setAlignment(Pos.CENTER);
        title.getChildren().addAll(prevMonth,month,nextMonth);
        
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);
        for(int x=0;x<7;x++){
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100/7);
            grid.getColumnConstraints().add(cc);
        }
        
        //Add components
        calHolder.add(grid, 0, 0);
        layout.getChildren().addAll(title,calHolder);
        getChildren().add(layout);
        update();
    }
    private void addDays(int start, int end, int month, int year){
        int count = 0;
        int row=1;
        int offset = 0;
        int d = 0;
        
        //Set offset to start months on correct day
        switch(start){
            case(Calendar.MONDAY):
                offset = 0;
                break;
            case(Calendar.TUESDAY):
                offset = 1;
                break;
            case(Calendar.WEDNESDAY):
                offset = 2;
                break;
            case(Calendar.THURSDAY):
                offset = 3;
                break;
            case(Calendar.FRIDAY):
                offset = 4;
                break;
            case(Calendar.SATURDAY):
                offset = 5;
                break;
            default:
                offset = 6;
                break;
        }
        
        //Start the grid with the last days of the last month
        cal.set(Calendar.MONTH,month-1);
        for(int i=offset;i>0;i--,d++){
            Date date = new GregorianCalendar(year, month-1
                    , cal.getActualMaximum(Calendar.DAY_OF_MONTH)-i)
                    .getTime();
            grid.add(new Day(date,Flag.NOFOCUS),d,row);
        }
        //Finish the first row after the previous month
        for(int i = offset;i<7;i++){
            count++;
            if (count >= end) {
                break;
            } else {
                Date date = new GregorianCalendar(year, month, count).getTime();
                if(cal.get(Calendar.DAY_OF_MONTH)==count){
                    grid.add(new Day(date,Flag.CURR),i,row);
                }else{
                    grid.add(new Day(date), i, row);
                }
            }
            }
            row++;
            
        //Add the rest of the month
        while(count<end){
            for(int i = 0;i<7;i++){
                count++;
                if(count>end){
                    Date date = new GregorianCalendar(year, month+1, 
                            count-end)
                            .getTime();
                    grid.add(new Day(date,Flag.NOFOCUS), i, row);
                }else{
                    Date date = new GregorianCalendar(year, month
                    , count).getTime();
                    if(cal.get(Calendar.DAY_OF_MONTH)==count){
                        grid.add(new Day(date,Flag.CURR),i,row);
                    }else{
                        grid.add(new Day(date), i, row);
                    }
                }
            }
            row++;
        }
        numRows = row-1;
        cal.set(Calendar.MONTH,month);
    }
    private void monthSet(Label l,int m){
        
        //Set the label with the name of the month
        switch(m){
            case(0):
                l.setText("January");
                break;
            case(1):
                l.setText("February");
                break;
            case(2):
                l.setText("March");
                break;
            case(3):
                l.setText("April");
                break;
            case(4):
                l.setText("May");
                break;
            case(5):
                l.setText("June");
                break;
            case(6):
                l.setText("July");
                break;
            case(7):
                l.setText("August");
                break;
            case(8):
                l.setText("September");
                break;
            case(9):
                l.setText("October");
                break;
            case(10):
                l.setText("November");
                break;
            case(11):
                l.setText("December");
                break;
        }
    }
    public void update(){
        
        //reset grid
        grid.getChildren().clear();
        
        //refresh month
        int m = cal.get(Calendar.MONTH);
        int d = cal.get(Calendar.DAY_OF_MONTH);
        int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int start = cal.get(Calendar.DAY_OF_WEEK);
        addDays(start,max,m,cal.get(Calendar.YEAR));
        monthSet(month,m);
       
        //Add week day labels
        for(int x = 0;x<7;x++){
            String t;
            switch(x){
                case(0):
                    t="Monday";
                    break;
                case(1):
                    t="Tuesday";
                    break;
                case(2):
                    t="Wednesday";
                    break;
                case(3):
                    t="Thursday";
                    break;
                case(4):
                    t="Friday";
                    break;
                case(5):
                    t="Saturday";
                    break;
                default:
                    t="Sunday";
                    break;
            }
            Label top = new Label(t);
            GridPane.setHgrow(top,Priority.ALWAYS);
            top.setAlignment(Pos.CENTER);
            grid.add(top, x, 0); 
        }
        
        //refresh row constraints
        grid.getRowConstraints().clear();
        RowConstraints rc = new RowConstraints();
        rc.setPrefHeight(20);
        grid.getRowConstraints().add(rc);
        for(int x=1;x<=numRows;x++){
            rc = new RowConstraints();
            rc.setPercentHeight(100/numRows);
            grid.getRowConstraints().add(rc);
        }
    }
    private void nextMonth(){
        cal.set(Calendar.MONTH,cal.get(Calendar.MONTH)+1);
        update();
    }
    private void prevMonth(){
        cal.set(Calendar.MONTH,cal.get(Calendar.MONTH)-1);
        update();
    }

    private void touchEvents(Node e) {
        e.setOnMousePressed((MouseEvent event) -> {
            dragged = false;
            next = false;
            prev = false;
            sourceX = event.getSceneX();
        });
        e.setOnMouseDragged((MouseEvent event) -> {
            dragged = true;
            e.setTranslateX(-(sourceX-event.getSceneX()));
            if(sourceX-event.getSceneX()>50){
                next = true;
                prev = false;
            }else if(sourceX-event.getSceneX()<-50){
                prev = true;
                next = false;
            }else{
                next = false;
                prev = false;
            }
        });
        e.setOnMouseReleased((MouseEvent event) -> {
            e.setTranslateX(0);
            if(next){
                nextMonth();
            }else if(prev){
                prevMonth();
            }
        });
    }

    public boolean getDragged(){
        return dragged;
    }
}