package ru.hyperboloid.emotracker.model;

/**
 * Представление пользовательского события
 */
public class Event
{
    private int image;
    private String details;
    private String info;
    private int status;

    private String data;

    public Event(int image, String details, String info, int status, String data)
    {
        this.image = image;
        this.details = details;
        this.info = info;
        this.status = status;
        this.data = data;
    }

    public Event(int image, String details, String info, int status)
    {
        this.image = image;
        this.details = details;
        this.info = info;
        this.status = status;
    }

    public String getData()
    {
        return data;
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
}
