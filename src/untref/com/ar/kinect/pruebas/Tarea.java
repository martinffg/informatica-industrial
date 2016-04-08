package untref.com.ar.kinect.pruebas;

import java.util.TimerTask;

public class Tarea extends TimerTask {

	private Form form;

	public Tarea(Form form) {
		this.form = form;
	}

	@Override
	public void run() {
		form.actualizar();
	}

}
