package untref.com.ar.kinect.pruebas;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public class Coversion {
	 BufferedImage toBufferedImage(Image image) {
			if( image instanceof BufferedImage ) {
				return( (BufferedImage)image );
			} else {
				image = new ImageIcon(image).getImage();
				BufferedImage bufferedImage = new BufferedImage(
												image.getWidth(null),
												image.getHeight(null),
												BufferedImage.TYPE_INT_RGB );
				 Graphics g = bufferedImage.createGraphics();
				 g.drawImage(image,0,0,null);
				 g.dispose();
	 
				return( bufferedImage );
			}
		}
}
