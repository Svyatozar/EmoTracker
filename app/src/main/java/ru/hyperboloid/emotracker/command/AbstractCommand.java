package ru.hyperboloid.emotracker.command;

/**
 * Created by olshanikov on 9/24/14.
 */
public abstract class AbstractCommand {

    private static final byte START = (byte) 0x80;
    private static final byte DIRECTION = 0x01;

    abstract byte commandId();

    abstract byte[] data();

    public byte[] serialize() {
        byte[] data = data();
        byte[] command = new byte[data.length + 4];
        command[0] = START;
        command[1] = DIRECTION;
        command[2] = commandId();
        for (int i = 3; i < command.length - 1; i++) {
            command[i] = data[i - 3];
        }
        command[command.length - 1] = calculateCrc(command);

        return command;
    }

    private byte calculateCrc(byte[] data) {
        int crc = 0;
        for (byte dataByte : data) {
            crc += dataByte;
        }

        return (byte) crc;
    }
}
