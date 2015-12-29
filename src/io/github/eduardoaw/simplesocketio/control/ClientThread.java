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

package io.github.eduardoaw.simplesocketio.control;

import io.github.eduardoaw.simplesocketio.SimpleSocketIOView;
import io.github.eduardoaw.simplesocketio.model.MessageUtil;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author Eduardo Adams Wohlfahrt
 * @date   2015-12-29
 */
public class ClientThread extends Thread {
    
    private Socket socket = null;
    private BufferedReader bufIn = null;
    private DataOutputStream dataOut = null;
    private SimpleSocketIOView sView = null;
    private boolean runThread = false;
    private String idClient = "";
    
    public ClientThread(Socket socket, SimpleSocketIOView sView) {
        
        this.sView = sView;
        this.socket = socket;
    }
    
    @Override
    public void run() {
        
        synchronized(this) {
            
            try {

                idClient = socket.getInetAddress().getCanonicalHostName() + ":" + socket.getPort();
                sView.setMessageServerRec(new Color(0, 180, 255), "Accepted connection from " + idClient, null, null);
                sView.addClientList(idClient);            

                bufIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                dataOut = new DataOutputStream(socket.getOutputStream()); 

                runThread = true;

                //notifyAll();

                while (socket.isConnected() && runThread) {

                    String inString = "";
                    while ((inString = bufIn.readLine()) == null);
                    sView.setMessageServerRec(new Color(155, 255, 0), MessageUtil.RECEIVED + idClient + " - ", new Color(255, 255, 0), inString);
                }

                closeConnection();

            } catch (IOException e) {
                closeConnection();
                e.printStackTrace();
            } //catch (InterruptedException e) {
                //e.printStackTrace();
            //}
        }
    }
    
    public void sendMessage(String msg) {
        
        boolean send = false;
        if(dataOut != null  && socket.isConnected()) {
            
            try {
                dataOut.writeBytes(msg + "\r\n");
                send = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            if(send)
                sView.setMessageServerRec(new Color(0, 255, 255), MessageUtil.SENDING + idClient + " - ", new Color(255, 255, 0), msg);
            else
                sView.setMessageServerRec(new Color(0, 255, 255), "Client " + idClient + " inaccessible - ", null, null);
        }
    }    
    
    public String getID() {
        
        return idClient;
    }
    
    public void closeConnection() {
        
        try {
            
            runThread = false;
            if(socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
