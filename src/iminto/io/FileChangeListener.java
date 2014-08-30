package iminto.io;
import java.io.File;

/**
 * Interface for listening to disk file changes.
 * @see jodd.io.FileMonitor
 */
public interface FileChangeListener {

	/**
	 * Invoked when one of the monitored files is created, deleted or modified.
	 */
	void onFileChange(File file);

}
