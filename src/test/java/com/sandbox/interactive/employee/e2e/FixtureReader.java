package com.sandbox.interactive.employee.e2e;

import java.nio.file.Files;
import java.nio.file.Paths;

public class FixtureReader {

    public String read(String filename) {
        try {
            return Files.readString(Paths.get(getClass().getClassLoader().getResource("fixtures/" + filename).toURI()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
