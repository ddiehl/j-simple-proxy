#!/bin/bash

########################    BEGIN Script parameters   ########################
SCRIPT_NAME="JSimple Proxy"
DEFAULT_APP_FOLDER="/opt/j-simple-proxy"
DEFAULT_ADMIN_USER="admin"
DEFAULT_ADMIN_PASS="!admin"
DEFAULT_PORT=8080
DEFAULT_USER=
DEFAULT_EXEC_USER=$(id|cut -d" " -f1|awk -F "[()]" '{ for (i=2; i<NF; i+=2) print $i }')

JAR_BASENAME="j-simple-proxy"
CONTENT_FOLDER="content"
JDK_FILE="jdk-8u92-linux-x64.tar.gz"
JDK_DOWNLOAD_URL="http://download.oracle.com/otn-pub/java/jdk/8u92-b14/$JDK_FILE"
BASE_FOLDER="$(pwd)"
RUNNING_USER=$(id|cut -d" " -f1|awk -F "[()]" '{ for (i=2; i<NF; i+=2) print $i }') 
########################    END Script parameters     ########################

########################     BEGIN discover platform     ########################
if [[ $OSTYPE == "darwin"* ]]; then 
    PLATFORM="OSX"
elif [[ $OSTYPE == "linux"* ]]; then 
    PLATFORM="linux"
fi
########################     BEGIN discover platform     ########################


echo "#####################################################"
echo "#   Welcome to $SCRIPT_NAME installation script,    #"
echo "#  it's recommended to start this script using sudo #"
echo "#      as some options requires permissions.        #"
echo "#####################################################"



########################     BEGIN Installation variables reading     ########################
while true; do
	read -p "System user to execute app [$DEFAULT_EXEC_USER]: " EXEC_USER
	EXEC_USER=${EXEC_USER:-$DEFAULT_EXEC_USER}
	id -u "$EXEC_USER" >/dev/null 2>&1
	if [ $? ]; then
		break;
	else 
		echo "User does not exist"
	fi
done


read -e -p "Installation folder [$DEFAULT_APP_FOLDER]: " APP_FOLDER
APP_FOLDER=${APP_FOLDER:-$DEFAULT_APP_FOLDER}
#convert folder to real path
if [[ $APP_FOLDER == ~* ]]; then
    APP_FOLDER=$HOME/${APP_FOLDER:2} 
fi 

TEXT_DEFAULT_JDK=""
if [ "$PLATFORM" ==  "linux" ]; then 
    TEXT_DEFAULT_JDK=" [leave it blank if you want the script to download]"
fi
while true; do
   read -e -p "JDK 7+ home folder$TEXT_DEFAULT_JDK: " JDK_FOLDER
   if [[ ( ("$PLATFORM" != "linux") || ( -n "$JDK_FOLDER" ) )  &&  ( ! -d "$JDK_FOLDER" ) ]]; then
      echo "Folder does not exists, please indicate valid value."
   else 
      break;
   fi
done


read -p "Admin username [$DEFAULT_ADMIN_USER]: " ADMIN_USER
ADMIN_USER=${ADMIN_USER:-$DEFAULT_ADMIN_USER}

read -s -p "Admin password [$DEFAULT_ADMIN_PASS]: " ADMIN_PASS
ADMIN_PASS=${ADMIN_PASS:-$DEFAULT_ADMIN_PASS}
echo ""

read -p "Listening Port [$DEFAULT_PORT]: " PORT
PORT=${PORT:-$DEFAULT_PORT}

read -p "Application name [$SCRIPT_NAME]: " APP_NAME
APP_NAME=${APP_NAME:-$SCRIPT_NAME}
########################     END Installation variables reading     ########################


########################     BEGIN Installation folder checking     ########################
if [ -d "$APP_FOLDER" ]; then
   while true; do
      read -p "Installation folder already exists, installing will override it, do you wish to proceed[Y/N]?" yn
      case $yn in
          [Yy]* ) rm -rf $APP_FOLDER; break;;
          [Nn]* ) echo "Aborting installation"; exit;;
          * ) echo "Please answer yes or no.";;
      esac
   done
fi
########################       END Installation folder checking     ########################


########################     BEGIN output installation info     ########################
echo "-----------------------------------------------------"
echo "Going to proceed with installation with: "
echo "Installation folder: $APP_FOLDER"
if [ -z "$JDK_FOLDER" ]; then
   echo "JDK: Script will download"
else 
   echo "JDK: $JDK_FOLDER"
fi
echo "Admin username: $ADMIN_USER"
echo "Listening port: $PORT"
echo "Application name: $APP_NAME" 

read -p "Press enter key to continue..."
########################     END output installation info     #######################





######################## BEGIN Generating application folder    ########################
echo "Creating application folder..."
mkdir -pv "$APP_FOLDER"
mkdir "$APP_FOLDER/logs"
######################## END Generating application folder    ########################



######################## BEGIN JDK Configuration    ########################
echo "Configuring JDK..."
if [ -z "$JDK_FOLDER" ]; then
   echo "Downloading JDK..."
   PWD=$(pwd)
   cd "$APP_FOLDER"
   wget --no-check-certificate --no-cookies --header "Cookie: oraclelicense=accept-securebackup-cookie" $JDK_DOWNLOAD_URL
   tar zxvf "$JDK_FILE" 
   cd jdk1.8*
   JDK_FOLDER=$(pwd)
   cd "$PWD"
fi

ln -s "$JDK_FOLDER" "$APP_FOLDER/jdk"
cd "$APP_FOLDER/jdk"
JDK_FOLDER=$(pwd)

cd "$BASE_FOLDER"

cp -rf $CONTENT_FOLDER/* $APP_FOLDER
######################## END JDK Configuration    ########################



######################## BEGIN config properties replacement    ########################
echo "Adjusting properties file"
CONFIG_FILE="$APP_FOLDER/config/application.yml"

sed "s/__PORT__/$PORT/" "$CONFIG_FILE" > "/tmp/.install_xdr"; mv "/tmp/.install_xdr" "$CONFIG_FILE"
sed "s/__ADMIN_USER__/$ADMIN_USER/" "$CONFIG_FILE" > "/tmp/.install_xdr"; mv "/tmp/.install_xdr" "$CONFIG_FILE"
sed "s/__ADMIN_PASS__/$ADMIN_PASS/" "$CONFIG_FILE" > "/tmp/.install_xdr"; mv "/tmp/.install_xdr" "$CONFIG_FILE"
sed "s/__APP_NAME__/$APP_NAME/" "$CONFIG_FILE" > "/tmp/.install_xdr"; mv "/tmp/.install_xdr" "$CONFIG_FILE"

######################## END config properties replacement    ########################



######################## BEGIN config execution replacement    ########################
echo "Adjusting conf file"
CONFIG_FILE="$APP_FOLDER/$JAR_BASENAME.conf"
sed "s,__JAVA_HOME__,$JDK_FOLDER," "$CONFIG_FILE" > "/tmp/.install_xdr"; mv "/tmp/.install_xdr" "$CONFIG_FILE"

#sed "s,__JAVA_HOME__,$JDK_FOLDER," "$APP_FOLDER/start.sh" > "/tmp/.install_xdr"; mv "/tmp/.install_xdr" "$APP_FOLDER/start.sh"
#sed "s,__JAVA_HOME__,$JDK_FOLDER," "$APP_FOLDER/stop.sh" > "/tmp/.install_xdr"; mv "/tmp/.install_xdr" "$APP_FOLDER/stop.sh"
######################## END config execution replacement    ########################


######################## BEGIN Adjust scripts and create symlink for conf    ########################
cd "$APP_FOLDER"
for jar in $JAR_BASENAME-*.jar; do
   PREFIX_NAME=${jar%????}
   echo "prefix=$PREFIX_NAME"
   echo "JAR=$jar"

   echo "creating conf symlink..."
   ln -sf $JAR_BASENAME.conf $PREFIX_NAME.conf

   echo "Adjusting scripts..."   
   sed "s/__JAR_NAME__/$jar/" start.sh > "/tmp/.install_xdr"; mv "/tmp/.install_xdr" start.sh
   sed "s/__APP_NAME__/$SCRIPT_NAME/" start.sh > "/tmp/.install_xdr"; mv "/tmp/.install_xdr" start.sh
   sed "s/__JAR_NAME__/$jar/" stop.sh > "/tmp/.install_xdr"; mv "/tmp/.install_xdr" stop.sh
   chmod u+x *.sh
done
cd -
######################## END Adjust scripts and create symlink for conf    ########################


######################## BEGIN Create/configure exec user    ########################
if [ "$EXEC_USER" != "$RUNNING_USER" ]; then
   chown -R "$EXEC_USER" "$APP_FOLDER"
fi
######################## BEGIN Create/configure exec user    ########################

echo "Process done!"