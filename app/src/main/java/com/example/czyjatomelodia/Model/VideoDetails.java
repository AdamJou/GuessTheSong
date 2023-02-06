package com.example.czyjatomelodia.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoDetails {

        @SerializedName("items")
        @Expose
        private Item[] items;

        public Item[] getItems() {
            return items;
        }

        public void setItems(Item[] items) {
            this.items = items;
        }



}
