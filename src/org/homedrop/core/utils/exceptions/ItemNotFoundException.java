package org.homedrop.core.utils.exceptions;

public class ItemNotFoundException extends Exception {
    public ItemNotFoundException(String msg) {
        super(msg);
    }

    @Override
    public String getMessage() {
        return "Item not found: [" + super.getMessage() + "]";
    }
}
