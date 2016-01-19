package org.homedrop.core.utils.exceptions;

public class ItemWithValueAlreadyExistsException extends Exception {
    public ItemWithValueAlreadyExistsException(String msg) {
        super(msg);
    }

    @Override
    public String getMessage() {
        return "Item with that value already exists: [" + super.getMessage() + "]";
    }
}
