import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
	
	public void paint(Graphics g){
		
	}
	
	public static void main(String[] args) {
		IAT455CourseProject img = new IAT455CourseProject();//instantiate this object
	    img.repaint();//render the image
	}

}
