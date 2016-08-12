package com.waverunnah.swg.harvesterdroid.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
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
