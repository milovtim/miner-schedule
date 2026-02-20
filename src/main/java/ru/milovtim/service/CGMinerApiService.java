package ru.milovtim.service;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

@Slf4j
public class CGMinerApiService {
    public static final int MAXRECEIVESIZE = 65535;

    private final String command;
    private final InetAddress ip;
    private final Integer port;

    public String rpcCommand() {
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[MAXRECEIVESIZE];
        int len;

        System.out.println("Attempting to send '" + command + "' to " + ip.getHostAddress() + ":" + port);

        try (Socket socket = new Socket(ip, port)) {
            PrintStream ps = new PrintStream(socket.getOutputStream());
            ps.print(command.toLowerCase().toCharArray());
            ps.flush();

            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            while (true) {
                len = isr.read(buf, 0, MAXRECEIVESIZE);
                if (len < 1)
                    break;
                if (buf[len - 1] == '\0') {
                    sb.append(buf, 0, len - 1);
                    break;
                } else {
                    sb.append(buf, 0, len);
                }
            }
        } catch (IOException ioe) {
            log.error("Error sending command to miner host. Message: {}", ioe.getMessage(), ioe);
            return null;
        }
        return sb.toString();
    }

    public CGMinerApiService(String command, InetAddress _ip) {
        this(command, _ip, 4028);
    }

    public CGMinerApiService(String command, InetAddress ip, Integer port) {
        this.command = command;
        this.ip = ip;
        this.port = port;
    }

    public static void main(String[] params) throws Exception {
        String command = "summary";

        if (params.length > 0 && !params[0].trim().isEmpty())
            command = params[0].trim();

        InetAddress ipAddr = InetAddress.getLocalHost();
        if (params.length > 1) {
            String ipStr = params[1];
            if (!ipStr.trim().isEmpty()) {
                byte[] ipSegments = new byte[4];
                String[] ipArr = ipStr.trim().split("\\.");
                for (int i = 0; i < ipArr.length; i++) {
                    ipSegments[i] = (byte) Integer.parseInt(ipArr[i]);
                }
                ipAddr = InetAddress.getByAddress(ipSegments);
            }
        }
        int portInt = 4028;
        if (params.length > 2 && !params[2].trim().isEmpty()) {
            String portStr = params[2].trim();
            portInt = Integer.parseInt(portStr);
        }

        String resp = new CGMinerApiService(command, ipAddr, portInt).rpcCommand();
        log.info(resp);
    }
}

