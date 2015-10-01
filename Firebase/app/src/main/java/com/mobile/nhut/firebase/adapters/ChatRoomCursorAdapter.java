package com.mobile.nhut.firebase.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobile.nhut.firebase.ChatRoomActivity;
import com.mobile.nhut.firebase.R;
import com.mobile.nhut.firebase.dagger.Injector;
import com.mobile.nhut.firebase.model.Message;

public class ChatRoomCursorAdapter extends AbsCursorAdapter<ChatRoomCursorAdapter.ViewHolder> {
    private ItemClickListener mClickListener;

    public ChatRoomCursorAdapter(Cursor cursor) {
        super(cursor);
        Injector.inject(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.lv_chat_room, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        Message message = Message.fromCursor(cursor, ChatRoomActivity.mYourName);
        if (message != null) {
            viewHolder.txtName.setText(message.getAuthor());
            viewHolder.txtMessage.setText(message.getContent());
            if (message.getAuthor().equals(message.getYourName())) {
                viewHolder.txtName.setVisibility(View.GONE);

                viewHolder.lnParams.gravity = Gravity.RIGHT;
                viewHolder.txtMessage.setLayoutParams(viewHolder.lnParams);
            } else {
                viewHolder.txtName.setVisibility(View.VISIBLE);

                viewHolder.lnParams.gravity = Gravity.LEFT;
                viewHolder.txtMessage.setLayoutParams(viewHolder.lnParams);
            }
        }
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtName;

        TextView txtMessage;

        LinearLayout.LayoutParams lnParams;

        public ViewHolder(View v) {
            super(v);
            lnParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            txtName = (TextView) v.findViewById(R.id.txt_name_firebase_lv_item);
            txtMessage = (TextView) v.findViewById(R.id.txt_message_content);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onClick(view, getAdapterPosition());
            }
        }
    }
}
