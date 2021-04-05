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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import javafx.scene.shape.Polyline;

public class IAT455CourseProject extends JFrame {

	static int width, height, mouseX, mouseY, timer;
	public static JButton button1;
	static BufferedImage select, placeholderImage, before;
	boolean pressed, browsePressed;
	public JSlider slider;
	ArrayList<Integer> brushTrailX = new ArrayList<Integer>();
	ArrayList<Integer> brushTrailY = new ArrayList<Integer>();

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

		before = select;

		this.setTitle("IAT 455 Course Project");
		setVisible(true);

		width = placeholderImage.getWidth();
		height = placeholderImage.getHeight();
		timer = 100;

		this.addWindowListener(new WindowAdapter() {// anonymous class definition
			public void windowClosing(WindowEvent e) {
				System.exit(0);// terminate the program
			}// end windowClosing()
		}// end WindowAdapter
				);// end addWindowListener

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

		button1 = new JButton("browse");
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

				// You should use the parent component instead of null
				// but it was impossible to tell from the code snippet what that was.
				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					brushTrailX.clear();
					brushTrailY.clear();
					repaint();
					File selectedFile = fc.getSelectedFile();
					try {
						select = ImageIO.read(selectedFile);
						before = ImageIO.read(selectedFile);
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

	public BufferedImage multiplyImages(BufferedImage src1, BufferedImage src2) {
		BufferedImage result = new BufferedImage(src1.getWidth(), src1.getHeight(), src1.getType());

		for (int i = 0; i < result.getWidth(); i++) {
			for (int j = 0; j < result.getHeight(); j++) {
				int rgb1 = src1.getRGB(i, j);
				int rgb2 = src2.getRGB(i, j);
				int newR = 0, newG = 0, newB = 0;

				newR = (getRed(rgb1) * (getRed(rgb2))) / 255;
				newG = (getGreen(rgb1) * (getGreen(rgb2))) / 255;
				newB = (getBlue(rgb1) * (getBlue(rgb2))) / 255;
				newR = clip(newR);
				newG = clip(newG);
				newB = clip(newB);

				result.setRGB(i, j, new Color(newR, newG, newB).getRGB());
			}
		}
		return result;
	}
	
	public void test() {
		
		
		
	}

	public void paint(Graphics g) {
		int w = width / 2;
		int h = height / 2;

		Graphics2D g2 = (Graphics2D)g;

		this.setSize(w * 12 + 80, h * 5 + 90);

		g.drawString("1. Select Image (click on small image for browsing UI)", 18, 50);
		g.drawString("2. Outline with left mouse button, backspace to undo", 400, 50);
		g.drawImage(select, 18, 61, w, h, this);
		g.drawImage(before, 200, 61, w*5, h*5, this);

		if ((int) MouseInfo.getPointerInfo().getLocation().getX()-this.getX() >= 200 
				&& (int) MouseInfo.getPointerInfo().getLocation().getX()-this.getX() <= 200 + w*5
				&& (int) MouseInfo.getPointerInfo().getLocation().getY()-this.getY() >= 61
				&& (int) MouseInfo.getPointerInfo().getLocation().getY()-this.getY() <= 61 + h*5) {
			g.drawOval((int) MouseInfo.getPointerInfo().getLocation().getX()-this.getX() - slider.getValue()*2, 
					(int) MouseInfo.getPointerInfo().getLocation().getY()-this.getY() - slider.getValue()*2, 
					slider.getValue()*2, slider.getValue()*2); 
			timer--;
		}

		if ((int) MouseInfo.getPointerInfo().getLocation().getX()-this.getX() >= 200 
				&& (int) MouseInfo.getPointerInfo().getLocation().getX()-this.getX() <= 200 + w*5
				&& (int) MouseInfo.getPointerInfo().getLocation().getY()-this.getY() >= 61
				&& (int) MouseInfo.getPointerInfo().getLocation().getY()-this.getY() <= 61 + h*5
				&& pressed == true) {
			brushTrailX.add(Integer.valueOf((int) MouseInfo.getPointerInfo().getLocation().getX()-this.getX() - slider.getValue()));
			brushTrailY.add(Integer.valueOf((int) MouseInfo.getPointerInfo().getLocation().getY()-this.getY() - slider.getValue()));
		}

		int[] trailX = null;
		int[] trailY = null;

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
		
		test();

		repaint(0,0,1,1);

		if (timer < 0) {
			repaint(200, 61, w*5, h*5);
			timer = 100;
		}

	}

	private int clip(int v) {
		v = v > 255 ? 255 : v;
		v = v < 0 ? 0 : v;
		return v;
	}

	protected int getRed(int pixel) {
		return (new Color(pixel)).getRed();
	}

	protected int getGreen(int pixel) {
		return (new Color(pixel)).getGreen();
	}

	protected int getBlue(int pixel) {
		return (new Color(pixel)).getBlue();
	}

	public static void main(String[] args) {
		IAT455CourseProject img = new IAT455CourseProject();
		img.repaint();
	}

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
		}
	}
}
