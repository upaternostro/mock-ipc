package org.paternostro.mock.ipc;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EndpointFactoryTest {
    @Test
    public void testFactory() {
        Object factory = EndpointFactory.getFactory();

        assertNotNull(factory);
        assertTrue(factory instanceof EndpointFactory);
    }

    @Test
    public void testFactorySingleton() {
        Object factory = EndpointFactory.getFactory();

        assertNotNull(factory);
        assertTrue(factory instanceof MyEndpointFactory);

        Object factory2 = EndpointFactory.getFactory();

        assertNotNull(factory2);
        assertTrue(factory2 instanceof MyEndpointFactory);
        assertSame(factory, factory2);
    }
}
