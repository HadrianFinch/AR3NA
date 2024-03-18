package Arena.test;

import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.world.World;

import Arena.engine.Controller;
import Arena.engine.OperationOutOfTurnException;

public class TestBot extends Controller
{
    // private float directionMultiplier = 1;
    // private float speedMultiplier = 1;

    public TestBot(World<SimulationBody> world)
    {
        super(world);
    }

    @Override
    protected void Update() throws OperationOutOfTurnException
    {
        SetMotionInput(new MotionInput(1f, 0.8f));
    }

    @Override
    protected String getName()
    {   
        return "Test Bot 1";
    }
}
