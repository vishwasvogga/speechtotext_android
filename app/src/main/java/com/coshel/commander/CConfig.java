package com.coshel.commander;

/**
 * This class provides all configurable variables from single place
 */
public class CConfig {
    private static final CConfig ourInstance = new CConfig();

    public static CConfig getInstance() {
        return ourInstance;
    }

    private CConfig() {
    }

    //Server endpoint for speech recognition
   // String serverEndpoint = "http://192.168.225.64:3001";
    String serverEndpoint = "https://api.algo.coshel.co.in/tts-demo";
}
