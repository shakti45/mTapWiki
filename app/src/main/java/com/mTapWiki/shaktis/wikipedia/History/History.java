package com.mTapWiki.shaktis.wikipedia.History;

public class History {
   private String pageID;
   private String title;
   private String imgSrc;
   private String time;
   public  History(){

    }
  public History(String pageID, String title,String imgsrc,String time){
        this.pageID=pageID;
        this.title=title;
        this.imgSrc = imgsrc;
        this.time=time;
    }
    public String getPageID() {
        return pageID;
    }
    public String getTitle() {
        return title;
    }
    public String getTime() {
        return time;
    }

    public String getImgSrc() {
        return imgSrc;
    }
}
