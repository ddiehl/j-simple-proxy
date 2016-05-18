# JSImple Proxy 
JSimple proxy is a minimalist proxy tool, that's intended to be used for routing web services calls, helping applications in different networks to communicate.

This project is written in Java which makes it O.S. agnostic, you don't need to worry about system dependencies and copmatibilities, all that you need is a JVM 7+ in the machine (which can be downloaded using the install script).

An admin tool is also provided to make proxy configuration easy. After installing JSimple Proxy, just open your browser and access [http://<<YOU_HOST>>:<<YOUR_PORT>>/_jSimpleProxyAdmin/](http://<<YOU_HOST>>:<<YOUR_PORT>>/_jSimpleProxyAdmin/).

## Installation instructions
Bundled in the package tarball are the application files and the install script. Installing the application in Linux/MAC is very easy:

- Download tarball: eg: __j-simple-proxy-1.0.tar.gz__
- Uncompress it: tar zxvf __j-simple-proxy-1.0.tar.gz__
- Execute the install-script.sh (highly recommended to run as root): __sudo ./install-script.sh__
- Script will ask information about the desired configuration, once it's done the application will be installed.

## Initialization instructions
Located in the installation folder you indicated during the installation script's execution are two scripts:

- start.sh: _Initializes the application_
- stop.sh:  _Shuts down the application_

The application logs are located in a folder logs within the application folder. (this location can be changed in config/logback-conf.xml)

## Configuring a proxy

Once the application is up-and-running, open the admin tools web-page [http://<<YOU_HOST>>:<<YOUR_PORT>>/_jSimpleProxyAdmin/](http://<<YOU_HOST>>:<<YOUR_PORT>>/_jSimpleProxyAdmin/).

Once you login (using the credentials you've configured during the installation script's execution), click in the config option on the menu. 
The config page allows you to create as many proxies as you want. To configure a proxy you should inform an origin (relative path, should start with /) and a destination (full url of the destination for the proxy).

Example: __Origin__: /new-proxy, __destination__: http://192.168.0.5/recipes.json

Once JSimple Proxy receives a call to: 
[http://<<YOU_HOST>>:<<YOUR_PORT>>/new-proxy](http://<<YOU_HOST>>:<<YOUR_PORT>>/new-proxy)

It will proxy the call to [http://192.168.0.5/recipes.json](http://192.168.0.5/recipes.json)


There are three configuration types that you can create: 

- __Direct proxy:__ This is exactly like the example above, from one URL it's directed to another URL. No Parameters used
- __Wildcard proxy:__ You can also use wilcard in the origin. Eg:

	- Examples:
		- __Origin__: /*, __destination__: http://192.168.0.5/recipes.json
		- __Origin__: /new-proxy/*, __destination__: http://192.168.0.5/recipes.json
		- __Origin__: /*/new-proxy, __destination__: http://192.168.0.5/recipes.json
- __Proxy with variables:__ Another option is to use variables in the origin/destination. Variables allows you to pass a value from the origin to destination (although you're not required to use the variables from origin in the destination)
	- Examples:
		- __Origin__: /recipes/{type}, __destination__: http://192.168.0.5/recipes.json?type={type}
		- __Origin__: /new-proxy/{type}/{status}, __destination__: http://192.168.0.5/recipes.json?type={type}



