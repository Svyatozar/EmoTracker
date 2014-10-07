package ru.hyperboloid.emotracker.command;

import ru.hyperboloid.emotracker.util.BinaryUtil;

/**
 * Created by olshanikov on 9/25/14.
 */
public class CancelPeriodicCommand extends AbstractCommand {
    @Override
    byte commandId() {
        return BinaryUtil.COMMAND_CANCEL_PERIODIC;
    }

    @Override
    byte[] data() {
        return new byte[0];
    }
}
