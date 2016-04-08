package untref.com.ar.kinect.pruebas;

import java.awt.Color;
import java.awt.image.BufferedImage;

public interface SensorData {

	BufferedImage getImagenColor();
	
	Color getColorEnPixel(int x, int y);
	
	double getDistancia(int x, int y);
	
	double getAltura(int x, int y);
	
	BufferedImage getImagenProfundidad();
	
	void setPixelColorPorProfundidad(float dist, int cantPixeles, Color colorContorno);
	
	public void pintarCurvaNivel(int distEntreCurvas);
	
	public boolean exportarDatos(String path);
	
}
