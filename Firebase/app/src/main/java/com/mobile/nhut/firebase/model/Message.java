package com.mobile.nhut.firebase.model;

import android.database.Cursor;

import com.mobile.nhut.firebase.offline.OfflineProvider;

public class Message {

  private String yourName;

  private String Content;

  private String Author;

  public Message() {
  }

  public Message(String author, String content, String yourname) {
    this.Author = author;
    this.Content = content;
    this.yourName = yourname;
  }

  public String getContent() {
    return Content;
  }

  public void setContent(String content) {
    this.Content = content;
  }

  public String getAuthor() {
    return Author;
  }

  public void setAuthor(String name) {
    Author = name;
  }

  public String getYourName() {
    return yourName;
  }

  public void setYourName(String yourName) {
    this.yourName = yourName;
  }

  public static Message fromCursor(Cursor cursor, String yourName){
    Message message = null;
    if(!cursor.isClosed()) {
      message = new Message();
      message.setYourName(yourName);
      message.setAuthor(cursor.getString(cursor.getColumnIndex(OfflineProvider.AUTHOR)));
      message.setContent(cursor.getString(cursor.getColumnIndex(OfflineProvider.MESSAGE)));
    }
    return message;
  }
}
