/*******************************************************************************
 *
 *   Copyright 2016 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 *   Copyright 2016 Luis Llamas <luis.llamas@mytechia.com>
 *
 *   This file is part of Robobo HRI Modules.
 *
 *   Robobo HRI Modules is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Robobo HRI Modules is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Robobo HRI Modules.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/


package com.mytechia.robobo.framework.hri.touch.android;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Looper;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;


import com.mytechia.robobo.framework.LogLvl;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.exception.ModuleNotFoundException;
import com.mytechia.robobo.framework.hri.touch.ATouchModule;
import com.mytechia.robobo.framework.hri.touch.TouchGestureDirection;
import com.mytechia.robobo.framework.power.PowerMode;
import com.mytechia.robobo.framework.remote_control.remotemodule.IRemoteControlModule;

import java.sql.Timestamp;


/**
 * Implementation of the Robobo touch module using the Android API
 */
public class AndroidTouchModule extends ATouchModule implements GestureDetector.OnGestureListener {

    private GestureDetectorCompat mDetector;

    private String TAG = "TouchModule";
    public  AndroidTouchModule(){
        super();
    }
    private long startupTime ;
    private int display_width;
    private int display_height;

    public void startup(RoboboManager manager){
        m = manager;
        //Looper.prepare();
        startupTime = System.currentTimeMillis();

        WindowManager wm = (WindowManager) manager.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        display_width = size.x;
        display_height = size.y;

        mDetector = new GestureDetectorCompat(manager.getApplicationContext(),this);
        try {
            rcmodule = manager.getModuleInstance(IRemoteControlModule.class);
        } catch (ModuleNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void shutdown(){

    }

    @Override
    public String getModuleInfo() {
        return "Touch module";
    }

    @Override
    public String getModuleVersion() {
        return "0.3.0";
    }


    private void awakeRobo() {
        this.m.changePowerModeTo(PowerMode.NORMAL);
    }


    public boolean onTouchEvent(MotionEvent event){

        awakeRobo(); //awake the robot on any touch event

        return this.mDetector.onTouchEvent(event);

    }

    public boolean feedTouchEvent(MotionEvent event){

        return this.mDetector.onTouchEvent(event);

    }


    @Override
    public boolean onDown(MotionEvent motionEvent) {

        awakeRobo(); //awake the robot on any touch event

        return false;

    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

        awakeRobo(); //awake the robot on any touch event

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {

        awakeRobo(); //awake the robot on any touch event

        MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
        motionEvent.getPointerCoords(0,coords);
        m.log(LogLvl.TRACE, TAG,"Current "+motionEvent.getEventTime()+"ms");
        m.log(LogLvl.TRACE, TAG,"Event "+motionEvent.getDownTime()+"ms");
        m.log(LogLvl.TRACE, TAG,"Difference "+(motionEvent.getEventTime()-(int)motionEvent.getDownTime())+"ms");
        if((motionEvent.getEventTime()-(int)motionEvent.getDownTime())>=500){

            onLongPress(motionEvent);

        }else {
            notifyTap(Math.round((coords.x/display_width)*100), Math.round((coords.y/display_height)*100));
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

        m.log(LogLvl.TRACE, TAG,"onScroll");

        awakeRobo(); //awake the robot on any touch event

        MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
        motionEvent.getPointerCoords(0,coords);
        MotionEvent.PointerCoords coords1 = new MotionEvent.PointerCoords();
        motionEvent1.getPointerCoords(0,coords1);
        int motionx = Math.round(coords.x)-Math.round(coords1.x);
        int motiony = Math.round(coords.y)-Math.round(coords1.y);
        if (Math.abs(motionx)>Math.abs(motiony)){
            if (motionx>=0){
                notifyCaress(TouchGestureDirection.LEFT);
            }else {
                notifyCaress(TouchGestureDirection.RIGHT);
            }
        }else{
            if (motiony>=0){
                notifyCaress(TouchGestureDirection.UP);
            }else {
                notifyCaress(TouchGestureDirection.DOWN);
            }
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

        awakeRobo(); //awake the robot on any touch event

        m.log(LogLvl.TRACE, TAG,"onLongPress");
        MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
        motionEvent.getPointerCoords(0,coords);
        notifyTouch(Math.round((coords.x/display_width)*100), Math.round((coords.y/display_height)*100));

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

        awakeRobo(); //awake the robot on any touch event

        long time =(motionEvent1.getEventTime()-motionEvent.getEventTime());
        m.log(LogLvl.TRACE, TAG,"onFling "+time);
        int x1,x2,y1,y2;

        MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
        motionEvent.getPointerCoords(0,coords);

        x1 = Math.round(coords.x);
        y1 = Math.round(coords.y);


        MotionEvent.PointerCoords coords1 = new MotionEvent.PointerCoords();
        motionEvent1.getPointerCoords(0,coords1);

        x2 = Math.round(coords1.x);
        y2 = Math.round(coords1.y);
        m.log(LogLvl.TRACE, TAG,"x1: "+x1+" x2: "+x2+" y1: "+y1+" y2: "+y2);
        double distance = Math.sqrt(Math.pow((x2-x1),2)+Math.pow((y2-y1),2));
        m.log(LogLvl.TRACE, TAG,"Distance: "+distance );
        int motionx = x1-x2;
        int motiony = y1-y2;
        //y1 - y2 for top left reference system
        double angle = Math.atan2((y1-y2),(x2-x1));
        m.log(LogLvl.TRACE, TAG,"Angle: "+angle);
        if (angle<0){angle = Math.PI +(Math.PI+angle);}
        if (Math.abs(motionx)>Math.abs(motiony)){
            if (motionx>=0){
                notifyFling(TouchGestureDirection.LEFT,angle,time,distance);
            }else {
                notifyFling(TouchGestureDirection.RIGHT,angle,time,distance);
            }
        }else{
            if (motiony>=0){
                notifyFling(TouchGestureDirection.UP,angle,time,distance);
            }else {
                notifyFling(TouchGestureDirection.DOWN,angle,time,distance);
            }
        }
        return true;
    }
}
