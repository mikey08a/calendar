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
import static javafx.collections.FXCollections.observableArrayList;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class WeekView extends StackPane{
    
    private Calendar cal, today;
    private ObservableList<ArrayList> rows;
    private HashMap dates;
    private ArrayList names;
    private TableView days;
    private Date date;
    public static final int rowHeight = 5;
    private Day mon,tue,wed,thu,fri,sat,sun;
    
    public WeekView(){
        init();
    }
    
    private void init(){
        dates = new HashMap();
        names = new ArrayList();
        rows = observableArrayList();
        date = new Date();
        cal = Calendar.getInstance();
        today = Calendar.getInstance();
        cal.setTime(date);
        today.setTime(date);
        VBox base = new VBox();
        days = new TableView();
        days.setItems(rows);
        addColumns();
        Label title = new Label();
        base.getChildren().addAll(title,days);
        getChildren().add(base);
        update();
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
        for(int x=0;x<7;x++){
            Date temp = new GregorianCalendar(cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH))
                    .getTime();
            dates.put(names.get(x),temp);
            System.out.println(cal.get(Calendar.MONTH)+" "+cal.get(Calendar.DAY_OF_MONTH));
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH)+1);
            
            switch(x){
                case(0):
                    mon = new Day((Date) dates.get(names.get(x)));
                    break;
                case(1):
                    tue = new Day((Date) dates.get(names.get(x)));
                    break;
                case(2):
                    wed = new Day((Date) dates.get(names.get(x)));
                    break;
                case(3):
                    thu = new Day((Date) dates.get(names.get(x)));
                    break;
                case(4):
                    fri = new Day((Date) dates.get(names.get(x)));
                    break;
                case(5):
                    sat = new Day((Date) dates.get(names.get(x)));
                    break;
                default:
                    sun = new Day((Date) dates.get(names.get(x)));
                    break;
            }
        }
    }
    
    public void update(){
        addDays();
        //getAppts();
    }

    private void timePane(GridPane holder, boolean reset, double clock, int row) {
        int min;
        if(reset){
            min = 0;
        }else{
            min = 30;
        }
        int hour = (int) clock;
        String time = String.format("%d:%02d",hour,min);
        Label timeLabel = new Label(time);
        clock+=.5;
        holder.add(timeLabel,0,row,6,1);
            //System.out.println(time);
    }
    
    private void addColumns(){
        TableColumn times = new TableColumn();
        TableColumn monday = new TableColumn("Monday");
        //monday.setCellValueFactory(value);
        TableColumn tuesday = new TableColumn("Tuesday");
        TableColumn wednesday = new TableColumn("Wednesday");
        TableColumn thursday = new TableColumn("Thursday");
        TableColumn friday = new TableColumn("Friday");
        TableColumn saturday = new TableColumn("Saturday");
        TableColumn sunday = new TableColumn("Sunday");
        days.getColumns().addAll(monday,tuesday,wednesday,thursday,friday,
                saturday,sunday);
        
        names.add("mon");
        names.add("tue");
        names.add("wed");
        names.add("thu");
        names.add("fri");
        names.add("sat");
        names.add("sun");
    }

    private void getAppts() {
        ArrayList sources = new ArrayList();
        sources.add(mon);
        sources.add(tue);
        sources.add(wed);
        sources.add(thu);
        sources.add(fri);
        sources.add(sat);
        sources.add(sun);
        ApptArray array = new ApptArray(sources);
        
        for(int x=0;x<288;x++){
            rows.add(array.getAppts(x));
        }
    }
}
