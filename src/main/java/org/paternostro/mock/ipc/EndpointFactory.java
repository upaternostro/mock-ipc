package org.paternostro.mock.ipc;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for {@link Endpoint} instances.
 * <p>
 * {@code EndpointFactory} is the single entry point client/server code
 * should use to obtain {@link Endpoint}s, instead of instantiating
 * {@link ClientSocketEndpointImpl}, {@link ServerSocketEndpointImpl} or {@link PipeEndpointImpl} directly. This
 * keeps production and test code identical: the only thing that changes is
 * which kind of endpoint the factory hands out.
 * <p>
 * The factory itself is a lazily-initialized, thread-safe singleton
 * accessed via {@link #getFactory()}. Both the factory implementation class
 * and the concrete {@link Endpoint} implementation classes it instantiates
 * can be overridden via a {@code EndpointFactory.properties} resource file
 * placed next to this class on the classpath, using the
 * {@link #CLASS_NAME_PROPERTY}, {@link #PIPE_ENDPOINT_CLASS}, {@link #CLIENT_SOCKET_ENDPOINT_CLASS} and
 * {@link #SERVER_SOCKET_ENDPOINT_CLASS} keys; if the file is missing, unreadable,
 * or any configured class cannot be instantiated, the built-in defaults are
 * used instead.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 *
 * @see Endpoint
 */
public class EndpointFactory {
    /** Logger used to report configuration problems while resolving classes. */
    public static final Logger logger = LoggerFactory.getLogger(EndpointFactory.class);

    /**
     * Name of the classpath resource, resolved relative to this class, that
     * may contain overrides for the class names used by this factory.
     */
    public static final String PROPERTIES_FILE_NAME = "EndpointFactory.properties";

    /** Property key used to override the {@code EndpointFactory} implementation class. */
    public static final String CLASS_NAME_PROPERTY  = "org.paternostro.mock.ipc.EndpointFactory.class";

    /** Default {@code EndpointFactory} implementation class, used when no override is configured. */
    public static final String CLASS_NAME_DEFAULT   = "org.paternostro.mock.ipc.EndpointFactory";

    private static EndpointFactory   singleton = null;

    /**
     * Returns the singleton {@code EndpointFactory} instance, creating it on
     * first call.
     * <p>
     * Initialization reads {@value #PROPERTIES_FILE_NAME} from the
     * classpath (if present) to determine which factory implementation
     * class to instantiate; if the property is absent, the file cannot be
     * read, or the configured class cannot be instantiated, a plain
     * {@code EndpointFactory} is used instead. This method is thread-safe.
     *
     * @return the singleton factory instance
     */
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

    /**
     * Creates a factory with empty (default) configuration.
     * <p>
     * Protected so that only {@link #getFactory()} and configured subclasses
     * can create instances; client code should always go through
     * {@link #getFactory()}.
     */
    protected EndpointFactory()
    {
        this.properties = new Properties();
    }

    /**
     * Replaces the configuration properties used to resolve endpoint
     * implementation classes.
     *
     * @param properties the properties loaded from {@value #PROPERTIES_FILE_NAME}
     */
    private void setProperties(Properties properties)
    {
        this.properties = properties;
    }

    /** Property key used to override the pipe {@link Endpoint} implementation class. */
    public static final String PIPE_ENDPOINT_CLASS      = "org.paternostro.mock.ipc.PipeEndpoint.class";

    /** Default pipe {@link Endpoint} implementation class, used when no override is configured. */
    public static final String PIPE_ENDPOINT_DEFAULT    = "org.paternostro.mock.ipc.PipeEndpointImpl";

    /** Property key used to override the socket {@link Endpoint} implementation class for server use. */
    public static final String SERVER_SOCKET_ENDPOINT_CLASS    = "org.paternostro.mock.ipc.ServerSocketEndpoint.class";

    /** Default socket {@link Endpoint} implementation class for server use, used when no override is configured. */
    public static final String SERVER_SOCKET_ENDPOINT_DEFAULT  = "org.paternostro.mock.ipc.ServerSocketEndpointImpl";

    /** Property key used to override the socket {@link Endpoint} implementation class for client use. */
    public static final String CLIENT_SOCKET_ENDPOINT_CLASS    = "org.paternostro.mock.ipc.ClientSocketEndpoint.class";

    /** Default socket {@link Endpoint} implementation class for client use, used when no override is configured. */
    public static final String CLIENT_SOCKET_ENDPOINT_DEFAULT  = "org.paternostro.mock.ipc.ClientSocketEndpointImpl";

    /**
     * Resolves and instantiates an {@link Endpoint} implementation by class
     * name, falling back to a known-good default class if the configured
     * one cannot be used.
     *
     * @param key the properties key that may hold a class name override
     * @param defaultClass the class name to use when {@code key} is not set
     * @param endpointClass the fallback class to instantiate if the resolved
     *        class name cannot be loaded, instantiated, or cast to
     *        {@link Endpoint}
     * @return a freshly instantiated (but not yet {@code init}-ed) endpoint
     */
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

    /**
     * Creates a pipe-based {@link Endpoint}, suitable for unit testing.
     * <p>
     * The returned endpoint is already connected. To build a full duplex
     * pair for a "client" and a "server", create two {@link Channel}s and
     * call this method twice, swapping the write/read channel arguments
     * between the two calls.
     *
     * @param writeChannel the channel the returned endpoint will write to
     * @param readChannel the channel the returned endpoint will read from
     * @return a ready-to-use pipe endpoint
     */
    public Endpoint getPipeEndpoint(Channel writeChannel, Channel readChannel)
    {
        PipeEndpointImpl    retval = (PipeEndpointImpl)getEndpoint(PIPE_ENDPOINT_CLASS, PIPE_ENDPOINT_DEFAULT, PipeEndpointImpl.class);

        retval.init(writeChannel, readChannel);

        return retval;
    }

    /**
     * Creates a socket-based {@link Endpoint} configured to connect to the
     * given address and port.
     * <p>
     * Typically used for the client side of a connection. The returned
     * endpoint is not yet connected: call {@link Endpoint#connect()} before
     * using its streams.
     *
     * @param inetAddr the address to connect to
     * @param port the port to connect to
     * @return a socket endpoint ready to be connected
     */
    public Endpoint getSocketEndpoint(InetAddress inetAddr, int port)
    {
        ClientSocketEndpointImpl    retval = (ClientSocketEndpointImpl)getEndpoint(CLIENT_SOCKET_ENDPOINT_CLASS, CLIENT_SOCKET_ENDPOINT_DEFAULT, ClientSocketEndpointImpl.class);

        retval.init(inetAddr, port);

        return retval;
    }

    /**
     * Creates a socket-based {@link Endpoint} wrapping an already-connected
     * socket.
     * <p>
     * Typically used for the server side of a connection, with a socket
     * obtained from {@link java.net.ServerSocket#accept()}.
     *
     * @param socket the already-connected socket to wrap
     * @return a ready-to-use socket endpoint
     */
    public Endpoint getSocketEndpoint(Socket socket)
    {
        ServerSocketEndpointImpl    retval = (ServerSocketEndpointImpl)getEndpoint(SERVER_SOCKET_ENDPOINT_CLASS, SERVER_SOCKET_ENDPOINT_DEFAULT, ServerSocketEndpointImpl.class);

        retval.init(socket);

        return retval;
    }
}
