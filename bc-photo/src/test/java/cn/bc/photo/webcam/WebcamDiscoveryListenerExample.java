package cn.bc.photo.webcam;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryEvent;
import com.github.sarxos.webcam.WebcamDiscoveryListener;

public class WebcamDiscoveryListenerExample implements WebcamDiscoveryListener {
  public WebcamDiscoveryListenerExample() {
    for (Webcam webcam : Webcam.getWebcams()) {
      System.out.println("This webcam has been found in the system: "
        + webcam.getName());
    }
    Webcam.addDiscoveryListener(this);
    System.out
      .println("Now, please connect additional webcam, or disconnect already connected one.");
  }

  public void webcamFound(WebcamDiscoveryEvent event) {
    System.out.println("webcamFound:" + event.getWebcam().getName());
  }

  public void webcamGone(WebcamDiscoveryEvent event) {
    System.out.println("webcamGone:" + event.getWebcam().getName());
  }
}