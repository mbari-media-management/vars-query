package org.mbari.m3.vars.query.queryfx;

import org.junit.Test;
import org.mbari.m3.vars.query.old.util.StateLookup;

import static org.junit.Assert.*;

/**
 * @author Brian Schlining
 * @since 2016-10-17T13:58:00
 */
public class StateLookupTest {

    @Test
    public void testGetConfig() {
        assertNotNull(StateLookup.getConfig());
    }
}
