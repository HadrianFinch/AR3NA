package Arena.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Rotation;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.world.World;

import Arena.engine.Controller;
import Arena.engine.OperationOutOfTurnException;
import Arena.engine.Time;
import Arena.player.RaycastData;

public class TestBot extends Controller
{
    private float targetDeg = 90f;
    private static final float DEG_ACCURACY_THRESHOLD = 0.3f;

    private ArrayList<RaycastData> sightData = new ArrayList<>();

    public TestBot(World<SimulationBody> world)
    {
        super(world);
    }

    private float RotateToDegrees()
    {
        Rotation rotation = this.getRotation();
        Rotation targeRotation = new Rotation(targetDeg * Math.PI / 180);

        float turning = rotation.compare(targeRotation);
        
        if (Math.abs(rotation.getRotationBetween(targeRotation).toDegrees()) < DEG_ACCURACY_THRESHOLD)
        {
            turning = 0;
        }
        return turning;
    }

    private void ProcessVision() throws OperationOutOfTurnException
    {
        sightData.clear();
        for (float angle = -MAX_SIGHT_ANGLE; angle <= MAX_SIGHT_ANGLE; angle += 1f)
        {
            RaycastData line = this.getObjectInSight(angle);
            if (line != null)
            {
                sightData.add(line);
            }
        }
    }
    
    @Override
    protected void Update() throws OperationOutOfTurnException
    {
        ProcessVision();
        float speed = 0.0f;

        if (Time.getCurrentTick() % 300 == 0)
        {
            targetDeg += 90;
            targetDeg = targetDeg % 360;
        }

        SetMotionInput(new MotionInput(speed, /* RotateToDegrees() */0));
    }

    @Override
    public void DrawGizmos(Graphics2D g, double timeDelta, double scale)
    {
        for (RaycastData raycastData : sightData)
        {
            Vector2 start = this.getPos();
            Vector2 direction = raycastData.getDirection();
            double length = /* start.distance */(raycastData.getEndPoint()).getMagnitude();
    
            Ray ray = new Ray(start, direction);
            g.setColor(Color.RED);
            g.draw(new Line2D.Double(ray.getStart().x * scale, ray.getStart().y * scale, ray.getStart().x * scale + ray.getDirectionVector().x * length * scale, ray.getStart().y * scale + ray.getDirectionVector().y * length * scale));
        }
    }

    @Override
    protected String getName()
    {   
        return "Test Bot 1";
    }
}
