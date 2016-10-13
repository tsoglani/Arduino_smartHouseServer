Arduino smart house project

******inside the SH.java code you might have to modify:

-path  by default it is "/home/pi/Desktop/SmartHouseApp" : this MUST be the path to this java file 

-NumberOfBindingCommands : the number of commands you want to bind with one or more outputs. -DeviceID : in case you want to use more than one arduino (or any) devices in the same local network, each arduino device must have a unique DeviceID. Example: if we have 4 arduino devices connected to local network, each one MUST have a unique ID : the first Ruspberry device DeviceID will be 0, the second device's DeviceID will be 1 the third will be 2 the fourth will be 3 ... (this is important if you want to open each output seperately)

initializePowerCommans() function (most important): in this function you activate addCommandsAndPorts function; addCommandsAndPorts function has 3 parameters: -the first parameter is an id (inside the code it is "i" and used in switch cases, so it changes automatically) you don't have to change it, except if your Relay has more that 8 outputs.

-the second parameter is an array of a string-text, the first text in this array is sent to the client's device and is used for switching the buttons from the client's device. When you switch the button, the command is sent to arduino server. One or more commands are bound to one or more arduino outputs (see the third parameter paragraph below). This way you can set one or more arduino outputs on and off at once, with one command. You have the option to put more than one commands in case you want to activate or deactivate the outputs with speech commands (you can also do it with speech command, by saying the command and then "on" or "off" word).

-You can download this (https://github.com/tsoglani/Java_SmartHouseClient/blob/master/SmartHouseClient/dist/SmartHouseClient.jar) jar file by pressing "View Raw"; it's a Free testing application for your computer device ( before you run it, make sure that your computer device is on same local network with your raspberry device ).


*** THERE IS A "libs_to_Import" FOLDER WHICH CONTAINS LIBRARIES WHICH MUST BE IMPORTED TO THE PROJECT ( import the from IDE )
