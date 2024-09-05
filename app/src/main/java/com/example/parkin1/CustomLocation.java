package com.example.parkin1;

public class CustomLocation {
        private String number;
        private String name;
        private String address;
        private String distance;
        private String price;

        public CustomLocation(String number, String name, String address, String distance, String price) {
            this.number=number;
            this.name = name;
            this.address = address;
            this.distance = distance;
            this.price = price;
        }

        public String getNumber() {
        return number;
    }
        public void setNumber(Integer number){this.number= String.valueOf(number);}

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }

