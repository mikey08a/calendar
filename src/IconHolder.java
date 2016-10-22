package calendar;

import java.net.URI;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import java.io.InputStream;
import javafx.scene.shape.Circle;

public class IconHolder {
    
    Image full, profileImg;
    InputStream path;
    int radius;
    Canvas canvas;
    WritableImage temp;
    
    public IconHolder(){
    }
    
    public IconHolder(InputStream p){
        path = p;
        setup();
    }
    
    public IconHolder(InputStream p, int r){
        path = p;
        if(r>20){
            radius=r;
        }else{
            radius=20;
        }
        setup();
    }
    
    public IconHolder(URI uri){
        //setup();
    }
    
    public void setImg(InputStream p){
        path = p;
        setup();
    }
    
    public void setRadius(int r){
        radius = r;
        setup();
    }
    
    public Image getImg(){
        return profileImg;
    }
    
    public int getWidth(){
        return radius;
    }
    
    public int getHeight(){
        return radius;
    }
    public WritableImage getCanvas(){
        return temp;
    }
    
    public void setup(){
        int w, h;
        full = new Image(path,radius*2,radius*2,false,false);
        canvas = new Canvas(full.getWidth(),full.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(full, 0, 0);
        
        double scaleFactor;
        gc.setStroke(Color.BLACK);
        canvas.setWidth(radius*2);
        canvas.setHeight(radius*2);
        
        Circle e = new Circle(radius,radius,radius);
        
        for(int y=0;y<radius*2;y++){
            for(int x=0;x<radius*2;x++){
                if(!e.contains(x,y)){
                    gc.clearRect(x,y,1,1);
                }
            }
        }
        
        gc.strokeOval(1,1,radius*2-1,radius*2-1);
        temp = new WritableImage(radius*2,radius*2);
        SnapshotParameters param = new SnapshotParameters();
        param.setFill(Color.TRANSPARENT);
        WritableImage snapshot = canvas.snapshot(param, temp);
        profileImg = snapshot;
    }
}
