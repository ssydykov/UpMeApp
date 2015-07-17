package com.example.saken.upmeapp;

public class CategoryModel {

    public String id;
    public String name;
    public String sub_name;
    public String img_url;


    public CategoryModel(String _name, String _sub_name, String _img_url) {
        name = _name;
        sub_name = _sub_name;
        img_url = _img_url;
    }
}
