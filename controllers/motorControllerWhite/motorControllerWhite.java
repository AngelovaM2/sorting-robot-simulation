import com.cyberbotics.webots.controller.Robot;
import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.Camera;
import com.cyberbotics.webots.controller.PositionSensor;
import com.cyberbotics.webots.controller.LED;

public class motorControllerWhite {
  
  public static void main(String[] args) {
  
    // Robot initialization
    final Robot robot = new Robot();
    final LED light = robot.getLED("led");
    final Motor motorW = robot.getMotor("linear motor");
    final Camera cameraW = robot.getCamera("Wcamera");
    final PositionSensor positionSensorW = robot.getPositionSensor("position sensor");
    
    final Motor motorB = robot.getMotor("linear motor B");
    final Camera cameraB = robot.getCamera("Bcamera");
    final PositionSensor positionSensorB = robot.getPositionSensor("position sensor B");

    final int timeStep = 64;
    double currentTime = 0;
    
    // Enabling camera, position sensor and motor for the white and black disks separately
    // as Webots does not support multithreading and one sensor cannot be used for both hands at
    // the same time.
    cameraW.enable(timeStep); 
    cameraB.enable(timeStep);
    positionSensorW.enable(timeStep);
    positionSensorB.enable(timeStep);
    motorW.setVelocity(15);
    motorB.setVelocity(15);
    
    // Disk counters
    int blackCounter = 0;
    int whiteCounter = 0;
    
    // The software does not support console input that is why we
    // need to do it manualy 
    // MODE SELECTOR
    // Can be either "SortAll" or "SortSome"
    String mode = "SortAll";
    
    // Used only if the selected mode is "SortSome"
    int blackWanted = 2;
    int whiteWanted = 1;
    
    // For some reason the sensors detects each disk twice
    // so we adjust the number of wanted disks
    blackWanted *= 2;
    whiteWanted *= 2;

    while (robot.step(timeStep) != -1) {
    
      // Reading sensor values
      int[] imageW = cameraW.getImage();
      
      // Adding RGB to both sensors
      int pixelW = imageW[2048];
      int rw = Camera.pixelGetRed(pixelW);
      int gw = Camera.pixelGetGreen(pixelW);
      int bw = Camera.pixelGetBlue(pixelW);
      
      int[] imageB = cameraB.getImage();
      
      int pixelB = imageB[2048];
      int r = Camera.pixelGetRed(pixelB);
      int g = Camera.pixelGetGreen(pixelB);
      int b = Camera.pixelGetBlue(pixelB);
      
      // Running the chosen mode
      // Nested if-else statements is not a very clear code,
      // however the software does not allow us to define functions,
      // does not support input (cannot use switch for the modes)
      // and some imports do not work, as well
      if (mode == "SortAll") {
      
        if (robot.getTime() > currentTime + 0.1) {
          
          // Green disk detection turns on a green light for a second
          if (r == 11 & g == 234 & b == 22) {
          
            light.set(1);
            System.out.println(robot.getTime());
            robot.step(timeStep * 5);
            light.set(0);
            
          }  
          
          // White disk sorting    
          if (rw == 234 & gw == 234 & bw == 237) {
          
            motorW.setPosition(-0.2); 
          }
        
          if (positionSensorW.getValue() < -0.035) {
          
            currentTime = robot.getTime();
            motorW.setPosition(0);
            whiteCounter += 1;
          } 
          
          // Black disk sorting 
          if (r == 11 & g == 14 & b == 22) {
          
            motorB.setPosition(-0.2);
          }
          
          if (positionSensorB.getValue() < -0.035) {
          
            blackCounter += 1;
            currentTime = robot.getTime();
            motorB.setPosition(0);
          }
        
        }
      } else if (mode == "SortSome") {
      
        if (robot.getTime() > currentTime + 0.1) {  
          
          // Green disk detection turns on a green light for a second
          if (r == 11 & g == 234 & b == 22) {
          
            light.set(1);
            System.out.println(robot.getTime());
            robot.step(timeStep * 5);
            light.set(0);
          }  
          
          // White disk sorting   
          if (whiteWanted > whiteCounter) {
          
            if (rw == 234 & gw == 234 & bw == 237) {
            
              motorW.setPosition(-0.2);
            }
          
            if (positionSensorW.getValue() < -0.035) {
            
              currentTime = robot.getTime();
              motorW.setPosition(0);
              whiteCounter += 1;
            }
          } 
          
          // Black disk sorting 
          if (blackWanted > blackCounter) {
          
            if (r == 11 & g == 14 & b == 22) {
            
              motorB.setPosition(-0.2);
            }
            
            if (positionSensorB.getValue() < -0.035) {
            
              blackCounter += 1;
              currentTime = robot.getTime();
              motorB.setPosition(0);
            }
          }
        
        }
      }   
    };
  }
  
}
