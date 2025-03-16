package com.cs407.snapnourish;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private final List<ChatMessage> chatMessages;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);

        holder.textViewMessage.setText(message.getMessage());

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.textViewMessage.getLayoutParams();

        if (message.isUser()) {
            holder.textViewMessage.setBackgroundResource(R.drawable.message_bubble_yellow);
            params.gravity = Gravity.END;
        } else {
            holder.textViewMessage.setBackgroundResource(R.drawable.message_bubble_white);
            params.gravity = Gravity.START;
        }

        holder.textViewMessage.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;

        public ChatViewHolder(View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textView_message);
        }
    }
}
