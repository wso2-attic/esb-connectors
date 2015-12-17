/**
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * <p/>
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.inbound.localfile;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.core.SynapseEnvironment;
import org.wso2.carbon.inbound.endpoint.protocol.generic.GenericPollingConsumer;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public class LocalFileOneTimePolling extends GenericPollingConsumer {
    private static final Log log = LogFactory.getLog(LocalFileOneTimePolling.class);

    private String injectingSeq;
    private String watchedDir;
    private String contentType;
    private String actionAfterProcess;
    private String moveFileURI;
    private String processBeforeWatch;
    private boolean isPolled = false;

    public LocalFileOneTimePolling(Properties localFileProperties, String name,
                                   SynapseEnvironment synapseEnvironment, long scanInterval,
                                   String injectingSeq, String onErrorSeq, boolean coordination,
                                   boolean sequential) {
        super(localFileProperties, name, synapseEnvironment, scanInterval, injectingSeq, onErrorSeq,
                coordination, sequential);
        this.injectingSeq = injectingSeq;
        this.onErrorSeq = onErrorSeq;
        this.coordination = coordination;
        this.sequential = sequential;
        setUpParameters(localFileProperties);
        log.info("Initialized the custom LocalFile inbound consumer.");
    }

    /**
     * Load needed parameters from the localFile inbound endpoint.
     *
     * @param properties the local file properties.
     */
    private void setUpParameters(Properties properties) {
        watchedDir = properties.getProperty(LocalFileConstants.FILE_URI);
        contentType = properties.getProperty(LocalFileConstants.CONTENT_TYPE);
        actionAfterProcess = properties.getProperty(LocalFileConstants.ACTION_AFTER_PROCESS);
        moveFileURI = properties.getProperty(LocalFileConstants.MOVE_FILE_URI);
        processBeforeWatch = properties.getProperty(LocalFileConstants.PROCESS_BEFORE_WATCH);
        if (StringUtils.isEmpty(watchedDir)) {
            log.error("watchedDir can not be empty.");
        }
    }

    @Override
    public Object poll() {
        if (!isPolled) {
            if (StringUtils.isEmpty(processBeforeWatch) || processBeforeWatch.toUpperCase().equals(LocalFileConstants.YES)) {
                setProcessAndWatch();
            } else if (StringUtils.isNotEmpty(processBeforeWatch) && processBeforeWatch.toUpperCase().equals(LocalFileConstants.NO)) {
                startWatch();
            }
            isPolled = true;
        }
        return null;
    }

    /**
     * Before start the watch directory if watchedDir has any files it will start to process.
     */
    private void setProcessAndWatch() {
        Path dir = FileSystems.getDefault().getPath(watchedDir);
        DirectoryStream<Path> stream = null;
        try {
            stream = Files.newDirectoryStream(dir);
            for (Path path : stream) {
                processFile(path, contentType);
            }
        } catch (IOException e) {
            log.error("Error while processing the directory." + e.getMessage(), e);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                log.error("Error while close the DirectoryStream." + e.getMessage(), e);
            }
        }
        startWatch();
    }

    /**
     * Start the ExecutorService for watchDirectory.
     */
    private void startWatch() {
        ExecutorService executorService = Executors.newFixedThreadPool(LocalFileConstants.THREAD_SIZE);
        executorService.execute(new Runnable() {
            public void run() {
                try {
                    watchDirectory();
                } catch (IOException e) {
                    log.error("Error while watching the directory." + e.getMessage(), e);
                }
            }
        });
        executorService.shutdown();
    }

    /**
     * Injecting the file contents to the sequence.
     *
     * @param contents    is the contents of file.
     * @param contentType is the contentType of file.
     */
    public void injectFileContent(String contents, String contentType) {
        if (injectingSeq != null) {
            injectMessage(contents, contentType);
            if (log.isDebugEnabled()) {
                log.debug("Injecting localFile content message to the sequence : " + injectingSeq);
            }
        } else {
            log.error("The Sequence is not found.");
        }
    }

    private void processFile(final Path path, String contentType) {
        try {
            if (StringUtils.isEmpty(contentType)) {
                contentType = Files.probeContentType(path);
            }
            String readAllBytes = new String(Files.readAllBytes(path));
            injectFileContent(readAllBytes, contentType);
            if (StringUtils.isNotEmpty(actionAfterProcess) && actionAfterProcess.toUpperCase().equals(LocalFileConstants.MOVE)) {
                if (Files.exists(Paths.get(moveFileURI))) {
                    moveFile(path.toString(), moveFileURI);
                } else {
                    Files.createDirectory(Paths.get(moveFileURI));
                    moveFile(path.toString(), moveFileURI);
                }
            } else if (StringUtils.isNotEmpty(actionAfterProcess) && actionAfterProcess.toUpperCase().equals(LocalFileConstants.DELETE)) {
                Files.delete(path);
            }
        } catch (IOException e) {
            log.error("Error while processing file : " + e.getMessage(), e);
        }
        if (log.isDebugEnabled()) {
            log.debug("The processing file path is : " + path);
        }
    }

    /*
    * Start to watching the directory using JAVA NIO.
    * */
    @SuppressWarnings("unchecked")
    private Object watchDirectory() throws IOException {
        Path newPath = Paths.get(watchedDir);
        WatchService watchService = FileSystems.getDefault().newWatchService();
        try {
            newPath.register(watchService, ENTRY_MODIFY);
            while (true) {
                WatchKey key = watchService.take();
                if (key != null) {
                    for (WatchEvent<?> watchEvent : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = watchEvent.kind();
                        WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
                        Path entry = watchEventPath.context();

                        Path filePath = Paths.get(watchedDir, entry.toString());
                        if (kind == ENTRY_MODIFY) {
                            processFile(filePath, contentType);
                        } else if (kind == OVERFLOW) {
                            continue;
                        }
                        if (log.isDebugEnabled()) {
                            log.debug("Processing file is : " + entry);
                        }
                    }
                    key.reset();
                    if (!key.isValid()) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error while watching directory: " + e.getMessage(), e);
        } catch (InterruptedException ie) {
            log.error("Error while get the WatchKey : " + ie.getMessage(), ie);
        } finally {
            watchService.close();
        }
        return null;
    }

    private void moveFile(String source, String destination) {
        Path fromPath = Paths.get(source);
        Path toPath = Paths.get(destination);
        try {
            Files.move(fromPath, toPath.resolve(fromPath.getFileName()), REPLACE_EXISTING, ATOMIC_MOVE);
        } catch (IOException ie) {
            log.error("Error while move file : " + ie.getMessage(), ie);
        }
    }
}