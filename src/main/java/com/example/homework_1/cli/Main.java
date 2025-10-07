package com.example.homework_1.cli;


import com.example.homework_1.controllers.UserController;
import com.example.homework_1.server.CustomWebServer;
import com.example.homework_1.services.UserService;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Initialize server
        CustomWebServer virtualServer = new CustomWebServer(8080,50,true);

        UserService userService = new UserService();
        UserController userController = new UserController(userService);
        virtualServer.registerController(userController);

        try{
            virtualServer.start();

            System.out.println("CustomWebServer started");
            System.out.println("REST API server started:  http://localhost:8080");

            Thread.sleep(12_0000);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            virtualServer.stop();
        }
    }
}
