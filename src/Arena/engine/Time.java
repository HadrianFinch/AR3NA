package Arena.engine;

public class Time
{
    private static Time main = null;
    public static float DeltaTtime()
    {
        return main.deltaTime;
    }
    public static float TotalTime()
    {
        return main.lastUpdateTime;
    }

    private float lastUpdateTime = 0f;
    private float deltaTime = 0f;
    private float programStartTime;

    public Time()
    {
        programStartTime = (float)(System.nanoTime()) / (1000000f * 1000f);
        if (Time.main == null)
        {
            Time.main = this;
        }
    }

    public void Update()
    {
        float currentTime = (float)(System.nanoTime()) / (1000000f * 1000f) - programStartTime;
        deltaTime = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;
    }
}
