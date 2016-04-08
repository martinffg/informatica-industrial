package untref.com.ar.kinect.pruebas;

import edu.ufl.digitalworlds.j4k.J4KSDK;

public class Kinect extends J4KSDK {

	public Kinect() {
		setColorResolution(640, 480);
		setDepthResolution(640, 480);
	}
	
	@Override
	public void onColorFrameEvent(byte[] arg0) {
		
	}

	@Override
	public void onDepthFrameEvent(short[] arg0, byte[] arg1, float[] arg2,
			float[] arg3) {
		
	}

	@Override
	public void onSkeletonFrameEvent(boolean[] arg0, float[] arg1,
			float[] arg2, byte[] arg3) {
	}

}