package com.blakequ.androidblemanager.ui.scan;

/**
 * Created by DorisLiu on 6/6/17.
 */

public class IbeaconStore {

    double Y;
    double X;

    public IbeaconStore(double X, double Y){
        this.X = X;
        this.Y = Y;
    }

    public double getY(){
        return Y;
    }

    public double getX(){
        return  X;
    }


}
