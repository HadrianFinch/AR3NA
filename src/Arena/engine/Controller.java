 package Arena.engine;

import java.awt.Graphics2D;
import java.security.InvalidParameterException;

import org.dyn4j.dynamics.joint.FrictionJoint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rotation;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.world.World;
import org.tinylog.Logger;
import org.tinylog.TaggedLogger;

import Arena.player.RaycastData;

public abstract class Controller
{
    protected final TaggedLogger logger = Logger.tag(getName());
    
    private static final float MAX_DRIVE_SPEED = 10f;
    private static final float MAX_TURNING_SPEED = 6f;
    private static final float MAX_SIGHT_DISTANCE = 10;
    public static final float MAX_SIGHT_ANGLE = 15;

    private final SimulationBody tank;
    private final World<SimulationBody> world;
    // private SimulationBody barrel;
    
    private boolean inTurn = false;
    private MotionInput motionInput = new MotionInput(0, 0);

    public Controller(World<SimulationBody> world)
    {
        this.world = world;

        // Circle
        SimulationBody circle = new SimulationBody();
        circle.setUserData(this);
        circle.addFixture(Geometry.createCircle(0.5));
        circle.translate(new Vector2(3.2, 3.5));
        circle.rotateAboutCenter(Math.PI * 0.25);
        circle.setMass(MassType.INFINITE);
        world.addBody(circle);

        tank = new SimulationBody();
        tank.addFixture(Geometry.createRectangle(1.0, 1.5));
        tank.addFixture(Geometry.createCircle(0.35));
        tank.setMass(MassType.NORMAL);
        world.addBody(tank);

        // add friction to the motion of the body driving
        FrictionJoint<SimulationBody> fj2 = new FrictionJoint<SimulationBody>(tank, circle, tank.getWorldCenter());
        fj2.setMaximumForce(2);
        fj2.setMaximumTorque(1);
        fj2.setCollisionAllowed(true);
        world.addJoint(fj2);
    }

    public static class MotionInput
    {
        /**
         * Between -1 (backwards) and 1 (forwards), controls the forward speed of the bot
         */
        public final float speed;

        /**
         * Between -1 (left) and 1 (right), controls the turning speed of the bot
         */
        public final float turn;

        /**
         * @param speed Between -1 (backwards) and 1 (forwards), controls the forward speed of the bot
         * @param turn Between -1 (left) and 1 (right), controls the turning speed of the bot
         */
        public MotionInput(float speed, float turn)
        {
            this.speed = Math.max(-1, Math.min(1, speed));
            this.turn = Math.max(-1, Math.min(1, turn));
        }
    }

    public Vector2 getPos()
    {
        return tank.getWorldCenter().copy();
    }

    public Transform getTransform()
    {
        return tank.getTransform().copy();
    }

    public Rotation getRotation()
    {
        return tank.getTransform().getRotation().copy();
    }
    
    private void ThrowIfOutOfTurn() throws OperationOutOfTurnException
    {
        if (!inTurn)
        {
            throw new OperationOutOfTurnException();
        }
    }
    
    protected void SetMotionInput(MotionInput motionInput) throws OperationOutOfTurnException
    {
        ThrowIfOutOfTurn();
        this.motionInput = motionInput;
    }

    protected RaycastData getObjectInSight() throws OperationOutOfTurnException
    {
        ThrowIfOutOfTurn();
        return RaycastData.Raycast(world, getPos(), getTransform().getTransformedR(new Vector2(0, 1)), MAX_SIGHT_DISTANCE);
    } 
    protected RaycastData getObjectInSight(float angle) throws OperationOutOfTurnException
    {
        ThrowIfOutOfTurn();

        if (angle > MAX_SIGHT_ANGLE || angle < -MAX_SIGHT_ANGLE)
        {
            throw new InvalidParameterException("Trying to look outside of sight lines");
        }

        return RaycastData.Raycast(world, getPos(), getTransform().getTransformedR(new Vector2(0, 1).rotate(angle * (Math.PI / 180.0))), MAX_SIGHT_DISTANCE);
    } 

    public void DrawGizmos(Graphics2D g, double timeDelta, double scale)
    {
        return;
    }
    
    public void GlobalUpdate()
    {
        InternalUpdate();
    }

    private void InternalUpdate()
    {
        inTurn = true;
        
        try
        {
            Update();
        }
        catch (OperationOutOfTurnException e)
        {
            logger.error("Failed in turn check but marked in turn!");
        }

        Vector2 normal = tank.getTransform().getTransformedR(new Vector2(0.0, 1.0));
        double defl = tank.getLinearVelocity().dot(normal);
        // clamp the velocity
        defl = Interval.clamp(defl, -2, 2);
        tank.setLinearVelocity(normal.multiply(defl));
        
        // clamp the angular velocity
        double av = tank.getAngularVelocity();
        av = Interval.clamp(av, -1, 1);
        tank.setAngularVelocity(av);
        
        // // clamp the angular velocity of the barrel
        // av = barrel.getAngularVelocity();
        // av = Interval.clamp(av, -1, 1);
        // barrel.setAngularVelocity(av);


        // apply force based on vars
        // driving
        normal = tank.getTransform().getTransformedR(new Vector2(0.0, 1.0));
        normal.multiply(MAX_DRIVE_SPEED * motionInput.speed);
        tank.applyForce(normal);

        // turning
        tank.applyTorque((Math.PI / 2) * motionInput.turn * MAX_TURNING_SPEED);
        
        
        inTurn = false;
    }

    protected abstract void Update() throws OperationOutOfTurnException;
    protected abstract String getName();
}
