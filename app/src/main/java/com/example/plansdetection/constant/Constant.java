package com.example.plansdetection.constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface Constant {
    class Category {
        String engTitle;
        String viTitle;
        String search;

        public Category(String engTitle, String viTitle, String search) {
            this.engTitle = engTitle;
            this.viTitle = viTitle;
            this.search = search;
        }

        public String getEngTitle() {
            return engTitle;
        }

        public String getViTitle() {
            return viTitle;
        }

        public String getSearch() {
            return search;
        }
    }
    String API_PATH = "https://newsdata.io/api/1/";
    //    apikey=
    String API_KEY = "pub_4236115dd8d2bac8f21ab0829672f5edd4ab1";
    //    country=
    String API_COUNTRY = "vi";
    String API_CATEGORY_ALL = "food,health,lifestyle,other,technology";
    //    category=
    List<Category> API_CATEGORY = new ArrayList<>(Arrays.asList(
            new Category("environment,technology,top", "Nông nghiệp", "nong-nghiep"),
            new Category("environment,technology,top", "Cây Trồng", "cay-trong"),
            new Category("lifestyle", "Đời sống", "*"),
            new Category("health", "Sức khỏe", "*"),
            new Category("science", "Khoa học", "*"),
            new Category("technology", "Công nghệ", "*"),
            new Category("other", "Khác", "*")
    ));
}
