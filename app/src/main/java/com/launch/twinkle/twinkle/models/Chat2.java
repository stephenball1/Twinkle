package com.launch.twinkle.twinkle.models;

public class Chat2 {

  private String message;
  private String author;

  // Required default constructor for Firebase object mapping
  @SuppressWarnings("unused")
  private Chat2() {
  }

  public Chat2(String message, String author) {
    this.message = message;
    this.author = author;
  }

  public String getMessage() {
    return message;
  }

  public String getAuthor() {
    return author;
  }
}