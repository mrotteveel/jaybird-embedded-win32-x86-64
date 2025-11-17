package org.firebirdsql.embedded;

import org.firebirdsql.gds.impl.jni.EmbeddedGDSFactoryPlugin;
import org.firebirdsql.management.FBManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConnectionTest {

    @TempDir
    Path tempDir;

    /**
     * Test if the provider can be loaded and an embedded connection can be established.
     */
    @Test
    void canConnectUsingEmbedded() throws Exception {
        Path database = tempDir.resolve("can-connect.fdb");
        try (FBManager fbManager = new FBManager(EmbeddedGDSFactoryPlugin.EMBEDDED_TYPE_NAME)) {
            fbManager.start();
            assertDoesNotThrow(() -> fbManager.createDatabase(database.toString(), "sysdba", ""),
                    "Database creation failure likely indicates a non-embedded connection was attempted");
        }
        try (Connection connection = assertDoesNotThrow(
                () -> DriverManager.getConnection("jdbc:firebird:embedded:" + database, "sysdba", ""),
                "Connection failure likely indicates a non-embedded connection was attempted");
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "select MON$REMOTE_PROTOCOL from MON$ATTACHMENTS where MON$ATTACHMENT_ID = CURRENT_CONNECTION")) {
            assertTrue(rs.next(), "Expected a row");
            assertNull(rs.getString(1), "Expected remote protocol null for embedded");
        }
    }

}
