package Arena.engine;

public class Time
{
    private int tick = 0;
    private static Time main = null;

    public static int getCurrentTick()
    {
        return main.tick;
    }

    public Time()
    {
        if (Time.main == null)
        {
            Time.main = this;
        }
    }

    public void Update()
    {
        tick++;
    }
}
