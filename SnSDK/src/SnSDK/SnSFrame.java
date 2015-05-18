/*
 * Copyright (C) 2015 Software&System Lab. Kangwon National University.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package SnSDK;

import SnSDK.ExternalDevice.*;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class SnSFrame {

    private DeviceManager devicemanager;
    private Scheduler scheduler;
    static final int DEFAULT_THREAD_NUM = 5;
    static final int DEFAULT_REQEUSTQUEUE_LENGTH = 5;

    /**
     * 프레임 생성 Default number of WorkerThread and Requestqueuelength : 5
     */
    public SnSFrame() {
        devicemanager = new DeviceManager();
        scheduler = new Scheduler(DEFAULT_REQEUSTQUEUE_LENGTH, DEFAULT_THREAD_NUM);
    }

    /**
     * 
     * @param requestQueueLength Maximym request queue length
     * @param numOfThread number of workerthread in framework
     */
    public SnSFrame(int requestQueueLength, int numOfThread) {
        devicemanager = new DeviceManager();
        scheduler = new Scheduler(requestQueueLength, numOfThread);
    }

    public void addDevice(Device device) {
        devicemanager.addDevice(device);
    }

    public Device getDevice(int DeviceID) {
        return devicemanager.getDevice(DeviceID);
    }

    public void removeDevice(int deviceID) {
        devicemanager.removeDevice(deviceID);
    }

    public int[] getDeviceIDList() {
        return devicemanager.getDeviceList();
    }

    public boolean hasDevice(int key) {
        return devicemanager.hasDevice(key);
    }

    public void initAllDevice() {
        devicemanager.initAllDevice();

    }

    public void clearDeviceList() {
        devicemanager.clearDeviceList();
    }

    public void stopRequest(Request request) {
        scheduler.stopRequest(request);
    }

    public void putRequest(Request request) {
        scheduler.putRequest(request);
    }

    public void showWorkingThreads() {
        scheduler.showWorkingThreads();
    }

    public void run() {
        prevRun();
        SchedRun();
        nextRun();
    }

    protected void prevRun() {

    }

    protected void nextRun() {

    }

    protected void SchedRun() {
        scheduler.SchedulerRunning();
    }
}
