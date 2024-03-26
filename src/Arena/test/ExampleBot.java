package Arena.test;

import org.dyn4j.geometry.Rotation;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.world.World;

import Arena.engine.Controller;
import Arena.engine.OperationOutOfTurnException;
import Arena.engine.Time;
import Arena.player.RaycastData;

public class ExampleBot extends Controller
{
    private float targetDeg = 90f;
    private static final float DEG_ACCURACY_THRESHOLD = 0.3f;

    public ExampleBot(World<SimulationBody> world)
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
    
    @Override
    protected void Update() throws OperationOutOfTurnException
    {
        float speed = 1;

        RaycastData data = getObjectInSight();
        if (data != null)
        {
            data.getEndPoint();
            speed = 1;
        }

        SetMotionInput(new MotionInput(speed, RotateToDegrees()));

        if (Time.getCurrentTick() % 300 == 0)
        {
            targetDeg += 90;
            targetDeg = targetDeg % 360;
        }
    }

    @Override
    protected String getName()
    {   
        return "Evil Example Bot";
    }
}
