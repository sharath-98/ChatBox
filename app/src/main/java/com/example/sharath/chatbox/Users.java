package com.example.sharath.chatbox;

public class Users {

    public String name;
    public String image;
    public String status;



    public String thumb_image;
    //-------------------------new content
    public String status1;



    public Users(){

    }





    public Users(String name, String image, String status,String thumb_image,String status1) {
        this.name = name;
        this.image = image;
        this.status = status;
        //new line here
        this.status1=status1;
        this.thumb_image = thumb_image;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getStatus1() {
        return status1;
    }

    public void setStatus1(String status1) {
        this.status1 = status1;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getStatus() {
        return status;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

}
