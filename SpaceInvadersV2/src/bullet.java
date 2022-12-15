public class bullet {
    private int locationY = 0;

    public bullet(int y){
        locationY = y;
    }

    public final void height(int y){
        locationY = y;
    }

    public final boolean move(){
        locationY -= 4;

        if(locationY < -50){
            return true;
        }
        return false;
    }

    public int getLocationY(){
        return locationY;
    }

}