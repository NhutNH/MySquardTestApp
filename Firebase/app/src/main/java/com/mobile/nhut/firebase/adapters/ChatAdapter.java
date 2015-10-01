package com.mobile.nhut.firebase.adapters;

import com.mobile.nhut.firebase.R;
import com.mobile.nhut.firebase.model.Message;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

  private ArrayList<Message> mMessageList;

  public ChatAdapter(ArrayList<Message> myDataset) {
    mMessageList = myDataset;

  }

  @Override
  public int getItemCount() {
    return mMessageList.size();
  }

  @Override
  public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
      int viewType) {
    // create a new view
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lv_chat_room, parent, false);
    // set the view's size, margins, paddings and layout parameters
    ViewHolder vh = new ViewHolder(v);
    return vh;
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    final Message message = mMessageList.get(position);
    if (message != null) {
      holder.txtName.setText(message.getAuthor());
      holder.txtMessage.setText(message.getContent());
      if (message.getAuthor().equals(message.getYourName())) {
        holder.txtName.setVisibility(View.GONE);

        holder.lnParams.gravity = Gravity.RIGHT;
        holder.txtMessage.setLayoutParams(holder.lnParams);
      } else {
        holder.txtName.setVisibility(View.VISIBLE);

        holder.lnParams.gravity = Gravity.LEFT;
        holder.txtMessage.setLayoutParams(holder.lnParams);
      }
    }
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    TextView txtName;

    TextView txtMessage;

    LinearLayout.LayoutParams lnParams;

    public ViewHolder(View v) {
      super(v);
      lnParams = new LinearLayout.LayoutParams(
          LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
      txtName = (TextView) v.findViewById(R.id.txt_name_firebase_lv_item);
      txtMessage = (TextView) v.findViewById(R.id.txt_message_content);
    }
  }
}

