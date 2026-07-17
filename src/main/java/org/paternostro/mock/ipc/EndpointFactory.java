package org.paternostro.mock.ipc;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EndpointFactory
 * 
 * Factory for IPC classes
 * 
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public class EndpointFactory {
    public static final Logger logger = LoggerFactory.getLogger(EndpointFactory.class);

    public static final String PROPERTIES_FILE_NAME = "EndpointFactory.properties";
    public static final String CLASS_NAME_PROPERTY  = "org.paternostro.mock.ipc.EndpointFactory.class";
    public static final String CLASS_NAME_DEFAULT   = "org.paternostro.mock.ipc.EndpointFactory";

    private static EndpointFactory   singleton = null;

    public static EndpointFactory getFactory()
    {
        EndpointFactory retval = singleton;

        if (retval == null) {
            synchronized (EndpointFactory.class) {
                retval = singleton;

                if (retval == null) {
                    Properties p = new Properties();

                    try {
                        InputStream propertiesStream = EndpointFactory.class.getResourceAsStream(PROPERTIES_FILE_NAME);

                        if (propertiesStream != null) {
                            p.load(propertiesStream);
                        }
                    } catch (IOException e) {
                        logger.warn("Error reading " + PROPERTIES_FILE_NAME + ", using defaults", e);
                    }

                    String className = p.getProperty(CLASS_NAME_PROPERTY, CLASS_NAME_DEFAULT);

                    try {
                        retval = singleton = (EndpointFactory)Class.forName(className).newInstance();
                        singleton.setProperties(p);
                    } catch (ClassNotFoundException e) {
                        logger.warn("Class " + className + " not found, using defaults", e);
                        retval = singleton = new EndpointFactory();
                    } catch (InstantiationException e) {
                        logger.warn("Class " + className + " instantiation error, using defaults", e);
                        retval = singleton = new EndpointFactory();
                    } catch (IllegalAccessException e) {
                        logger.warn("Class " + className + " instantiation error, using defaults", e);
                        retval = singleton = new EndpointFactory();
                    } catch (ClassCastException e) {
                        logger.warn("Cannot cast class " + className + " to ElkrommFactory, using defaults", e);
                        retval = singleton = new EndpointFactory();
                    }
                }
            }
        }

        return retval;
    }

    private Properties  properties;

    protected EndpointFactory()
    {
        this.properties = new Properties();
    }

    private void setProperties(Properties properties)
    {
        this.properties = properties;
    }

    public static final String PIPE_ENDPOINT_CLASS      = "org.paternostro.mock.ipc.PipeEndpoint.class";
    public static final String PIPE_ENDPOINT_DEFAULT    = "org.paternostro.mock.ipc.PipeEndpointImpl";

    public static final String SOCKET_ENDPOINT_CLASS    = "org.paternostro.mock.ipc.SocketEndpoint.class";
    public static final String SOCKET_ENDPOINT_DEFAULT  = "org.paternostro.mock.ipc.SocketEndpointImpl";

    private Endpoint getEndpoint(String key, String defaultClass, Class<? extends Endpoint> endpointClass)
    {
        Endpoint    retval = null;
        String      className = properties.getProperty(key, defaultClass);

        try {
            retval = (Endpoint)Class.forName(className).newInstance();
        } catch (ClassNotFoundException e) {
            logger.warn("Class " + className + " not found, using defaults", e);
        } catch (InstantiationException|IllegalAccessException e) {
            logger.warn("Class " + className + " instantiation error, using defaults", e);
        } catch (ClassCastException e) {
            logger.warn("Cannot cast class " + className + " to ElkrommSerializer<?>, using defaults", e);
        } finally {
            if (retval == null) {
                try {
                    retval = endpointClass.newInstance();
                } catch (InstantiationException|IllegalAccessException e) {
                    logger.error("Class " + endpointClass.getName() + " cannot be instantiated!", e);
                }
            }
        }

        return retval;
    }

    public Endpoint getPipeEndpoint(Channel writeChannel, Channel readChannel)
    {
        PipeEndpointImpl    retval = (PipeEndpointImpl)getEndpoint(PIPE_ENDPOINT_CLASS, PIPE_ENDPOINT_DEFAULT, PipeEndpointImpl.class);

        retval.init(writeChannel, readChannel);

        return retval;
    }

    public Endpoint getSocketEndpoint(InetAddress inetAddr, int port)
    {
        SocketEndpointImpl  retval = (SocketEndpointImpl)getEndpoint(SOCKET_ENDPOINT_CLASS, SOCKET_ENDPOINT_DEFAULT, SocketEndpointImpl.class);

        retval.init(inetAddr, port);

        return retval;
    }

    public Endpoint getSocketEndpoint(Socket socket)
    {
        SocketEndpointImpl  retval = (SocketEndpointImpl)getEndpoint(SOCKET_ENDPOINT_CLASS, SOCKET_ENDPOINT_DEFAULT, SocketEndpointImpl.class);

        retval.init(socket);

        return retval;
    }
}
