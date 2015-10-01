package com.mobile.nhut.firebase.adapters;

import com.mobile.nhut.firebase.R;
import com.mobile.nhut.firebase.adapters.util.ImageLoader;
import com.mobile.nhut.firebase.dagger.Injector;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

public class ListNoteAdapter extends RecyclerView.Adapter<ListNoteAdapter.ViewHolder> {

  @Inject
  ImageLoader mImageLoader;

  private List<String> mItems;

  private ItemClickListener mClickListener;

  public ListNoteAdapter(List<String> friendList) {
    super();
    Injector.inject(this);
    mItems = friendList;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
    View v = LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.lv_notes, viewGroup, false);
    ViewHolder viewHolder = new ViewHolder(v);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, int position) {
    viewHolder.mTxtTitle.setText(mItems.get(position));
  }

  @Override
  public int getItemCount() {
    return mItems.size();
  }

  public void setOnItemClickListener(ItemClickListener itemClickListener) {
    this.mClickListener = itemClickListener;
  }

  public interface ItemClickListener {

    void onClick(View view, int position);
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView mImgThumbnail;

    public TextView mTxtTitle;

    public ViewHolder(View itemView) {
      super(itemView);
      //            imgThumbnail = (ImageView) itemView.findViewById(R.id.img_thumbnail);
      mTxtTitle = (TextView) itemView.findViewById(R.id.txt_title);

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
