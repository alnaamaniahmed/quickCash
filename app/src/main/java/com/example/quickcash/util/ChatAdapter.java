package com.example.quickcash.util;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcash.R;
import com.example.quickcash.firebase.Chat;

import java.util.List;
/**
 * Adapter for the chat messages in the RecyclerView.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<Chat> chatMessages;
    private String currentUserName;
    /**
     * Constructs the ChatAdapter with the list of chat messages and the current user's name.
     *
     * @param chatMessages List of Chat objects to be displayed.
     * @param currentUserName The name of the user currently using the app.
     */
    public ChatAdapter(List<Chat> chatMessages, String currentUserName) {
        this.chatMessages = chatMessages;
        this.currentUserName = currentUserName;
    }

    /**
     * Called when RecyclerView needs a new {@link MessageViewHolder} of the given type to represent
     * an item.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     * @return A new MessageViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new MessageViewHolder(view);
    }
    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *               item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Chat chatMessage = chatMessages.get(position);
        if (chatMessage.getSenderName().equals(currentUserName)) {
            // This message was sent by us so hide the other user's message layout
            holder.anyUserLayout.setVisibility(View.GONE);
            holder.currentUserLayout.setVisibility(View.VISIBLE);
            holder.currentUserNameTextView.setText(chatMessage.getSenderName());
            holder.currentUserMessageTextView.setText(chatMessage.getMessage());
        } else {
            // This message was sent by them so hide our message layout
            holder.currentUserLayout.setVisibility(View.GONE);
            holder.anyUserLayout.setVisibility(View.VISIBLE);
            holder.anyUserNameTextView.setText(chatMessage.getSenderName());
            holder.anyUserMessageTextView.setText(chatMessage.getMessage());
        }
    }
    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout anyUserLayout;
        TextView anyUserMessageTextView;
        LinearLayout currentUserLayout;
        TextView anyUserNameTextView;
        TextView currentUserMessageTextView;
        TextView currentUserNameTextView;
        /**
         * Constructs the MessageViewHolder and binds the layout elements.
         *
         * @param itemView The View that you inflated in {@link #onCreateViewHolder(ViewGroup, int)}.
         */
        public MessageViewHolder(View itemView) {
            super(itemView);
            anyUserLayout = itemView.findViewById(R.id.anyUserLL);
            anyUserMessageTextView = itemView.findViewById(R.id.anyUserMessageTV);
            currentUserLayout = itemView.findViewById(R.id.currentUserLL);
            currentUserMessageTextView = itemView.findViewById(R.id.currentUserMessageTV);
            currentUserNameTextView = itemView.findViewById(R.id.currentUserNameTV);
            anyUserNameTextView = itemView.findViewById(R.id.anyUserNameTV);

        }
    }
}