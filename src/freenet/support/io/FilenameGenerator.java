package freenet.support.io;

import java.io.File;
import java.io.IOException;

import org.tanukisoftware.wrapper.WrapperManager;

import freenet.crypt.RandomSource;
import freenet.support.HexUtil;
import freenet.support.Logger;
import freenet.support.TimeUtil;

public class FilenameGenerator {

    private final RandomSource random;
    private final String prefix;
    private final File tmpDir;

    /**
     * @param random
     * @param wipeFiles
     * @param dir if <code>null</code> then use the default temporary directory
     * @param prefix
     * @throws IOException
     */
	public FilenameGenerator(RandomSource random, boolean wipeFiles, File dir, String prefix) throws IOException {
		this.random = random;
		this.prefix = prefix;
		if (dir == null)
			tmpDir = new File(System.getProperty("java.io.tmpdir"));
		else
			tmpDir = dir;
        if(!tmpDir.exists()) {
            tmpDir.mkdir();
		}
		if(!(tmpDir.isDirectory() && tmpDir.canRead() && tmpDir.canWrite()))
			throw new IOException("Not a directory or cannot read/write: "+tmpDir);
		
		if(wipeFiles) {
			long wipedFiles = 0;
			long wipeableFiles = 0;
			long startWipe = System.currentTimeMillis();
			File[] filenames = tmpDir.listFiles();
			if(filenames != null) {
				for(int i=0;i<filenames.length;i++) {
					WrapperManager.signalStarting(5*60*1000);
					if(i % 1024 == 0 && i > 0)
						// User may want some feedback during startup
						System.err.println("Deleted "+wipedFiles+" temp files ("+(i - wipeableFiles)+" non-temp files in temp dir)");
					File f = filenames[i];
					String name = f.getName();
					if((((File.separatorChar == '\\') && name.toLowerCase().startsWith(prefix.toLowerCase())) ||
							name.startsWith(prefix))) {
						wipeableFiles++;
						if((!f.delete()) && f.exists())
							System.err.println("Unable to delete temporary file "+f+" - permissions problem?");
						else
							wipedFiles++;
					}
				}
			}
			long endWipe = System.currentTimeMillis();
			System.err.println("Deleted "+wipedFiles+" of "+wipeableFiles+" temporary files ("+(filenames.length-wipeableFiles)+" non-temp files in temp directory) in "+TimeUtil.formatTime(endWipe-startWipe));
		}
	}

	public File makeRandomFilename() {
		byte[] randomFilename = new byte[8]; // should be plenty
		while(true) {
			random.nextBytes(randomFilename);
			String filename = prefix + HexUtil.bytesToHex(randomFilename);
			File ret = new File(tmpDir, filename);
			if(!ret.exists()) {
				if(Logger.shouldLog(Logger.MINOR, this))
					Logger.minor(this, "Made random filename: "+ret, new Exception("debug"));
				return ret;
			}
		}
	}

}
