import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;


public class IAT455CourseProject extends JFrame {
	//fields
	static int width, height, mouseX, mouseY, timer;
	public static JButton button1, button2;
	static BufferedImage select, placeholderImage, before, after;
	boolean pressed, browsePressed;
	public JSlider slider;
	static ArrayList<Integer> brushTrailX = new ArrayList<Integer>();
	static ArrayList<Integer> brushTrailY = new ArrayList<Integer>();

	public IAT455CourseProject() {
		// constructor

		// Get an image from the specified file in the current directory on the
		// local hard disk.
		try {
			placeholderImage = ImageIO.read(new File("placeholderImg.jpg"));
			select = ImageIO.read(new File("placeholderImg.jpg"));
		} catch (Exception e) {
			System.out.println("Cannot load the provided image");
		}
		
		// instantiate the main images 
		before = select;
		after = select;


		// set up JFrame
		this.setTitle("IAT 455 Course Project");
		setVisible(true);

		// variables needed for images in paint()
		width = placeholderImage.getWidth();
		height = placeholderImage.getHeight();
		// refresh timer
		timer = 100;

		
		this.addWindowListener(new WindowAdapter() {// anonymous class definition
			public void windowClosing(WindowEvent e) {
				System.exit(0);// terminate the program
			}// end windowClosing()
		}// end WindowAdapter
		);// end addWindowListener

		// slider menu for brush tool
		slider = new JSlider(JSlider.HORIZONTAL, 0, 10, 5);
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(1);
		slider.setPaintLabels(true);
		JFrame frame = new JFrame("Slider frame");
		JLabel label1 = new JLabel("Brush Width");
		frame.setLayout(new FlowLayout());

		// Add JSlider into JFrame
		frame.add(label1);
		frame.add(slider);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set JFrame size
		frame.setSize(400, 100);

		// Make JFrame visible. So we can see it.
		frame.setVisible(true);

		// Add KeyListener for backspace functionality
		addKeyListener(new MKeyListener());

		// add buttons for image selection
		button1 = new JButton();
		button1.setBounds(10, 30, width / 2, height / 2);
		button1.setFocusable(false);
		
		add(button1);
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		button1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				browsePressed = true;
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Please choose an image...");
				FileNameExtensionFilter filter = new FileNameExtensionFilter("jpg", "png", "bmp", "gif");
				fc.addChoosableFileFilter(filter);

				// Action on approval of image type
				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					brushTrailX.clear();
					brushTrailY.clear();
					repaint();
					File selectedFile = fc.getSelectedFile();
					// sets images to the selected file
					try {
						select = ImageIO.read(selectedFile);
						before = ImageIO.read(selectedFile);
						after = ImageIO.read(selectedFile);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		// create class and listener
		myMouseListener mml = new myMouseListener();
		this.addMouseListener(mml);
	}// end constructor

//	public BufferedImage multiplyImages(BufferedImage src1, BufferedImage src2) {
//		BufferedImage result = new BufferedImage(src1.getWidth(), src1.getHeight(), src1.getType());
//
//		for (int i = 0; i < result.getHeight(); i++) {
//			for (int j = 0; j < result.getWidth(); j++) {
//				int rgb1 = src1.getRGB(i, j);
//				int rgb2 = src2.getRGB(i, j);
//				int newR = 0, newG = 0, newB = 0;
//
//				newR = (getRed(rgb1) * (getRed(rgb2))) / 255;
//				newG = (getGreen(rgb1) * (getGreen(rgb2))) / 255;
//				newB = (getBlue(rgb1) * (getBlue(rgb2))) / 255;
//				newR = clip(newR);
//				newG = clip(newG);
//				newB = clip(newB);
//
//				result.setRGB(i, j, new Color(newR, newG, newB).getRGB());
//			}
//		}
//		return result;
//	}
	
	//method to apply the blur on the shadow of an image. 
	public static BufferedImage applyBlur(BufferedImage src){
		//by using the matrix of 3x3 with the GaussianBlur kernel matrix, a blur will be applied to the passed BufferedImg
		BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
		float[] matrix = {
				1/16f, 1/8f, 1/16f,
				1/8f, 1/4f, 1/8f,
				1/16f, 1/8f, 1/16f,
		};
		//construct a blur filter using the ConvolveOp and Kernel
		//assigning them to a BufferedImageOp object allows us to return it as a BufferedImage using the filter method
		BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, matrix));
		
		result = op.filter(src, result);
		
		for (int a = Collections.min(brushTrailY); a < Collections.max(brushTrailY); a++) {
			int xMin, xMax;
			ArrayList<Integer> x = new ArrayList<Integer>();
			for (int b = 0; b < brushTrailY.size(); b++) {
				if (brushTrailY.get(b) == a) {
					x.add(brushTrailX.get(b));
				}
			}
			if(!x.isEmpty()) {
				xMin = Collections.min(x);
				xMax = Collections.max(x);
				for (int c = xMin-2; c < xMax; c++) {
					result.setRGB((int)((c-200)*src.getWidth()/((width/2)*5)), (int)((a-61)*src.getHeight()/((height/2)*5)), src.getRGB((int)((c-200)*src.getWidth()/((width/2)*5)), (int)((a-61)*src.getHeight()/((height/2)*5))));
				}
			}
		}
		
		return result;
	}
	
	//method to apply the drop shadow on an image
	public static BufferedImage dropShadow(BufferedImage src, int sWidth) {

		// 1. Move the current image to the bottom right
		// 2. Gray the current image located at the bottom right
		// 3. Blur the image (shadow)	
		// 4. Paint/draw over the original. 

		//the resulting image will be the source image width and height + the shadow width and height which is specified from the users
		BufferedImage result = new BufferedImage(src.getWidth() + sWidth, src.getHeight() + sWidth, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = result.createGraphics();
		g2.drawImage(src, sWidth >> 1, sWidth >> 1, null);

		//graying out the image while still keeping the alpha values
		int[] pixels = new int[result.getWidth() * result.getHeight()];
		result.getRGB(0, 0, result.getWidth(), result.getHeight(), pixels, 0 , result.getWidth());

		for (int i = 0; i < pixels.length; i++) {
			// This will keep only the alpha channel and the grey values
			pixels[i] = (pixels[i] & (0xff000000)) | 0x00505050;
		}
		result.setRGB(0, 0, result.getWidth(), result.getHeight(), pixels,
				0 , result.getWidth());
		//dispose any irrelevant objects in the graphic variable
		g2.dispose();

		//blurring the opaque gray and the surrounding.
		result = applyBlur(result);
		g2 = result.createGraphics();
		g2.drawImage(src, 0, 0, null);

		g2.dispose();
		return result;
	}

	// paint on JFrame
	public void paint(Graphics g) {
		int w = width / 2;
		int h = height / 2;

		Graphics2D g2 = (Graphics2D)g;

		// set JFrame dimensions
		this.setSize(w * 12 + 80, h * 5 + 90);

		// instructions
		g.drawString("1. Select Image (click on small image for browsing UI)", 18, 50);
		g.drawString("2. Outline with left mouse button, backspace to clear", 400, 50);
		g.drawString("3. Enter to create new composite", 1000, 50);
		// UI components
		g.drawImage(select, 18, 61, w, h, this);
		g.setColor(new Color (255, 255, 255));
		g.drawRect(200, 61, w*5, h*5);
		g.drawImage(before, 200, 61, w*5, h*5, this);
		g.drawImage(after, 1000, 61, w*5, h*5, this);

		// brush cursor
		if ((int) MouseInfo.getPointerInfo().getLocation().getX()-this.getX() >= 200 + slider.getValue()*2
				&& (int) MouseInfo.getPointerInfo().getLocation().getX()-this.getX() <= 200 + w*5
				&& (int) MouseInfo.getPointerInfo().getLocation().getY()-this.getY() >= 61 + slider.getValue()*2
				&& (int) MouseInfo.getPointerInfo().getLocation().getY()-this.getY() <= 61 + h*5) {
			g.setColor(new Color (0));
			g.drawOval((int) MouseInfo.getPointerInfo().getLocation().getX()-this.getX() - slider.getValue()*2, 
					(int) MouseInfo.getPointerInfo().getLocation().getY()-this.getY() - slider.getValue()*2, 
					slider.getValue()*2, slider.getValue()*2); 
			timer--;
		}
		

		// adds new point to add to polyline
		if ((int) MouseInfo.getPointerInfo().getLocation().getX()-this.getX() >= 200 + slider.getValue()*2
				&& (int) MouseInfo.getPointerInfo().getLocation().getX()-this.getX() <= 200 + w*5
				&& (int) MouseInfo.getPointerInfo().getLocation().getY()-this.getY() >= 61 + slider.getValue()*2
				&& (int) MouseInfo.getPointerInfo().getLocation().getY()-this.getY() <= 61 + h*5
				&& pressed == true) {
			brushTrailX.add(Integer.valueOf((int) MouseInfo.getPointerInfo().getLocation().getX()-this.getX() - slider.getValue()));
			brushTrailY.add(Integer.valueOf((int) MouseInfo.getPointerInfo().getLocation().getY()-this.getY() - slider.getValue()));
		}

		// polyline variables for coordinates
		int[] trailX = null;
		int[] trailY = null;

		// draws polyline with fill
		if (brushTrailX != null) {
			trailX = new int[brushTrailX.size()];
			trailY = new int[brushTrailX.size()];
			for (int i=0; i < trailX.length; i++) {
				trailX[i] = brushTrailX.get(i).intValue();
				trailY[i] = brushTrailY.get(i).intValue();
			}
			g.setColor(new Color(255, 0, 0, 75));
			g2.setStroke(new BasicStroke(slider.getValue()*2, 0, BasicStroke.JOIN_ROUND));
			g.drawPolygon(trailX, trailY, brushTrailX.size()); 
			g.setColor(new Color(255, 0, 0, 25));
			g.fillPolygon(trailX, trailY, brushTrailX.size()); 
		}

		// ensures the code still refreshes and gives a baud rate
		repaint(0,0,1,1);

		// repaints the drawing UI at a lower baud rate for image loading purposes
		if (timer < 0) {
			repaint(200, 61, w*5, h*5);
			timer = 50;
		}

	}

//	private int clip(int v) {
//		v = v > 255 ? 255 : v;
//		v = v < 0 ? 0 : v;
//		return v;
//	}

	// helper classes 
	protected int getRed(int pixel) {
		return (new Color(pixel)).getRed();
	}

	protected int getGreen(int pixel) {
		return (new Color(pixel)).getGreen();
	}

	protected int getBlue(int pixel) {
		return (new Color(pixel)).getBlue();
	}

	// main class
	public static void main(String[] args) {
		IAT455CourseProject img = new IAT455CourseProject();
		img.repaint();
	}

	// mouse listener for the brush UI
	class myMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {

		}

		@Override
		public void mouseEntered(MouseEvent arg0) {

		}

		@Override
		public void mouseExited(MouseEvent arg0) {

		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			pressed = true;
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			pressed = false;
		}
	}

	// key listener for inputs to clear or process the drawn on image
	class MKeyListener extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent event) {

			char ch = event.getKeyChar();

			if (ch == KeyEvent.VK_BACK_SPACE) {

				for (int a = 1; a < 20; a++) {
					brushTrailX.clear();
					brushTrailY.clear();
				}
			}
			
			if (ch == KeyEvent.VK_ENTER) {
				after = dropShadow(after, 10);
				repaint();
			}
		}
	}
}
