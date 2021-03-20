import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

public class IAT455CourseProject extends JFrame {

	static int width; // width of the image
	int height; // height of the image
	public static JButton button1;
	static BufferedImage select, placeholderImage;
	
	public IAT455CourseProject() {
		// constructor
    	
        // Get an image from the specified file in the current directory on the
        // local hard disk.
        try {
        	placeholderImage = ImageIO.read(new File("placeholderImg.jpg"));
        } catch (Exception e) {
            System.out.println("Cannot load the provided image");
        }
        this.setTitle("IAT 455 Course Project");
        setVisible(true);
        
        width = placeholderImage.getWidth();
        height = placeholderImage.getHeight();
        
		this.addWindowListener(
				new WindowAdapter(){//anonymous class definition
					public void windowClosing(WindowEvent e){
						System.exit(0);//terminate the program
					}//end windowClosing()
				}//end WindowAdapter
				);//end addWindowListener
		button1 = new JButton("Browse");
	    button1.setBounds(17, 560, width/2, height/2);
	    
	    add(button1);
        
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        button1.setOpaque(false);
        button1.setContentAreaFilled(false);
        button1.setBorderPainted(false);
        
        button1.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		JFileChooser fc = new JFileChooser();
        		fc.setDialogTitle("Please choose an image...");
        		FileNameExtensionFilter filter = new FileNameExtensionFilter("jpg", "png", "bmp", "gif");
        		fc.addChoosableFileFilter(filter);

        		// You should use the parent component instead of null
        		// but it was impossible to tell from the code snippet what that was.
        		if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        		    File selectedFile = fc.getSelectedFile();
        		    try {
        		        select = ImageIO.read(selectedFile);
        		    } catch (IOException ex) {
        		        ex.printStackTrace();
        		    }
        		}
        	}
        });
	}// end constructor
	
	public BufferedImage combineImages(BufferedImage src1, BufferedImage src2) {
        BufferedImage result = new BufferedImage(src1.getWidth(),src1.getHeight(),src1.getType());

        for(int i=0; i <result.getWidth();i++){
            for (int j=0; j< result.getHeight();j++){
                int rgb1 = src1.getRGB(i,j);
                int rgb2 = src2.getRGB(i,j);
                int newR = 0,newG = 0,newB = 0;

                result.setRGB(i,j, new Color (newR,newG,newB).getRGB());
            }
        }
        return result;
    }
	
	public void paint(Graphics g){
		int w = width / 2;
        int h = height / 2;
		
		this.setSize(w * 7 + 300, h * 5 + 220);
	}
	
	public static void main(String[] args) {
		IAT455CourseProject img = new IAT455CourseProject();//instantiate this object
	    img.repaint();//render the image
	}

}
