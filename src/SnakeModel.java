public class SnakeModel {

    private static final long LITTLE_DELAY = 50;
    private static final long NORMAL_DELAY = 70;
    private static final long BIG_DELAY = 90;

    private long delay = NORMAL_DELAY;

    public long getDelay() {
        return delay;
    }

    public void setVelocityToQuick() {
        delay = LITTLE_DELAY;
    }

    public void setVelocityToNormal() {
        delay = NORMAL_DELAY;
    }

    public void setVelocityToSlow() {
        delay = BIG_DELAY;
    }
    
}
