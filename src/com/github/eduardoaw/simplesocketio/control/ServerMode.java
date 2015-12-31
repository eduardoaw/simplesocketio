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

import com.github.eduardoaw.simplesocketio.model.Mode;
import com.github.eduardoaw.simplesocketio.view.SimpleSocketIOView;
import java.awt.Color;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Eduardo Adams Wohlfahrt
 * @date   2015-12-29
 */
public class ServerMode extends Thread implements Mode {

    private ServerSocket sSocket = null;
    private SimpleSocketIOView sView = null;
    private boolean runThread = false;
    private HashMap<String, ConnectionThread> listConnections = null;
    
    public ServerMode(SimpleSocketIOView sView) {
        
        this.sView = sView;
        listConnections = new HashMap<String, ConnectionThread>();
    }

    @Override
    public void run() {
        waitForClientConnection();
    }
        
    private void waitForClientConnection() {
        
        runThread = true;
        
        try {
            sSocket = new ServerSocket(sView.getLocalPort());
        } catch (BindException e) {
            runThread = false;
            sView.resetServer("Port " + sView.getLocalPort() + " already in use", true);
            e.printStackTrace();
        } catch (IOException e) {
            runThread = false;
            e.printStackTrace();
        }
        
        if(runThread)
            sView.setMessageServerRec(new Color(255, 180, 0), "Listen on socket at " + sView.getLocalPort(), null, null);
        
        while (runThread) {
            
            try {
                
                ConnectionThread handlerConn = new ConnectionThread(sSocket.accept(), this);
                handlerConn.start();
            } catch (IOException e) {
                runThread = false;
                e.printStackTrace();
            } 
        }   
    }
    
    @Override
    public void addConnection(String idConn, ConnectionThread handlerConn) {
        
        listConnections.put(idConn, handlerConn);
        sView.setMessageServerRec(new Color(0, 180, 255), "Accepted connection from " + idConn, null, null);                 
        sView.addServerList(idConn); 
    }    
           
    @Override
    public void setNameThreadConn(String idConn, ConnectionThread handlerConn) {
        
        handlerConn.setName("Server connection " + idConn);
    }
    
    @Override
    public void printMsgReceived(String idConn, String msg) {
        
        sView.setMessageServerRec(new Color(155, 255, 0), "Received from " + idConn + " - ", new Color(255, 255, 0), msg);        
    }      
    
    @Override
    public void printMsgSend(String idConn, String msg, boolean send) {
        
        if (send) {
           sView.setMessageServerRec(new Color(0, 255, 255), "Sending to " + idConn + " - ", new Color(255, 255, 0), msg);
        } else {
           sView.setMessageServerRec(new Color(0, 255, 255), "Client " + idConn + " inaccessible - ", null, null);
           clearDisconnected(idConn);
        }   
    }       
    
    @Override
    public void clearDisconnected(String idConn) {
        
        sView.setMessageServerRec(new Color(174, 182, 193), "Disconnected " + idConn, null, null);
        if(listConnections.get(idConn).statusConn())
            listConnections.get(idConn).closeConnection();
        listConnections.remove(idConn);
        sView.removeClientList(idConn);
    }
    
    @Override
    public void send(String id, String msg) {
        
        listConnections.get(id).sendMessage(msg);
    }
    
    @Override
    public void disconnect(String idConn) {

        listConnections.get(idConn).closeConnection();
    } 
    
    @Override
    public void close() {
       
        runThread = false;
        
        for (Map.Entry<String, ConnectionThread> entry : listConnections.entrySet())
            entry.getValue().closeConnection();

        try {    
            if(sSocket != null)
                sSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }    
}
