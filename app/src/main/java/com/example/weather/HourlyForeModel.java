package com.example.weather;

public class HourlyForeModel {
    String img;
    String mTime, mTemp;

    public HourlyForeModel(String img, String mTime, String mTemp) {
        this.img = img;
        this.mTime = mTime;
        this.mTemp = mTemp;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

    public String getmTemp() {
        return mTemp;
    }

    public void setmTemp(String mTemp) {
        this.mTemp = mTemp;
    }
}
