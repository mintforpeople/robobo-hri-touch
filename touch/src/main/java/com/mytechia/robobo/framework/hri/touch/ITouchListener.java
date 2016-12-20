package com.mytechia.robobo.framework.hri.touch;
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

/**
 * Interface of the touch listener
 */
public interface ITouchListener {
    //TODO Cambiar integers por floats?

    /**
     * Notifies a single tap on nthe screen
     * @param x X coordinate
     * @param y Y coordinate
     */
    void tap(Integer x, Integer y);

    /**
     * Notifies a long tap on the screen
     * @param x X coordinate
     * @param y Y COordinate
     */
    void touch(Integer x, Integer y);

    /**
     * Notifies a fast movement through the screen
     * @param dir Direction of the movement
     * @param angle Angle of the movement
     * @param time Time elapsed during the movement
     * @param distance Distance over the screen
     */
    void fling(TouchGestureDirection dir, double angle, long time,double distance);

    /**
     * Notifies a slow scroll over the screen
     * @param dir Direction nof the movement
     */
    void caress(TouchGestureDirection dir);

}
