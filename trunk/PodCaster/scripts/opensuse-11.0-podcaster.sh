#!/bin/sh

# Set jvm heap initial and maximum sizes (in megabytes).
JAVA_HEAP_INIT_SIZE=64
JAVA_HEAP_MAX_SIZE=192

# Find a java installation.
if [ -z "$JAVA_HOME" ]; then
        echo "Warning: \$JAVA_HOME environment variable not set! Consider setting it."
        echo "          Attempting to locate java..."
        j=`which java 2>/dev/null`
        if [ -z "$j" ]; then
                echo "Failed to locate the java virtual machine! Bailing..."
                exit 1
        else
                echo "Found a virtual machine at: $j..."
                JAVA="$j"
        fi
else
        JAVA="$JAVA_HOME/bin/java"
fi

# Launch application.
CLASSPATH=/usr/share/java/PodCaster.jar
CLASSPATH=$CLASSPATH:/usr/share/java/jaudiotagger.jar
CLASSPATH=$CLASSPATH:/usr/share/java/jdom.jar
CLASSPATH=$CLASSPATH:/usr/share/java/ROME.jar
CLASSPATH=$CLASSPATH:/usr/share/java/commons-logging.jar
CLASSPATH=$CLASSPATH:/usr/share/java/log4j.jar
exec $JAVA -Xms${JAVA_HEAP_INIT_SIZE}M -Xmx${JAVA_HEAP_MAX_SIZE}M -classpath $CLASSPATH org.stanwood.podcaster.PodCaster "$@"
