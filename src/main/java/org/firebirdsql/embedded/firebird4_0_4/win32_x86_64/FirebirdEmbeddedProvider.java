package org.firebirdsql.embedded.firebird4_0_4.win32_x86_64;

import org.firebirdsql.jna.embedded.classpath.ClasspathFirebirdEmbeddedLibrary;
import org.firebirdsql.jna.embedded.classpath.ClasspathFirebirdEmbeddedResource;
import org.firebirdsql.jna.embedded.spi.FirebirdEmbeddedLibrary;
import org.firebirdsql.jna.embedded.spi.FirebirdEmbeddedLoadingException;

import java.util.Arrays;
import java.util.Collection;

public final class FirebirdEmbeddedProvider implements org.firebirdsql.jna.embedded.spi.FirebirdEmbeddedProvider {

    @Override
    public String getPlatform() {
        return "win32-x86-64";
    }

    @Override
    public String getVersion() {
        return "WI-V4.0.4.3010 Firebird 4.0";
    }

    @Override
    public FirebirdEmbeddedLibrary getFirebirdEmbeddedLibrary() throws FirebirdEmbeddedLoadingException {
        return ClasspathFirebirdEmbeddedLibrary.load(this, new ResourceInfo());
    }

    private static final class ResourceInfo implements ClasspathFirebirdEmbeddedResource {

        @Override
        public Collection<String> getResourceList() {
            return Arrays.asList(
                    "fbclient.dll",
                    "firebird.msg",
                    "ib_util.dll",
                    "icudt63.dll",
                    "icudt63l.dat",
                    "icuin63.dll",
                    "icuuc63.dll",
                    "IDPLicense.txt",
                    "intl/fbintl.conf",
                    "intl/fbintl.dll",
                    "IPLicense.txt",
                    "msvcp140.dll",
                    "plugins/chacha.dll",
                    "plugins/engine13.dll",
                    "plugins/fbtrace.dll",
                    "plugins/legacy_auth.dll",
                    "plugins/legacy_usermanager.dll",
                    "plugins/srp.dll",
                    "plugins/udr/udf_compat.dll",
                    "plugins/udr_engine.conf",
                    "plugins/udr_engine.dll",
                    "plugins.conf",
                    "tzdata/ids.dat",
                    "tzdata/metaZones.res",
                    "tzdata/timezoneTypes.res",
                    "tzdata/windowsZones.res",
                    "tzdata/zoneinfo64.res",
                    "vcruntime140.dll",
                    "zlib1.dll");
        }

        @Override
        public String getLibraryEntryPoint() {
            return "fbclient.dll";
        }
    }
}
