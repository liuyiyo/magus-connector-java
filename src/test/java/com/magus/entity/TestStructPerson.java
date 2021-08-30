package com.magus.entity;

import com.magus.entity.RelationInfo;

public class TestStructPerson {

    public TestStructPerson(){

    }
    private String name;
    private int age;
    private long height;
    private float weight;
    private double width;
    private RelationInfo relationInfo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public RelationInfo getRelationInfo() {
        return relationInfo;
    }

    public void setRelationInfo(RelationInfo relationInfo) {
        this.relationInfo = relationInfo;
    }

    public TestStructPerson(String name, int age, long height, float weight, double width) {
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.width = width;
    }

    public TestStructPerson(String name, int age, long height, float weight, double width, RelationInfo relationInfo) {
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.width = width;
        this.relationInfo = relationInfo;
    }

    @Override
    public String toString() {
        return "TestStructPerson{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", height=" + height +
                ", weight=" + weight +
                ", width=" + width +
                ", relationInfo=" + relationInfo +
                '}';
    }
}

