package ir.saeiddrv.iso8583.socket;

import ir.saeiddrv.iso8583.message.utilities.TypeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Client {

    public static byte[] send(byte[] data) {
        try {
            Socket socket = new Socket("46.36.103.1", 49999);
            socket.setSoTimeout(15000);

            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            output.write(data);
            output.flush();

            int index = 0;
            byte[] messageLengthBytes = new byte[2];

            while (index < 2) {
                messageLengthBytes[index] = (byte) input.read();
                index += 1;
            }

            int messageLength = Integer.parseInt(TypeUtils.byteArrayToHexString(messageLengthBytes),16);
            System.out.println("messageLength: " + messageLength);

            byte[] messageBytes = new byte[messageLength];
            index = 0;

            while (index < messageLength) {
                messageBytes[index] = (byte) input.read();
                index += 1;
            }

            System.out.println("message: " + TypeUtils.byteArrayToHexString(messageBytes));

            return ByteBuffer.allocate(2 + messageLength)
                    .put(messageLengthBytes)
                    .put(messageBytes)
                    .array();


        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

}
