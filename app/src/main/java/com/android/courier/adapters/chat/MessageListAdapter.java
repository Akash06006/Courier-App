package com.android.courier.adapters.chat;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.courier.R;
import com.android.courier.constants.GlobalConstants;
import com.android.courier.model.chat.ChatListModel;
import com.android.courier.sharedpreference.SharedPrefClass;
import com.android.courier.views.chat.ChatActivity;
import com.android.courier.views.chat.DriverChatActivity;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_MESSAGE_SENT_IMAGE = 3;
    private static final int VIEW_TYPE_MESSAGE_RECEIVE_IMAGE = 4;

    private SharedPrefClass sharedPreferenceClass;

    private Context mContext;
    private ArrayList<ChatListModel> mMessageList;
    private String chatType = "";

    public MessageListAdapter(Context context, ArrayList<ChatListModel> messageList, SharedPrefClass mSharedPrefClass, String chatType) {
        mContext = context;
        mMessageList = messageList;
        sharedPreferenceClass = mSharedPrefClass;
        this.chatType = chatType;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        ChatListModel message = (ChatListModel) mMessageList.get(position);
        //chatType = mMessageList.get(position).getChatType();
        // Log.e("chatType: ", chatType);
        String userId = new SharedPrefClass().getPrefValue(
                mContext,
                "USERID"
        ).toString();
        //   if (!TextUtils.isEmpty(chatType) && chatType.equalsIgnoreCase("driver")) {
        if ((message.getSenderId() != null && message.getSenderId().equalsIgnoreCase(userId)) && message.getType() != null && message.getType() == 1) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else if ((message.getSenderId() != null && message.getSenderId().equalsIgnoreCase(userId)) && message.getType() != null && message.getType() == 2) {
            return VIEW_TYPE_MESSAGE_SENT_IMAGE;
        } else if ((message.getSenderId() != null && !message.getSenderId().equalsIgnoreCase(userId)) && message.getType() != null && message.getType() == 2) {
            return VIEW_TYPE_MESSAGE_RECEIVE_IMAGE;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
        /*} else {
            if (message.getSenderId() != null && message.getType() != null && message.getType() == 1) {
                // If the current user is the sender of the message
                return VIEW_TYPE_MESSAGE_SENT;
            } else if (message.getSenderId() != null && message.getType() != null && message.getType() == 2) {
                return VIEW_TYPE_MESSAGE_SENT_IMAGE;
            } else if (message.getSenderId() == null && message.getType() != null && message.getType() == 2) {
                return VIEW_TYPE_MESSAGE_RECEIVE_IMAGE;
            } else {
                // If some other user sent the message
                return VIEW_TYPE_MESSAGE_RECEIVED;
            }
        }*/
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_SENT_IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent_image, parent, false);//item_message_recieve_image
            return new SentImageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVE_IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_recieve_image, parent, false);//item_message_sent_image
            return new ReceiveImageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatListModel message = (ChatListModel) mMessageList.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_SENT_IMAGE:
                ((SentImageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVE_IMAGE:
                ((ReceiveImageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
        }
    }

    public void setData(ArrayList<ChatListModel> chatList) {
        this.mMessageList = chatList;
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(ChatListModel message) {
            messageText.setText(message.getMessage());

            // Format the stored timestamp into a readable String using method.
            if (message.getSentAt() != null)
                timeText.setText(getDateCurrentTimeZone(message.getSentAt()));
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(ChatListModel message) {
            messageText.setText(message.getMessage());

            // Format the stored timestamp into a readable String using method.
            if (message.getSentAt() != null)
                timeText.setText(getDateCurrentTimeZone(message.getSentAt()));


            // Insert the profile image from the URL into the ImageView.
            //   Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
        }
    }

    private class SentImageHolder extends RecyclerView.ViewHolder {
        TextView timeText;
        ImageView imageView;

        SentImageHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.img_message);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMessageList.get(getAdapterPosition()).getMedia() != null) {
                        if (chatType.equals("admin")) {
                            ((ChatActivity) mContext).showImageData(false, mMessageList.get(getAdapterPosition()).getMedia());
                        } else {
                            ((DriverChatActivity) mContext).showImageData(false, mMessageList.get(getAdapterPosition()).getMedia());
                        }
                    }

                }
            });
        }

        void bind(ChatListModel message) {
            Glide.with(mContext)
                    .load(GlobalConstants.getSOCKET_CHAT_URL() + "" + message.getMedia())
                    //.apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                    .placeholder(R.drawable.ic_dummy)
                    .into(imageView);

            // Format the stored timestamp into a readable String using method.
            if (message.getSentAt() != null)
                timeText.setText(getDateCurrentTimeZone(message.getSentAt()));
        }
    }

    private class ReceiveImageHolder extends RecyclerView.ViewHolder {
        TextView timeText;
        ImageView imageView;

        ReceiveImageHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.img_message);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMessageList.get(getAdapterPosition()).getMedia() != null) {
                        if (chatType.equals("admin")) {
                            ((ChatActivity) mContext).showImageData(false, mMessageList.get(getAdapterPosition()).getMedia());
                        } else {
                            ((DriverChatActivity) mContext).showImageData(false, mMessageList.get(getAdapterPosition()).getMedia());
                        }
                    }
                    // ((ChatActivity) mContext).showImageData(false, mMessageList.get(getAdapterPosition()).getMedia());
                }
            });
        }

        void bind(ChatListModel message) {
            Glide.with(mContext)
                    .load(GlobalConstants.getSOCKET_CHAT_URL() + "" + message.getMedia())
                    //.apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                    .placeholder(R.drawable.ic_dummy)
                    .into(imageView);

            // Format the stored timestamp into a readable String using method.
            if (message.getSentAt() != null)
                timeText.setText(getDateCurrentTimeZone(message.getSentAt()));
        }
    }

    private String getDateCurrentTimeZone(long timestamp) {
        try {
            SimpleDateFormat localDateFormat = new SimpleDateFormat("hh:mm a");
            return localDateFormat.format(new Date(timestamp * 1000));
        } catch (Exception e) {
            return "";
        }
    }
}
