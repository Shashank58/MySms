package shashank.mysms.model;

/**
 * Created by shashankm on 14/01/16.
 */
public class Sms {
    private String address, body, time;
    private boolean isSpam;
    private long id;
    private int date, month;

    public Sms(){
    }

    public void setIsSpam(boolean isSpam){
        this.isSpam = isSpam;
    }

    public void setId(long id){
        this.id = id;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public void setBody(String body){
        this.body = body;
    }

    public void setTime(String time){
        this.time = time;
    }

    public void setMonth(int month){
        this.month = month;
    }

    public void setDate(int date){
        this.date = date;
    }

    public long getId(){
        return id;
    }

    public String getAddress(){
        return address;
    }

    public String getBody(){
        return body;
    }

    public int getDate(){
        return date;
    }

    public int getMonth(){
        return month;
    }

    public boolean getisSpam(){
        return isSpam;
    }

    public String getTime(){
        return time;
    }
}
