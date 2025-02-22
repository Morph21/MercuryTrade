package com.mercury.platform.core.utils;

import com.mercury.platform.shared.config.Configuration;
import com.mercury.platform.shared.store.MercuryStoreCore;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class FileMonitor {
    private static final long POLLING_INTERVAL = 350;
    private Logger logger = LogManager.getLogger(FileMonitor.class.getSimpleName());
    private MessageFileHandler fileHandler;
    private FileAlterationMonitor monitor;

    public FileMonitor() {
        MercuryStoreCore.poeFolderChangedSubject.subscribe(state -> {
            if (monitor != null) {
                try {
                    monitor.stop();
                } catch (Exception e) {
                    logger.error("Error in FileMonitor: ", e);
                }
                start();
            }
        });
    }

    public void start() {
        String gamePath = Configuration.get().applicationConfiguration().get().getGamePath();

        File folder = new File(gamePath + "logs");

        if (new File(gamePath + "logs/Client.txt").exists()) {
            this.fileHandler = new MessageFileHandler(gamePath + "logs/Client.txt");
        } else if (new File(gamePath + "logs/KakaoClient.txt").exists()){
            this.fileHandler = new MessageFileHandler(gamePath + "logs/KakaoClient.txt");
        }

        FileAlterationObserver observer = new FileAlterationObserver(folder);
        monitor = new FileAlterationMonitor(POLLING_INTERVAL);
        FileAlterationListener listener = new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(File file) {
                fileHandler.parse();
            }
        };

        observer.addListener(listener);
        monitor.addObserver(observer);
        try {
            monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
