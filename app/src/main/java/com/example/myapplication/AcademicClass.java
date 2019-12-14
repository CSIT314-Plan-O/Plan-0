package com.example.myapplication;

public class AcademicClass {

    private String mClass = "";
    private String mTiming = "";

    AcademicClass(String acedemicClass, String timing){
        this.mClass = acedemicClass;
        this.mTiming = timing;
    }

    public String getmClass() {
        return mClass;
    }

    public void setmClass(String mClass) {
        this.mClass = mClass;
    }

    public String getmTiming() {
        return mTiming;
    }

    public void setmTiming(String mTiming) {
        this.mTiming = mTiming;
    }
}
