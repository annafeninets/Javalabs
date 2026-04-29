package main;
import client.Client;
import client.ui.ChatClientGui;
import exception.ConfigurationException;
import server.Server;
import server.ui.ServerGUI;

import java.io.IOException;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {

                Server server = null;
                ServerGUI serverUI = null;
                Client client = null;

                args = new String[]{"c"};

                if(args.length == 0) {
                    try {
                        server = new Server();
                        new ServerGUI(server);

                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }

                    try {
                        client = new Client();
                        new ChatClientGui(client);
                    } catch (IOException | ConfigurationException e) {
                        System.out.println(e.getMessage());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    if(Objects.equals(args[0], "s")){
                        try {
                            server = new Server();
                            new ServerGUI(server);

                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
                    } else if(Objects.equals(args[0], "c")){
                        try {
                            client = new Client();
                            new ChatClientGui(client);
                        } catch (IOException | ConfigurationException e) {
                            System.out.println(e.getMessage());
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
    }
}
