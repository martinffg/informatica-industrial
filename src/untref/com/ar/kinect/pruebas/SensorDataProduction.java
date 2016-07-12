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
	private int umbralOptimoOtsu;
	private BufferedImage imagenColor;
	private BufferedImage imagenProfundidad;
	// private BufferedImage imagenBordes;
	private Form form;

	public void setForm(Form newForm) {

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
		this.umbralOptimoOtsu = 128;

		this.construirMatrizColor();
		this.construirMatrizProfundidad();

	}

	private void construirMatrizColor() {
		matrizColor = new Color[this.getWidth()][this.getHeight()];
		imagenColor = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

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
		return new Color(this.colorFrame[red] & 0xFF, this.colorFrame[green] & 0xFF, this.colorFrame[blue] & 0xFF,
				this.colorFrame[alpha] & 0xFF);
	}

	private void construirMatrizProfundidad() {
		matrizProfundidad = new double[this.getWidth()][this.getHeight()];
		imagenProfundidad = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

		for (int i = 0; i < 480; i++) {
			for (int j = 0; j < 640; j++) {

				int height = 640 * i;
				int z = j + height;

				float max = 30000;// 3 metros
				float min = 7000;// 70 cm

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

		for (int i = 0; i < this.getWidth(); i += cantPixeles) {
			for (int j = 0; j < this.getHeight(); j += cantPixeles) {

				if (this.getDistancia(i, j) < dist - deltaContorno) {

					this.pintarContorno(i, j, cantPixeles, Color.BLACK, this.getImagenProfundidad());

				} else if (this.getDistancia(i, j) >= dist - deltaContorno
						&& this.getDistancia(i, j) <= dist + deltaContorno) {

					this.pintarContorno(i, j, cantPixeles, colorContorno, this.getImagenProfundidad());
				}
			}
		}
	}

	private void pintarContorno(int fila, int columna, int cantidadDePixeles, Color color, BufferedImage img) {

		int pixeles = cantidadDePixeles / 2 + cantidadDePixeles % 2;

		int pixelInicioDeFila = fila - pixeles;
		int pixelFinalDeFila = fila + pixeles;
		int pixelInicioDeColumna = columna - pixeles;
		int pixelFinalDeColumna = columna + pixeles;

		for (int i = pixelInicioDeFila; i < pixelFinalDeFila; i++) {

			for (int j = pixelInicioDeColumna; j < pixelFinalDeColumna; j++) {

				if (esPosicionValida(i, j)) {
					img.setRGB(i, j, color.getRGB());
				}
			}
		}
	}

	private boolean esPosicionValida(int fila, int columna) {

		boolean filaValida = fila >= 0 && fila < this.getWidth();
		boolean columnaValida = columna >= 0 && columna < this.getHeight();

		return filaValida && columnaValida;
	}

	@Override
	public void pintarCurvaNivel(int distEntreCurvas) {

		int deltaContorno = 50;
		int max = 30000;// 3 metros
		int min = 7000;// 70 cm
		int dist = max;
		Color color;
		float hue;

		int i, j;
		if (dist >= 2) {
			deltaContorno = 100;
		}

		while (dist >= min) {

			for (i = getWidth() - 1; i > 0; i -= 3) {
				for (j = getHeight() - 1; j > 0; j -= 3) {

					if (this.getDistancia(i, j) >= dist - deltaContorno
							&& this.getDistancia(i, j) <= dist + deltaContorno) {

						hue = (float) (this.getDistancia(i, j) / 100000);
						color = new Color(Color.HSBtoRGB(hue, 1.0f, 1.0f));

						this.pintarContorno(i, j, 3, color, this.getImagenColor());
					}
				}
			}

			dist -= distEntreCurvas;
		}
	}

	@Override
	public double getAltura(int columna, int fila) {

		double tg_angulo = 0.54;
		int aux_fila = fila;
		double alt = 0.0;
		int rango_profundidad = form.getValorAltura();

		if (this.getDistancia(columna, fila) == 0.0)
			return this.getDistancia(columna, fila);

		while (aux_fila < this.getHeight()
				&& ((this.getDistancia(columna, fila) - this.getDistancia(columna, aux_fila)) <= rango_profundidad)) {
			alt += (float) (this.getDistancia(columna, aux_fila) * tg_angulo) / (this.getHeight() / 2);

			aux_fila++;
		}

		return alt;
	}

	@Override
	public boolean exportarDatos(String path) {

		boolean estadoExportacion = false;
		try {
			Date fechaActual = new Date();
			DateFormat formatoHora = new SimpleDateFormat("HHmmss");
			DateFormat formatoFecha = new SimpleDateFormat("ddMMyyyy");

			String nombreArchivo = path + "DatosDeMuestra_" + formatoFecha.format(fechaActual)
					+ formatoHora.format(fechaActual) + ".csv";

			File archivo = new File(nombreArchivo);
			FileWriter escribir = new FileWriter(archivo, true);
			String linea, coordenadaY, coordenadaZ;
			int i, j;

			for (i = 0; i < 480; i++) {
				for (j = 0; j < 640; j++) {

					coordenadaY = String.valueOf(this.getAltura(j, i) / 100);
					coordenadaZ = String.valueOf(this.matrizProfundidad[j][i] / 100);
					linea = '(' + coordenadaY + ',' + coordenadaZ + ')';
					escribir.write(linea);
					escribir.write(';');

				}
				escribir.write('\n');

			}
			escribir.close();
			estadoExportacion = true;
		} catch (Exception e) {
			System.out.println("Error al escribir" + e);
		}

		return estadoExportacion;
	}

	public BufferedImage getImagenBordesSobel(boolean isHough) {

		BufferedImage buff = this.imagenColor;
		BufferedImage salida = null;
		if (buff != null) {
			Integer[][] matrizResultado = new Integer[buff.getWidth()][buff.getHeight()];
			int[][] matrizMascaraY = { { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } };
			int[][] matrizMascaraX = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
			// Obtengo la matriz de magnitud de borde
			matrizResultado = obtenerMatrizPyS(buff, buff.getWidth(), buff.getHeight(), matrizMascaraX, matrizMascaraY);
			Imagen matrizResultadoBuff = convertirMatrizEnBuff(matrizResultado, buff.getWidth(), buff.getHeight());
			// Aplico la TL a la matriz de borde
			// salida =
			// umbralizarPyS(matrizResultado,buff.getWidth(),buff.getHeight(),
			// umbral);

			if (isHough)
				salida = hough(umbralizarConOtsu(matrizResultadoBuff));
			else
							
			salida = umbralizarConOtsu(matrizResultadoBuff);
		}

		return salida;
	}

	public BufferedImage getImagenBordesCanny(float lowThreshold, float highThreshold, boolean isHough) {
		// int umbral=200;
		BufferedImage buff = this.imagenColor;
		BufferedImage salida = null;
		if (buff != null) {
			Integer[][] matrizResultado = new Integer[buff.getWidth()][buff.getHeight()];
			int[][] matrizMascaraY = { { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } };
			int[][] matrizMascaraX = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
			// Obtengo la matriz de magnitud de borde
			matrizResultado = obtenerMatrizPyS(buff, buff.getWidth(), buff.getHeight(), matrizMascaraX, matrizMascaraY);
			Imagen matrizResultadoBuff = convertirMatrizEnBuff(matrizResultado, buff.getWidth(), buff.getHeight());

			Canny detector = new Canny();

			// adjust its parameters as desired
			detector.setLowThreshold(lowThreshold * 10);
			detector.setHighThreshold(highThreshold);

			// apply it to an image
			detector.setSourceImage(matrizResultadoBuff);
			detector.process();
			BufferedImage edges = detector.getEdgesImage();
			
			if (isHough)
				salida = hough(edges);
			else
				salida = edges;
		}

		return salida;
	}

	private BufferedImage hough(BufferedImage edges) {
		Hough h = new Hough();

		return h.devolver(edges);

	}

	private Imagen convertirMatrizEnBuff(Integer[][] matrizResultado, int ancho, int alto) {
		Imagen salida = new Imagen(ancho, alto);
		int valor = 0;
		int maximo = buscarMaximo(matrizResultado, ancho, alto);
		int minimo = buscarMinimo(matrizResultado, ancho, alto);

		for (int i = 0; i < ancho; i++) {
			for (int j = 0; j < alto; j++) {
				valor = (int) transformacionLineal(matrizResultado[i][j], maximo, minimo);
				salida.setValorPixel(i, j, new Color(valor, valor, valor));
			}
		}

		return salida;
	}

	private Integer[][] obtenerMatrizPyS(BufferedImage buff, int ancho, int alto, int[][] matrizMascaraX,
			int[][] matrizMascaraY) {
		int[][] matriX = new int[ancho][alto];
		int[][] matriY = new int[ancho][alto];
		Integer[][] matrizResultado = new Integer[ancho][alto];
		int grisX = 0;
		int grisY = 0;
		for (int i = 0; i < ancho; i++) {
			for (int j = 0; j < alto; j++) {
				matriX[i][j] = 0;
				matriY[i][j] = 0;
			}
		}
		for (int i = 0; i <= ancho - 3; i++) {
			for (int j = 0; j <= alto - 3; j++) {
				for (int k = 0; k < 3; k++) {
					for (int m = 0; m < 3; m++) {
						grisX = grisX + calcularPromedio(buff.getRGB(i + k, j + m)) * matrizMascaraX[k][m];
						grisY = grisY + calcularPromedio(buff.getRGB(i + k, j + m)) * matrizMascaraY[k][m];
					}
				}

				matriX[i + 1][j + 1] = grisX;
				matriY[i + 1][j + 1] = grisY;
				grisX = 0;
				grisY = 0;
			}
		}
		for (int i = 0; i < ancho; i++) {
			for (int j = 0; j < alto; j++) {
				matrizResultado[i][j] = (int) Math.sqrt(Math.pow(matriX[i][j], 2) + Math.pow(matriY[i][j], 2));
			}
		}
		return matrizResultado;
	}

	@SuppressWarnings("unused")
	private BufferedImage umbralizarPyS(Integer[][] matrizResultado, int ancho, int alto, int umbral) {
		Color blanco = new Color(255, 255, 255);
		Color negro = new Color(0, 0, 0);
		BufferedImage salida = new BufferedImage(ancho, alto, 1);
		for (int i = 0; i < ancho; i++) {
			for (int j = 0; j < alto; j++) {
				if (matrizResultado[i][j] >= umbral) {
					salida.setRGB(i, j, blanco.getRGB());
				} else {
					salida.setRGB(i, j, negro.getRGB());
				}
			}
		}
		return salida;
	}

	private int calcularPromedio(int rgb) {
		int promedio;
		Color c = new Color(rgb);
		promedio = (int) ((c.getBlue() + c.getGreen() + c.getRed()) / 3);
		return promedio;
	}

	public Imagen umbralizarConOtsu(Imagen buff) {
		Imagen salida = null;
		int umbralOptimo = 0;
		double gw = 0;
		double gwMaximo = 0;
		Color blanco = new Color(255, 255, 255);
		Color negro = new Color(0, 0, 0);
		if (buff != null) {
			int pixeles = buff.getHeight() * buff.getWidth();
			salida = new Imagen(buff.getWidth(), buff.getHeight());
			int[] histograma = histograma(buff);
			double[] ocurrencia = new double[256];
			for (int i = 0; i < 256; i++) {
				ocurrencia[i] = (double) histograma[i] / pixeles;
			}
			gwMaximo = calculoVarianza(0, ocurrencia);
			for (int umbral = 1; umbral < 256; umbral++) {
				gw = calculoVarianza(umbral, ocurrencia);
				if (gw > gwMaximo) {
					gwMaximo = gw;
					umbralOptimo = umbral;
				}
			}

			this.setUmbralOptimoOtsu(umbralOptimo);

			for (int i = 0; i < buff.getWidth(); i++) {
				for (int j = 0; j < buff.getHeight(); j++) {
					if (calcularPromedio(buff.getRGB(i, j)) >= umbralOptimo) {
						salida.setRGB(i, j, blanco.getRGB());
					} else {
						salida.setRGB(i, j, negro.getRGB());
					}
				}
			}
		}
		return salida;
	}

	public int[] histograma(Imagen buff) {
		int histograma[] = new int[256];
		for (int i = 0; i < buff.getWidth(); i++) {
			for (int j = 0; j < buff.getHeight(); j++) {
				histograma[calcularPromedio(buff.getRGB(i, j))] += 1;
			}
		}
		return histograma;
	}

	private double calculoVarianza(int umbral, double[] ocurrencia) {
		double w1 = 0;
		double w2 = 0;
		double u1 = 0;
		double u2 = 0;
		double ut = 0;
		double gb = 0;
		for (int i = 0; i < 256; i++) {
			if (i < umbral) {
				w1 += ocurrencia[i];
			} else {
				w2 += ocurrencia[i];
			}
		}
		for (int i = 0; i < 256; i++) {
			if (i < umbral) {
				u1 += i * ocurrencia[i];
			} else {
				u2 += i * ocurrencia[i];
			}
		}
		if (w1 != 0) {
			u1 = (double) u1 / w1;
		}
		if (w2 != 0) {
			u2 = (double) u2 / w2;
		}
		ut = w1 * u1 + w2 * u2;
		gb = w1 * Math.pow(u1 - ut, 2) + w2 * Math.pow(u2 - ut, 2);
		return gb;
	}

	private double transformacionLineal(double suma, double max, double min) {
		double salida = suma * (255 / (max - min)) + (255 - ((255 * max) / (max - min)));
		return salida;
	}

	public int buscarMaximo(Integer[][] matriz, int ancho, int alto) {
		int max = matriz[0][0];
		for (int i = 0; i < ancho; i++) {
			for (int j = 0; j < alto; j++) {
				if (max < matriz[i][j]) {
					max = matriz[i][j];
				}
			}
		}
		return max;
	}

	public int buscarMinimo(Integer[][] matriz, int ancho, int alto) {
		int min = matriz[0][0];
		for (int i = 0; i < ancho; i++) {
			for (int j = 0; j < alto; j++) {
				if (min > matriz[i][j]) {
					min = matriz[i][j];
				}
			}
		}
		return min;
	}

	@Override
	public int getUmbralOptimoOtsu() {
		return umbralOptimoOtsu;
	}

	private void setUmbralOptimoOtsu(int umbralOptimoOtsu) {
		this.umbralOptimoOtsu = umbralOptimoOtsu;
	}
}