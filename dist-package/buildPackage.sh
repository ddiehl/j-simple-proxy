#!/bin/bash

#############################################
##     Builds JSimpleProxy package         ##
##                                         ##
## Author: Daniel Conde Diehl - 2016-05-15 ##
#############################################

######################## BEGIN Script variables    ########################
BASEDIR=$(pwd)
JAR_BASENAME="j-simple-proxy"
########################   END Script variables    ########################


################# BEGIN Copying contents to stage  ########################
rm -rf .stage 2>/dev/null
mkdir .stage
cp -rf content .stage/
cp install-script.sh .stage/
#################   END Copying contents to stage  ########################


#################   BEGIN building Maven Project  ########################
echo "Building maven project..."
cd ..
mvn clean package
cd -
#################     END building Maven Project  ########################


#################   BEGIN Copying builded JAR  ########################
echo "Copying generated package ..."
cd "../target/"
cp -v $JAR_BASENAME-*.jar "$BASEDIR/.stage/content"
cd -
#################   END Copying builded JAR  ########################


#################   BEGIN Generating tar package  ########################
cd .stage
echo "Generating final package..."
rm ../$JAR_BASENAME.tar.gz 2>/dev/null
tar zcvf ../$JAR_BASENAME.tar.gz *
cd - 
#################   END Generating tar package  ########################



#################   Cleaning up stage  ########################
rm -rf .stage

echo "Process done."
