## uncomment to enable remote debugging on port 7001
#DEBUG_OPTS=-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=7001

java $DEBUG_OPTS -mp jars -m nbartlett.jigsaw_osgi
