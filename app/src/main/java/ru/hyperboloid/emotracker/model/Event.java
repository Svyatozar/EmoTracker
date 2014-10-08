package ru.hyperboloid.emotracker.model;

public class Event
{
    private int image;
    private String details;
    private String info;
    private int status;

    int pulse;
    int stress;
    int activity;
    int steps;

    public Event(int image, String details, String info, int status, int pulse, int stress, int activity, int steps)
    {
        this.image = image;
        this.details = details;
        this.info = info;
        this.status = status;

        this.pulse = pulse;
        this.stress = stress;
        this.activity = activity;
        this.steps = steps;
    }

    public Event(int image, String details, String info, int status)
    {
        this.image = image;
        this.details = details;
        this.info = info;
        this.status = status;
    }

    public int getImage()
    {
        return image;
    }

    public String getDetails()
    {
        return details;
    }

    public String getInfo()
    {
        return info;
    }

    public int getStatus()
    {
        return status;
    }

    public int getPulse()
    {
        return pulse;
    }

    public int getStress()
    {
        return stress;
    }

    public int getActivity()
    {
        return activity;
    }

    public int getSteps()
    {
        return steps;
    }
}
