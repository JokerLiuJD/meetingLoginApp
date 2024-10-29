package com.example.demo.Dto;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenDto {
    private int code;
    private Data data;

    // Getter and setter for 'code'
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    // Getter and setter for 'data'
    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

   
    public static class Data {
        @JsonProperty("token")
        private String token;

        // Getter and setter for 'token'
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    

}
