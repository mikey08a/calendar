package calendar;

import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;

public class ApptArray {
    
    private ArrayList appts,sources;
    
    public ApptArray(){
        sources = new ArrayList();
        create();
    }
    
    public ApptArray(ArrayList object){
        sources = new ArrayList(object);
        create();
    }
    
    public void add(Day o){
        sources.add(o);
    }
    
    public void addAll(ArrayList list){
        
    }
    
    private void create(){
        appts = new ArrayList();
        
    }
    
    public ArrayList getAppts(int time){
        if(!sources.isEmpty()){
            for(int x=0;x<sources.size();x++){
                Day temp = (Day) sources.get(x);
                SimpleStringProperty string = new SimpleStringProperty(
                        (String) temp.getAppts(time));
                appts.add(string);
            }
        }
        return appts;
    }
}
