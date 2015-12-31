/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Eduardo Adams Wohlfahrt
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.eduardoaw.simplesocketio.control;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

/**
 *
 * @author Eduardo Adams Wohlfahrt
 * @date 2015-12-29
 */
public class ConnectionThread extends Thread {

    private Socket socket = null;
    private InputStream in = null;
    private OutputStream out = null;
    private boolean runThread = false;
    private String idConn;
    private Object obj;

    public ConnectionThread(Socket socket, Object obj) {

        this.socket = socket;
        this.obj = obj;
    }

    @Override
    public void run() {
        listen();
    }

    private void listen() {

        try {

            idConn = socket.getInetAddress().getCanonicalHostName() + ":" + socket.getPort();
            
            obj.getClass().getMethod("setNameThreadConn", String.class, ConnectionThread.class).invoke(obj, idConn, this);
            
            in = socket.getInputStream();
            out = socket.getOutputStream();

            runThread = true;
            obj.getClass().getMethod("addConnection", String.class, ConnectionThread.class).invoke(obj, idConn, this);
            
            while (socket.isConnected() && runThread) {

                byte[] buf = new byte[1024];
                int bytes_read = 0;
                
                bytes_read = in.read(buf, 0, buf.length);
                if (bytes_read < 0) {
                    runThread = false;
                }
                
                obj.getClass().getMethod("printMsgReceived", String.class, String.class).invoke(obj, idConn, new String(buf, 0, bytes_read));
            }

            obj.getClass().getMethod("clearDisconnected", String.class).invoke(obj, idConn);

        } catch (IOException e) {

            try {
                obj.getClass().getMethod("clearDisconnected", String.class).invoke(obj, idConn);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public boolean statusConn() {
        
        return socket.isConnected();
    }
    
    public void sendMessage(String msg) {

        boolean send = false;
        if (out != null && socket.isConnected()) {

            byte[] b = msg.getBytes();
            try {
                out.write(b, 0, b.length);
                out.flush();
                send = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                obj.getClass().getMethod("printMsgSend", String.class, String.class, boolean.class).invoke(obj, idConn, msg, send);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
               e.printStackTrace();
            }
        }
    }

    public void closeConnection() {

        try {

            runThread = false;
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
