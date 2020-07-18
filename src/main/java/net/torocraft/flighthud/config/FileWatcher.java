package net.torocraft.flighthud.config;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class FileWatcher implements Runnable {
  private final File file;
  private final Path filename;
  private final Listener listener;

  @FunctionalInterface
  public static interface Listener {
    void onUpdate();
  }

  public static Thread watch(File file, Listener listener) {
    Thread thread = new Thread(new FileWatcher(file, listener));
    thread.setDaemon(true);
    thread.start();
    return thread;
  }

  private FileWatcher(File file, Listener listener) {
    this.file = file;
    this.listener = listener;
    this.filename = file.toPath().getFileName();
  }

  @Override
  public void run() {
    try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
      Path path = file.toPath().getParent();
      path.register(watchService, ENTRY_MODIFY);
      boolean poll = true;
      while (poll) {
        poll = pollEvents(watchService);
      }
    } catch (IOException | InterruptedException | ClosedWatchServiceException e) {
      Thread.currentThread().interrupt();
    }
  }

  protected boolean pollEvents(WatchService watchService) throws InterruptedException {
    WatchKey key = watchService.take();
    for (WatchEvent<?> event : key.pollEvents()) {
      Path changedFilename = ((Path)event.context()).getFileName();
      if (changedFilename.equals(filename)) {
        try {
          listener.onUpdate();
        }catch(Exception e){
          new Exception("Error during file watch of " + file.getAbsolutePath(), e).printStackTrace();
        }
      }
    }
    return key.reset();
  }

}
