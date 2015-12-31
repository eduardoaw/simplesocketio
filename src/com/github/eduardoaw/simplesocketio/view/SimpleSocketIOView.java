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

package com.github.eduardoaw.simplesocketio.view;

import com.github.eduardoaw.simplesocketio.control.ClientMode;
import com.github.eduardoaw.simplesocketio.control.ServerMode;
import com.github.eduardoaw.simplesocketio.util.MessageUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Eduardo Adams Wohlfahrt
 * @date   2015-12-29
 */

public class SimpleSocketIOView extends JFrame {

    private JTabbedPane tabbedPane;
    private JPanel panelServer;
    private JPanel panelClient;
    private JPanel panelAbout;

    private ServerMode sMode;
    private JTextField fieldLocalIP;
    private JTextField fieldLocalPort;
    private JTextPane textAreaServerRec;
    private JTextPane textAreaServerSend;
    private JList jListSever;
    private JButton btnServerListen;
    private JButton btnServerDisconnect;
    private JButton btnServerSend;
    private DefaultListModel listModelServer;
    private StyledDocument docServerRec;

    private ClientMode cMode;
    private JTextField fieldRemoteHost;
    private JTextField fieldRemotePort;
    private JTextPane textAreaClientRec;
    private JTextPane textAreaClientSend;
    private JList jListClient;
    private JButton btnClientConnect;
    private JButton btnClientDisconnect;
    private JButton btnClientDisconnectAll;
    private JButton btnClientSend;
    private DefaultListModel listModelClient;
    private StyledDocument docClientRec;    
   
    private SimpleSocketIOView sView = null;
    
    private StringBuilder sbServerRec = new StringBuilder();

    public SimpleSocketIOView() {

        setTitle("Simple Socket IO");
        setSize(1000, 560);
        setBackground(Color.gray);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        getContentPane().add(topPanel);

        createPanelServer();
        createPanelClient();
        createPanelAbout();

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Server Mode", panelServer);
        tabbedPane.addTab("Client Mode", panelClient);
        tabbedPane.addTab("About", panelAbout);
        topPanel.add(tabbedPane, BorderLayout.CENTER);

        this.sView = this;
    }

    private void createPanelServer() {

        panelServer = new JPanel();
        panelServer.setLayout(null);

        JLabel labelLocalIP = new JLabel("Local IP:");
        labelLocalIP.setBounds(10, 15, 150, 20);
        panelServer.add(labelLocalIP);

        fieldLocalIP = new JTextField();
        fieldLocalIP.setBounds(10, 35, 150, 20);
        fieldLocalIP.setEditable(false);
        try {
            fieldLocalIP.setText(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        panelServer.add(fieldLocalIP);

        JLabel labelLocalPort = new JLabel("Local port:");
        labelLocalPort.setBounds(10, 60, 150, 20);
        panelServer.add(labelLocalPort);

        fieldLocalPort = new JTextField();
        fieldLocalPort.setBounds(10, 80, 150, 20);
        panelServer.add(fieldLocalPort);

        btnServerListen = new JButton();
        btnServerListen.setBounds(10, 110, 150, 25);
        btnServerListen.setText("Listen");

        btnServerListen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fieldLocalPort.getText().trim().equals("") && sMode == null) {
                    JOptionPane.showMessageDialog(null, "Local Port is required", "Alert", JOptionPane.PLAIN_MESSAGE);
                } else if (!validateIntegerInput(fieldLocalPort.getText()) && sMode == null) {
                    JOptionPane.showMessageDialog(null, "Value not valid from Local Port", "Alert", JOptionPane.PLAIN_MESSAGE);
                    fieldLocalPort.setText("");
                } else {
                    if(sMode == null) {
                        btnServerListen.setText("Stop Listen");
                        sMode = new ServerMode(sView);
                        sMode.setName("Server Mode");
                        sMode.start();
                    }
                    else {
                        resetServer("Close Listen", false);
                    }
                }
            }
        });
        panelServer.add(btnServerListen);
        
        JLabel labelListServer = new JLabel("Connected Clients:");
        labelListServer.setBounds(10, 155, 150, 20);
        panelServer.add(labelListServer);        
        
        listModelServer = new DefaultListModel();
        
        jListSever = new JList(listModelServer);
        JScrollPane scrollPaneList = new JScrollPane(jListSever);
        scrollPaneList.setBounds(10, 180, 150, 260);
        panelServer.add(scrollPaneList);
        
        btnServerDisconnect = new JButton();
        btnServerDisconnect.setBounds(10, 453, 150, 25);
        btnServerDisconnect.setText("Disconnect");
        btnServerDisconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(sMode != null) {           
                    sMode.disconnect(jListSever.getSelectedValue().toString());
                }
            }
        });
        panelServer.add(btnServerDisconnect);        
        
        textAreaServerRec = new JTextPane();
        docServerRec = textAreaServerRec.getStyledDocument();
        DefaultCaret caretRec = (DefaultCaret)textAreaServerRec.getCaret();
        caretRec.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scrollPaneRec = new JScrollPane(textAreaServerRec);
        scrollPaneRec.setBounds(180, 15, 785, 350);
        panelServer.add(scrollPaneRec);
        
        textAreaServerSend = new JTextPane();
        DefaultCaret caretSend = (DefaultCaret)textAreaServerSend.getCaret();
        caretSend.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);        
        JScrollPane scrollPaneSend = new JScrollPane(textAreaServerSend);
        scrollPaneSend.setBounds(180, 380, 675, 100);
        panelServer.add(scrollPaneSend);

        btnServerSend = new JButton();
        btnServerSend.setBounds(865, 380, 100, 100);
        btnServerSend.setText("Send");
        btnServerSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
         
                sMode.send(jListSever.getSelectedValue().toString(), textAreaServerSend.getText());
                textAreaServerSend.setText("");
            }
        });
        panelServer.add(btnServerSend);
    }

    private void createPanelClient() {

        panelClient = new JPanel();
        panelClient.setLayout(null);

        JLabel labelRemoteHost = new JLabel("Remote Host:");
        labelRemoteHost.setBounds(10, 15, 150, 20);
        panelClient.add(labelRemoteHost);

        fieldRemoteHost = new JTextField();
        fieldRemoteHost.setBounds(10, 35, 150, 20);
        panelClient.add(fieldRemoteHost);

        JLabel labelRemotePort = new JLabel("Remote port:");
        labelRemotePort.setBounds(10, 60, 150, 20);
        panelClient.add(labelRemotePort);

        fieldRemotePort = new JTextField();
        fieldRemotePort.setBounds(10, 80, 150, 20);
        panelClient.add(fieldRemotePort);

        btnClientConnect = new JButton();
        btnClientConnect.setBounds(10, 110, 150, 25);
        btnClientConnect.setText("Connect");
        btnClientConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fieldRemoteHost.getText().trim().equals("")) {
                    JOptionPane.showMessageDialog(null, "Remote Host is required", "Alert", JOptionPane.PLAIN_MESSAGE);                
                } else if (fieldRemotePort.getText().trim().equals("")) {
                    JOptionPane.showMessageDialog(null, "Remote Port is required", "Alert", JOptionPane.PLAIN_MESSAGE);
                } else if (!validateIntegerInput(fieldRemotePort.getText())) {
                    JOptionPane.showMessageDialog(null, "Value not valid from Remote Port", "Alert", JOptionPane.PLAIN_MESSAGE);
                    fieldLocalPort.setText("");
                } else {
                    if(cMode == null) {
                        cMode = new ClientMode(sView);
                    }
                    cMode.connectToServer();
                }
            }
        });
        panelClient.add(btnClientConnect);
        
        btnClientDisconnectAll = new JButton();
        btnClientDisconnectAll.setBounds(10, 140, 150, 25);
        btnClientDisconnectAll.setText("Disconnect All");
        btnClientDisconnectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(cMode != null) {           
                    resetClient("Close connections", false);
                }
            }
        });
        panelClient.add(btnClientDisconnectAll);                  

        JLabel labelListClient = new JLabel("Connected Servers:");
        labelListClient.setBounds(10, 175, 150, 20);
        panelClient.add(labelListClient);        
        
        listModelClient = new DefaultListModel();

        jListClient = new JList(listModelClient);
        JScrollPane scrollPaneList = new JScrollPane(jListClient);
        scrollPaneList.setBounds(10, 195, 150, 250);
        panelClient.add(scrollPaneList);
        
        btnClientDisconnect = new JButton();
        btnClientDisconnect.setBounds(10, 453, 150, 25);
        btnClientDisconnect.setText("Disconnect");
        btnClientDisconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(cMode != null && listModelClient.getSize() > 0) {           
                    cMode.disconnect(jListClient.getSelectedValue().toString());
                }
            }
        });
        panelClient.add(btnClientDisconnect);        
        
        textAreaClientRec = new JTextPane();
        docClientRec = textAreaClientRec.getStyledDocument();
        DefaultCaret caretRec = (DefaultCaret)textAreaClientRec.getCaret();
        caretRec.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scrollPaneRec = new JScrollPane(textAreaClientRec);
        scrollPaneRec.setBounds(180, 15, 785, 350);
        panelClient.add(scrollPaneRec);
        
        textAreaClientSend = new JTextPane();
        DefaultCaret caretSend = (DefaultCaret)textAreaClientSend.getCaret();
        caretSend.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);        
        JScrollPane scrollPaneSend = new JScrollPane(textAreaClientSend);
        scrollPaneSend.setBounds(180, 380, 675, 100);
        panelClient.add(scrollPaneSend);

        btnClientSend = new JButton();
        btnClientSend.setBounds(865, 380, 100, 100);
        btnClientSend.setText("Send");
        btnClientSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if(listModelClient.getSize() > 0) {                
                    cMode.send(jListClient.getSelectedValue().toString(), textAreaClientSend.getText());
                    textAreaClientSend.setText("");
                }
            }
        });
        panelClient.add(btnClientSend);
    }

    private void createPanelAbout() {

        panelAbout = new JPanel();
        panelAbout.setLayout(null);

        JTextPane jPaneAbout = new JTextPane();
        jPaneAbout.setText("This software is developed by Eduardo Adams Wohlfahrt and is licensed under the MIT license https://opensource.org/licenses/MIT \r\n"
                + "Contribute to the project by accessing https://github.com/eduardoaw/simplesocketio \r\n"
                + "Find use cases of this tool on my page http://eduardoaw.github.io");
        jPaneAbout.setBounds(10, 15, 950, 60);
        panelAbout.add(jPaneAbout);        
    }

    private boolean validateIntegerInput(String value) {

        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }    
    
    private int getIndexJList(String id) {

        int i = 0;
        for(; i < listModelServer.getSize(); i++) {
            if(listModelServer.getElementAt(i).equals(id))
                break;
        }
      
        return i;
    }    
    
    public void setMessageServerRec(Color colorMsg, String message, Color colorData, String data) {
        setMessageRec(colorMsg, message, colorData, data, textAreaServerRec, docServerRec);
    }

    public void setMessageClientRec(Color colorMsg, String message, Color colorData, String data) {
        setMessageRec(colorMsg, message, colorData, data, textAreaClientRec, docClientRec);
    }    
    
    private void setMessageRec(Color colorMsg, String message, Color colorData, String data, JTextPane textAreaRec, StyledDocument doc) {
        
        Style style = textAreaRec.addStyle("Custom Style", null);
        StyleConstants.setBackground(style, colorMsg);

        try { 
            if(colorData == null && (data == null || data.equals(""))) {
                doc.insertString(doc.getLength(), MessageUtil.getTimeStamp() + " - " + message + "\r\n", style); 
            }
            else {
                doc.insertString(doc.getLength(), MessageUtil.getTimeStamp() + " - " + message, style); 
                StyleConstants.setBackground(style, colorData);        
                doc.insertString(doc.getLength(), data + "\r\n", style); 
            }
        }
        catch (BadLocationException e){
            e.printStackTrace();
        }
    }
    
    public int getLocalPort() {
        
        return Integer.parseInt(fieldLocalPort.getText());
    }
    
    public int getRemotePort() {
        
        return Integer.parseInt(fieldRemotePort.getText());
    }    

    public String getRemoteHost() {
        
        return fieldRemoteHost.getText();
    }    
    
    
    public void clearInputsClientMode() {
        
        fieldRemoteHost.setText("");
        fieldRemotePort.setText("");
    }    
    
    public void resetServer(String msg, boolean dialog) {
        
        setMessageServerRec(new Color(219, 204, 204), msg, null, null);
        btnServerListen.setText("Listen");
        sMode.close();
        sMode = null;
        listModelServer.clear();
        if(dialog)
            JOptionPane.showMessageDialog(null, msg, "Alert", JOptionPane.PLAIN_MESSAGE);
    }
    
    public void resetClient(String msg, boolean dialog) {
        
        setMessageClientRec(new Color(219, 204, 204), msg, null, null);
        cMode.close();
        cMode = null;
        listModelClient.clear();
        if(dialog)
            JOptionPane.showMessageDialog(null, msg, "Alert", JOptionPane.PLAIN_MESSAGE);
    }    

    public void addServerList(String id) {
        
        addList(id, listModelServer, jListSever);
    }    
    
    public void addClientList(String id) {

        addList(id, listModelClient, jListClient);
    }
    
    private void addList(String id, DefaultListModel listModel, JList jList) {
        
        listModel.addElement(id);
        jList.setSelectedIndex(getIndexJList(id));
    }
    
    public void removeServerList(String id) {

        removeList(id, listModelClient, jListClient);
    }        
    
    public void removeClientList(String id) {

        removeList(id, listModelServer, jListSever);
    }
    
    private void removeList(String id, DefaultListModel listModel, JList jList) {
        
        listModel.removeElement(id);
        if(listModel.getSize() > 0)
             jList.setSelectedIndex(0);
    }
    
    public static void main(String args[]) {
        SimpleSocketIOView mainFrame = new SimpleSocketIOView();
        mainFrame.setVisible(true);
    }
}
