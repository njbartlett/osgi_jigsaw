DEBUG_OPTS="-Dsun.reflect.debugModuleAccessChecks=true \
    --add-opens java.base/java.lang=nbartlett.jigsaw_osgi \
    --add-opens java.base/java.net=nbartlett.jigsaw_osgi \
    --add-opens java.base/java.security=nbartlett.jigsaw_osgi \
    --add-opens java.base/sun.net.www.protocol.file=nbartlett.jigsaw_osgi \
    --add-opens java.base/sun.net.www.protocol.ftp=nbartlett.jigsaw_osgi \
    --add-opens java.base/sun.net.www.protocol.http=nbartlett.jigsaw_osgi \
    --add-opens java.base/sun.net.www.protocol.https=nbartlett.jigsaw_osgi \
    --add-opens java.base/sun.net.www.protocol.jar=nbartlett.jigsaw_osgi \
    --add-opens nbartlett.jigsaw_osgi/org.apache.felix.framework=ALL-UNNAMED"

## uncomment to enable remote debugging on port 7001
# DEBUG_OPTS="$DEBUG_OPTS -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=7001"

java $DEBUG_OPTS --module-path jars --module nbartlett.jigsaw_osgi
