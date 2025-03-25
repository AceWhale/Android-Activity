package itstep.learning.android_212.chat;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Locale;

import itstep.learning.android_212.R;
import itstep.learning.android_212.orm.ChatMessage;

public class ChatMessageViewHolder extends RecyclerView.ViewHolder {
    public static final SimpleDateFormat momentFormat =
            new SimpleDateFormat ("dd-MM-yyyy HH:mm:ss", Locale.ROOT ) ;
    private final TextView tvAuthor;
    private final TextView tvText;
    private final TextView tvMoment;
    private ChatMessage chatMessage;

    public ChatMessageViewHolder(@NotNull View itemView){
        super(itemView);
        tvAuthor = itemView.findViewById(R.id.chat_msg_author);
        tvText = itemView.findViewById(R.id.chat_msg_text);
        tvMoment = itemView.findViewById(R.id.chat_msg_moment);
        chatMessage = null;
        itemView.setOnClickListener( v -> Toast.makeText(v.getContext(), chatMessage.getText(), Toast.LENGTH_SHORT).show());
    }

    public void setChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
        tvAuthor.setText( chatMessage.getAuthor());
        tvText.setText( chatMessage.getText());
        tvMoment.setText(momentFormat.format(chatMessage.getMoment()));
    }
}
