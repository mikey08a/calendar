package calendar;

import java.util.ArrayList;
import java.util.Date;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class WeekView extends StackPane{
    
    private Calendar cal, today;
    private ObservableList<ArrayList> rows;
    private GridPane days,container;
    private Date date;
    public static final int ROW_HEIGHT = 5;
    public static final int SPACING = 2;
    private Day mon,tue,wed,thu,fri,sat,sun;
    
    public WeekView(){
        init();
    }
    
    private void init(){
        rows = observableArrayList();
        date = new Date();
        cal = Calendar.getInstance();
        today = Calendar.getInstance();
        cal.setTime(date);
        today.setTime(date);
        VBox base = new VBox();
        ScrollPane scrollPane = new ScrollPane();
        days = new GridPane();
        days.setHgap(SPACING);
        days.setVgap(SPACING);
        borderSetup();
        scrollPane.setContent(days);
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
        container = new GridPane();
        container.add(scrollPane,0,1,8,1);
        GridPane.setVgrow(scrollPane,Priority.ALWAYS);
        VBox.setVgrow(container, Priority.ALWAYS);
        days.minWidthProperty().bind(container.widthProperty());
        addColumns();
        Label title = new Label();
        base.getChildren().addAll(title,container);
        getChildren().add(base);
        update();
        scrollPane.setVvalue(.45);
    }
    
    private void addDays(){
        int offset = 0;
        switch(cal.get(Calendar.DAY_OF_WEEK)){
            case(3):
                offset = 1;
                break;
            case(4):
                offset = 2;
                break;
            case(5):
                offset = 3;
                break;
            case(6):
                offset = 4;
                break;
            case(0):
                offset = 5;
                break;
            case(1):
                offset = 6;
                break;
            default:
                break;
        }
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH)-offset);
        for(int x=1;x<=7;x++){
            Date temp = new GregorianCalendar(cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH))
                    .getTime();
            ArrayList appts = getAppts(temp);
            String old = "", curr;
            int span = 1, start = -1;
            for(int y=0; y<appts.size(); y++){
                if(appts.get(y) != null){
                    curr = appts.get(y).toString();
                    System.out.println(old + " " + curr);
                    if(curr.equals(old)){
                        span++;
                    }else{
                        if(!old.equals("")){
                            Pane appt = new Pane();
                            appt.setStyle("-fx-background-color: red;"
                                    + "-fx-background-radius: 5;");
                            appt.getChildren().add(new Label(old));
                            days.add(appt,x,start,1,span);
                        }
                        start = y;
                        span = 1;
                        old = curr;
                    }
                }
                if(appts.size()-1==y && start != -1){
                    Pane appt = new Pane();
                    appt.setStyle("-fx-background-color: red;"
                            + "-fx-background-radius: 5;");
                    appt.getChildren().add(new Label(old));
                    days.add(appt,x,start,1,span);
                }
            }
            System.out.println(cal.get(Calendar.MONTH)+" "+cal.get(Calendar.DAY_OF_MONTH));
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH)+1);
        }
    }
    
    public void update(){
        addDays();
        timePane();
        //getAppts();
    }

    private void timePane() {
        
        RowConstraints rc = new RowConstraints();
        rc.setMinHeight(ROW_HEIGHT);
        rc.setMaxHeight(ROW_HEIGHT);
        
        //Set up clock
        int hr = 0;
        int min = 0;
        
        //24hrs in 5 minute intervals is 288 rows
        for(int x=0;x<288;x++){
            days.getRowConstraints().add(rc);
            if(x%6==0){
                //Step clock
                if(30 == min){
                    min = 0;
                    hr++;
                }else{
                    min = 30;
                }
                
                //Add timestamp
                String time = String.format("%02d:%02d",hr,min);
                Label timeLabel = new Label(time);
                //timeLabel.setTextAlignment(TextAlignment.RIGHT);
                //timeLabel.setAlignment(Pos.CENTER_RIGHT);
                days.add(timeLabel,0,x,1,6);
            }
        }
    }
    
    private void addColumns(){
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(100/8);
        for(int x=0;x<8;x++){
            container.getColumnConstraints().add(cc);
            days.getColumnConstraints().add(cc);
            switch(x){
                case 1:
                    container.add(new Label("Monday"),x,0);
                    break;
                case 2:
                    container.add(new Label("Tuesday"),x,0);
                    break;
                case 3:
                    container.add(new Label("Wednesday"),x,0);
                    break;
                case 4:
                    container.add(new Label("Thursday"),x,0);
                    break;
                case 5:
                    container.add(new Label("Friday"),x,0);
                    break;
                case 6:
                    container.add(new Label("Saturday"),x,0);
                    break;
                case 7:
                    container.add(new Label("Sunday"),x,0);
            }
        }
    }

    private ArrayList getAppts(Date date) {
        ArrayList apptList = new ArrayList();
        Thread apptThread = new Thread(new ApptLoader(date,apptList));
        apptThread.start();
        synchronized(apptList){
            try {
                apptList.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(WeekView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return apptList;
    }
    
    private void borderSetup(){
        for(int x=0; x<7; x++){
            for(int y=0; y<288; y++){
                if(y%12 == 0){
                    //days.get
                }
            }
        }
    }
}
