package ru.milovtim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.milovtim.domain.MinerItem;
import ru.milovtim.repo.MinerItemRepo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimpleTcpPoller {
    // Список хостов для опроса
    private final MinerItemRepo itemRepo;

    public void pollHost() {
        Optional<MinerItem> byAlias = itemRepo.findByAlias("asic1");

        byAlias.map(item -> item.ipAddr())
                .map(ip -> new InetSocketAddress(ip, 4028))
                .map(address -> sendCommand(address, this::handleCommand))
                .ifPresent(log::info);
    }

    private ThreadLocal<Integer> waitIfTimeout = ThreadLocal.withInitial(() -> 0);

    private String sendCommand(InetSocketAddress address, Function<Socket, String> socketConsumer) {
        try (Socket socket = new Socket()) {
            if (this.waitIfTimeout.get() > 0) {
                Thread.sleep(waitIfTimeout.get());
                waitIfTimeout.set(0);
            }
            log.info("Connecting to {}...", address);
            socket.connect(address, 3000);
            log.info("Connected to {} success.", address);
            return socketConsumer.apply(socket);
        } catch (SocketTimeoutException ste) {
            log.warn("Can't connect to socket: {}", address, ste);
            waitIfTimeout.set(3000);
        } catch (IOException e) {
            log.error("Error connecting {}", address, e);
            // Переподключение произойдет автоматически в следующем цикле scheduler
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private String handleCommand(Socket socket) {
        StringBuilder sb = new StringBuilder();
        try {
            PrintStream ps = new PrintStream(socket.getOutputStream());
            String command = "stats";
            ps.print(command.toLowerCase().toCharArray());
            ps.flush();

            char[] buf = new char[CGMinerApiService.MAXRECEIVESIZE];

            int len;
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            while (true) {
                len = isr.read(buf, 0, CGMinerApiService.MAXRECEIVESIZE);
                if (len < 1)
                    break;
                if (buf[len - 1] == '\0') {
                    sb.append(buf, 0, len - 1);
                    break;
                } else {
                    sb.append(buf, 0, len);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

}
