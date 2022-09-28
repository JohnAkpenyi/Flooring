package com.fm.view;

public interface UserIO {
    /**
     * Prints a string (msg) passed into the method from the call to the console
     * @param msg 
     */
    void print(String msg);
    
    /**
     * Reads in an integer from System.in. Rejects any values
     * <= min paramater and >= max. 
     * @param prompt
     * @param min
     * @param max
     * @return 
     */
    int readInt(String prompt, int min, int max);
    
    /**
     * Reads in a string from System.in after prompting the user
     * for the data using the prompt string parameter.
     * @param prompt
     * @return 
     */
    String readString(String prompt);
}

