import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class MouseMover {

    private static final Logger log = Logger.getLogger(MouseMover.class.getName());

    public static void main(String... args) throws AWTException {
        int shortDelay = 1;
        int longDelay = 5;
        int distance = 50;

        log.info("Short Delay: " + shortDelay + "; Long Delay: " + longDelay + "; Distance: " + distance);

        Robot robot = new Robot();
        Point oldLocation = new Point(-1, -1);
        while (true) {
            try {
                PointerInfo pointerInfo = MouseInfo.getPointerInfo();
                Point location = new Point(pointerInfo.getLocation());
                Rectangle bounds = pointerInfo.getDevice().getDefaultConfiguration().getBounds();
                location.translate(-bounds.x, -bounds.y);
                if (location.x < 0)
                    location.x *= -1;
                if (location.y < 0)
                    location.y *= -1;

                if (location.equals(oldLocation)) {
                    Point newLocation = Stream.of(new Point(distance, 0), new Point(-distance, 0))
                            .map(d -> new Point(bounds.x + location.x + d.x, bounds.y + location.y + d.y))
                            .filter(bounds::contains)
                            .findAny()
                            .orElseThrow(() -> new IllegalStateException("Cannot compute new location"));
                    robot.mouseMove(newLocation.x, newLocation.y);
                    log.fine("New " + newLocation + " in " + bounds);
                    newLocation.translate(-bounds.x, -bounds.y);
                    oldLocation.setLocation(newLocation);
                    distance = -distance;
                    Thread.sleep(shortDelay * 1000);
                } else {
                    log.fine("Old " + location + " in " + bounds);
                    oldLocation.setLocation(location);
                    Thread.sleep(longDelay * 1000);
                }
            } catch (Exception e) {
                log.log(Level.SEVERE, e.getMessage(), e);
                break;
            }
        }
    }
}
