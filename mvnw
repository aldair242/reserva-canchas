#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.3.2
#
# Environment Variable Prerequisites
#
#   JAVA_HOME           Must point at your Java Development Kit installation.
#   MAVEN_OPTS          (Optional) Java runtime options used when Maven is executed.
#   MAVEN_SKIP_RC       (Optional) Flag to disable loading of mavenrc files.
# ----------------------------------------------------------------------------

if [ -z "$MAVEN_SKIP_RC" ] ; then

  if [ -f /usr/local/etc/mavenrc ] ; then
    . /usr/local/etc/mavenrc
  fi

  if [ -f /etc/mavenrc ] ; then
    . /etc/mavenrc
  fi

  if [ -f "$HOME/.mavenrc" ] ; then
    . "$HOME/.mavenrc"
  fi

fi

# OS specific support.  $var _must_ be set to either true or false.
solaris=false
darwin=false
case "$(uname)" in
    Darwin*) darwin=true;;
    SunOS*) solaris=true;;
esac

if [ -z "$JAVA_HOME" ] ; then
  if $darwin; then
    if [ -x "/usr/libexec/java_home" ] ; then
      JAVA_HOME=$(/usr/libexec/java_home)
    fi
  fi
fi

if [ -z "$JAVA_HOME" ] ; then
  javaExecutable="$(which javac)"
  if [ -n "$javaExecutable" ] && ! [ "$(expr "$javaExecutable" : '\([^ ]*')" = "no" ]; then
    JAVA_HOME=$(dirname "$(dirname "$(readlink -f "$javacExecutable" 2>/dev/null || echo "$javacExecutable")")
  fi
fi

if [ ! -x "$JAVA_HOME/jre/sh/java" ] ; then
  JAVACMD="$JAVA_HOME/bin/java"
else
  JAVACMD="$JAVA_HOME/jre/sh/java"
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "The JAVA_HOME environment variable is not defined correctly, this environment variable is needed to run this program" >&2
  exit 1
fi

# traverses directory structure from process work directory to filesystem root
# first directory with .mvn subdirectory is considered project base directory
find_maven_basedir() {
  if [ -z "$1" ]
  then
    echo "$1"
  else
    echo "$(pwd)"
  fi

  local basedir
  local wdir
  while true
  do
    if [ -d "$wdir"/.mvn ]
    then
      basedir="$wdir"
      break
    elif [ "$wdir" = "/" ]
    then
      break
    fi
    wdir="$(dirname "$wdir")"
  done

  echo "$basedir"
}

# concatenates all lines of a file
concat_lines() {
  if [ -f "$1" ]; then
    echo "$(tr -s '\n' ' ' < "$1")"
  fi
}

MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$(find_maven_basedir "$0")"
WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        echo "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME" >&2
        echo "" >&2
        echo "Please set the JAVA_HOME variable in your environment to match the" >&2
        echo "location of your Java installation." >&2
        exit 1
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || { echo "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH." >&2
        echo "" >&2
        echo "Please set the JAVA_HOME variable in your environment to match the" >&2
        echo "location of your Java installation." >&2
        exit 1
    }
fi

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin ; then
    [ -n "$JAVA_HOME" ] && JAVA_HOME=$(cygpath --unix "$JAVA_HOME")
    [ -n "$CLASSPATH" ] && CLASSPATH=$(cygpath --path --unix "$CLASSPATH")
fi

# For MinGW, ensure paths are in UNIX format before anything is touched
if $mingw ; then
    [ -n "$JAVA_HOME" ] &&
        JAVA_HOME="$(cd "$JAVA_HOME" && pwd)"
fi

# For OS400
if $os400 ; then
    # Use qsh
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        echo "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME" >&2
        exit 1
    fi
fi

# For Darwin, use the correct java_home command if available
if $darwin && [ -z "$JAVA_HOME" ] && [ -x "/usr/libexec/java_home" ]; then
    JAVA_HOME=$(/usr/libexec/java_home)
    JAVACMD="$JAVA_HOME/bin/java"
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin ; then
    [ -n "$JAVA_HOME" ] && JAVA_HOME=$(cygpath --path --windows "$JAVA_HOME")
    [ -n "$CLASSPATH" ] && CLASSPATH=$(cygpath --path --windows "$CLASSPATH")
    [ -n "$MAVEN_PROJECTBASEDIR" ] && MAVEN_PROJECTBASEDIR=$(cygpath --path --windows "$MAVEN_PROJECTBASEDIR")
fi

WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"

if [ -f "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties" ]; then
    # Read the property file
    WRAPPER_URL=$(grep -v '^#' "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties" 2>/dev/null | grep "wrapperUrl" | sed 's/\r$//' | cut -d '=' -f2-)
fi

# if the wrapper jar can be downloaded if not present
if [ ! -f "$WRAPPER_JAR" ]; then
    echo "Couldn't find $WRAPPER_JAR" >&2
    if [ "$MVNW_REPOURL" != "" ]; then
        WRAPPER_URL="$MVNW_REPOURL/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"
    fi
    echo "Downloading from: $WRAPPER_URL" >&2
    if command -v wget > /dev/null; then
        wget -q -O "$WRAPPER_JAR" "$WRAPPER_URL" || echo "wget failed" >&2
    elif command -v curl > /dev/null; then
        curl -sL -o "$WRAPPER_JAR" "$WRAPPER_URL" || echo "curl failed" >&2
    fi
fi

if [ ! -f "$WRAPPER_JAR" ]; then
    echo "Failed to download $WRAPPER_JAR" >&2
    exit 1
fi

# Provide a "standardized" way to retrieve the CLI args that will
# work with both Windows and non-Windows executions.
MAVEN_CMD_LINE_ARGS="$MAVEN_CONFIG $*"

# For Cygwin, switch paths to Windows format before running java
if $cygwin ; then
    [ -n "$JAVA_HOME" ] && JAVA_HOME=$(cygpath --path --windows "$JAVA_HOME")
    [ -n "$CLASSPATH" ] && CLASSPATH=$(cygpath --path --windows "$CLASSPATH")
    [ -n "$MAVEN_PROJECTBASEDIR" ] && MAVEN_PROJECTBASEDIR=$(cygpath --path --windows "$MAVEN_PROJECTBASEDIR")
fi

# If specified, validate the SHA-256 sum of the Maven distribution zip file
DISTRIBUTION_SHA256=$(grep -v '^#' "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties" 2>/dev/null | grep "distributionSha256Sum" | sed 's/\r$//' | cut -d '=' -f2-)
WRAPPER_SHA256=$(grep -v '^#' "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties" 2>/dev/null | grep "wrapperSha256Sum" | sed 's/\r$//' | cut -d '=' -f2-)

if [ -n "$WRAPPER_SHA256" ]; then
    echo -n "Checking Maven Wrapper SHA-256 hash..." >&2
    if command -v shasum > /dev/null 2>&1; then
        WRAPPER_SHA256_FOUND=$(shasum -a 256 "$WRAPPER_JAR" | sed 's/ .*//')
    elif command -v sha256sum > /dev/null 2>&1; then
        WRAPPER_SHA256_FOUND=$(sha256sum "$WRAPPER_JAR" | sed 's/ .*//')
    else
        echo "FAILED" >&2
        echo "Neither shasum nor sha256sum was not found, cannot verify the Maven wrapper SHA-256 hash." >&2
        exit 1
    fi
    if [ "$WRAPPER_SHA256" != "$WRAPPER_SHA256_FOUND" ]; then
        echo "FAILED" >&2
        echo "Maven wrapper SHA-256 hash verification failed: expected $WRAPPER_SHA256 but found $WRAPPER_SHA256_FOUND" >&2
        echo "If you updated your Maven version, you need to update the specified wrapperSha256Sum property." >&2
        exit 1
    fi
    echo "Done" >&2
fi

MAVEN_OPTS="$(concat_lines "$MAVEN_PROJECTBASEDIR/.mvn/jvm.config") $MAVEN_OPTS"

# Provide a "standardized" way to retrieve the CLI args that will
# work with both Windows and non-Windows executions.
MAVEN_CMD_LINE_ARGS="$MAVEN_CONFIG $*"

# For Cygwin, switch paths to Windows format before running java
if $cygwin ; then
    [ -n "$JAVA_HOME" ] && JAVA_HOME=$(cygpath --path --windows "$JAVA_HOME")
    [ -n "$CLASSPATH" ] && CLASSPATH=$(cygpath --path --windows "$CLASSPATH")
    [ -n "$MAVEN_PROJECTBASEDIR" ] && MAVEN_PROJECTBASEDIR=$(cygpath --path --windows "$MAVEN_PROJECTBASEDIR")
fi

# For MinGW, switch paths to Windows format before running java
if $mingw ; then
    [ -n "$JAVA_HOME" ] &&
        JAVA_HOME="$(cd "$JAVA_HOME" && pwd)"
fi

# For OS400
if $os400 ; then
    # Use qsh
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
fi

# For Darwin, use the correct java_home command if available
if $darwin && [ -z "$JAVA_HOME" ] && [ -x "/usr/libexec/java_home" ]; then
    JAVA_HOME=$(/usr/libexec/java_home)
    JAVACMD="$JAVA_HOME/bin/java"
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin ; then
    [ -n "$JAVA_HOME" ] && JAVA_HOME=$(cygpath --path --windows "$JAVA_HOME")
    [ -n "$CLASSPATH" ] && CLASSPATH=$(cygpath --path --windows "$CLASSPATH")
    [ -n "$MAVEN_PROJECTBASEDIR" ] && MAVEN_PROJECTBASEDIR=$(cygpath --path --windows "$MAVEN_PROJECTBASEDIR")
fi

WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

# Provide a "standardized" way to retrieve the CLI args that will
# work with both Windows and non-Windows executions.
MAVEN_CMD_LINE_ARGS="$MAVEN_CONFIG $*"

# For Cygwin, switch paths to Windows format before running java
if $cygwin ; then
    [ -n "$JAVA_HOME" ] && JAVA_HOME=$(cygpath --path --windows "$JAVA_HOME")
    [ -n "$CLASSPATH" ] && CLASSPATH=$(cygpath --path --windows "$CLASSPATH")
    [ -n "$MAVEN_PROJECTBASEDIR" ] && MAVEN_PROJECTBASEDIR=$(cygpath --path --windows "$MAVEN_PROJECTBASEDIR")
fi

# For MinGW, switch paths to Windows format before running java
if $mingw ; then
    [ -n "$JAVA_HOME" ] &&
        JAVA_HOME="$(cd "$JAVA_HOME" && pwd)"
fi

# For OS400
if $os400 ; then
    # Use qsh
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
fi

# For Darwin, use the correct java_home command if available
if $darwin && [ -z "$JAVA_HOME" ] && [ -x "/usr/libexec/java_home" ]; then
    JAVA_HOME=$(/usr/libexec/java_home)
    JAVACMD="$JAVA_HOME/bin/java"
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin ; then
    [ -n "$JAVA_HOME" ] && JAVA_HOME=$(cygpath --path --windows "$JAVA_HOME")
    [ -n "$CLASSPATH" ] && CLASSPATH=$(cygpath --path --windows "$CLASSPATH")
    [ -n "$MAVEN_PROJECTBASEDIR" ] && MAVEN_PROJECTBASEDIR=$(cygpath --path --windows "$MAVEN_PROJECTBASEDIR")
fi

# For MinGW, switch paths to Windows format before running java
if $mingw ; then
    [ -n "$JAVA_HOME" ] &&
        JAVA_HOME="$(cd "$JAVA_HOME" && pwd)"
fi

# For OS400
if $os400 ; then
    # Use qsh
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
fi

# For Darwin, use the correct java_home command if available
if $darwin && [ -z "$JAVA_HOME" ] && [ -x "/usr/libexec/java_home" ]; then
    JAVA_HOME=$(/usr/libexec/java_home)
    JAVACMD="$JAVA_HOME/bin/java"
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin ; then
    [ -n "$JAVA_HOME" ] && JAVA_HOME=$(cygpath --path --windows "$JAVA_HOME")
    [ -n "$CLASSPATH" ] && CLASSPATH=$(cygpath --path --windows "$CLASSPATH")
    [ -n "$MAVEN_PROJECTBASEDIR" ] && MAVEN_PROJECTBASEDIR=$(cygpath --path --windows "$MAVEN_PROJECTBASEDIR")
fi

exec "$JAVACMD" \
  $MAVEN_OPTS \
  $MAVEN_DEBUG_OPTS \
  -classpath "$WRAPPER_JAR" \
  "-Dmaven.multiModuleProjectDirectory=${MAVEN_PROJECTBASEDIR}" \
  ${WRAPPER_LAUNCHER} \
  "$@
