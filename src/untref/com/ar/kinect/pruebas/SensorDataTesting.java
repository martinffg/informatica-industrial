package untref.com.ar.kinect.pruebas;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SensorDataTesting implements SensorData {

	private BufferedImage color;
	private BufferedImage profundidad;
	
	public SensorDataTesting() {

		try {
			color = ImageIO.read(new File("heat-bikini-orig.jpg"));
			profundidad = ImageIO.read(new File("heat-bikini-alpha.png"));			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Color getColorEnPixel(int x, int y) {
		return new Color(color.getRGB(x, y));
	}

	@Override
	public double getDistancia(int x, int y) {
		return 0;
	}

	@Override
	public BufferedImage getImagenColor() {
		return color;
	}

	@Override
	public BufferedImage getImagenProfundidad() {
		return profundidad;
	}
	
	@Override
	public void setPixelColorPorProfundidad(float dist, int cantPixeles,Color colorContorno){}
	
	@Override
	public void pintarCurvaNivel(int distEntreCurvas){}
	
	@Override
	public double getAltura(int x, int y){
		return 0;
	}
		
	@Override
	public boolean exportarDatos(String path){
		System.out.println("Botón presionado");
		return true;
	}

}