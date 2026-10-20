package com.shelfwise.dto;

import java.util.ArrayList;
import java.util.List;

public class ImportResult {

    private int imported;
    private int skipped;
    private List<String> errors = new ArrayList<>();

    public void incrementImported() {
        imported++;
    }

    public void incrementSkipped() {
        skipped++;
    }

    public void addError(String error) {
        errors.add(error);
    }

    public int getImported() {
        return imported;
    }

    public int getSkipped() {
        return skipped;
    }

    public List<String> getErrors() {
        return errors;
    }
}
