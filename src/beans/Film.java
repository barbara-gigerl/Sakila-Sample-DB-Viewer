package beans;

import java.util.List;

public class Film
{

    private String title;
    private String description;
    private String category;
    private int length;
    private List<Actor> actors;

    public Film()
    { }

    public Film(String title, String description, String category, int length, List<Actor> actors)
    {
        this.title = title;
        this.description = description;
        this.category = category;
        this.length = length;
        this.actors = actors;
    }

    public List<Actor> getActors()
    {
        return actors;
    }

    public String getCategory()
    {
        return category;
    }

    public String getDescription()
    {
        return description;
    }

    public int getLength()
    {
        return length;
    }

    public String getTitle()
    {
        return title;
    }

    public String toString()
    {
        return title;
    }

    public String getHtmlStringFromFilm()
    {
        String actor = actors.toString().replace("[", "").replace("]", "");
        String htmlString = "<html>";
        htmlString += "<br>";
        htmlString += "<table border = 0><tr><td><b>Title</b></td><td>" + title + "</td></tr>";
        htmlString += "<tr><td><b>Description</b></td><td>" + description + "</td></tr>";
        htmlString += "<tr><td><b>Category</b></td><td>" + category + "</td></tr>";
        htmlString += "<tr><td><b>Length</b></td><td>" + length + "</td></tr>";
        htmlString += "<tr><td><b>Actors</b></td><td>" + actor + "</td></tr></table>";
        htmlString += "</html>";

        return htmlString;
    }
}
