package org.stanwood.podcaster.util;

public class Piper extends Thread implements IStreamGobbler {

    private java.io.InputStream input;

    private java.io.OutputStream output;

	private boolean done;

	private boolean running;

    public Piper(java.io.InputStream input, java.io.OutputStream output) {
        this.input = input;
        this.output = output;
    }

    @Override
	public void run() {
    	running = true;

        try {
        	byte[] buffer = new byte[1024];
        	int len = input.read(buffer);
        	done = false;
        	while (len != -1) {
        	    output.write(buffer, 0, len);
        	    len = input.read(buffer);
        	    if (Thread.interrupted()) {
        	        throw new InterruptedException();
        	    }
        	}
        } catch (Exception e) {
        	if (!done) {
	            // Something happened while reading or writing streams; pipe is broken
	            throw new RuntimeException("Broken pipe", e);
        	}
        } finally {
            try {
                input.close();
            } catch (Exception e) {
            }
            try {
                output.close();
            } catch (Exception e) {
            }
            running = false;
            done = true;
        }
    }

	@Override
	public void done() {
		done = true;
		// Block until it finishes
		while (running) {
			Thread.yield();
		}
	}

	@Override
	public boolean isDone() {
		return done;
	}

}