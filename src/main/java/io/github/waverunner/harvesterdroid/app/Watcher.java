/*
 * HarvesterDroid - A Resource Tracker for Star Wars Galaxies
 * Copyright (C) 2017  Waverunner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.waverunner.harvesterdroid.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Watcher {
    private static Map<String, Runnable> watchedFiles = new HashMap<>();
    private static ExecutorService threadPool = Executors.newCachedThreadPool();
    private static WatchService watchService;
    private static boolean initialized;

    public static void createFileWatcher(File file, Runnable onChange) throws IOException {
        initialize();
        if (!watchedFiles.containsKey(file.getAbsolutePath()))
            watchedFiles.put(file.getAbsolutePath(), onChange);
    }

    private static void initialize() throws IOException {
        if (initialized)
            return;

        watchService = FileSystems.getDefault().newWatchService();

        WatcherThread thread = new WatcherThread();
        threadPool.submit(thread);

        initialized = true;
    }


    private static void handleFileModified(Path path) {
        if (!watchedFiles.containsKey(path.toString()))
            return;

        Runnable thread = watchedFiles.get(path.toString());
        threadPool.submit(thread);
    }

    public static void shutdown() {
        threadPool.shutdownNow();
    }

    static class WatcherThread implements Runnable {
        private AtomicBoolean running = new AtomicBoolean(true);

        @Override
        public void run() {
            while (running.get()) {
                WatchKey key;

                try {
                    key = watchService.poll(25, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    return;
                }
                if (key == null) {
                    Thread.yield();
                    continue;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path path = ev.context();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        Thread.yield();
                    } else if (kind == java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY) {
                        handleFileModified(path);
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    break;
                }

                Thread.yield();
            }
        }
    }
}
