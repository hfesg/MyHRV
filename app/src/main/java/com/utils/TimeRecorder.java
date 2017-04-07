package com.utils;

import java.util.ArrayList;

public class TimeRecorder {
    public static final boolean ENABLE = true;
    public static final boolean DISABLE = false;
    private final TimeThread timeThread;
    private boolean recording;
    private boolean enabled;
    private int count;
    private ArrayList<OnTimePlusOneSecondListener> listeners;

    public TimeRecorder() {
        listeners = new ArrayList<OnTimePlusOneSecondListener>();
        count = 0;
        enabled = true;
        timeThread = new TimeThread();
    }


    public void switchStatus(boolean status) {
        if (status == recording) {
            return;
        } else {
            recording = status;
        }
        if (status) {
            if (!timeThread.isAlive()) {
                timeThread.start();
            }
            synchronized (timeThread) {
                timeThread.notify();
            }
        } else {
            timeThread.interrupt();
        }
    }

    public void switchStatus() {
        switchStatus(!recording);
    }

    public void removeOnTimePlusOneSecondListener(OnTimePlusOneSecondListener listener) {
        listeners.remove(listener);
    }

    public void addOnTimePlusOneSecondListener(OnTimePlusOneSecondListener listener) {
        listeners.add(listener);
    }

    public void destroy() {
        enabled = false;
        listeners.clear();
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isRecording() {
        return recording;
    }

    public interface OnTimePlusOneSecondListener {
        void onTimePlusOneSecond(int count);
    }

    private class TimeThread extends Thread {
        @Override
        public void run() {
            while (enabled) {
                while (recording) {
                    try {
                        sleep(1000);
                        count++;
                        for (OnTimePlusOneSecondListener listener : listeners) {
                            listener.onTimePlusOneSecond(count);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                synchronized (timeThread) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}