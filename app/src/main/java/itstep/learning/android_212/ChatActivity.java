package itstep.learning.android_212;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import itstep.learning.android_212.chat.ChatMessageAdapter;
import itstep.learning.android_212.orm.ChatMessage;

public class ChatActivity extends AppCompatActivity {
    private static final String chatUrl = "https://chat.momentfor.fun/";
    private final List<ChatMessage> chatMessages = new ArrayList<>();
    ExecutorService threadPool;
    RecyclerView rvContainer;
    ChatMessageAdapter chatMessageAdapter;
    private EditText etAuthor;
    private EditText etMessage;
    private Handler handler;
    private Animation bellAnimation;
    private View bell;
    public static String authorName;
    private MediaPlayer incomeSound;
    private static final String PREFS_NAME = "ChatPrefs";
    private static final String PREFS_AUTHOR_KEY = "authorName";
    private static final String PREFS_AUTHOR_SET_KEY = "authorSet";
    private boolean isMessageSent = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets imeBars = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(
                    systemBars.left, systemBars.top, systemBars.right,
                    Math.max(systemBars.bottom, imeBars.bottom));
            return insets;
        });
        threadPool = Executors.newFixedThreadPool(3);
        handler = new Handler();

        incomeSound = MediaPlayer.create(this, R.raw.income);
        bell = findViewById(R.id.chat_iv_reminder);
        bellAnimation = AnimationUtils.loadAnimation(this, R.anim.demo_bell);

        rvContainer = findViewById(R.id.chat_rv_container);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rvContainer.setLayoutManager(new LinearLayoutManager(this));
        chatMessageAdapter = new ChatMessageAdapter(chatMessages);
        rvContainer.setAdapter(chatMessageAdapter);
        rvContainer.setOnTouchListener((view, event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP) {
                view.performClick();
            }
            else {
                hideKeyboard();
            }
            return false;
        });

        etAuthor = findViewById(R.id.chat_et_author);
        etMessage = findViewById(R.id.chat_et_message);
        findViewById(R.id.chat_btn_send).setOnClickListener(this::onSendClick);

        authorName = loadAuthorName();
        etAuthor.setText(authorName);

        etAuthor.setEnabled(!isMessageSent);

        updateChat();
        handler.post(this::repeater);
    }

    private void hideKeyboard() {
        View focusedView = getCurrentFocus();
        if(focusedView != null) {
            InputMethodManager inputMethodManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    focusedView.getWindowToken(),0);
            focusedView.clearFocus();
        }
    }

    private void repeater() {
        updateChat();
        handler.postDelayed(this::repeater, 2000);
    }

    private String loadAuthorName() {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(PREFS_AUTHOR_KEY, "");
    }

    private void saveAuthorName(String authorName) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(PREFS_AUTHOR_KEY, authorName)
                .apply();
    }

    private boolean isAuthorSet() {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean(PREFS_AUTHOR_SET_KEY, false);
    }

    private void setAuthorSet(boolean isSet) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putBoolean(PREFS_AUTHOR_SET_KEY, isSet)
                .apply();
    }



    private void onSendClick(View view) {
        String author = etAuthor.getText().toString();
        if (author.isBlank()) {
            Toast.makeText(this, "Заповніть поле 'Автор'", Toast.LENGTH_SHORT).show();
            return;
        }
        String message = etMessage.getText().toString();
        if (message.isBlank()) {
            Toast.makeText(this, "Заповніть поле 'MSG'", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isMessageSent) {
            saveAuthorName(author);
            isMessageSent = true;
            etAuthor.setEnabled(false);
        }

        CompletableFuture
                .runAsync(() -> sendChatMessage(new ChatMessage(author, message)), threadPool);
    }

    private CompletableFuture<Void> updateChat(){
        return CompletableFuture
                .supplyAsync(this::loadChat, threadPool)
                .thenAccept( (cnt) -> runOnUiThread( () -> showChat(cnt)));
    }

    private int loadChat(){
        try(InputStream urlStream = new URL(chatUrl).openStream()) {
            String content = Services.readStreamToString(urlStream);
            JSONObject jsonObject = new JSONObject(content);
            JSONArray arr = jsonObject.getJSONArray("data");
            int newMessagesCount = 0;
            for(int i = 0; i < arr.length(); i++) {
                ChatMessage chatMessage = ChatMessage.fromJson( arr.getJSONObject( i ) );
                if( chatMessages
                        .stream()
                        .noneMatch(m -> m. getId() .equals( chatMessage.getId() ) )
                ) {
                    chatMessages.add(chatMessage);
                    newMessagesCount += 1;
                }
            }
            if(newMessagesCount > 0){
                chatMessages.sort(Comparator.comparing(ChatMessage::getMoment));
            }
            return newMessagesCount;
        }
        catch (MalformedURLException e) {
            Log.e("ChatActivity::loadChat", "MalformedURLException:" + e.getMessage());
        }
        catch (IOException e) {
            Log.e("ChatActivity::loadChat", "IOException:" + e.getMessage());
        }
        catch (JSONException e) {
            Log.e("ChatActivity::loadChat", "JSONException:" + e.getMessage());
        }
        return 0;
    }

    private void showChat(int newMessagesCount){
        int size = chatMessages.size();
        chatMessageAdapter.notifyItemRangeChanged(size - newMessagesCount, newMessagesCount);
        if(newMessagesCount > 0) {
            rvContainer.scrollToPosition(size - 1);
            bell.startAnimation(bellAnimation);
            incomeSound.start();
            showNotification();
        }
    }

    private void sendChatMessage(ChatMessage chatMessage) {
        try{
            URL url = new URL(chatUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput( true );
            connection.setDoOutput( true );
            connection.setRequestMethod( "POST" );
            connection.setRequestProperty( "Accept", "application/json" );
            connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );
            connection.setChunkedStreamingMode( 0 );

            OutputStream outputStream = connection.getOutputStream();
            String charsetName = StandardCharsets.UTF_8.name();
            String body = String.format( Locale.ROOT,
                    "author=%s&msg=%s",
                    URLEncoder.encode( chatMessage.getAuthor(), charsetName ),
                    URLEncoder.encode( chatMessage.getText(), charsetName )
            );
            outputStream.write(body.getBytes(charsetName));
            outputStream.flush();
            outputStream.close();

            int statusCode = connection.getResponseCode();
            if(statusCode == 201){
                updateChat().thenRun( () -> runOnUiThread( () -> {
                    etMessage.setText("");
                    Toast.makeText(this, R.string.chat_msg_sent, Toast.LENGTH_SHORT).show();
                }));
            } else {
                String content = Services.readStreamToString( connection.getErrorStream() );
                Log.d("sendChatMessage", statusCode + " " + content);
            }
        }
        catch (Exception ex){
            Log.d("sendChatMessage", ex.getCause() + ex.getMessage());
        }
    }

    private void showNotification() {
        String channelId = "ChatChannelId";
        String channelName = "ChatChannel";
        String channelDescription = "Main Chan Notification Channel";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel notificationChannel = new NotificationChannel(
                channelId,
                channelName,
                importance

        );
        notificationChannel.setDescription(channelDescription);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);

        Notification notification =
                new NotificationCompat.Builder( this, channelId)
                        .setContentTitle( "New Chat Message" )
                        .setContentText( "New incoming message" )
                        .setSmallIcon(android.R.drawable.sym_def_app_icon)
                        .build();

        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from( this );

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            if( Build. VERSION. SDK_INT >= Build. VERSION_CODES.TIRAMISU ) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        1050);
            }
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(100500, notification );
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super. onRequestPermissionsResult(requestCode, permissions, grantResults);
        if( requestCode == 1050 ){

        }
    }

    @Override
    protected void onDestroy() {
        threadPool.shutdownNow();
        handler.removeMessages(0);
        super.onDestroy();
    }
}