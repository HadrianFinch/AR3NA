package Arena;
/*
 * Copyright (c) 2010-2022 William Bittle http://www.dyn4j.org/ All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. * Neither the name of dyn4j nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.List;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Ray;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.samples.framework.Camera;
import org.dyn4j.samples.framework.SimulationBody;
import org.dyn4j.samples.framework.SimulationFrame;
import org.dyn4j.world.DetectFilter;
import org.dyn4j.world.World;
import org.dyn4j.world.result.RaycastResult;

import Arena.engine.Controller;
import Arena.engine.Time;
import Arena.test.ExampleBot;
import Arena.test.TestBot;

public class App extends SimulationFrame
{
    private Controller bot1;
    private Controller bot2;

    private Time time = new Time();
    
    public App()
    {
        super("Tank");
    }

    @Override
    protected void initializeCamera(Camera camera)
    {
        super.initializeCamera(camera);
        camera.scale = 48.0;
    }

    @Override
    protected void printControls()
    {
        super.printControls();
    }

    protected void initializeWorld()
    {
        this.world.setGravity(World.ZERO_GRAVITY);

        // Triangle
        SimulationBody triangle = new SimulationBody();
        triangle.addFixture(Geometry.createTriangle(new Vector2(0.0, 0.5), new Vector2(-0.5, -0.5), new Vector2(0.5, -0.5)));
        triangle.translate(new Vector2(-2.5, 3));
        triangle.setMass(MassType.NORMAL);
        this.world.addBody(triangle);

        // Segment
        SimulationBody segment = new SimulationBody();
        segment.addFixture(Geometry.createSegment(new Vector2(0.5, 0.5), new Vector2(-0.5, 0)));
        segment.translate(new Vector2(-4.2, 4));
        segment.setMass(MassType.INFINITE);
        this.world.addBody(segment);

        // Square
        SimulationBody square = new SimulationBody();
        square.addFixture(Geometry.createSquare(1.0));
        square.translate(new Vector2(1.5, -2.0));
        square.setMass(MassType.INFINITE);
        this.world.addBody(square);

        // Polygon
        SimulationBody polygon = new SimulationBody();
        polygon.addFixture(Geometry.createUnitCirclePolygon(5, 0.5));
        polygon.translate(new Vector2(2.0, 0));
        polygon.setMass(MassType.INFINITE);
        this.world.addBody(polygon);

        // Capsule
        SimulationBody capsule = new SimulationBody();
        capsule.addFixture(Geometry.createCapsule(2, 1));
        capsule.translate(new Vector2(-4.5, -5.0));
        capsule.setMass(MassType.INFINITE);
        this.world.addBody(capsule);

        bot1 = new ExampleBot(this.world);
        bot2 = new TestBot(this.world);
    }

    protected void render(Graphics2D g, double elapsedTime)
    {
        super.render(g, elapsedTime);

        final double scale = this.getCameraScale();
        bot1.DrawGizmos(g, elapsedTime, scale);
        bot2.DrawGizmos(g, elapsedTime, scale);
    }

    @Override
    protected void handleEvents()
    {
        super.handleEvents();

        if (!this.isPaused())
        {
            time.Update();
            bot1.GlobalUpdate();
            bot2.GlobalUpdate();
        }
    }

    public static void main(String[] args)
    {
        App simulation = new App();
        simulation.run();
    }
}
