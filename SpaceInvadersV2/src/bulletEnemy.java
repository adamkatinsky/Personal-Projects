public class bulletEnemy {
    private int locationY = 0;

    public bulletEnemy(int y){
        locationY = y;
    }

    public final void height(int y){
        locationY = y;
    }

    public final boolean move(){
        locationY += 2;

        if(locationY < 700){
            return true;
        }
        return false;
    }

    public int getLocationY(){
        return locationY;
    }

}