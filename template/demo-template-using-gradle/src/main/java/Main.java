package com.ganesshkumar.demotemplate;

import com.ganesshkumar.afterburn.parser.Parser;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Main {
    public static void main(String[] args) throws IOException {
        DataInputStream inputStream = new DataInputStream(System.in);
        BufferedWriter outputWriter = new BufferedWriter(new OutputStreamWriter(System.out));

        Parser parser = new Parser();

        while(true) {
            parser.acceptIncoming(inputStream, outputWriter);
        }
    }
}
