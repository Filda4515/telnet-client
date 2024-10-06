package utb.fai;

import java.io.*;
import java.net.*;

public class TelnetClient {

    private String serverIp;
    private int port;

    public TelnetClient(String serverIp, int port) {
        this.serverIp = serverIp;
        this.port = port;
    }

    public void run() {
        try {
            Socket socket = new Socket(serverIp, port);

            BufferedReader userReader = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            Thread inputThread = new Thread() {
                @Override
                public void run() {
                    try {
                        String userInput;
                        while ((userInput = userReader.readLine()) != null) {
                            if (userInput.equals("/QUIT")) {
                                socket.close();
                                break;
                            }
                            writer.println(userInput);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            Thread outputThread = new Thread() {
                @Override
                public void run() {
                    try {
                        String serverResponse;
                        while ((serverResponse = reader.readLine()) != null) {
                            System.out.println(serverResponse);
                        }
                    } catch (IOException e) {
                        if (!socket.isClosed()) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            inputThread.start();
            outputThread.start();

            inputThread.join();
            outputThread.join();
            
            userReader.close();
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
