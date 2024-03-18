package Arena.engine;

import org.dyn4j.dynamics.joint.FrictionJoint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Interval;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.world.World;
import org.tinylog.Logger;
import org.tinylog.TaggedLogger;

public abstract class Controller
{
    protected final TaggedLogger logger = Logger.tag(getName());
    
    private final float maxSpeed = 5f;
    private final float maxTurningSpeed = 4f;

    private SimulationBody tank;
    // private SimulationBody barrel;
    
    private boolean inTurn = false;
    private MotionInput motionInput = new MotionInput(0, 0);

    public Controller(World<SimulationBody> world)
    {
        // Circle
        SimulationBody circle = new SimulationBody();
        circle.setUserData(Arena.App.INDESTRUCTIBLE);
        circle.addFixture(Geometry.createCircle(0.5));
        circle.translate(new Vector2(3.2, 3.5));
        circle.setMass(MassType.INFINITE);
        world.addBody(circle);

        tank = new SimulationBody();
        tank.addFixture(Geometry.createRectangle(1.0, 1.5));
        tank.addFixture(Geometry.createCircle(0.35));
        tank.setMass(MassType.NORMAL);
        world.addBody(tank);

        // barrel = new SimulationBody();
        // // NOTE: make the mass of the barrel less so that driving doesn't turn the barrel
        // barrel.addFixture(Geometry.createRectangle(0.15, 1.0), 0.2);
        // barrel.setMass(MassType.NORMAL);
        // barrel.translate(0.0, 0.5);
        // world.addBody(barrel);

        // // make the barrel pivot about the body
        // RevoluteJoint<SimulationBody> rj = new RevoluteJoint<SimulationBody>(tank, barrel, tank.getWorldCenter());
        // world.addJoint(rj);

        // add friction to the motion of the body driving
        FrictionJoint<SimulationBody> fj2 = new FrictionJoint<SimulationBody>(tank, circle, tank.getWorldCenter());
        fj2.setMaximumForce(2);
        fj2.setMaximumTorque(1);
        fj2.setCollisionAllowed(true);
        world.addJoint(fj2);

        // // add fricition to the motion of the barrel
        // FrictionJoint<SimulationBody> fj = new FrictionJoint<SimulationBody>(circle, barrel, tank.getWorldCenter());
        // fj.setMaximumForce(0);
        // fj.setMaximumTorque(0.2);
        // fj.setCollisionAllowed(true);
        // world.addJoint(fj);
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
            this.speed = speed;
            this.turn = turn;
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
        normal.multiply(maxSpeed * motionInput.speed);
        tank.applyForce(normal);

        // turning
        tank.applyTorque((Math.PI / 2) * motionInput.turn * maxTurningSpeed);
        
        
        inTurn = false;
    }

    protected abstract void Update() throws OperationOutOfTurnException;
    protected abstract String getName();
}
