package com.kartik.homework.utils;

public class Student {
    String name, email, phoneNumber, gender, bloodGroup, image;
    int rollNumber;

    public Student() {
    }

    public Student(String name, String emailAddress, String phoneNumber, String gender, String bloodGroup, int rollNumber, String image) {
        this.name = name;
        this.email = emailAddress;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.rollNumber = rollNumber;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmailAddress(String emailAddress) {
        this.email = emailAddress;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setRollNumber(int rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getName() {
        return name;
    }

    public String getEmailAddress() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public int getRollNumber() {
        return rollNumber;
    }
}
