package untref.com.ar.kinect.pruebas;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SensorDataProduction implements SensorData {

	private byte[] colorFrame;
	private short[] depth;
	private Color[][] matrizColor;
	private double[][] matrizProfundidad;
	private int width;
	private int height;
	private BufferedImage imagenColor;
	private BufferedImage imagenProfundidad;
	private Form form;

	public void setForm(Form newForm){
		
		this.form = newForm;
	}
	
	public SensorDataProduction(Kinect kinect, Form form) {
		if (!kinect.isInitialized()) {
			System.out.println("Falla al inicializar la kinect.");
			System.exit(2);
		}
		
		this.setForm(form);
		
		this.colorFrame = kinect.getColorFrame();
		this.depth = kinect.getDepthFrame();		
		
		this.width = kinect.getColorWidth();
		this.height = kinect.getColorHeight();

		this.construirMatrizColor();
		this.construirMatrizProfundidad();

	}

	private void construirMatrizColor() {
		matrizColor = new Color[this.getWidth()][this.getHeight()];
		imagenColor = new BufferedImage(this.getWidth(), this.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		
		for (int i = 0; i < this.getHeight(); i++) {
			for (int j = 0; j < this.getWidth(); j++) {

				int posicionInicial = j * 4;
				
				int height = this.getWidth() * 4 * i;
				int blue = posicionInicial + height;
				int green = posicionInicial + 1 + height;
				int red = posicionInicial + 2 + height;
				int alpha = posicionInicial + 3 + height;

				Color color = construirColor(blue, green, red, alpha);
				this.matrizColor[j][i] = color;
				imagenColor.setRGB(j, i, color.getRGB());
			}
		}
	}

	private Color construirColor(int blue, int green, int red, int alpha) {
		return new Color(this.colorFrame[red] & 0xFF,
				this.colorFrame[green] & 0xFF,
				this.colorFrame[blue] & 0xFF,
				this.colorFrame[alpha] & 0xFF);
	}

	private void construirMatrizProfundidad() {
		matrizProfundidad = new double[this.getWidth()][this.getHeight()];		
		imagenProfundidad = new BufferedImage(this.getWidth(),this.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		
		for (int i = 0; i < 480; i++) {
			for (int j = 0; j < 640; j++) {
				
				int height = 640 * i;
				int z = j + height;

				float max = 30000;//3 metros
				float min = 7000;//70 cm

				Color color;
				if (depth[z] == 0) {
					color = Color.gray;
				} else if (depth[z] > max) {
					color = Color.WHITE;
				} else if (depth[z] < min) {
					color = Color.white;
				} else {
					float hue = (1 / (max - min)) * (depth[z] - min);
					color = new Color(Color.HSBtoRGB(hue, 1.0f, 1.0f));
				}
				this.matrizProfundidad[j][i] = depth[z];
				imagenProfundidad.setRGB(j, i, color.getRGB());
			}
		}
	}

	private int getWidth() {
		return this.width;
	}

	private int getHeight() {
		return this.height;
	}

	@Override
	public Color getColorEnPixel(int x, int y) {
		return matrizColor[x][y];
	}

	@Override
	public double getDistancia(int x, int y) {
		return matrizProfundidad[x][y];
	}

	@Override
	public BufferedImage getImagenColor() {
		return imagenColor;
	}

	@Override
	public BufferedImage getImagenProfundidad() {
		return imagenProfundidad;
	}
		
	@Override
	public void setPixelColorPorProfundidad(float dist, int cantPixeles, Color colorContorno) {
		
		short deltaContorno = 200;
		
		for (int i = 0; i < this.getWidth(); i+=cantPixeles) {
			for (int j = 0; j < this.getHeight() ; j+= cantPixeles) {								
				
				if(this.getDistancia(i,j) < dist - deltaContorno ){	
					
					this.pintarContorno(i, j, cantPixeles, Color.BLACK, this.getImagenProfundidad());		
				
				} else 
					if ( this.getDistancia(i, j) >= dist - deltaContorno && this.getDistancia(i, j) <= dist + deltaContorno) {
					
						this.pintarContorno(i, j, cantPixeles,colorContorno, this.getImagenProfundidad());
					}
			}
		}
	}	
	
	private void pintarContorno(int fila, int columna, int cantidadDePixeles, Color color,BufferedImage img){
		
		int pixeles = cantidadDePixeles/2 + cantidadDePixeles % 2;
		
		int pixelInicioDeFila = fila - pixeles;
		int pixelFinalDeFila = fila + pixeles;
		int pixelInicioDeColumna = columna - pixeles;
		int pixelFinalDeColumna = columna + pixeles;
		
		for (int i = pixelInicioDeFila; i < pixelFinalDeFila ; i++){
			
			for (int j = pixelInicioDeColumna; j < pixelFinalDeColumna ; j++){
				
				if (esPosicionValida(i,j)){
					img.setRGB(i,j, color.getRGB());
				}
			}
		}
	}

	private boolean esPosicionValida(int fila, int columna) {
		
		boolean filaValida = fila >=0 && fila < this.getWidth();
		boolean columnaValida =  columna >=0 && columna < this.getHeight();
		
		return filaValida && columnaValida;
	}
	
	
	@Override
	public void pintarCurvaNivel(int distEntreCurvas) {
		
		int deltaContorno = 50;
		int max = 30000;//3 metros
		int min = 7000;//70 cm
		int dist = max;
		Color color;
		float hue;
		
		int i,j;
		if (dist >= 2) {
			deltaContorno = 100;
		}
		
		while(dist >= min){
		
			for ( i = getWidth() -1; i > 0; i -= 3) {
				for ( j = getHeight() -1; j > 0; j -= 3) {
													
					if (this.getDistancia(i, j) >= dist - deltaContorno
							&& this.getDistancia(i, j) <= dist + deltaContorno) {
						
						hue = (float)(this.getDistancia(i, j)/100000);
						color = new Color(Color.HSBtoRGB(hue, 1.0f, 1.0f));
						
						this.pintarContorno(i, j, 3, color, this.getImagenColor());
					}
				}
			}
		
		dist -= distEntreCurvas;
		}
	}
	
	@Override
	public double getAltura(int columna,int fila){
		
		double tg_angulo = 0.54;	
		int aux_fila = fila;
		double alt = 0.0;
		int rango_profundidad = form.getValorAltura();
		
		if (this.getDistancia(columna, fila)==0.0) return this.getDistancia(columna, fila);		
		
		while (aux_fila < this.getHeight() &&
				((this.getDistancia(columna, fila) - this.getDistancia(columna, aux_fila))<= rango_profundidad)){
			alt += (float)(this.getDistancia(columna, aux_fila) * tg_angulo)/(this.getHeight()/2);
			
			aux_fila++;
		}
		
		return alt ;		
	}
	
	@Override
	public boolean exportarDatos(String path){
	
		boolean estadoExportacion = false;
		try{
			Date fechaActual = new Date();
			DateFormat formatoHora = new SimpleDateFormat("HHmmss");
	        DateFormat formatoFecha = new SimpleDateFormat("ddMMyyyy");
	        	        
			String nombreArchivo = path+"DatosDeMuestra_"+ formatoFecha.format(fechaActual) + formatoHora.format(fechaActual) + ".csv";
			
			File archivo = new File(nombreArchivo);
			FileWriter escribir = new FileWriter(archivo,true);
			String linea,coordenadaY,coordenadaZ;
			int i,j;
			
			for (i = 0; i < 480; i++) {
				for (j = 0; j < 640; j++) {
					
					coordenadaY = String.valueOf(this.getAltura(j, i)/100);
					coordenadaZ = String.valueOf(this.matrizProfundidad[j][i]/100);
					linea = '('+ coordenadaY + ',' + coordenadaZ+')';
					escribir.write(linea);
					escribir.write(';');
				
					
				}
				escribir.write('\n');				
					
			}
			escribir.close();
			estadoExportacion = true;
		}
		catch(Exception e)
		{
			System.out.println("Error al escribir"+e);
		}
		
		return estadoExportacion;	
	}
		
}