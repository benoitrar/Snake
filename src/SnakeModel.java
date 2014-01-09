public class SnakeModel {

    private static final long SLOW_VELOCITY = 50;
    private static final long NORMAL_VELOCITY = 70;
    private static final long QUICK_VELOCITY = 90;

    private long velocity = NORMAL_VELOCITY;

    public long getVelocity() {
        return velocity;
    }

    public void setVelocityToQuick() {
        velocity = QUICK_VELOCITY;
    }

    public void setVelocityToNormal() {
        velocity = NORMAL_VELOCITY;
    }

    public void setVelocityToSlow() {
        velocity = SLOW_VELOCITY;
    }
    
}
