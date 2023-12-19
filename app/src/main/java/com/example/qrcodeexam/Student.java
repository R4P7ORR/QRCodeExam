package com.example.qrcodeexam;

public class Student {
    private int id;
    private String name;
    private String grade;

    public Student(String name, String grade){
        this.name = name;
        this.grade = grade;
    }

    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getGrade(){
        return grade;
    }
}
