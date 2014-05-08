package com.habosa.grannytracker;

import com.habosa.javaduino.ArduinoConnection;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Author: samstern
 * Date: 5/2/14
 */
public class GrannyTracker implements SerialPortEventListener {

    public static final String URL_HOST = "http://localhost:9393";
    public static final String CHECKIN_PATH = URL_HOST + "/checkin";

    private ArduinoConnection ac;

    public GrannyTracker(ArduinoConnection ac) {
        this.ac = ac;
        boolean connected = ac.connectToBoard();
        if (connected) {
            System.out.println("CONNECTED TO BOARD");
        } else {
            System.out.println("UH OHHHH");
        }

        ac.addListener(this);
    }

    public void sendCheckin(int roomId) throws IOException {
        URL url = new URL(CHECKIN_PATH);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        OutputStream oStream = conn.getOutputStream();
        String postBody = "place[room_id]=" + roomId;
        oStream.write(postBody.getBytes());

        InputStream iStream = conn.getInputStream();
        InputStreamReader iStreamReader = new InputStreamReader(iStream);
        BufferedReader bufferedReader = new BufferedReader(iStreamReader);

        String line = bufferedReader.readLine();
        while (line != null) {
            System.out.println(line);
            line = bufferedReader.readLine();
        }
    }

    public void close() {
        ac.close();
    }

    public static void main(String[] args) throws IOException {
        ArduinoConnection ac = new ArduinoConnection();
        GrannyTracker gt = new GrannyTracker(ac);

        BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
        boolean wait = true;
        while(wait) {
            System.out.print("Enter command: ");
            String inLine = inReader.readLine();
            if (inLine.contains("close")) {
                wait = false;
            } else if (inLine.contains("test")) {
                System.out.println("Testing...");
                gt.sendCheckin(1);
            }
        }

        gt.close();
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            String inLine = ac.readLine();
            System.out.println("INPUT: " + inLine);
            if (inLine.contains("e1")) {
                // Entered room 1
                try {
                    sendCheckin(1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (inLine.contains("e2")) {
                try {
                    sendCheckin(2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
