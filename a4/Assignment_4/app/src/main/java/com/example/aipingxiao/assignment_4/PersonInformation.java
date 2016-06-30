package com.example.aipingxiao.assignment_4;

/**
 * Created by Aiping Xiao on 2016-02-11.
 */
public class PersonInformation {

    private long id;
    private String name;
    private String intro;
    private String imageUrl;

    //empty constructor
    public PersonInformation(){

    }

    //add data into database
    public PersonInformation(String name,String intro,String imageUrl){
        this.name = name;
        this.intro = intro;
        this.imageUrl = imageUrl;
    }

    //get fromdatabase
    public PersonInformation(long id,String name,String intro,String imageUrl){
        this.id = id;
        this.name = name;
        this.intro = intro;
        this.imageUrl = imageUrl;
    }

    //setter and gettter

    public void setId(long id) {
        this.id = id;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getIntro() {
        return intro;
    }

    public String getName() {
        return name;
    }
}
