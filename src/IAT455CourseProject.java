import java.awt.Color;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class IAT455CourseProject extends Frame {

	public IAT455CourseProject() {
		
		this.addWindowListener(
				new WindowAdapter(){//anonymous class definition
					public void windowClosing(WindowEvent e){
						System.exit(0);//terminate the program
					}//end windowClosing()
				}//end WindowAdapter
				);//end addWindowListener
	}// end constructor
	
	public BufferedImage combineImages(BufferedImage src1, BufferedImage src2)
    {
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
