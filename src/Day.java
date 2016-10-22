package calendar;

import static com.sun.java.accessibility.util.AWTEventMonitor.addActionListener;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.MXNodeAlgorithm;
import com.sun.javafx.jmx.MXNodeAlgorithmContext;
import com.sun.javafx.sg.prism.NGNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import java.util.Date;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.beans.binding.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;

public class Day extends StackPane{
    
    public static int FLAG_COMPACT=0;
    public static int FLAG_EXPANDED=1;
    public static int FLAG_LIST=2;
    public static int FLAG_DAILY=3;
    public static int FLAG_FOCUSED=4;
    public static int FLAG_NOFOCUS=5;
    public static int FLAG_CURR=6;
    private static int state;
    private double width;
    private ArrayList flags;
    private Date date;
    private StackPane main;
    private MonthView root;
    private Calendar cal;
    private ResultSet data;
    private ArrayList appts;
    private HashMap details;
    
    public Day(Date date){
        flags = new ArrayList();
        init(date);
    }
    public Day(Date date,double width){
        flags = new ArrayList();
        this.width = width;
        init(date);
    }
    public Day(Date date,double width,int flag){
        flags = new ArrayList();
        //System.out.println(width);
        this.width = width;
        flags.add(flag);
        init(date);
    }
    public Day(Date date,double width,int flag,int flag1){
        flags = new ArrayList();
        this.width = width;
        flags.add(flag);
        flags.add(flag1);
        init(date);
    }
    public Day(Date date,int flag){
        flags = new ArrayList();
        flags.add(flag);
        init(date);
    }
    public Day(Date date,int flag,int flag1){
        flags = new ArrayList();
        flags.add(flag);
        flags.add(flag1);
        init(date);
    }
    
    private void init(Date date){
        
        if(!(flags.contains(FLAG_COMPACT)||
                flags.contains(FLAG_EXPANDED)||
                flags.contains(FLAG_LIST))){
            flags.add(FLAG_COMPACT);
        }
        if(flags.contains(FLAG_CURR)){
            setStyle("-fx-background-color: lightblue");
        }
        
        cal = Calendar.getInstance();
        cal.setTime(date);
        
        //loadAppts();
        
        if(flags.contains(FLAG_COMPACT)){
            startCompact(date);
        }else if(flags.contains(FLAG_LIST)){
            startList(date);
        }else if(flags.contains(FLAG_DAILY)){
            startDay(date);
        }
    }
    
    private void expand(){
        main = (StackPane) getParent().getParent().getParent().getParent()
                .getParent().getParent();
        if(root.FLAG_EXPANDED==0){
            root.FLAG_EXPANDED=1;
            setStyle("-fx-background-color: rgba(100, 100, 100, 0.5)");
            StackPane holder = new StackPane();
            Text fullDate = new Text(getFullDate());
            Label day = new Label(fullDate.getText());
            day.setFont(new Font(26));
            ImageView underline = new ImageView(new Image(getClass()
                    .getResourceAsStream("gray_circle.png")));
            underline.setFitHeight(3);
            underline.setFitWidth(fullDate.getLayoutBounds().getWidth());
            day.setGraphic(underline);
            day.setContentDisplay(ContentDisplay.BOTTOM);
            VBox cont = new VBox();
            cont.setStyle(
                    "-fx-background-color: white;"
                    + "-fx-border-color: white;"
                    + "-fx-border-radius: 20 20 20 20;"
                    + "-fx-background-radius: 20 20 20 20;"
                    + "-fx-border-insets: 20px;"
                    +"-fx-background-insets: 20px;");
            cont.setAlignment(Pos.TOP_CENTER);
            DropShadow ds = new DropShadow();
            ds.setRadius(30);
            ds.setSpread(.3);
            cont.setEffect(ds);
            cont.getChildren().add(day);
            Rectangle bg = new Rectangle();
            bg.setArcWidth(20);
            bg.setArcHeight(20);
            bg.setFill(Color.WHITE);
            bg.setStroke(Color.BLACK);
            Button close = new Button();
            close.setShape(new Circle(10));
            close.setMinSize(20,20);
            close.setMaxSize(20,20);
            close.setEffect(ds);
            StackPane.setAlignment(close,Pos.TOP_LEFT);
            ImageView img = new ImageView(
                    new Image(getClass().getResourceAsStream("close.png")));
            img.setFitWidth(20);
            img.setFitHeight(20);
            close.setGraphic(img);
            close.setStyle("-fx-focus-color: transparent");
            close.setOnMouseClicked(e -> reduce());
            holder.getChildren().addAll(cont,close);
            getChildren().addAll(holder);
            setAlignment(close,Pos.TOP_RIGHT);
            main.getChildren().add(this);
            setPadding(new Insets(100,100,100,100));
        }
    }
    
    private void reduce(){
        main.getChildren().remove(this);
        root.FLAG_EXPANDED=0;
        root.update();
    }
    
    private String getFullDate(){
        String dayOfWeek;
        switch(cal.get(Calendar.DAY_OF_WEEK)){
            case(Calendar.SATURDAY):
                dayOfWeek = "Saturday";
                break;
            case(Calendar.SUNDAY):
                dayOfWeek = "Sunday";
                break;
            case(Calendar.MONDAY):
                dayOfWeek = "Monday";
                break;
            case(Calendar.TUESDAY):  
                dayOfWeek = "Tuesday";
                break;
            case(Calendar.WEDNESDAY):
                dayOfWeek = "Wednesday";
                break;
            case(Calendar.THURSDAY):
                dayOfWeek = "Thursday";
                break;
            case(Calendar.FRIDAY):
                dayOfWeek = "Friday";
                break;
            default:
                dayOfWeek = "Monday";
                break;
        }
        String month;
        switch(cal.get(Calendar.MONTH)){
            case(0):
                month = "January";
                break;
            case(1):
                month = "February";
                break;
            case(2):
                month = "March";
                break;
            case(3):
                month = "April";
                break;
            case(4):
                month = "May";
                break;
            case(5):
                month = "June";
                break;
            case(6):
                month = "July";
                break;
            case(7):
                month = "August";
                break;
            case(8):
                month = "September";
                break;
            case(9):
                month = "October";
                break;
            case(10):
                month = "November";
                break;
            case(11):
                month = "December";
                break;
            default:
                month = "January";
                break;
        }
        String s;
        if(cal.get(Calendar.DAY_OF_MONTH)==1){
            s = "st";
        }else if(cal.get(Calendar.DAY_OF_MONTH)==2){
            s = "nd";
        }else if(cal.get(Calendar.DAY_OF_MONTH)==3){
            s = "rd";
        }else{
            s = "th";
        }
        String full = String.format("%s, %s %d%s, %d", dayOfWeek,month,
                cal.get(Calendar.DAY_OF_MONTH),s,cal.get(Calendar.YEAR));
        return full;
    }

    private void clicked() {
        //System.out.println("clicked");
        root = (MonthView) getParent().getParent().getParent().getParent();
        GridPane calHolder = (GridPane) getScene().lookup("#calHolder");
        if(!root.getDragged()){
            if(flags.contains(FLAG_COMPACT)){
            flags.remove(FLAG_COMPACT);
            flags.add(FLAG_EXPANDED);
            expand();
            }
        }
        
    }
    
    private void update(){
    }

    private void startCompact(Date date) {
        setAlignment(Pos.TOP_CENTER);
        Label day = new Label(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
        if(flags.contains(FLAG_NOFOCUS)){
            day.setTextFill(Color.GRAY);
        }
        day.setFont(new Font(24));
        getChildren().add(day);
        setOnMouseClicked(e -> clicked());
        update();
    }

    private void startList(Date date) {
        GridPane base = new GridPane();
        RowConstraints rc = new RowConstraints();
        rc.setMinHeight(WeekView.rowHeight);
        rc.setMaxHeight(WeekView.rowHeight);
        ColumnConstraints cc = new ColumnConstraints();
        //cc.setPrefWidth();
        cc.setFillWidth(true);
        base.getColumnConstraints().add(cc);
        //base.setGridLinesVisible(true);
        for(int i=0,min=0,hr=0;i<288;i++,min+=5){
            if(min==60){
                hr++;
                min=0;
            }
            if(i%3==0){
                //base.add(new Label(hr+" "+min),0,i,1,3);
            }
            base.getRowConstraints().add(rc);
            try{
                data.first();
                for(data.previous();data.next();){
                    System.out.println(hr);
                    if(data.getInt("StartHr")==hr){
                        System.out.println("Almost");
                        if(data.getInt("StartMin")==min){
                            Pane event = new Pane();
                            event.setMinWidth(100);
                            event.setStyle("-fx-background-color: GREEN");
                            base.add(event,0,i);
                            System.out.println("DONE");
                        }
                    }
                }
            }catch(Exception e){System.out.println(e);}
            /*switch(i%4){
                case(0):
                    container.setBackground(new Background(new BackgroundFill(
                        Color.WHITE,null,null)));
                break;
                case(2):
                container.setBackground(new Background(new BackgroundFill(
                    Color.GREY,null,null)));
                break;
                case(1):
                default:
                container.setBackground(new Background(new BackgroundFill(
                    Color.LIGHTGREY,null,null)));
                break;
            }*/
            
        }
        base.add(new Label("h"),0,0);
        getChildren().add(base);
        
    }
    
    private void startDay(Date date){
        
    }
    
    private void connect(){
        try{
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/Data");
            String sql = String.format("select * from Y2016 where month = %d and day = %d",
                    cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH));
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            data = stmt.executeQuery(sql);
            //System.out.println(cal.get(Calendar.MONTH)+""+cal.get(Calendar.DAY_OF_MONTH));
            while(data.next()){
                HashMap temp = new HashMap();
                temp.put("title",data.getString("Title"));
                temp.put("month",data.getInt("month"));
                temp.put("day",data.getInt("day"));
                temp.put("startHr", data.getInt("startHr"));
                temp.put("startMin",data.getInt("StartMin"));
                temp.put("endHr",data.getInt("EndHr"));
                temp.put("endMin",data.getInt("EndMin"));
                temp.put("location",data.getString("Location"));
                temp.put("label",data.getString("label"));
                details.put(data.getString("title"),temp);
            }
            
        }catch(Exception e){
            Logger logger = Logger.getAnonymousLogger();
            logger.log(Level.WARNING,"An exeption was thrown",e);
        }
    }
    
    private void loadAppts(){
        appts = new ArrayList();
        details = new HashMap();
        for(int x=0;x<288;x++){
            appts.add(null);
        }
        
        connect();
        if(!details.isEmpty()){
            ArrayList maps = new ArrayList(details.values());
            for(int x=0;x<maps.size();x++){
                HashMap temp = (HashMap) maps.get(x);
                int startHr = (int) temp.get("startHr");
                int startMin = (int) temp.get("startMin");
                int endHr,endMin;
                if((int) temp.get("endHr")==0){
                    endHr = startHr+1;
                    endMin = startMin;
                }else{
                    endHr = (int) temp.get("endHr");
                    endMin = (int) temp.get("endMin");
                }
                int[] timeDetails = timeConvert(startHr,startMin,endHr,endMin);
                for(int y=timeDetails[0];y<timeDetails[1];y++){
                    appts.set(y,temp.get("title"));
                }
            }
        }
    }
    
    private int[] timeConvert(int startHr,int startMin,int endHr, int endMin){
        int start = startHr*12;
        start += startMin/5;
        
        int end = endHr*12;
        end += endMin/5;
        
        int length = end - start;
        
        return new int[] {start,end};
    }
    
    public Object getAppts(int time){
        return appts.get(time);
    }
}
