package org.nadozirny_sv.ua.cubic;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * Created by Nadozirny_SV on 25.08.2015.
 */
public class NotesItem {
    private String title;
    private String thumbnail;
    public boolean fullView=false;
    private String desc;
    private Date date;
    private String filename;
    private int color= Color.WHITE;
    private boolean selected=false;
    private  long size;

    public NotesItem(String s,Context c){

        filename=DestDir.get().path + '/' + s;
        File f=new File(filename);
        if (f.exists()){
            //read data
            date=new Date(f.lastModified());
            desc=null;
            if (f.length()<500){
                fullView=true;
                load();
            }
        }else {
            //create new
            date=new Date();
            desc="";
            save();
        }
            //parse color from filename
        if (s.matches(".*_#[0-9a-f]{8}$")){
            color=Color.parseColor(s.substring(s.length() - 9));
            title=s.substring(0, s.length() - 10);
        }else{
            title=s;
        }

    }
    public void save(){
        try {
            FileOutputStream os = new FileOutputStream(new File(filename));
            os.write(desc.getBytes());
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void load(){
        BufferedReader input=null;
        try {
            input= new BufferedReader(new InputStreamReader(new FileInputStream (new File(filename))));
            String line;
            StringBuffer buffer=new StringBuffer();
            while((line=input.readLine())!=null){
                buffer.append(line+System.getProperty("line.separator"));
            }
            desc=buffer.toString();

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (input!=null) {
                try{
                    input.close();
                }catch(Exception e){
                }
            }
        }
    }
    public String getTitle(){
        return title;
    }
    public String getThumbnail(){
        return thumbnail;
    }
    public void setTitle(String title){
        moveIfDiff(title,color);
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDesc() {
        if (desc == null) load();
        return desc;
    }

    public String getDate(Context c) {
        long diff=(new Date()).getTime()-date.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        if (diffDays > 1) return diffDays+" "+c.getResources().getString(R.string.date_days);
        if (diffDays == 1) return "1 "+c.getResources().getString(R.string.date_day);
        if (diffHours > 1) return diffHours+" "+c.getResources().getString(R.string.date_hours);
        if (diffHours == 1) return "1 "+c.getResources().getString(R.string.date_hour);
        if (diffMinutes > 1) return diffMinutes+" "+c.getResources().getString(R.string.date_mins);
        if (diffMinutes == 1) return "1 "+c.getResources().getString(R.string.date_min);
        if (diffSeconds > 10) return diffSeconds+" "+c.getResources().getString(R.string.date_sec);
        return c.getResources().getString(R.string.date_now);
    }

    public String getFilename() {
        return filename;
    }

    public void setDesc(String s) {
        this.desc=s;
    }

    public long getDateInt() {
        return date.getTime();
    }
    public void moveIfDiff(String title,int color){
        if (color!=this.color || !title.matches(this.title)){
            String old_name=this.getFilename();
            this.color = color;
            this.title = title;
            filename=DestDir.get().path + '/' + title+(color!=Color.WHITE?"_#"+String.format("%h",color):"");
            new File(old_name).renameTo(new File(getFilename()));
        }
    }
    public void setColor(int color) {
        moveIfDiff(title, color);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean b) {
        selected=b;
    }

    public int getColor() {
        return color;
    }
}
