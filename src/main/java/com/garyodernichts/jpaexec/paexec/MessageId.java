package com.garyodernichts.jpaexec.paexec;

public enum MessageId {
    Settings (1),
    RespSendFiles (2),
    SendFiles (3),
    OK (4),
    StartApp (5),
    Failed (6);

    public final short id;

    MessageId(int id) {
        this.id = (short) id;
    }
}
