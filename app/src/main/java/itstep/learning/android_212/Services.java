package itstep.learning.android_212;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Services {
    public static String readStreamToString(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[4096];
        ByteArrayOutputStream byteBuilder = new ByteArrayOutputStream();
        int len;
        while((len = inputStream.read(buffer)) > 0 ) {
            byteBuilder.write(buffer, 0, len);
        }
        return byteBuilder.toString();
    }
}
