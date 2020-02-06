package com.garyodernichts.jpaexec.paexec;

import com.garyodernichts.jpaexec.ByteHelper;

import java.util.Random;

public class Message {
    short m_msgID;
    byte[]	m_payload;
    int m_expectedLen;
    int m_uniqueProcessID;

    public Message(MessageId msgID, byte[] m_payload, int m_uniqueProcessID) {
        this.m_msgID = msgID.id;
        this.m_payload = m_payload;
        this.m_uniqueProcessID = m_uniqueProcessID;
    }

    public Message(byte[] data) {

    }

    public byte[] getDataToSend() {
        // 2 represents a 2 byte WORD, each 4 represents a 4 byte DWORD
        int totallenght = m_payload.length + 2 + 4 + 4;
        if (m_msgID == MessageId.Settings.id) {
            // another 4 bytes for the xor value
            totallenght += 4;
        }

        byte[] buffer = new byte[totallenght];

        int constructionIndex = 0;

        byte[] msg_id_bytes = ByteHelper.makeWORD(m_msgID);

        System.arraycopy(msg_id_bytes, 0, buffer, constructionIndex, msg_id_bytes.length);
        constructionIndex += msg_id_bytes.length;

        Random r = new Random();
        int xorVal = 123456789;//r.nextInt(2147483647);
        byte[] xorVal_bytes = ByteHelper.makeDWORD(xorVal);

        System.arraycopy(xorVal_bytes, 0, buffer, constructionIndex, xorVal_bytes.length);
        constructionIndex += xorVal_bytes.length;
        int XORStart = constructionIndex;

        byte[] uniqueProcessID_bytes = ByteHelper.makeDWORD(m_uniqueProcessID);

        System.arraycopy(uniqueProcessID_bytes, 0, buffer, constructionIndex, uniqueProcessID_bytes.length);
        constructionIndex += uniqueProcessID_bytes.length;

        int payload_lenght = m_payload.length;
        byte[] payload_lenght_bytes = ByteHelper.makeDWORD(payload_lenght);

        System.arraycopy(payload_lenght_bytes, 0, buffer, constructionIndex, payload_lenght_bytes.length);
        constructionIndex += payload_lenght_bytes.length;

        System.arraycopy(m_payload, 0, buffer, constructionIndex, m_payload.length);
        constructionIndex += m_payload.length;

        int dataLen = constructionIndex - XORStart;
        for (int i = 0; i < dataLen - 3; i++)
        {
            int currentindex = XORStart + i;

            byte[] bytestoxor = new byte[4];
            bytestoxor[0] = buffer[currentindex];
            bytestoxor[1] = buffer[currentindex + 1];
            bytestoxor[2] = buffer[currentindex + 2];
            bytestoxor[3] = buffer[currentindex + 3];

            int xoredvalue = ByteHelper.readDWORD(bytestoxor) ^ xorVal;

            byte[] valuebytes = ByteHelper.makeDWORD(xoredvalue);
            System.arraycopy(valuebytes, 0, buffer, currentindex, valuebytes.length);

            xorVal += 3;
        }

        return buffer;
    }
}
