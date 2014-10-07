package ru.hyperboloid.emotracker.command;

import ru.hyperboloid.emotracker.util.BinaryUtil;

/**
 * Created by olshanikov on 9/24/14.
 */
public class GetStatusCommand extends AbstractCommand {

    @Override
    byte commandId() {
        return BinaryUtil.COMMAND_GET_RATES;
    }

    @Override
    byte[] data() {
        return new byte[0];
    }
}
