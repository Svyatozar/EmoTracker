package ru.hyperboloid.emotracker.model;

import java.io.Serializable;

/**
 * Created by olshanikov on 9/25/14.
 */
public class SimpleResponse implements Serializable {

    public int commandId;

    public static SimpleResponse parse(byte[] data) {
        SimpleResponse response = new SimpleResponse();
        response.commandId = data[2];
        return response;
    }

    @Override
    public String toString() {
        return "SimpleResponse{" +
                "commandId=" + commandId +
                '}';
    }
}
