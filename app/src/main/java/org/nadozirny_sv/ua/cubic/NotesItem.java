package org.nadozirny_sv.ua.cubic;

import android.content.Context;
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

    public NotesItem(String s,Context c){
        title=s;
        filename=DestDir.get().path + '/' + title;
        File f=new File(filename);
        if (f.exists()){
            //read data
            date=new Date(f.lastModified());
            desc=null;
        }else {
            //create new
            date=new Date();
            desc="";
            save();
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
        if (!title.matches(this.title)){
            String new_name=DestDir.get().path+"/"+title;
            new File(filename).renameTo(new File(new_name));
            filename=new_name;
        }
        this.title=title;
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
}
