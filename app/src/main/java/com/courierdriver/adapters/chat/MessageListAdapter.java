package com.courierdriver.adapters.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.courierdriver.R;
import com.courierdriver.constants.GlobalConstants;
import com.courierdriver.model.chat.ChatListModel;
import com.courierdriver.sharedpreference.SharedPrefClass;
import com.courierdriver.views.chat.CustomerChatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_MESSAGE_SENT_IMAGE = 3;
    private static final int VIEW_TYPE_MESSAGE_RECEIVE_IMAGE = 4;

    private SharedPrefClass sharedPreferenceClass;

    private Context mContext;
    private ArrayList<ChatListModel> mMessageList;

    public MessageListAdapter(Context context, ArrayList<ChatListModel> messageList, SharedPrefClass mSharedPrefClass) {
        mContext = context;
        mMessageList = messageList;
        sharedPreferenceClass = mSharedPrefClass;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        ChatListModel message = (ChatListModel) mMessageList.get(position);
        String chatType = mMessageList.get(position).getChatType();
        String userId = new SharedPrefClass().getPrefValue(
                mContext,
                "USERID"
        ).toString();
        //if (!TextUtils.isEmpty(chatType) && chatType.equalsIgnoreCase("driver")) {
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
       /* } else {
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
                    .inflate(R.layout.item_message_recieve_image, parent, false);
            return new SentImageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVE_IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent_image, parent, false);
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
                    if (mMessageList.get(getAdapterPosition()).getMedia() != null)
                        ((CustomerChatActivity) mContext).showImageData(false, mMessageList.get(getAdapterPosition()).getMedia());
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
                    if (mMessageList.get(getAdapterPosition()).getMedia() != null)
                        ((CustomerChatActivity) mContext).showImageData(false, mMessageList.get(getAdapterPosition()).getMedia());
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
