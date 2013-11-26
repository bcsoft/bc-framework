package cn.bc.photo.webcam;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionDetector;
import com.github.sarxos.webcam.WebcamMotionEvent;
import com.github.sarxos.webcam.WebcamMotionListener;

/**
 * Detect motion.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class DetectMotionExample implements WebcamMotionListener {
	private int i = 0;

	public DetectMotionExample() {
		WebcamMotionDetector detector = new WebcamMotionDetector(
				Webcam.getDefault());
		detector.setInterval(100); // one check per 100 ms
		detector.addMotionListener(this);
		detector.start();
	}

	public void motionDetected(WebcamMotionEvent wme) {
		i++;
		System.out.println("Detected motion:" + i);
	}
}