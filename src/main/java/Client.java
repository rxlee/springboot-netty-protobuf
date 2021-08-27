import io.netty.buffer.ByteBuf;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class Client extends Frame {
    Socket s = null;
    OutputStream os = null;
    InputStream is = null;
//    DataInputStream dis = null;
    private boolean bConnected = false;

    TextField tfTxt = new TextField();
    TextArea taContent = new TextArea();

    Thread tRecv = new Thread(new RecvThread());

    public static void main(String[] args) {
        new Client().launchFrame(2404);
    }

    public void launchFrame(int port) {
        setLocation(400, 300);
        this.setSize(300, 300);
        add(tfTxt, BorderLayout.SOUTH);
        add(taContent, BorderLayout.NORTH);
        pack();
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent arg0) {
                disconnect();
                System.exit(0);
            }

        });
        tfTxt.addActionListener(new TFListener());
        setVisible(true);
        connect(port);

        tRecv.start();
    }

    public void connect(int port) {
        try {
            s = new Socket("192.168.1.147", port);
            os = s.getOutputStream();
            is = s.getInputStream();
//            dis = new DataInputStream(s.getInputStream());
            System.out.println("~~~~~~~~连接成功~~~~~~~~!");
            bConnected = true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void disconnect() {
        try {
            os.close();
            is.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private class TFListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (s.isClosed()||!s.isConnected()) {
                connect(2404);
            }
            String str = tfTxt.getText().trim();
            tfTxt.setText("");

            try {
                str+="\r\n";
                byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
                os.write(bytes);
                os.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

    }

    private class RecvThread implements Runnable {

        public void run() {
            try {
//                while (bConnected) {
//                    ByteArrayOutputStream result = new ByteArrayOutputStream();
//                    byte[] buffer = new byte[1024];
//                    int length;
//                    while ((length = is.read(buffer)) != -1) {
//                        result.write(buffer, 0, length);
//                    }
//                    String str = result.toString("UTF-8");
//                    taContent.setText(taContent.getText() + str + '\n');
//                }

                try {
                    byte[] bytes = new byte[1000];
                    int len;
                    while ((len = is.read(bytes)) != -1) {
                        //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
                        StringBuilder sb = new StringBuilder();
                        sb.append(new String(bytes, 0, len, "UTF-8"));
                        taContent.setText(taContent.getText() + new String(sb) + '\n');
                        System.out.println("收到数据：" + new String(sb));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
//                System.out.println("退出了，bye!");
                e.printStackTrace();
            }

        }

    }
}
