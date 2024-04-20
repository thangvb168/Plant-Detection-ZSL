package com.example.plansdetection.constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface Constant {
    class Category {
        String engTitle;
        String viTitle;

        public Category(String engTitle, String viTitle) {
            this.engTitle = engTitle;
            this.viTitle = viTitle;
        }

        public String getEngTitle() {
            return engTitle;
        }

        public String getViTitle() {
            return viTitle;
        }
    }
    String API_PATH = "https://newsdata.io/api/1/";
    //    apikey=
    String API_KEY = "pub_42286855b9322549b1cf635d2cd950607677e";
    //    country=
    String API_COUNTRY = "vi";
    //    category=
    List<Category> API_CATEGORY = new ArrayList<>(Arrays.asList(
        new Category("lifestyle", "Đời sống"),
        new Category("health", "Sức khỏe"),
        new Category("science", "Khoa học"),
        new Category("technology", "Công nghệ"),
        new Category("other", "Khác")
    ));
}
