package com.example.homework_1.cli;


import com.example.homework_1.controllers.UserController;
import com.example.homework_1.repository.UserRepository;
import com.example.homework_1.server.CustomWebServer;
import com.example.homework_1.services.UserService;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Initialize server
        CustomWebServer virtualServer = new CustomWebServer(8080,200,true);

        UserRepository userRepository = new UserRepository();
        UserService userService = new UserService(userRepository);
        UserController userController = new UserController(userService);
        virtualServer.registerController(userController);

        try{
            virtualServer.start();

            System.out.println("CustomWebServer started");
            System.out.println("REST API server started:  http://localhost:8080");
            System.out.println("Press Enter to stop the server...");

            //Waiting for input in the console
            System.in.read();

            virtualServer.stop();
            System.out.println("CustomWebServer stopped");
        }catch (Exception e){
            System.out.println("CustomWebServer stopped errors occured " + e.getMessage());
            e.printStackTrace();
        }
    }
}
