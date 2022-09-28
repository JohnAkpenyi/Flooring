package com.fm.dao;

public interface FlooringAuditDao {
    
    /**
     * adds parameter entry to audit file.
     * @param entry
     * @throws FlooringDaoException
     */
    public void writeAuditEntry(String entry) throws FlooringDaoException;
}
