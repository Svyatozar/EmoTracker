package ru.hyperboloid.emotracker.util;

import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import ru.hyperboloid.emotracker.model.DeviceInfo;
import ru.hyperboloid.emotracker.model.PeriodicResponse;
import ru.hyperboloid.emotracker.model.PorogShiftResponse;
import ru.hyperboloid.emotracker.model.SimpleResponse;

import java.io.Serializable;

/**
 * Created by olshanikov on 9/23/14.
 */
public class BinaryUtil {

    final protected static char[] HEX = "0123456789ABCDEF".toCharArray();

    public static final byte COMMAND_GET_RATES = 0x01;
    public static final byte COMMAND_SET_POROG = 0x03;
    public static final byte COMMAND_PERIODIC_RESPONSE = 0x04;
    public static final byte COMMAND_CANCEL_PERIODIC = 0x05;
    public static final byte COMMAND_POROG_SHIFT = 0x06;

    public static int buildInt(byte first, byte second) {
        return (byte) ((second << 8) & 0xFF) + (first & 0xFF);
    }

    public static byte[] splitInt(int value) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) value;
        bytes[1] = (byte) (value >>> 8);
        return bytes;
    }

    public static Serializable parseResponse(byte[] data) {
        Serializable response = null;
        switch (data[2]) {
            case COMMAND_GET_RATES:
                response = DeviceInfo.parse(data);
                break;
            case COMMAND_SET_POROG:
            case COMMAND_CANCEL_PERIODIC:
                response = SimpleResponse.parse(data);
                break;
            case COMMAND_PERIODIC_RESPONSE:
                response = PeriodicResponse.parse(data);
                break;
            case COMMAND_POROG_SHIFT:
                response = PorogShiftResponse.parse(data);
                break;
        }
        return response;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX[v >>> 4];
            hexChars[j * 2 + 1] = HEX[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static void send(Messenger messenger, Message msg) {
        try {
            messenger.send(msg);
        } catch (Exception e) {
            Log.i("LOG", "Error sending message", e);
        }
    }
}
