package itstep.learning.android_212.orm;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatMessage {
    public static final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT );

    private String id;
    private String author;
    private String text;
    private Date moment;


    public ChatMessage() { }

    public ChatMessage(String author, String text) {
        this.author = author;
        this.text = text;
    }

    public static ChatMessage fromJson(JSONObject jsonObject ) throws JSONException {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId( jsonObject.getString("id" ) );
        chatMessage. setAuthor( jsonObject.getString( "author" ) );
        chatMessage. setText( jsonObject.getString( "text" ) );
        String moment = jsonObject.getString( "moment");
        try { chatMessage. setMoment( dateFormat.parse( moment ) ); }
        catch (ParseException ex) {
            throw new JSONException(ex.getMessage());
        }
        return chatMessage;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getMoment() {
        return moment;
    }

    public void setMoment(Date moment) {
        this.moment = moment;
    }


}
