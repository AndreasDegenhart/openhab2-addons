/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.o1tec.binding.russmca.internal.protocol;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.o1tec.binding.russmca.protocol.RussCommand;
import de.o1tec.binding.russmca.protocol.RussConnection;
import de.o1tec.binding.russmca.protocol.event.RussDisconnectionEvent;
import de.o1tec.binding.russmca.protocol.event.RussDisconnectionListener;
import de.o1tec.binding.russmca.protocol.event.RussStatusUpdateEvent;
import de.o1tec.binding.russmca.protocol.event.RussUpdateListener;

/**
 *
 * A class that wraps the communication to Russound Controller by using Input/Ouptut streams.
 *
 *
 * @author Andreas Degenhart
 */
public class RussRioConnection implements RussConnection {

    private static final Logger logger = LoggerFactory.getLogger(RussRioConnection.class);

    /** default zone count **/
    public static final int MCAC5_ZONE_COUNT = 8;

    public static final int DEFAULT_NUMBER_OF_CONTROLLERS = 1;

    /** default port for IP communication **/
    public static final int DEFAULT_IPCONTROL_PORT = 9621;

    /** Connection timeout in milliseconds **/
    private static final int CONNECTION_TIMEOUT = 3000;

    /** Socket read timeout in milliseconds **/
    private static final int SOCKET_READ_TIMEOUT = 1000;

    // The maximum time to wait incoming messages.
    private static final Integer READ_TIMEOUT = 1000;

    private int numControllers;
    private int receiverPort;
    private String receiverHost;
    private Socket ipControlSocket;

    private boolean[] bZoneWatched;

    private List<RussUpdateListener> updateListeners;
    private List<RussDisconnectionListener> disconnectionListeners;

    private IpControlInputStreamReader inputStreamReader;
    private DataOutputStream outputStream;

    public RussRioConnection(String receiverHost) {
        this(receiverHost, null, null);
    }

    public RussRioConnection(String receiverHost, Integer ipControlPort, Integer numControllers) {
        this();
        this.receiverHost = receiverHost;
        this.receiverPort = ipControlPort != null && ipControlPort >= 1 ? ipControlPort : DEFAULT_IPCONTROL_PORT;
        this.numControllers = numControllers != null && numControllers >= 1 ? numControllers
                : DEFAULT_NUMBER_OF_CONTROLLERS;

        bZoneWatched = new boolean[this.numControllers * MCAC5_ZONE_COUNT];
    }

    protected RussRioConnection() {
        this.updateListeners = new ArrayList<>();
        this.disconnectionListeners = new ArrayList<>();
    }

    @Override
    public void addUpdateListener(RussUpdateListener listener) {
        synchronized (updateListeners) {
            updateListeners.add(listener);
        }
    }

    @Override
    public void addDisconnectionListener(RussDisconnectionListener listener) {
        synchronized (disconnectionListeners) {
            disconnectionListeners.add(listener);
        }
    }

    @Override
    public boolean connect() {

        if (!isConnected()) {
            try {
                openConnection();

                // Start the inputStream reader.
                inputStreamReader = new IpControlInputStreamReader(getInputStream());
                inputStreamReader.start();

                // Get Output stream
                outputStream = new DataOutputStream(getOutputStream());

                // initialize zone watched array
                for (int i = 0; i < this.numControllers * MCAC5_ZONE_COUNT; i++) {
                    bZoneWatched[i] = false;
                }
            } catch (IOException ioException) {
                logger.debug("Can't connect to {}. Cause: {}", getConnectionName(), ioException.getMessage());
            }

        }
        return isConnected();
    }

    @Override
    public boolean isConnected() {
        return ipControlSocket != null && ipControlSocket.isConnected() && !ipControlSocket.isClosed();
    }

    /**
     * Open the connection to the Controller.
     *
     * @throws IOException
     */
    protected void openConnection() throws IOException {
        ipControlSocket = new Socket();

        // Set this timeout to unblock a blocking read when no data is received. It is useful to check if the
        // reading thread has to be stopped (it implies a latency of SOCKET_READ_TIMEOUT at most before the
        // thread is really stopped)
        ipControlSocket.setSoTimeout(SOCKET_READ_TIMEOUT);

        // Connect to the Controller with a connection timeout.
        ipControlSocket.connect(new InetSocketAddress(receiverHost, receiverPort), CONNECTION_TIMEOUT);

        logger.debug("Connected to {}:{}", receiverHost, receiverPort);
    }

    /**
     * Return the inputStream to read responses.
     *
     * @return
     * @throws IOException
     */
    protected InputStream getInputStream() throws IOException {
        return ipControlSocket.getInputStream();
    }

    /**
     * Return the outputStream to send commands.
     *
     * @return
     * @throws IOException
     */
    protected OutputStream getOutputStream() throws IOException {
        return ipControlSocket.getOutputStream();
    }

    @Override
    public void close() {
        if (inputStreamReader != null) {
            // This method block until the reader is really stopped.
            inputStreamReader.stopReader();
            inputStreamReader = null;
            logger.debug("Stream reader stopped!");
        }
        try {
            if (ipControlSocket != null) {
                ipControlSocket.close();
                ipControlSocket = null;
                logger.debug("Closed socket!");
            }
        } catch (IOException ioException) {
            logger.error("Closing connection throws an exception!", ioException);
        }
    }

    protected void reconnect() {
        close();
        try {
            openConnection();
        } catch (IOException ioException) {
            logger.error("Error occured when reconnecting", ioException);
        }
    }

    @Override
    public String getConnectionName() {
        return receiverHost + ":" + receiverPort;
    }

    /**
     * Sends to command to the receiver. It does not wait for a reply.
     *
     * @param ipControlCommand
     *            the command to send.
     **/
    @Override
    public boolean sendCommand(RussCommand ipControlCommand) {
        boolean isSent = false;
        if (connect()) {
            String command = ipControlCommand.getCommand();
            try {
                if (logger.isTraceEnabled()) {
                    logger.trace("Sending {} bytes: {}", command.length(),
                            DatatypeConverter.printHexBinary(command.getBytes()));
                }
                outputStream.writeBytes(command);
                outputStream.flush();
                isSent = true;

            } catch (IOException ioException) {
                logger.error("Error occured when sending command", ioException);

                logger.debug("Trying to reconnect !");
                reconnect();
            }

            logger.debug("Command sent to Russound Controller @{}: {}", getConnectionName(), command);
        }

        return isSent;
    }

    @Override
    public int getControllerCount() {
        return numControllers;
    }

    /**
     * Read incoming data from the Controller and notify listeners for dataReceived and disconnection.
     *
     * @author Andreas Degenhart
     *
     */
    private class IpControlInputStreamReader extends Thread {

        private BufferedReader bufferedReader = null;

        private volatile boolean stopReader;

        // This latch is used to block the stop method until the reader is really stopped.
        private CountDownLatch stopLatch;

        /**
         * Construct a reader that read the given inputStream
         *
         * @param ipControlSocket
         * @throws IOException
         */
        public IpControlInputStreamReader(InputStream inputStream) {
            this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            this.stopLatch = new CountDownLatch(1);

            this.setDaemon(true);
            this.setName("IpControlInputStreamReader-" + getConnectionName());
        }

        @Override
        public void run() {
            try {

                while (!stopReader && !Thread.currentThread().isInterrupted()) {

                    String receivedData = null;
                    try {
                        receivedData = bufferedReader.readLine();
                    } catch (SocketTimeoutException e) {
                        // Do nothing. Just happen to allow the thread to check if it has to stop.
                    }

                    if (receivedData != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Data received from Russound Controller @{}: {}", getConnectionName(),
                                    receivedData);
                        }
                        RussStatusUpdateEvent event = new RussStatusUpdateEvent(RussRioConnection.this, receivedData);
                        synchronized (updateListeners) {
                            for (RussUpdateListener russEventListener : updateListeners) {
                                russEventListener.statusUpdateReceived(event);
                            }
                        }
                    }
                }

            } catch (IOException e) {
                logger.warn("The Russound Controller @{} is disconnected.", getConnectionName(), e);
                RussDisconnectionEvent event = new RussDisconnectionEvent(RussRioConnection.this, e);
                for (RussDisconnectionListener russDisconnectionListener : disconnectionListeners) {
                    russDisconnectionListener.onDisconnection(event);
                }
            }

            // Notify the stopReader method caller that the reader is stopped.
            this.stopLatch.countDown();
        }

        /**
         * Stop this reader. Block until the reader is really stopped.
         */
        public void stopReader() {
            this.stopReader = true;
            try {
                this.stopLatch.await(READ_TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // Do nothing. The timeout is just here for safety and to be sure that the call to this method will not
                // block the caller indefinitely.
                // This exception should never happen.
            }
        }

    }

}
