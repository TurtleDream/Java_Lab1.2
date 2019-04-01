package turtledream;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Future;

class MyABC implements AsynchronousByteChannel{

    int speed;
    int size;
    private long lastTime = 0;
    MyABC(int speed){
        this.speed = speed;
    }
    @Override
    public <A> void read(ByteBuffer dst, A attachment, CompletionHandler<Integer, ? super A> handler) {
        long t = size / speed;
        long currentTime = System.currentTimeMillis();
        if( lastTime == 0){
            try {
                Thread.sleep((t));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (currentTime - lastTime < t) {
            try {
                Thread.sleep((int) (t - currentTime - lastTime));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Future<Integer> read(ByteBuffer dst) {
        long t = 1000 * size / speed;
        long currentTime = System.currentTimeMillis();
        if( lastTime == 0){
            try {
                Thread.sleep((t));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (currentTime - lastTime < t) {
            try {
                Thread.sleep((int) (t - currentTime - lastTime));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public <A> void write(ByteBuffer src, A attachment, CompletionHandler<Integer, ? super A> handler) {
        this.size = src.position();
    }

    @Override
    public Future<Integer> write(ByteBuffer src) {
        this.size = src.position();
        return null;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public void close() throws IOException {

    }
}

class MyThread extends Thread{
    private String p;
    private int speed;
    MyThread(String p,int speed){
        this.p = p;
        this.speed = speed;
    }
    @Override
    public void run() {
        try {
            MyABC myABC;
            myABC = new MyABC(speed);
            InputStream inputStream;
            inputStream = new FileInputStream(new File(p));

            byte[] buffer = new byte[inputStream.available()];

            long cur_time = System.currentTimeMillis();
            inputStream.read(buffer);
            ByteBuffer buffer1 = ByteBuffer.allocate(100000);
            buffer1.put(buffer);
            myABC.write(buffer1);
            buffer1.clear();
            myABC.read(buffer1);
            buffer1.get(buffer);
            String s = new String(buffer);
            System.out.print(s);
            long cur_time2 = System.currentTimeMillis();
            synchronized (System.out) {
                System.out.println(s);
                System.out.println("");
                System.out.println("Время чтения файла = " + (cur_time2 - cur_time) + " миллисек");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
        }

        super.run();
    }
}

public class Main {

    public static void main(String[] args) throws IOException {
        Thread thread = new MyThread("C:\\Users\\stani\\IdeaProjects\\LR1\\src\\turtledream\\1.txt",500);
        Thread thread2 = new MyThread("C:\\Users\\stani\\IdeaProjects\\LR1\\src\\turtledream\\1.txt",1000);
        thread.start();
        thread2.start();
    }
}