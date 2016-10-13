
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sintef.jarduino.DigitalPin;
import org.sintef.jarduino.DigitalState;
import org.sintef.jarduino.JArduino;
import org.sintef.jarduino.PinMode;
import org.sintef.jarduino.comm.Serial4JArduino;
//import org.sintef.jarduino.comm.Serial4JArduino;
/*
 Blink
 Turns on an LED on for one second, then off for one second, repeatedly.
 This example code is in the public domain.
 */

public class SH extends JArduino {

    
    public static final String path="/home/pi/Desktop/SmartHouseApp";  // pathLocation
    private DatagramSocket serverSocket;
    protected DB db;

    //// user editable part
    // Pay attention on **
    private static final int NumberOfBindingCommands = 6;// ** Number of commands you want to bind with one or more outputs.

    private final static int port = 2222; // default port can change it, but you have to change it also in android device,
    //not recomented to change it

    private final static String deviceName = "home";// ** is used for global connection for safety, must put it on android device name field in global connection option.

    ///** every startingDeviceID must be unique in every raspberry device contected in local network.
    final static int DeviceID = 0; // Example: if we have 4 raspberry devices connected in local
    // the third will be 2 the fourth will be 3 ...    (it is very important)
    protected ArrayList<String>[] outputPowerCommands = new ArrayList[NumberOfBindingCommands];
    private ArrayList<Integer>[] activatePortOnCommand = new ArrayList[NumberOfBindingCommands];
     ArrayList<String>[] outputCommands = new ArrayList[20]; // list of outputs
    private ArrayList<String> ON, OFF;// = "on", OFF = "off";// word you have to use at the end of the command to activate or deactivate
    private ArrayList<String> ONAtTheStartOfSentence, OFFAtTheStartOfSentence;
    static Jarvis jarvis;
    static Fr fr;

    public SH(String port) {
        super(port);
        

    }
    boolean isOnSwitchView;

    @Override
    protected void setup() {
        try {
            db = new DB(this);
            new SheduleThread().start();
            initArrays();
            initStates();
            initializeOutputCommands();
            initializePowerCommands();
            pinMode(DigitalPin.PIN_0, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_1, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_2, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_3, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_4, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_5, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_6, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_7, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_8, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_9, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_10, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_11, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_12, PinMode.OUTPUT);
            pinMode(DigitalPin.PIN_13, PinMode.OUTPUT);

            pinMode(DigitalPin.A_0, PinMode.OUTPUT);
            pinMode(DigitalPin.A_1, PinMode.OUTPUT);
            pinMode(DigitalPin.A_2, PinMode.OUTPUT);
            pinMode(DigitalPin.A_3, PinMode.OUTPUT);
            pinMode(DigitalPin.A_4, PinMode.OUTPUT);
            pinMode(DigitalPin.A_5, PinMode.OUTPUT);
            isOnSwitchView = false;
            serverSocket = new DatagramSocket(port);
            System.out.println("Waiting for data..");
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void loop() {
        try {
            //        // set the LED on
//        digitalWrite(DigitalPin.PIN_12, DigitalState.HIGH);
//        delay(1000); // wait for a second
//        // set the LED off
//        digitalWrite(DigitalPin.PIN_12, DigitalState.LOW);
//        delay(1000); // wait for a second
            startReceivingData();
        } catch (IOException ex) {
            Logger.getLogger(SH.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) {
        String serialPort = DigitalPin.A_0.toString();

//        if (args.length == 1) {
//            serialPort = args[0];
//        } else {
//          
//            serialPort = Serial4JArduino.selectSerialPort();
//        }
        SH arduino = new SH(serialPort);
        arduino.runArduinoProcess();
        fr = new Fr(arduino);
        jarvis = new Jarvis(arduino);
        jarvis.run();
    }

    ///** 
    // these are the commannds that each device can receive and react,
    // so every outputPowerCommand must be unique in every device contected in local network.
    private void initializePowerCommands() {

        for (int i = 0; i < NumberOfBindingCommands; i++) {
            switch (i) {
                case 0:
                    //Number of command you can put in one Device:outputPowerCommands[0]... outputPowerCommands[RelayNumberOfChanels-1] NO MORE THAN 'RelayNumberOfChanels-1'
                    // else you will have an error mesasge
                    //ALL commands WITH LATIN LETERS 
                    addCommandsAndPorts(i // number of command
                            , new String[]{"kitchen lights", "kitchen light", "koyzina fos", "koyzina fota", "koyzinas fos", "koyzinas fota", "fos koyzina", "fota koyzina", "fos koyzinas", "fota koyzinas"},// command text for reaction
                            new Integer[]{2, 1} // on command 0 these outputs will open or close at once when the previous commands received
                    );
                    break;

                case 1:
                    addCommandsAndPorts(i // command no 1
                            , new String[]{"room light", "room lights", "bedroom light", "bedroom lights", "domatio fos",// command text for reaction
                                "domatio fota", "fos domatio", "fota domatio"},
                            new Integer[]{3, 5});// on command 1 these outputs will open or close at once when the previous commands received
                    break;

                case 2:
                    //Number of command you can put in one Device:outputPowerCommands[0]... outputPowerCommands[RelayNumberOfChanels-1] NO MORE THAN 'RelayNumberOfChanels-1'
                    // else you will have an error mesasge
                    //ALL commands WITH LATIN LETERS 
                    addCommandsAndPorts(i // number of command
                            , new String[]{"office lights", "office light",},// command text for reaction
                            new Integer[]{8, 9} // on command 0 these outputs will open or close at once when the previous commands received
                    );
                    break;

                case 3:
                    addCommandsAndPorts(i // command no 1
                            , new String[]{"tv", "television"},
                            new Integer[]{11, 10});// on command 1 these outputs will open or close at once when the previous commands received
                    break;
                case 4:
                    addCommandsAndPorts(i // command no 1
                            , new String[]{"kitchen"},
                            new Integer[]{12, 13});// on command 1 these outputs will open or close at once when the previous commands received
                case 5:
                    addCommandsAndPorts(i // command no 1
                            , new String[]{"air condition", "cooler"},
                            new Integer[]{14});// on command 1 these outputs will open or close at once when the previous commands received
                case 6:
                    addCommandsAndPorts(i // command no 1
                            , new String[]{"garage"},
                            new Integer[]{15});// on command 1 these outputs will open or close at once when the previous commands received
                case 7:
                    addCommandsAndPorts(i // command no 1
                            , new String[]{"toilet light", "toilet lights"},
                            new Integer[]{16});// on command 1 these outputs will open or close at once when the previous commands received

            }
        }

    }

    //// end of user editable part
    private ArrayList<InetAddress> addresses = new ArrayList<InetAddress>() {

        @Override
        public boolean add(InetAddress e) {
            if (!contains(e)) {
                return super.add(e);
            }
            return false;
        }
    };

    private void initArrays() {

        for (int i = 0; i < NumberOfBindingCommands; i++) {
            outputPowerCommands[i] = new ArrayList<String>();

            activatePortOnCommand[i] = new ArrayList<Integer>();
        }
    }

    // add commands text for reaction and the ports that want to react 
    private void addCommandsAndPorts(int number, String[] reactOnCommands, Integer[] ports) {
        for (int i = 0; i < reactOnCommands.length; i++) {
            outputPowerCommands[number].add(reactOnCommands[i]);
        }
        for (int i = 0; i < ports.length; i++) {
            activatePortOnCommand[number].add(ports[i]);
        }
    }

    private void addCommands(int number, String... reactOnCommands) {
        for (int i = 0; i < reactOnCommands.length; i++) {
            outputPowerCommands[number].add(reactOnCommands[i]);
        }
    }

    private void addPortsOnCommand(int number, Integer... ports) {
        for (int i = 0; i < ports.length; i++) {
            activatePortOnCommand[number].add(ports[i]);
        }
    }

    // greek letters match ( must be latin characters )
    //α=a,β=v,γ=g,δ=d,ε=e,ζ=z,  η=i,ι=i,θ=th,κ=k,
    //λ=l,μ =m, ν=n, ξ=ks, o=ο,ω=o,π=p,ρ=r,
    //ς=s,σ=s,τ=t,υ=y,φ=f,χ=x,ψ=ps
    //in this function you add multi command for each output.
    // these EXACT commands you must send from the Android device (speech or with Switch buttons ) to activate or deactivate the device output
    // Example send command "kitchen light" and "on" or "off" to activate or deactivate the device in output 0. 
    //You can modify your commands.
    private void initializeOutputCommands() {

        for (int i = 0; i < outputCommands.length; i++) {
            outputCommands[i] = new ArrayList<String>();
            String extraOnStart = null;
            if (i >= 13) {
                extraOnStart = "pin ";
            } else {
                extraOnStart = "A ";
            }
            outputCommands[i].add(extraOnStart + DeviceID + " output " + (i));
        }

    }

    private void initStates() {
        ON = new ArrayList<String>();
        OFF = new ArrayList<String>();
        ONAtTheStartOfSentence = new ArrayList<String>();
        OFFAtTheStartOfSentence = new ArrayList<String>();
        ON.add("on");
        ON.add("start");
        ON.add("open");
        OFF.add("off");
        OFF.add("stop");
        OFF.add("close");

        ONAtTheStartOfSentence.add("open");
        ONAtTheStartOfSentence.add("anoikse");
        OFFAtTheStartOfSentence.add("close");
        OFFAtTheStartOfSentence.add("kleise");

    }

    private void startReceivingData() throws IOException {
        boolean isOnSwitchView = false;
        serverSocket = new DatagramSocket(port);
        System.out.println("Waiting for data..");
        while (true) {
            boolean existAsLed;
            isOnSwitchView = false;
            byte[] receiveData = new byte[1024];

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String sentence = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
            //  System.out.println(sentence);
            //             if(!addresses.contains(receivePacket.getAddress()))
            //                 addresses.add(receivePacket.getAddress());
            //             if(!allPorts.contains(receivePacket.getPort()))
            //                 allPorts.add(receivePacket.getPort());
            String uniqueUserID = "1";
            if (sentence.startsWith("userUniqueID:")) {
                uniqueUserID = sentence.split(DB.USER_ID_SPLIT)[0];
                sentence = sentence.substring((uniqueUserID + DB.USER_ID_SPLIT).length());
            } else {
                //  System.out.println("No Unique user id");
            }

            if (sentence.startsWith("speech@@@")) {
                sentence = sentence.substring("speech@@@".length(), sentence.length());
                if (jarvis != null) {
                    boolean posResp = jarvis.processRespond(sentence);
                    if (posResp) {
                        sendData("SpeechCommandOK", receivePacket.getAddress(), receivePacket.getPort());
                        System.out.println("SpeechCommandOK");
                    } else {
                        sendData("SpeechCommandNotOK", receivePacket.getAddress(), receivePacket.getPort());
                        System.out.println("SpeechCommandNotOK");
                    }

                    continue;
                }
            }

            if (sentence.startsWith("globalReturning")) {// used when connect for first time and send ok back, when the android receive the ok open to next view
                String sentence2 = sentence.substring("globalReturning".length());
                if (sentence2.replace(" ", "").equalsIgnoreCase(deviceName.replace(" ", ""))) {
                    addForSending(receivePacket.getAddress(), receivePacket.getPort(), uniqueUserID);
                }
            } else {
                addForSending(receivePacket.getAddress(), receivePacket.getPort(), uniqueUserID);
            }
            if (sentence.startsWith("switch ")) {

                sentence = sentence.substring("switch ".length(), sentence.length());
                System.out.println(sentence);
                //   sendData(sentence, receivePacket.getAddress(), receivePacket.getPort());
                sendToAll(sentence);
                sendTheUpdates(sentence);
                //                 for (int i = 0; i < addresses.size(); i++) {
                //                     for (int k = 0; k < allPorts.size(); k++) {
                //                         try {
                // 
                //                             sendData(sentence, addresses.get(i), allPorts.get(k));
                //                             //System.out.println( addresses.get(i)+" "+allPorts.get(k)+"     "+ receivePacket.getAddress()+ " "+receivePacket.getPort()   );
                //                         } catch (IOException ex) {
                //                             ex.printStackTrace();
                //                         }
                //                     }
                // 
                //                }
                if (!fr.isSwitchModeSelected) {
                    fr.manualSelected();
                    isOnSwitchView = true;
                }
            }

            //            if (isOnSwitchView) { // on switch mode return data to say that the info is here and the light will toght so the toggle button to change status
            //                // this data sends here only on Auto switch view mode
            //                sendData(sentence, receivePacket.getAddress(), receivePacket.getPort());
            ////                System.out.println("send " + sentence);
            //            }
            if (sentence.equalsIgnoreCase("chooseSpeechFunction") || sentence.equalsIgnoreCase("chooseSwitchFunction")
                    || sentence.equalsIgnoreCase("chooseSheduleFunction") || sentence.equalsIgnoreCase("chooseAutomationFunction")
                    || sentence.equalsIgnoreCase("chooseTimerFunction")) {// used when connect for first time and send ok back, when the android receive the ok open to next view
                sendData(sentence, receivePacket.getAddress(), receivePacket.getPort());
                final String sent = sentence;
                new Thread() {
                    public void run() {
                        try {

                            sendData(sent, receivePacket.getAddress(), receivePacket.getPort());
                        } catch (Exception e) {
                        }
                    }
                }.start();
                System.out.println(sentence);
            } else if (sentence.startsWith("globalReturning")) {// used when connect for first time and send ok back, when the android receive the ok open to next view
                sentence = sentence.substring("globalReturning".length());
                System.out.println(sentence + " " + deviceName);
                if (sentence.replace(" ", "").equalsIgnoreCase(deviceName.replace(" ", ""))) {
                    sendData("ok", receivePacket.getAddress(), receivePacket.getPort());
                    System.out.println("<ok/>");
                } else {
                    sendData("wrong", receivePacket.getAddress(), receivePacket.getPort());
                    continue;
                }
                //     addresses.add(receivePacket.getAddress());
            } else if (sentence.startsWith("returning")) {// used when connect for first time and send ok back, when the android receive the ok open to next view
                sendData("ok", receivePacket.getAddress(), receivePacket.getPort());
                System.out.println("<ok/>");
                //     addresses.add(receivePacket.getAddress());
            } //            if (sentence.startsWith("manual_switch_view")) {
            //                String msg = "works";// getAllOutput();
            //                sendData(msg, receivePacket.getAddress(), receivePacket.getPort());
            //                System.out.println("send data");
            //            }
            else if (sentence.startsWith("getAllOutput")) { // I say than I need all the outputs
                String msg = getAllOutput();//"getput on@@@getoutt2 off";//
                System.out.println("msg = " + msg);
                if (msg != null && !msg.replaceAll(" ", "").equalsIgnoreCase("")) {
                    sendData("respondGetAllOutput" + msg, receivePacket.getAddress(), receivePacket.getPort());
                }
            } else if (sentence.startsWith("getAllCommandsOutput")) { // I say than I need all the commands that open ports with each one state ( Example : "kitchen light on" kitchen light is the commands and on or of are the states  )

                String msg = getAllCommandOutput();//"getput on@@@getoutt2 off";//
                if (msg != null && !msg.replaceAll(" ", "").equalsIgnoreCase("")) {
                    sendData("respondGetAllCommandsOutput" + msg, receivePacket.getAddress(), receivePacket.getPort());
                }
                System.out.println(msg);
            } else if (sentence.startsWith("saveShedule")) { // I say than I need all the commands that open ports with each one state ( Example : "kitchen light on" kitchen light is the commands and on or of are the states  )
                //prepei na stelnw device id

                ///saveShedule:DeviceID:0##CommandText:kitchen lights##ActiveDays:2 on3 on5 off##ActiveTime:00:00##IsWeekly:true##IsActive:true
                String usingCommand = sentence.substring("saveShedule:DevideID:".length(), sentence.length());
                String[] list = usingCommand.split(DB.COMMAND_SPLIT_STRING);
                String wantedDeviceIDString = list[0];
                if (Integer.parseInt(wantedDeviceIDString) == DeviceID) {
                    if (!fr.isSheduleModeSelected) {
                        new SheduleView(fr);
                    }
                    usingCommand = usingCommand.substring((wantedDeviceIDString + DB.COMMAND_SPLIT_STRING).length(), usingCommand.length());
                    String out = db.add(usingCommand);
                    if (out != null) {
                        if (!out.equals("addedNotOk")) {
                            sendToAllExcept("Shedules:" + out, receivePacket.getAddress(), receivePacket.getPort());
                            sendData("addedOk", receivePacket.getAddress(), receivePacket.getPort());
                        } else {
                            sendData("addedNotOk", receivePacket.getAddress(), receivePacket.getPort());
                        }
                    }
                }
            } else if (sentence.startsWith("updateShedule")) { // I say than I need all the commands that open ports with each one state ( Example : "kitchen light on" kitchen light is the commands and on or of are the states  )
                //prepei na stelnw device id+
                String usingCommand = sentence.substring(("updateShedule:" + DB.DEVICE_ID).length(), sentence.length());
                String[] list = usingCommand.split(DB.COMMAND_SPLIT_STRING);
                String wantedDeviceIDString = list[0];
                if (Integer.parseInt(wantedDeviceIDString) == DeviceID) {
                    if (!fr.isSheduleModeSelected) {
                        new SheduleView(fr);
                    }
                    usingCommand = usingCommand.substring((wantedDeviceIDString + DB.COMMAND_SPLIT_STRING + DB.COMMAND_ID).length(), usingCommand.length());

                    String wantedCommandID = usingCommand.split(DB.COMMAND_SPLIT_STRING)[0];

                    usingCommand = usingCommand.substring((wantedCommandID + DB.COMMAND_SPLIT_STRING).length(), usingCommand.length());

                    db.updateShedule(usingCommand, wantedCommandID);//thelw command id edw

                }
            } else if (sentence.startsWith("updateSingleShedule")) { // I say than I need all the commands that open ports with each one state ( Example : "kitchen light on" kitchen light is the commands and on or of are the states  )
                //prepei na stelnw device id+

                //updateSingleShedule:DeviceID:0##CommandID:0##CommandText:kitchen lights on##IsActive:false
                String usingCommand = sentence.substring(("updateSingleShedule:" + DB.DEVICE_ID).length(), sentence.length());
                String[] list = usingCommand.split(DB.COMMAND_SPLIT_STRING);
                String wantedDeviceIDString = list[0];
                if (Integer.parseInt(wantedDeviceIDString) == DeviceID) {
                    if (!fr.isSheduleModeSelected) {
                        new SheduleView(fr);
                    }
                    usingCommand = usingCommand.substring((wantedDeviceIDString + DB.COMMAND_SPLIT_STRING + DB.COMMAND_ID).length(), usingCommand.length());

                    String[] list2 = usingCommand.split(DB.COMMAND_SPLIT_STRING);
                    String wantedCommandID = list2[0];

                    usingCommand = list2[1].substring(DB.COMMAND_TEXT_STRING.length(), list2[1].length());
                    String stringModeModify = list2[2];
                    db.updateSingleShedule(usingCommand, wantedCommandID, stringModeModify);//thelw command id edw

                }
            } else if (sentence.startsWith("removeShedule")) { // I say than I need all the commands that open ports with each one state ( Example : "kitchen light on" kitchen light is the commands and on or of are the states  )

                //
                //// px removeShedule(1)" prepei na stelnw device id
                String usingCommand = sentence.substring(("removeShedule:" + DB.DEVICE_ID).length(), sentence.length());
                String[] list = usingCommand.split(DB.COMMAND_SPLIT_STRING);
                String wantedDeviceIDString = list[0];
                //  System.out.println(usingCommand);
                if (Integer.parseInt(wantedDeviceIDString) == DeviceID) {
                    if (!fr.isSheduleModeSelected) {
                        new SheduleView(fr);
                    }
                    usingCommand = list[1].substring((DB.COMMAND_TEXT_STRING).length(), list[1].length());

                    db.removeShedule(usingCommand, list[2]);//thelw command id edw

                }
            } else if (sentence.equals("getShedules")) {

                sendData("Shedules:DeviceID:" + DeviceID + DB.COMMAND_SPLIT_STRING + db.getAllSheduleText(), receivePacket.getAddress(), receivePacket.getPort());
            } else if (sentence.equals("getIDS")) {

                sendData("getIDS" + Integer.toString(DeviceID), receivePacket.getAddress(), receivePacket.getPort());
            } else if (sentence.startsWith("getCommandID")) {
                String idCommandWanted = sentence.substring("getCommandID".length(), sentence.length());

                if (idCommandWanted.equalsIgnoreCase(Integer.toString(DeviceID))) {
                    String msg = getAllCommandOutput();//"getput on@@@getoutt2 off";//
                    if (msg != null && !msg.replaceAll(" ", "").equalsIgnoreCase("")) {
                        sendData("getComandID" + msg, receivePacket.getAddress(), receivePacket.getPort());
                    }
                }

            } else if (sentence.startsWith("newTimer:")) {
                // create new Timer
                String UsingCommand = sentence.substring("newTimer:".length(), sentence.length());
                String[] list = UsingCommand.split(DB.COMMAND_SPLIT_STRING);
                String device_id = list[0].substring(DB.DEVICE_ID.length());
                if (Integer.parseInt(device_id) != DeviceID) {
                    return;
                }
                String timeStamp = list[1].substring(DB.TIME_STAMP.length());
                String command_text = list[2].substring(DB.COMMAND_TEXT_STRING.length());
                if (!db.conainsCommandInDevice(command_text)) {
                    return;
                }
                String timeInSeconds = list[3].substring(DB.SENDING_TIME.length());
                System.out.println("TIMER : device_id = " + device_id + " , timeStamp= " + timeStamp + " , command_text= "
                        + command_text + " , timeInSeconds= " + timeInSeconds);
                sendData("newTimerOK", receivePacket.getAddress(), receivePacket.getPort());
                if (!TimerCountdown.containsTimestamp(Long.parseLong(timeStamp))) {
                    if (!fr.isTimerModeSelected) {
                        new TimerView(fr);
                    }
                    TimerCountdown timer = new TimerCountdown(this, command_text, timeInSeconds, timeStamp);
                    timer.start();
                    sendToAllExcept("Timers:DeviceID:" + DeviceID + DB.COMMAND_SPLIT_STRING + timer.toString(), receivePacket.getAddress(), receivePacket.getPort());
                }
            } else if (sentence.startsWith("getTimers")) {

                sendData("Timers:DeviceID:" + DeviceID + DB.COMMAND_SPLIT_STRING + TimerCountdown.getAllTimers(), receivePacket.getAddress(), receivePacket.getPort());

            } else if (sentence.startsWith("removeTimer:")) {
                // removeTimer:DeviceID:0CommandID:0##TimeStamp:1457625771345##CommandText:kitchen lights on
                String UsingCommand = sentence.substring("removeTimer:".length(), sentence.length());

                String[] list = UsingCommand.split(DB.COMMAND_SPLIT_STRING);
                String device_id = list[0].substring(DB.DEVICE_ID.length());
                if (Integer.parseInt(device_id) != DeviceID) {
                    return;
                }
                String commandID = list[1].substring(DB.COMMAND_ID.length());
                String timeStamp = list[2].substring(DB.TIME_STAMP.length());
                String command_text = list[3].substring(DB.COMMAND_TEXT_STRING.length());

                if (!db.conainsCommandInDevice(command_text)) {
                    return;
                }
                if (!fr.isTimerModeSelected) {
                    new TimerView(fr);
                }
                TimerCountdown.removeFromList(Long.parseLong(timeStamp));
                sendToAll(sentence);

            }
            // 
            //             if (sentence.startsWith("update_manual_mode")) { // I say than I need all the commands that open ports with each one state ( Example : "kitchen light on" kitchen light is the commands and on or of are the states  )
            // 
            //                 String msg = "kouzina fwta on@@@domatio fos on";//getAllOutput();
            //                 if (msg != null && !msg.replaceAll(" ", "").equalsIgnoreCase("")) {
            //                     sendData("update_manual_mode" + msg, receivePacket.getAddress(), receivePacket.getPort());
            //                 }
            // 
            //             }
            existAsLed = processLedString(sentence);
            if (!existAsLed) {

                processCommandString(sentence);
                System.out.println("processCommandString runs ");
            }
        }

    }

    private void addForSending(InetAddress address, int port, String userID) {
        boolean contains = false;
        if (sendingTo.size() >= 20) {
            for (int i = 0; i < resetSendingTo.size() / 2; i++) {
                resetSendingTo.get(i).isRunning = false;
            }
        }

        for (int i = 0; i < sendingTo.size(); i++) {
            Object[] obj = sendingTo.get(i);
            InetAddress ia = (InetAddress) obj[0];
            int prt = (Integer) obj[1];
            String id = (String) obj[2];
            if (ia.equals(address) && prt == port) {
                contains = true;
                for (int j = 0; j < resetSendingTo.size(); j++) {
                    ResetThread rt = resetSendingTo.get(j);
                    if (rt.obj == obj || rt.obj[0].equals(obj[0]) && rt.obj[1].equals(obj[1])) {
                        rt.remaining = rt.maxTime;
                        System.out.println("renew time address =" + ia.toString() + "  port=" + prt);
                    }
                    // resetSendingTo.get(i).remaining= resetSendingTo.get(i).maxTime;
                }
            } else if (userID.equals(id)) {
                contains = true;
                for (int j = 0; j < resetSendingTo.size(); j++) {
                    ResetThread rt = resetSendingTo.get(j);
                    if (rt.userID.equals(userID)) {
                        rt.remaining = rt.maxTime;
                        obj[0] = address;
                        obj[1] = port;
                        System.out.println("renew time address =" + ia.toString() + "  port=" + prt + "  USER ID=" + userID);
                    }
                    // resetSendingTo.get(i).remaining= resetSendingTo.get(i).maxTime;
                }
            }

        }
        if (!contains) {
            final Object[] objects = {address, port, userID};
            sendingTo.add(objects);

            ResetThread thread = new ResetThread(objects) {

                public void run() {
                    System.out.println("added =" + objects[0].toString() + "  port=" + objects[1] + "  USER ID=" + objects[2]);
                    while (remaining > 0 && isRunning) {
                        try {
                            Thread.sleep(sleepingtime);
                        } catch (Exception e) {
                        }
                        remaining -= sleepingtime;

                    }
                    sendingTo.remove(objects);
                    resetSendingTo.remove(this);
                    System.out.println("removed =" + objects[0].toString() + "  port=" + objects[1]);
                }
            };
            thread.start();
            resetSendingTo.add(thread);
        }
        System.out.println("sendingTo.size() =" + sendingTo.size());

    }

//     protected void sendTheUpdates(String command){
//     //   System.out.println("outputPowerCommands = "+command);
//        String mode=null;
//        String shCm=null;
//        if(command.endsWith(" on")){
//            shCm=command.substring(0,command.length()-" on".length());
//            mode="on";
//        }else if(command.endsWith("on")){
//            shCm=command.substring(0,command.length()-"on".length());
//            mode="on";
//        } else if(command.endsWith(" off")){
//            shCm=command.substring(0,command.length()-" off".length());
//            mode="off";
//        } else if(command.endsWith("off")){
//            shCm=command.substring(0,command.length()-"off".length());
//            mode="off"; 
//        }
//        //if(outputPowerCommands.contains(shCm)){
//        for(int j=0;j<outputPowerCommands.length;j++){
//
//            if(outputPowerCommands[j].get(0).equals(shCm)){
//                for(int h=0;h<activatePortOnCommand[j].size();h++){
//                    //
//
//                    for(int p=0;p<pins.length;p++){
//
//                        if(pins[p].getPin().getAddress()== activatePortOnCommand[j].get(h)){
//
//                            System.out.println(DeviceID+ " switch "+p+" "+mode);
//                            sendToAll("switch "+DeviceID+ " output "+p+" "+mode);
//                        }
//                    }
//
//                    // System.out.print( activatePortOnCommand[j].get(h)+" ");
//                    //  System.out.println(h+"switch "+ outputCommands[activatePortOnCommand[j].get(0)]+" "+mode);
//                    //       System.out.println(h+"switch "+ outputCommands[activatePortOnCommand[h].get(0)]+" "+mode);
//
//                } 
//
//                for(int k=0;k<activatePortOnCommand.length;k++){
//                    if(k!=j){
//                        if(activatePortOnCommand[j].containsAll(activatePortOnCommand[k])){
//
//                            sendToAll("switch "+outputPowerCommands[k].get(0)+" "+mode);
//                            // System.out.println("update 1 switch "+outputPowerCommands[k].get(0)+" "+mode);
//                        }
//                        if(activatePortOnCommand[k].containsAll(activatePortOnCommand[j])){
//                            if(mode.equals(OFF.get(0))){
//                                sendToAll("switch "+outputPowerCommands[k].get(0)+" "+mode);
//                            }else if(mode.equals(ON.get(0))){
//                                boolean isActive=true;
//                                for(int kk=0;kk<activatePortOnCommand[k].size();kk++){
//                                    //  System.out.println("j= "+j+"  k= "+k+" kk= "+kk+"  "+activatePortOnCommand[k].get(kk)+"   update 2 switch "+getPinFromOutput(activatePortOnCommand[k].get(kk)).isHigh());
//                                    if(!activatePortOnCommand[j].contains(activatePortOnCommand[k].get(kk)))
//                                        if(!getPinFromOutput(activatePortOnCommand[k].get(kk)).isHigh()){
//                                            isActive=false;
//                                        }
//                                }
//                                if(isActive){
//                                    sendToAll("switch "+outputPowerCommands[k].get(0)+" "+mode);
//                                }
//                            }
//                            // System.out.println("update 2 switch "+outputPowerCommands[k].get(0)+" "+mode);
//                        }
//
//                    }
//                }
//
//            }
//       
//        }
//        for(int i=0;i<outputCommands.length;i++){
//              //System.out.println(outputCommands[i]+"  "+(shCm) );
//            if(outputCommands[i].get(0).equals(shCm)){
//              //   System.out.println("outputCommands[i].equals(shCm) ");
//                  boolean isHight;
//                 if(mode.equals(ON.get(0))){
//                     isHight=true;
//                    }else{
//                    isHight=false;
//                }
//               
//                 boolean isActive=true;
//                int pinAddress=pins[i].getPin().getAddress();
//                for(int j=0;j<activatePortOnCommand.length;j++){
//                    
//                    if(activatePortOnCommand[j].contains(pinAddress)){
//                      for(int k=0;k<activatePortOnCommand[j].size();k++){
//                          
//                  //   System.out.println(activatePortOnCommand[j]+" k= "+k+"  j= "+j+" i= "+i+"  "+activatePortOnCommand[j].get(k)+"  "+
//                  //   Boolean.toString(getPinFromOutput(activatePortOnCommand[j].get(k)).isHigh()==isHight));
//                      
//
//
//                    if(pinAddress!=activatePortOnCommand[j].get(k))
//                    if(getPinFromOutput(activatePortOnCommand[j].get(k)).isHigh()!=isHight){
//                                            isActive=false;
//                                            break;
//                                        }
//                        }
//                        if(isActive){
//                        System.out.println("switch "+outputPowerCommands[j].get(0)+" "+mode);
//                          sendToAll("switch "+outputPowerCommands[j].get(0)+" "+mode);
//                        }
//                    }
//                }
//
//            } 
//        }
//
//        //             for(int j=0;j<outputPowerCommands.length;j++){
//        // 
//        //                 for(int h=0;h<activatePortOnCommand[j].size();h++){
//        //                   
//        //                     boolean isActive=true;
//        //                     for(int p=0;p<pins.length;p++){
//        // 
//        //                         if(pins[p].getPin().getAddress()== activatePortOnCommand[j].get(h)){
//        //                             if(!pins[p].isHigh()){
//        //                                 isActive=false;
//        //                             }
//        //                      
//        //                     }
//        //                     if(isActive&&pins.length>=1){sendToAll("switch "+outputPowerCommands[j].get(0)+" "+mode);
//        //                     }
//        //                
//        // 
//        //             }
//        //         }}
//    }
    public void sendToAllExcept(final String message, InetAddress ia, int port) {

        new Thread() {
            public void run() {
                try {
                    for (int i = 0; i < sendingTo.size(); i++) {
                        Object[] obj = sendingTo.get(i);
                        InetAddress ia = (InetAddress) obj[0];
                        int prt = (Integer) obj[1];
                        try {
                            Thread.sleep(50);
                        } catch (Exception e) {
                        }
                        if (!ia.equals(ia) && prt != port) {
                            sendData(message, ia, prt);
                        }

                    }
                    //                     for (int i = 0; i < addresses.size(); i++) {
                    //                         for (int k = 0; k < allPorts.size(); k++) {
                    //                             if(!addresses.get(i).equals(ia)&&allPorts.get(k)!=port)
                    //                                 sendData(message, addresses.get(i), allPorts.get(k));
                    // 
                    //                         }
                    // 
                    //                     }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }

    public void sendToAll(final String message) {
        new Thread() {
            public void run() {
                try {

                    for (int i = 0; i < sendingTo.size(); i++) {
                        Object[] obj = sendingTo.get(i);
                        InetAddress ia = (InetAddress) obj[0];
                        int prt = (Integer) obj[1];
                        try {
                            Thread.sleep(5);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        sendData(message, ia, prt);

                    }
                    //   }
                    //                     for (int i = 0; i < addresses.size(); i++) {
                    //                         for (int k = 0; k < allPorts.size(); k++) {
                    //                             try{
                    //                                 Thread.sleep(50);
                    //                             }catch(Exception e){}
                    //                             sendData(message, addresses.get(i), allPorts.get(k));
                    // 
                    //                         }
                    // 
                    //     }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }

    private ArrayList<ResetThread> resetSendingTo = new ArrayList<ResetThread>();
    private ArrayList<Object[]> sendingTo = new ArrayList<Object[]>();

    private class ResetThread extends Thread {

        final int maxTime = 20 * 60 * 1000;
        int remaining = maxTime, sleepingtime = 30 * 1000;
        //   ArrayList <Object[]> list;
        boolean isRunning = true;
        Object[] obj;
        String userID;

        public ResetThread(Object[] obj) {
            this.obj = obj;
            this.userID = (String) obj[2];
        }
        //public void run(){}

    }
    DigitalState[] pins = new DigitalState[20];

     String getAllOutput() {

        String output = new String();
        try {
            DigitalState ds = null;
            String isDoing = new String();
            for (int i = 0; i < outputCommands.length; i++) {

                switch (i) {
                    case 0:
                        ds = digitalRead(DigitalPin.PIN_0);
                        break;
                    case 1:
                        ds = digitalRead(DigitalPin.PIN_1);
                        break;
                    case 2:
                        ds = digitalRead(DigitalPin.PIN_2);
                        break;
                    case 3:
                        ds = digitalRead(DigitalPin.PIN_3);
                        break;
                    case 4:
                        ds = digitalRead(DigitalPin.PIN_4);
                        break;
                    case 5:
                        ds = digitalRead(DigitalPin.PIN_5);
                        break;
                    case 6:
                        ds = digitalRead(DigitalPin.PIN_6);
                        break;
                    case 7:
                        ds = digitalRead(DigitalPin.PIN_7);
                        break;
                    case 8:
                        ds = digitalRead(DigitalPin.PIN_8);
                        break;
                    case 9:
                        ds = digitalRead(DigitalPin.PIN_9);
                        break;
                    case 10:
                        ds = digitalRead(DigitalPin.PIN_10);
                        break;
                    case 11:
                        ds = digitalRead(DigitalPin.PIN_11);
                        break;
                    case 12:
                        ds = digitalRead(DigitalPin.PIN_12);
                        break;
                    case 13:
                        ds = digitalRead(DigitalPin.PIN_13);
                        break;
//                  

                    case 14:
                        ds = digitalRead(DigitalPin.A_0);
                        break;
                    case 15:
                        ds = digitalRead(DigitalPin.A_1);
                        break;
                    case 16:
                        ds = digitalRead(DigitalPin.A_2);
                        break;
                    case 17:
                        ds = digitalRead(DigitalPin.A_3);
                        break;
                    case 18:
                        ds = digitalRead(DigitalPin.A_4);
                        break;
                    case 19:
                        ds = digitalRead(DigitalPin.A_5);
                        break;
//                    case 20:
//                     ds = digitalRead(DigitalPin.A_5);
//                        break;

                }
                if (ds == null) {
                    continue;
                }
                if (ds.equals(DigitalState.HIGH)) {
                    isDoing = ON.get(0);
                } else if (ds.equals(DigitalState.LOW)) {
                    isDoing = OFF.get(0);
                }

                //for (int j = 0; j < outputCommands[i].size(); j++) {
                if (i != 0) {
                    output += "@@@";

                }

                pins[i] = ds;
                output += outputCommands[i].get(0) + " " + isDoing;

                //  }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output;
    }

     String getAllCommandOutput() {

        String output = new String();
        try {
            DigitalState ds = null;

            for (int i = 0; i < outputPowerCommands.length; i++) {
                ArrayList<String> isOpenList = new ArrayList<String>();
                String finalIsDoing = ON.get(0);
                for (int j = 0; j < activatePortOnCommand[i].size(); j++) {

                    String isDoing = new String();
                    switch (activatePortOnCommand[i].get(j)) {
                        case 0:
                            ds = digitalRead(DigitalPin.PIN_0);
                            break;
                        case 1:
                            ds = digitalRead(DigitalPin.PIN_1);
                            break;
                        case 2:
                            ds = digitalRead(DigitalPin.PIN_2);
                            break;
                        case 3:
                            ds = digitalRead(DigitalPin.PIN_3);
                            break;
                        case 4:
                            ds = digitalRead(DigitalPin.PIN_4);
                            break;
                        case 5:
                            ds = digitalRead(DigitalPin.PIN_5);
                            break;
                        case 6:
                            ds = digitalRead(DigitalPin.PIN_6);
                            break;
                        case 7:
                            ds = digitalRead(DigitalPin.PIN_7);
                            break;
                        case 8:
                            ds = digitalRead(DigitalPin.PIN_8);
                            break;
                        case 9:
                            ds = digitalRead(DigitalPin.PIN_9);
                            break;
                        case 10:
                            ds = digitalRead(DigitalPin.PIN_10);
                            break;
                        case 11:
                            ds = digitalRead(DigitalPin.PIN_11);
                            break;
                        case 12:
                            ds = digitalRead(DigitalPin.PIN_12);
                            break;
                        case 13:
                            ds = digitalRead(DigitalPin.PIN_13);
                            break;
//                      

                        case 14:
                            ds = digitalRead(DigitalPin.A_0);
                            break;
                        case 15:
                            ds = digitalRead(DigitalPin.A_1);
                            break;
                        case 16:
                            ds = digitalRead(DigitalPin.A_2);
                            break;
                        case 17:
                            ds = digitalRead(DigitalPin.A_3);
                            break;
                        case 18:
                            ds = digitalRead(DigitalPin.A_4);
                            break;
                        case 19:
                            ds = digitalRead(DigitalPin.A_5);
                            break;
//                        case 20:
//                            ds = digitalRead(DigitalPin.A_5);
//                            break;

                    }
                    if (ds == null) {
                        continue;
                    }
                    if (ds.equals(DigitalState.HIGH)) {
                        isDoing = ON.get(0);
                    } else if (ds.equals(DigitalState.LOW)) {
                        isDoing = OFF.get(0);
                    }
                    isOpenList.add(isDoing);

                }
                if (i != 0) {
                    output += "@@@";

                }
                if (isOpenList.contains("off")) {
                    finalIsDoing = OFF.get(0);
                }
                if (!outputPowerCommands[i].isEmpty()) {
                    output += outputPowerCommands[i].get(0) + " " + finalIsDoing;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    protected void sendTheUpdates(String command) {
        try {
            //   System.out.println("outputPowerCommands = "+command);
            String mode = null;
            String shCm = null;
            if (command.endsWith(" on")) {
                shCm = command.substring(0, command.length() - " on".length());
                mode = "on";
            } else if (command.endsWith("on")) {
                shCm = command.substring(0, command.length() - "on".length());
                mode = "on";
            } else if (command.endsWith(" off")) {
                shCm = command.substring(0, command.length() - " off".length());
                mode = "off";
            } else if (command.endsWith("off")) {
                shCm = command.substring(0, command.length() - "off".length());
                mode = "off";
            }
            //if(outputPowerCommands.contains(shCm)){

            for (int j = 0; j < outputPowerCommands.length; j++) {
                System.out.println("shCm=" + shCm + "  vs" + outputPowerCommands[j].get(0));
                if (outputPowerCommands[j].get(0).equals(shCm)) {
                    for (int h = 0; h < activatePortOnCommand[j].size(); h++) {
                        //

                        for (int p = 0; p < pins.length; p++) {

                            if (p == activatePortOnCommand[j].get(h)) {

                                int sendid = (p);
                                System.out.println("switch " + outputCommands[p].get(0) + " " + mode);
                                //    System.out.println("switch "+DeviceID+ " output "+sendid+" "+mode);
                                // sendToAll("switch "+DeviceID+ " output "+sendid+" "+mode);
                                sendToAll("switch " + outputCommands[p].get(0) + " " + mode);
                            }
                        }

                        // System.out.print( activatePortOnCommand[j].get(h)+" ");
                        //  System.out.println(h+"switch "+ outputCommands[activatePortOnCommand[j].get(0)]+" "+mode);
                        //       System.out.println(h+"switch "+ outputCommands[activatePortOnCommand[h].get(0)]+" "+mode);
                    }

                    for (int k = 0; k < activatePortOnCommand.length; k++) {
                        if (k != j) {
                            if (activatePortOnCommand[j].containsAll(activatePortOnCommand[k])) {

                                sendToAll("switch " + outputPowerCommands[k].get(0) + " " + mode);
                                // System.out.println("update 1 switch "+outputPowerCommands[k].get(0)+" "+mode);
                            }
                            if (activatePortOnCommand[k].containsAll(activatePortOnCommand[j])) {
                                if (mode.equals(OFF.get(0))) {
                                    sendToAll("switch " + outputPowerCommands[k].get(0) + " " + mode);
                                } else if (mode.equals(ON.get(0))) {
                                    boolean isActive = true;
                                    for (int kk = 0; kk < activatePortOnCommand[k].size(); kk++) {
                                        //  System.out.println("j= "+j+"  k= "+k+" kk= "+kk+"  "+activatePortOnCommand[k].get(kk)+"   update 2 switch "+getPinFromOutput(activatePortOnCommand[k].get(kk)).isHigh());
                                        if (!activatePortOnCommand[j].contains(activatePortOnCommand[k].get(kk))) {

                                            if (!pins[(activatePortOnCommand[k].get(kk))].equals(DigitalState.HIGH)) {
                                                isActive = false;
                                            }
                                        }
                                    }
                                    if (isActive) {
                                        sendToAll("switch " + outputPowerCommands[k].get(0) + " " + mode);

                                    }
                                }
                                // System.out.println("update 2 switch "+outputPowerCommands[k].get(0)+" "+mode);
                            }

                        }
                    }

                }

            }
            for (int i = 0; i < outputCommands.length; i++) {
                //System.out.println(outputCommands[i]+"  "+(shCm) );
                if (outputCommands[i].get(0).equals(shCm)) {
                    //   System.out.println("outputCommands[i].equals(shCm) ");
                    boolean isHight;
                    if (mode.equals(ON.get(0))) {
                        isHight = true;
                    } else {
                        isHight = false;
                    }

                    boolean isActive = true;
                    int pinAddress = i;
                    for (int j = 0; j < activatePortOnCommand.length; j++) {

                        if (activatePortOnCommand[j].contains(pinAddress)) {
                            for (int k = 0; k < activatePortOnCommand[j].size(); k++) {

                                //   System.out.println(activatePortOnCommand[j]+" k= "+k+"  j= "+j+" i= "+i+"  "+activatePortOnCommand[j].get(k)+"  "+
                                //   Boolean.toString(getPinFromOutput(activatePortOnCommand[j].get(k)).isHigh()==isHight));
                                if (pinAddress != activatePortOnCommand[j].get(k)) {
                                    if (pins[(activatePortOnCommand[j].get(k))].equals(DigitalState.HIGH) != isHight) {
                                        isActive = false;
                                        break;
                                    }
                                }
                            }

                            if (!isActive) {
                                isActive = true;
                                for (int k = 0; k < activatePortOnCommand[j].size(); k++) {

                                    //   System.out.println(activatePortOnCommand[j]+" k= "+k+"  j= "+j+" i= "+i+"  "+activatePortOnCommand[j].get(k)+"  "+
                                    //   Boolean.toString(getPinFromOutput(activatePortOnCommand[j].get(k)).isHigh()==isHight));
                                    if (isHight) {
                                        if (pinAddress != activatePortOnCommand[j].get(k)) {
                                            if (pins[(activatePortOnCommand[j].get(k))].equals(DigitalState.HIGH) != true) {
                                                isActive = false;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }

                            //                         if(!isActive){
                            //                             isActive=true;
                            //                         for(int k=0;k<activatePortOnCommand[j].size();k++){
                            // 
                            //                             //   System.out.println(activatePortOnCommand[j]+" k= "+k+"  j= "+j+" i= "+i+"  "+activatePortOnCommand[j].get(k)+"  "+
                            //                             //   Boolean.toString(getPinFromOutput(activatePortOnCommand[j].get(k)).isHigh()==isHight));
                            // 
                            //                             if(pinAddress!=activatePortOnCommand[j].get(k))
                            //                                 if(getPinFromOutput(activatePortOnCommand[j].get(k)).isHigh()==isHight){
                            //                                     isActive=false;
                            //                                     break;
                            //                                 }
                            //                         }
                            //                         }
                            if (isActive) {
                                System.out.println("switch " + outputPowerCommands[j].get(0) + " " + mode);
                                sendToAll("switch " + outputPowerCommands[j].get(0) + " " + mode);

                            }
                        }
                    }

                }
            }

            //             for(int j=0;j<outputPowerCommands.length;j++){
            // 
            //                 for(int h=0;h<activatePortOnCommand[j].size();h++){
            //                   
            //                     boolean isActive=true;
            //                     for(int p=0;p<pins.length;p++){
            // 
            //                         if(pins[p].getPin().getAddress()== activatePortOnCommand[j].get(h)){
            //                             if(!pins[p].isHigh()){
            //                                 isActive=false;
            //                             }
            //                      
            //                     }
            //                     if(isActive&&pins.length>=1){sendToAll("switch "+outputPowerCommands[j].get(0)+" "+mode);
            //                     }
            //                
            // 
            //             }
            //         }}
            if (fr.isSwitchModeSelected) {
                fr.updateManual();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    private void sendData(String msg, InetAddress IPAddress, int port) throws IOException {
        byte[] sendData;
        sendData = msg.getBytes();
        DatagramPacket sendPacket
                = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        serverSocket.send(sendPacket);
    }

    protected void processCommandString(String input) {
        String isDoing = "off";

        for (int i = 0; i < outputPowerCommands.length; i++) {

            for (int j = 0; j < outputPowerCommands[i].size(); j++) {

                if (input.startsWith(outputPowerCommands[i].get(j))) {
                    isDoing = input.replace(outputPowerCommands[i].get(j), "").replaceAll(" ", "");
                } else {

                    String firstWord = input.split(" ")[0];
                    if (OFFAtTheStartOfSentence.contains(firstWord)) {
                        isDoing = "off";
                        input = input.substring((firstWord + " ").length() + 1, input.length());
                    } else if (ONAtTheStartOfSentence.contains(firstWord)) {
                        isDoing = "on";
                        input = input.substring((firstWord + " ").length() + 1, input.length());

                    }
                }
                if (input.startsWith(outputPowerCommands[i].get(j))) {

                    if (ON.contains(isDoing)) {
                        System.out.println("found command " + outputPowerCommands[i].get(j) + " on" + ", these ports will open: " + activatePortOnCommand[i]);
                        for (int k = 0; k < activatePortOnCommand[i].size(); k++) {
                            ToggleLedNo(activatePortOnCommand[i].get(k), ON.get(0));
                        }
                    } else if (OFF.contains(isDoing)) {
                        System.out.println("found command " + outputPowerCommands[i].get(j) + " off" + ", these ports will close: " + activatePortOnCommand[i]);
                        for (int k = 0; k < activatePortOnCommand[i].size(); k++) {
                            ToggleLedNo(activatePortOnCommand[i].get(k), OFF.get(0));
                        }
                    }
                }

            }
        }
    }

    private void ToggleLedNo(int number, String state) {

        DigitalPin dpin = null;
        switch (number) {
            case 0:
                dpin = DigitalPin.PIN_0;
                break;
            case 1:
                dpin = DigitalPin.PIN_1;
                break;
            case 2:
                dpin = DigitalPin.PIN_2;
                break;
            case 3:
                dpin = DigitalPin.PIN_3;
                break;
            case 4:
                dpin = DigitalPin.PIN_4;
                break;
            case 5:
                dpin = DigitalPin.PIN_5;
                break;
            case 6:
                dpin = DigitalPin.PIN_6;
                break;
            case 7:
                dpin = DigitalPin.PIN_7;
                break;
            case 8:
                dpin = DigitalPin.PIN_8;
                break;
            case 9:
                dpin = DigitalPin.PIN_9;
                break;
            case 10:
                dpin = DigitalPin.PIN_10;
                break;
            case 11:
                dpin = DigitalPin.PIN_11;
                break;
            case 12:
                dpin = DigitalPin.PIN_12;
                break;
            case 13:
                dpin = DigitalPin.PIN_13;
                break;
//           

            case 14:
                dpin = DigitalPin.A_0;
                break;
            case 15:
                dpin = DigitalPin.A_1;
                break;
            case 16:
                dpin = DigitalPin.A_2;
                break;
            case 17:
                dpin = DigitalPin.A_3;
                break;
            case 18:
                dpin = DigitalPin.A_4;
                break;
            case 19:
                dpin = DigitalPin.A_5;
                break;
//            case 20:
//               dpin = DigitalPin.A_5;
//                break;

        }
        if (dpin == null) {
            return;
        }
        if (state.equalsIgnoreCase(ON.get(0))) {
            digitalWrite(dpin, DigitalState.HIGH);
        } else if (state.equalsIgnoreCase(OFF.get(0))) {
            digitalWrite(dpin, DigitalState.LOW);
        }
    }

     boolean processLedString(String input) {
        // get a handle to the GPIO controller
        boolean found = false;
        String isDoing = "off"; // creating the pin with parameter PinState.HIGH
        // will instantly power up the pin
        for (int i = 0; i < outputCommands.length; i++) {

            for (int j = 0; j < outputCommands[i].size(); j++) {
                if (input.startsWith(outputCommands[i].get(j))) {
                    isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                } else {
                    try {
                        String firstWord = input.split(" ")[0];
                        if (OFFAtTheStartOfSentence.contains(firstWord)) {
                            isDoing = "off";
                            input = input.substring((firstWord + " ").length() + 1, input.length());
                        } else if (ONAtTheStartOfSentence.contains(firstWord)) {
                            isDoing = "on";
                            input = input.substring((firstWord + " ").length() + 1, input.length());

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (input.startsWith(outputCommands[i].get(j))) {
                    found = true;
                    switch (i) {
                        case 0:// output no 0
//                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_0, DigitalState.HIGH);
                                System.out.println("led 0 on");
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_0, DigitalState.LOW);
                                System.out.println("led 0 off");
                            }

                            break;
                        case 1:// output no 1
//                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                System.out.println("led 1 on");
                                digitalWrite(DigitalPin.PIN_1, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                System.out.println("led 1 off");
                                digitalWrite(DigitalPin.PIN_1, DigitalState.LOW);
                            }
                            break;
                        case 2:// output no 1
//                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                System.out.println("led 2 on");
                                digitalWrite(DigitalPin.PIN_2, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_2, DigitalState.LOW);
                                System.out.println("led 2 off");
                            }
                            break;
                        case 3:// output no 1
//                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_3, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_3, DigitalState.LOW);
                            }
                            break;
                        case 4:// output no 1
//                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_4, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_4, DigitalState.LOW);
                            }

                            break;
                        case 5:// output no 1

                            if (ON.contains(isDoing)) {
                                System.out.println("led 5 on");
                                digitalWrite(DigitalPin.PIN_5, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                System.out.println("led 5 off");
                                digitalWrite(DigitalPin.PIN_5, DigitalState.LOW);
                            }

                            break;
                        case 6:// output no 1
//                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_6, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_6, DigitalState.LOW);
                            }

                            break;
                        case 7:// output no 1
//                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_7, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_7, DigitalState.LOW);
                            }

                            break;
                        case 8:// output no 1
//                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_8, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_8, DigitalState.LOW);
                            }

                            break;
                        case 9:// output no 1
//                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_9, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_9, DigitalState.LOW);
                            }

                            break;
                        case 10:// output no 1
                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_10, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_10, DigitalState.LOW);
                            }

                            break;
                        case 11:// output no 1
                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_11, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_11, DigitalState.LOW);
                            }

                            break;

                        case 12:// output no 1
                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_12, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_12, DigitalState.LOW);
                            }

                            break;
                        case 13:// output no 1
                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_13, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.PIN_13, DigitalState.LOW);
                            }

                            break;

                        case 14:// output no 1
                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.A_0, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.A_0, DigitalState.LOW);
                            }

                            break;
                        case 15:// output no 1
                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.A_1, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.A_1, DigitalState.LOW);
                            }

                            break;
                        case 16:// output no 1
                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.A_2, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.A_2, DigitalState.LOW);
                            }

                            break;
                        case 17:// output no 1
                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.A_3, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.A_3, DigitalState.LOW);
                            }

                            break;
                        case 18:// output no 1
                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.A_4, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.A_4, DigitalState.LOW);
                            }

                            break;
                        case 19:// output no 1
                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
                            if (ON.contains(isDoing)) {
                                digitalWrite(DigitalPin.A_5, DigitalState.HIGH);
                            } else if (OFF.contains(isDoing)) {
                                digitalWrite(DigitalPin.A_5, DigitalState.LOW);
                            }

                            break;
//                        case 20:// output no 1
//                            isDoing = input.replace(outputCommands[i].get(j), "").replaceAll(" ", "");
//                            if (ON.contains(isDoing)) {
//                                digitalWrite(DigitalPin.A_5, DigitalState.HIGH);
//                            } else if (OFF.contains(isDoing)) {
//                              digitalWrite(DigitalPin.A_5, DigitalState.LOW);
//                            }

//                            break;
                    }

                }

            }
        }

//        pin.high();
//        System.out.println("light is: ON");
//
//        // wait 2 seconds
//        Thread.sleep(2000);
//
//        // turn off GPIO 1
//        pin.low();
//        System.out.println("light is: OFF");
//
//        // wait 1 second
//        Thread.sleep(1000);
//
//        // turn on GPIO 1 for 1 second and then off
//        System.out.println("light is: ON for 1 second");
//        pin.pulse(1000, true);
//
//        // release the GPIO controller resources
        return found;
    }

    private String getTime(Calendar calendar) {

        Date curentDate = calendar.getTime();
        String hour = Integer.toString(curentDate.getHours());
        String min = Integer.toString(curentDate.getMinutes());
        String output;

        if (hour.length() != 2) {
            hour = "0" + hour;
        }

        if (min.length() != 2) {
            min = "0" + min;
        }

        return hour + ":" + min;
    }

    private String getDay(Calendar calendar) {

        Date curentDate = calendar.getTime();
        return Integer.toString((curentDate.getDay() + Calendar.SUNDAY));
    }
    private boolean isSearchingForShedules = true;
    private int secondsSheduleDelay = 10;
    private String prevTime;

    private class SheduleThread extends Thread {

        public void run() {
            new Thread() {
                public void run() {

                }
            }.start();
            while (isSearchingForShedules) {
                try {

                    Thread.sleep(secondsSheduleDelay * 1000);
                    Calendar calendar = Calendar.getInstance();

                    if (fr.shv != null) {
                        if (prevTime == null || !prevTime.equals(getTime(calendar))) {
                            prevTime = getTime(calendar);
                        } else {
                            continue;
                        }
                    }
                    for (int i = 0; i < db.getShedules().size(); i++) {
                        Shedule shedule = db.getShedules().get(i);
                        if (Boolean.parseBoolean(shedule.getIsActive())) {
                            System.out.println("isActive");
                            if (shedule.getActiveDays().contains(getDay(calendar))) {

                                System.out.println("Contains Day");
                                System.out.println(shedule.getTime() + " timers" + getTime(calendar));
                                if (shedule.getTime().equals(getTime(calendar))) {
                                    System.out.println("Time is equal");
                                    // excecute command
                                    String extraString = null;
                                    if (shedule.getActiveDays().contains(getDay(calendar) + " on")) {
                                        extraString = " on";
                                    }
                                    if (shedule.getActiveDays().contains(getDay(calendar) + " off")) {
                                        extraString = " off";
                                    }
                                    System.out.println(shedule.getCommandText() + extraString + "  excecuted");
                                    processCommandString(shedule.getCommandText() + extraString);

                                    if (!Boolean.parseBoolean(shedule.getIsWeekly())) {
                                        shedule.setActiveDays((shedule.getActiveDays().replace(getDay(calendar), "")));
                                    }
                                    //  sendToAll("UpdatedOk:DeviceID:"+SH.DeviceID+DB.COMMAND_SPLIT_STRING+shedule.toString());
                                    String out = db.updateShedule(shedule);
                                    for (int j = 0; j < outputPowerCommands.length; j++) {
                                        String mode = null;
                                        String shCm = null;
                                        if (shedule.getCommandText().endsWith(" on")) {
                                            shCm = shedule.getCommandText().substring(0, shedule.getCommandText().length() - " on".length());
                                            mode = "on";
                                        } else if (shedule.getCommandText().endsWith("on")) {
                                            shCm = shedule.getCommandText().substring(0, shedule.getCommandText().length() - "on".length());
                                            mode = "on";
                                        } else if (shedule.getCommandText().endsWith(" off")) {
                                            shCm = shedule.getCommandText().substring(0, shedule.getCommandText().length() - " off".length());
                                            mode = "off";
                                        } else if (shedule.getCommandText().endsWith("off")) {
                                            shCm = shedule.getCommandText().substring(0, shedule.getCommandText().length() - "off".length());
                                            mode = "off";
                                        }

                                        if (outputPowerCommands[j].get(0).equals(shCm)) {

                                            for (int h = 0; h < activatePortOnCommand[j].size(); h++) {
                                                //
                                                ToggleLedNo(activatePortOnCommand[j].get(h), mode);

                                                // System.out.print( activatePortOnCommand[j].get(h)+" ");
                                                //  System.out.println(h+"switch "+ outputCommands[activatePortOnCommand[j].get(0)]+" "+mode);
                                                //       System.out.println(h+"switch "+ outputCommands[activatePortOnCommand[h].get(0)]+" "+mode);
                                            }

                                        }
                                    }
                                    sendToAll("switch " + shedule.getCommandText() + extraString);
                                    sendTheUpdates(shedule.getCommandText() + extraString);
                                    sendToAll(out);

                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

}
