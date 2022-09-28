package com.fm.view;

import java.util.InputMismatchException;
import java.util.Scanner;

public class UserIOConsoleImpl implements UserIO {
    
    @Override
    public void print(String message) {
        System.out.println(message);
    }
    
    
    @Override
    public String readString(String prompt) {
        System.out.println(prompt);
        
        String returnString;
        Scanner scanner = new Scanner(System.in);
        returnString = scanner.nextLine();
        
        return returnString;
    }

    @Override
    public int readInt(String prompt, int min, int max){
        int returnInt;
            while(true){
                try{
                    System.out.println(prompt);
                    Scanner scanner = new Scanner(System.in);
                    returnInt = scanner.nextInt();
                    if(returnInt >= min & returnInt <= max){
                        break;
                    }
                    else{
                        System.out.println("PLease choose a valid choice");
                    }
                }
                catch(InputMismatchException e){
                    System.out.println("That's not a number.");
                }
            }

        return returnInt;
    }


}

