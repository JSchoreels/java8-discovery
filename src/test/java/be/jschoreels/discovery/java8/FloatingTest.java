package be.jschoreels.discovery.java8;

import org.junit.Test;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


public class FloatingTest {

    @Test
    public void floatTest() throws Exception {
//        for(double a=2;a!=0;a-=0.1){
//            System.out.println(a);
//        }

        final Socket socket = new Socket("jschoreels.myds.me", 5001);
        final OutputStream outputStream = socket.getOutputStream();
        outputStream.write(42);
        outputStream.flush();
        outputStream.close();
    }
}
