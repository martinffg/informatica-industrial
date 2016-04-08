package untref.com.ar.kinect.pruebas;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class Form extends JFrame implements ActionListener {

	private Kinect kinect;
	private Timer timer;
	private SensorData data;
	private JLabel labelPrincipalImagen;
	private JLabel label_x_value;
	private JLabel label_y_value;
	private JLabel label_color_value;
	private JLabel label_altura_value;
	private JLabel label_distancia_value;
	private JLabel label_scrollBar;
	private JLabel label_Nota;
	private JRadioButton radioAlturaSupPlana;
	private JRadioButton radioAlturaSupCurva;
	private JButton buttonImportarDatos;
	private JComboBox<String> combo_DistanciaObstaculos;
	private JComboBox<String> combo_cantPixeles;
	private JComboBox<String> combo_coloresContorno;
	private JLabel label_DistanciaObstaculos;	
	private JTextField input_RutaDeArchivo;
	private JTextField input_IntervaloEntreCurvas;
	private JLabel label_IntervaloEntreCurvas;
	private JLabel label_BarridoPixeles;
	private JLabel label_ColoresContorno;
	private JLabel label_RutaDeArchivo;
	private JScrollBar scrollBar;
	private float alpha;
	private int valorAltura;
	private boolean testing;

	private JMenu menuVistas;
	private JMenu menuAyuda;
	private JPanel panelDeOpciones;
	private boolean colorSeleccionado,profundidadSeleccionada,ambosSeleccionado,alturaSeleccionada, curvasDeNivelSeleccionado, importarDatosSeleccionado;
	
	private void setOpcionesDeSeleccion(boolean valor){
		
		colorSeleccionado = valor;
		profundidadSeleccionada = valor;
		ambosSeleccionado = valor;
		alturaSeleccionada = valor;
		curvasDeNivelSeleccionado = valor;
		importarDatosSeleccionado = valor;
	}
	
	public void setValorAltura(int altura){
		
		this.valorAltura = altura;
	}
	
	public int getValorAltura(){
		
		return this.valorAltura;
	}
	 public void actionPerformed(ActionEvent e) {
	    	
		 this.setOpcionesDeSeleccion(false);
		 Border border = BorderFactory.createLineBorder(Color.black);
		 String title = "";
		 
		if (e.getSource() == menuVistas.getItem(0))			 
			colorSeleccionado = true;
		
		if (e.getSource() == menuVistas.getItem(1)){			 	
			title = "Opciones vista ambos";				
			this.setVisibilidadScrollBar(true);
			ambosSeleccionado = true;
		}
		 
		if (e.getSource() == menuVistas.getItem(2)){
			title = "Opciones para detección obstáculos";
			this.setVisibleFiltrosProfundidad(true);
			profundidadSeleccionada = true;
		}
				
		if (e.getSource() == menuVistas.getItem(3)){					
			title = "Opciones de Curvas de Nivel";
			this.setVisibilidadFiltrosCurvasDeNivel(true);
			curvasDeNivelSeleccionado = true;
		}
		
		if (e.getSource() == menuVistas.getItem(4)){
			title = "Opciones de Altura";
			setVisibilidadFiltrosAltura(true);
			alturaSeleccionada = true;
		}
		
		if (e.getSource() == menuVistas.getItem(5)){
			title = "Opciones de Exportación de Datos";
			this.setVisibilidadFiltrosExportarDeDatos(true);
			importarDatosSeleccionado = true;
		}
		
		if (e.getSource() == menuAyuda.getItem(1)){
			
			try {
	            Desktop.getDesktop().browse(new URI("https://github.com/Mariani88/roboticaUntref"));
	        } catch (URISyntaxException ex) {
	            System.out.println(ex);
	        }catch(IOException ex){
	            System.out.println(ex);
	        }
		}
		
		if (e.getSource() == menuAyuda.getItem(2)){
			
			try {
	            Desktop.getDesktop().browse(new URI("http://research.dwi.ufl.edu/ufdw/j4k/J4KSDK.php"));
	        } catch (URISyntaxException ex) {
	            System.out.println(ex);
	        }catch(IOException ex){
	            System.out.println(ex);
	        }
		}
		
		panelDeOpciones.setBorder(BorderFactory.createTitledBorder(border,title, TitledBorder.CENTER, TitledBorder.TOP, null, Color.BLUE));
	 }	    
	 
	public Form(Boolean esTest) {
		testing = esTest;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setupUI();
		this.pack();

		if (!testing) {
			setupKinect();
		}

		this.setVisible(true);
	}

	public void start() {
		this.timer = new Timer();
		long period = (1 / 10) * 1000;
		period = 100;
		this.timer.scheduleAtFixedRate(new Tarea(this), 0, period);
		
		this.mostrarMensajeDeInicio();
	}
	
	public void mostrarMensajeDeInicio(){
		
		JOptionPane.showMessageDialog(null,"Instrucciones de uso:\n	"
				+ "		1. Elija un tipo de vista del menú\n	"
				+ "		2. Haga click sobre la imagen para obtener los datos en la matriz de resultados");
		
	}

	private void inicializarPanel(){
		
		panelDeOpciones = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();				
		this.getContentPane().add(panelDeOpciones, c);				
	}
	
	private void setupUI() {
		
		this.construirMenu();		
		this.inicializarPanel();
		
		this.setContentPane(new JPanel(new GridBagLayout()));
		GridBagConstraints c;

		JPanel panelImagen = new JPanel(new GridBagLayout());
		panelImagen.setPreferredSize(new Dimension(640 + 50, 480 + 50));		

		labelPrincipalImagen = new JLabel();
		labelPrincipalImagen.setBorder(BorderFactory.createLineBorder(Color.black));
		labelPrincipalImagen.setOpaque(true);
		labelPrincipalImagen.setBackground(Color.gray);

		panelImagen.add(labelPrincipalImagen);

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 100;
		this.getContentPane().add(panelImagen, c);

		labelPrincipalImagen.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {

				updateValues(e);
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});		
				
		this.construirMatrizResultados();			
		this.contruirPanelDeOpciones();	

	}
	
	private JMenu construirMenuVistas(){
		
		menuVistas=new JMenu("Vistas");        
        
        JMenuItem mi0=new JMenuItem("Imagen Color");
        mi0.addActionListener(this);
        menuVistas.add(mi0);
        
        JMenuItem mi1=new JMenuItem("Imagen Color/Profundidad");
        mi1.addActionListener(this);
        menuVistas.add(mi1);
        
        JMenuItem mi2=new JMenuItem("Detección obstáculos");
        mi2.addActionListener(this);
        menuVistas.add(mi2);
        
        JMenuItem mi3=new JMenuItem("Obtener Curvas de Nivel");
        mi3.addActionListener(this);
        menuVistas.add(mi3);
        
        JMenuItem mi4=new JMenuItem("Configuraciones de Altura");
        mi4.addActionListener(this);
        menuVistas.add(mi4);
        
        JMenuItem mi5=new JMenuItem("Exportar Datos");
        mi5.addActionListener(this);
        menuVistas.add(mi5);
        
       return menuVistas;
	}
	
	private JMenu construirMenuAyuda(){
		
		menuAyuda=new JMenu("Ayuda");        
        
        JMenuItem item1=new JMenuItem("Manual de usuario");
        item1.addActionListener(this);
        menuAyuda.add(item1);
        
        JMenuItem item2=new JMenuItem("Acerca de la Aplicación");
        item2.addActionListener(this);
        menuAyuda.add(item2);
        
        JMenuItem item3=new JMenuItem("Acerca de kinect");
        item3.addActionListener(this);
        menuAyuda.add(item3);
        
        return menuAyuda;
	}
	
	private void construirMenu(){
		
		setLayout(null);
        JMenuBar mb=new JMenuBar();
        setJMenuBar(mb);
        
        mb.add(this.construirMenuVistas());
        mb.add(this.construirMenuAyuda());                
	}

	public class MyActionListener implements ActionListener
	{
	   public void actionPerformed (ActionEvent e)
	   {
	     boolean estadoOperacion;
	     estadoOperacion = data.exportarDatos(input_RutaDeArchivo.getText().toString());
	     
	     if (estadoOperacion){
	    	 JOptionPane.showMessageDialog(null,"Los datos fueron exportados con éxito");
	    	 estadoOperacion = false;
	     }else
	    	 JOptionPane.showMessageDialog(null,"Surgio un error al exportar los datos.\nComuniquese con el administrador");
	   }
	}
	
	
	private void opcionesDeProfundidad(){
		
		BasicComponentBuilder builder = new BasicComponentBuilder(this,panelDeOpciones);
		
		label_DistanciaObstaculos = builder.construirLabel("Obstaculos a:", 0, 1);
		String[] opcionesCombo = {"0.7","1","1.5","2","2.5","3"};
		combo_DistanciaObstaculos = builder.construirCombo(0, 1, opcionesCombo);					
		
		label_BarridoPixeles = builder.construirLabel("Cant. Pixeles", 0, 2);
		String[] opcionesComboPixeles = {"5","10","15"};
		combo_cantPixeles = builder.construirCombo(0, 2, opcionesComboPixeles);
		combo_cantPixeles.setSelectedIndex(1);	
		
		label_ColoresContorno = builder.construirLabel("Contorno:", 0, 3);
		String[] opcionesComboColores = {"Azul","Amarillo","Naranja","Rojo","Verde"};
		//Si agrega otro color al combo modificar metodo #getColorContorno()
		combo_coloresContorno = builder.construirCombo(0, 3, opcionesComboColores);
		
	}
	
	private void opcionesDeCurvasDeNivel(){
		
		BasicComponentBuilder builder = new BasicComponentBuilder(this,panelDeOpciones);
		
		label_IntervaloEntreCurvas = builder.construirLabel("Curvas cada(cm):", 0, 1);
		input_IntervaloEntreCurvas = builder.construirInputText(0, 1);		
		input_IntervaloEntreCurvas.setText("10");
		
	}
	
	private void opcionesDeAltura(){
		
		BasicComponentBuilder builder = new BasicComponentBuilder(this,panelDeOpciones);
		
		radioAlturaSupPlana = builder.construirRadioButton("Altura Sup. Plana", 0, 1);
		radioAlturaSupPlana.setSelected(true);
		radioAlturaSupCurva = builder.construirRadioButton("Altura Sup. Curva", 0, 2);
		
		ButtonGroup radioButtonsAltura = new ButtonGroup();
		radioButtonsAltura.add(radioAlturaSupPlana);	
		radioButtonsAltura.add(radioAlturaSupCurva);
	}
	
	private void opcionesDeExportarDeDatos(){
		
		BasicComponentBuilder builder = new BasicComponentBuilder(this,panelDeOpciones);
		
		label_RutaDeArchivo = builder.construirLabel("Ruta de Archivo (*):", 0, 1);
	
		input_RutaDeArchivo = new JTextField();
		input_RutaDeArchivo.setVisible(false);
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(5, 5, 5, 5);
		
		input_RutaDeArchivo.setPreferredSize(new Dimension(200,25));
		this.getContentPane().add(panelDeOpciones, c);
		panelDeOpciones.add(input_RutaDeArchivo, c);
		
		buttonImportarDatos = builder.construirButton("Exportar datos", 0, 3);
		MyActionListener listener = new MyActionListener();
		buttonImportarDatos.addActionListener(listener);
		
		label_Nota = builder.construirLabel("<html>(*)De no ingresar una ruta para el archivo<br> el mismo se guardará en el directorio <br>donde está la aplicación.</html>", 0, 4);
		
	}
	
private void opcionesAmbos(){
		
		BasicComponentBuilder builder = new BasicComponentBuilder(this,panelDeOpciones);
		label_scrollBar = builder.construirLabel("Modifique la tonalidad de la imagen", 0, 1);
		scrollBar = builder.construirScrollBar(0, 2);
		alpha = 0.50f;
}
	
	//Construir del panel de acciones
	private void contruirPanelDeOpciones(){
		
		this.opcionesAmbos();
		this.opcionesDeProfundidad();
		this.opcionesDeCurvasDeNivel();
		this.opcionesDeAltura();
		this.opcionesDeExportarDeDatos();
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10, 10, 10, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 6;
		c.gridwidth = 2;
		this.getContentPane().add(panelDeOpciones, c);
		
		Container contentPane = this.getContentPane();
		JScrollPane scrollPane = new JScrollPane(contentPane);
		scrollPane.setBorder(null);
		this.setContentPane(scrollPane);
	}
	
	//contruye Matriz de resultados
	private void construirMatrizResultados(){
		
		
		JLabel label = new JLabel();
		GridBagConstraints c = new GridBagConstraints();
		
		label.setPreferredSize(new Dimension(100, 50));
		label.setBorder(BorderFactory.createLineBorder(Color.black));
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setText("Matriz Resultado");
		label.setForeground(Color.RED);		
				
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(0, 0, 0, 5);
		this.getContentPane().add(label, c);
		
		construirFila(1,1,120,50,"X");
		construirFila(1,2,120,50,"Y");
		construirFila(1,3,120,50,"Color (R,G,B)");
		construirFila(1,4,120,50,"Distancia");
		construirFila(1,5,120,50,"Altura");
		label_x_value = construirFila(2,1,150,50,"");
		label_y_value = construirFila(2,2,150,50,"");
		label_color_value = construirFila(2,3,150,50,"");
		label_distancia_value = construirFila(2,4,150,50,"");
		label_altura_value = construirFila(2,5,150,50,"");
	}
	
	//ConstruirFila
	private JLabel construirFila(int posX, int posY,int dimensionX, int dimensionY,String nombreDeFila){
		
		JLabel label = new JLabel();
		GridBagConstraints c = new GridBagConstraints();
		
		label.setPreferredSize(new Dimension(dimensionX, dimensionY));
		label.setBorder(BorderFactory.createLineBorder(Color.black));
		label.setHorizontalAlignment(JLabel.CENTER);
		if (nombreDeFila != "")
			label.setText(nombreDeFila);
		else	
			c.insets = new Insets(0, 0, 0, 5);
				
		c.gridx = posX;
		c.gridy = posY;
		this.getContentPane().add(label, c);
		
		return label;		
	}
	
	public void setAlpha(float val){
		this.alpha = val;
	}
	
	
	private void setVisibleFiltrosProfundidad(boolean bool){		
	
		combo_DistanciaObstaculos.setVisible(bool);
		label_DistanciaObstaculos.setVisible(bool);
		combo_cantPixeles.setVisible(bool);
		label_BarridoPixeles.setVisible(bool);
		combo_coloresContorno.setVisible(bool);
		label_ColoresContorno.setVisible(bool);
	}
		
	private void setVisibilidadFiltrosCurvasDeNivel(boolean bool){
		input_IntervaloEntreCurvas.setVisible(bool);
		label_IntervaloEntreCurvas.setVisible(bool);
	}
	
	private void setVisibilidadFiltrosExportarDeDatos(boolean bool){		
		label_RutaDeArchivo.setVisible(bool);		
		input_RutaDeArchivo.setVisible(bool);
		buttonImportarDatos.setVisible(bool);
		label_Nota.setVisible(bool);
	}
	
	private void setVisibilidadScrollBar(boolean bool){
		label_scrollBar.setVisible(bool);
		scrollBar.setVisible(bool);
	}

	private void setVisibilidadFiltrosAltura(boolean bool){
		radioAlturaSupPlana.setVisible(bool);
		radioAlturaSupCurva.setVisible(bool);
	}
	
	private Color getColorContorno(){
		
		String nombre_color = this.combo_coloresContorno.getSelectedItem().toString();
		Color colorContorno = Color.GREEN ;
		
		if (nombre_color == "Amarillo")
			colorContorno = Color.YELLOW;
		else if (nombre_color == "Azul")
			colorContorno = Color.BLUE;
		else if (nombre_color == "Rojo")
			colorContorno = Color.RED;
		else if (nombre_color == "Naranja")
			colorContorno = Color.ORANGE;
		
		return colorContorno;
	}
	
	private void updateValues(MouseEvent e) {

		label_x_value.setText(String.valueOf(e.getX()));
		label_y_value.setText(String.valueOf(e.getY()));
		Color color = data.getColorEnPixel(e.getX(), e.getY());
		label_color_value.setText("(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")");
		label_distancia_value.setText(String.valueOf(data.getDistancia(e.getX(), e.getY()) / 100) + " cm");			
	
		if(radioAlturaSupPlana.isSelected())
			this.setValorAltura(1200);						
		else
			this.setValorAltura(4000);
		
		double altura = 0.0;
		
		altura = data.getAltura(e.getX(), e.getY());
		label_altura_value.setText(String.valueOf(altura/100) + " cm");
	}

	private void setupKinect() {
		construirKinect();
		startKinect();
		esperarUmbralInicioKinect();
		chequearInicializacionKinect();
		setearAnguloDeElevacionDefault();
	}

	private void setearAnguloDeElevacionDefault() {
		kinect.setElevationAngle(0);
	}

	private void chequearInicializacionKinect() {
		if (!kinect.isInitialized()) {
			System.out.println("Falla al inicializar la kinect.");
			System.exit(1);
		}
	}

	private void esperarUmbralInicioKinect() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void construirKinect() {
		kinect = new Kinect();
	}

	private void startKinect() {
		kinect.start(Kinect.DEPTH | Kinect.COLOR | Kinect.SKELETON | Kinect.XYZ
				| Kinect.PLAYER_INDEX);
	}
		
	
	public void actualizar() {	
		
		if (!testing)
			data = new SensorDataProduction(kinect,this);
		else 
			data = new SensorDataTesting();	
		
		BufferedImage imagen = data.getImagenColor();
		
		if(!alturaSeleccionada)
			this.setVisibilidadFiltrosAltura(false);
		
		if (colorSeleccionado){
			imagen = data.getImagenColor();
			panelDeOpciones.setBorder(null);
		}
		
		if (profundidadSeleccionada){
			
			float dist = Float.parseFloat(combo_DistanciaObstaculos.getSelectedItem().toString());
			int cantPixeles = Integer.parseInt(combo_cantPixeles.getSelectedItem().toString());
			data.setPixelColorPorProfundidad(dist * 10000, cantPixeles,this.getColorContorno());
			imagen = data.getImagenProfundidad();
			
		}else this.setVisibleFiltrosProfundidad(false);
		
		if (ambosSeleccionado)	
			imagen = both(data.getImagenColor(), data.getImagenProfundidad());
		else 
			this.setVisibilidadScrollBar(false);
		
		if (curvasDeNivelSeleccionado){									
			
			int intervalo = Integer.parseInt(input_IntervaloEntreCurvas.getText().isEmpty()?"3":input_IntervaloEntreCurvas.getText());			
			data.pintarCurvaNivel(intervalo * 100);
			imagen =data.getImagenColor();
			
		}else this.setVisibilidadFiltrosCurvasDeNivel(false);
		
		if (!importarDatosSeleccionado)
			this.setVisibilidadFiltrosExportarDeDatos(false);
		
		labelPrincipalImagen.setIcon(new ImageIcon(imagen));
	}

	private BufferedImage both(BufferedImage color, BufferedImage profundidad) {

		BufferedImage image = color;
		BufferedImage overlay = profundidad;

		int w = Math.max(image.getWidth(), overlay.getWidth());
		int h = Math.max(image.getHeight(), overlay.getHeight());
		BufferedImage combined = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = combined.createGraphics();
		g.drawImage(image, 0, 0, null);

		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				alpha);
		g.setComposite(ac);

		g.drawImage(overlay, 0, 0, null);

		return combined;
	}
}
