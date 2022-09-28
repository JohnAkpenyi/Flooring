package com.fm.dao;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class FlooringAuditDaoFileImpl implements FlooringAuditDao {
    public static final String PATH = "audit.txt";
    
    @Override
    public void writeAuditEntry(String entry) throws FlooringDaoException{
        PrintWriter pw;

        try {
            pw = new PrintWriter(new FileWriter(PATH, true));
        } catch (IOException e) {
            throw new FlooringDaoException("Problem Writing Audit Information");
        }

        LocalDateTime timestamp = LocalDateTime.now();
        pw.println(timestamp.toString() + " : " + entry);
        pw.flush();
        }
}
