package calendar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mikey
 */
public class ApptLoader implements Runnable{
    private int month,day,year;
    private HashMap details;
    final ArrayList apptList;
    
    public ApptLoader(Date date, ArrayList apptList){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        month = cal.get(Calendar.MONTH)+1;
        day = cal.get(Calendar.DAY_OF_MONTH);
        year = cal.get(Calendar.YEAR);
        this.apptList = apptList;
    }

    @Override
    public void run() {
        loadAppts();
    }
    
    private void loadAppts(){
        
        //Synchronize to prevent data corruption
        synchronized(apptList){

            //Create empty list of appointments
            details = new HashMap();
            for(int x=0; x<288; x++){
                apptList.add(null);
            }

            //Try to connect to the database
            connect();

            //If any appointments exist, add them to the list
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
                        apptList.set(y,temp.get("title"));
                    }
                }
            }
            
            apptList.notifyAll();
        }
    }
    
    private void connect(){
        try{
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/Data");
            String sql = String.format("select * from Y2016 where month = %d and day = %d",
                    month,day);
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet data = stmt.executeQuery(sql);
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
    
    //Convert hrs and mins to 0-288 5min intervals
    private int[] timeConvert(int startHr,int startMin,int endHr, int endMin){
        int start = startHr*12;
        start += startMin/5;
        
        int end = endHr*12;
        end += endMin/5;
        
        int length = end - start;
        
        return new int[] {start,end};
    }
}
