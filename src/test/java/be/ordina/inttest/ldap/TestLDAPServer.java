package be.ordina.inttest.ldap;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifs;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.apache.directory.shared.client.api.LdapApiIntegrationUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(FrameworkRunner.class)
@CreateLdapServer(
    transports =
      {
        @CreateTransport(protocol = "LDAP")
      })
public class TestLDAPServer extends AbstractLdapTestUnit
{
    private static final String DEFAULT_HOST = "localhost";
    private static final String ADMIN_DN = "uid=admin,ou=system";

    private LdapConnection connection;


    @Before
    public void setup() throws Exception
    {
        connection = LdapApiIntegrationUtils.getPooledAdminConnection( getLdapServer() );
    }


    @After
    public void shutdown() throws Exception
    {
        LdapApiIntegrationUtils.releasePooledAdminConnection( connection, getLdapServer() );
    }

     /**
     * Test a successful bind request
     *
     * @throws IOException
     */
    @Test
    public void testBindRequest() throws Exception
    {
        final LdapConnection connection = new LdapNetworkConnection( "localhost", getLdapServer().getPort() );
        try
        {
            connection.bind( ADMIN_DN, "secret" );
            assertTrue( connection.isAuthenticated() );
        }
        finally
        {
            if ( connection != null )
            {
                connection.close();
            }
        }
    }

    @ApplyLdifs(
        {
            "dn: uid=cnorris,ou=system",
            "objectClass: organizationalPerson",
            "objectClass: person",
            "objectClass: inetOrgPerson",
            "objectClass: top",
            "cn: chuck norris",
            "sn: cnorris",
            "uid: cnorris"
    })
    @Test
    public void testLookup() throws Exception
    {
        final Entry entry = connection.lookup( "uid=cnorris,ou=system");
        assertNotNull( entry );
        assertTrue(entry.toString().contains("chuck norris"));
    }

}
