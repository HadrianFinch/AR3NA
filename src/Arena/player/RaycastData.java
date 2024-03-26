package Arena.player;

import java.util.List;
import java.util.Vector;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.world.DetectFilter;
import org.dyn4j.world.World;
import org.dyn4j.world.result.RaycastResult;

public class RaycastData
{
    private final Vector2 startPoint;
    private final Vector2 endPoint;
    private final RaycastResult<SimulationBody, BodyFixture> result;
    private final SimulationBody hitBody;

    public Vector2 getEndPoint()
    {
        return endPoint.copy();
    }
    public Vector2 getNormal()
    {
        return result.getRaycast().getNormal();
    }
    public Vector2 getDirection()
    {
        Vector2 v = endPoint.subtract(startPoint);
        // v.normalize();
        return v;
    }

    
    private RaycastData(RaycastResult<SimulationBody, BodyFixture> result, Vector2 startPoint)
    {
        this.result = result;
        this.startPoint = startPoint;
        this.endPoint = result.getRaycast().getPoint();
        this.hitBody = result.getBody();
    }

    public static RaycastData Raycast(World<SimulationBody> world, Vector2 start, Vector2 dir, double maxLength)
    {
        Ray ray = new Ray(start, dir);
        // g.setColor(Color.RED);
        // g.draw(new Line2D.Double(ray.getStart().x * scale, ray.getStart().y * scale, ray.getStart().x * scale + ray.getDirectionVector().x * length * scale, ray.getStart().y * scale + ray.getDirectionVector().y * length * scale));

        RaycastResult<SimulationBody, BodyFixture> result = world.raycastClosest(ray, maxLength, new DetectFilter<SimulationBody, BodyFixture>(true, true, null));
        return result != null ? (new RaycastData(result, start)) : null;
    }
}
