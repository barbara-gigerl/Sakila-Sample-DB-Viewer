package database;

import beans.Actor;
import beans.Film;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataBaseAccess
{
    private DataBase dataBase;
    private Connection connection;
    private PreparedStatement filmsFromCategoryStatement;
    private static final String filmsFromCategoryQuery = "SELECT title, description, "
            + "category, length, actors FROM film_list WHERE category LIKE ? ORDER BY title";
//    private static final String filmsFromCategoryQuery =  "SELECT * FROM film INNER JOIN film_category USING (film_id) " + 
//                           "INNER JOIN category c USING (category_id) WHERE c.name = ? ;";
    private PreparedStatement filmsContainingFragmentStatement;
    private static final String filmsContainingFragment = "SELECT title, description, "
            + "category, length, actors FROM film_list WHERE title LIKE ?";
    private PreparedStatement actorsContainingFragmentStatement;
    private static final String actorsContainingFragment = "SELECT first_name, last_name"
            + " FROM actor WHERE last_name LIKE ?";

    private PreparedStatement testStatement;
    private static final String testQuery = "SELECT * FROM TESTTABLE WHERE TABLENAME = ?";
    
    public DataBaseAccess() throws FileNotFoundException, IOException, ClassNotFoundException, SQLException
    {
        dataBase = DataBase.getInstance();
        connection = dataBase.getConnection();
    }

    public static void main(String[] args)
    {
        try
        {
            DataBaseAccess dataBaseAccess = new DataBaseAccess();
            List<String> categories = dataBaseAccess.getCategories();

            for (String categorie : categories)
            {
                System.out.print(categorie + " ");
            }

            List<Film> films = dataBaseAccess.getFilmsFromCategory("*");
           
            for (Film film : films)
            {
                System.out.println(film);
            }

        }
        catch (Exception ex)
        {
            Logger.getLogger(DataBaseAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<String> getCategories() throws Exception
    {
        List<String> categories = new LinkedList<String>();

        String sqlString = "SELECT name FROM category;";
        Statement statement = dataBase.getStatement();

        ResultSet resultSet = statement.executeQuery(sqlString);
        
        while (resultSet.next())
        {
            categories.add(resultSet.getString("name"));
        }

        dataBase.releaseStatement(statement);

        return categories;
    }

//    public List<Film> getFilmsFromCategory(String category) throws SQLException
//    {
//        List<Film> films = new LinkedList<Film>();
//        
//        if(filmsFromCategoryStatement == null)
//        {
//            filmsFromCategoryStatement = connection.prepareStatement(filmsFromCategoryQuery);  
//        }
//        
//        filmsFromCategoryStatement.setString(1, category);
//       
//        ResultSet resultSet = filmsFromCategoryStatement.executeQuery();
//        
//        return films;
//    }
    public List<Film> getFilmsFromCategory(String category) throws SQLException
    {
        List<Film> films = new LinkedList<Film>();

        if (filmsFromCategoryStatement == null)
        {
            filmsFromCategoryStatement = connection.prepareStatement(filmsFromCategoryQuery, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        }

        if (category.equals("*"))
        {
            filmsFromCategoryStatement.setString(1, "%");
        }
        else
        {
            filmsFromCategoryStatement.setString(1, category);
        }

        ResultSet resultSet = filmsFromCategoryStatement.executeQuery();
//        resultSet.last();
//        System.out.println("\nAnzahl an Filmen: " + resultSet.getRow());
        while (resultSet.next())
        {
            String title = resultSet.getString("title");
            String description = resultSet.getString("description");
            String categoryFromRes = resultSet.getString("category");
            int length = resultSet.getInt("length");
            String actors = resultSet.getString("actors");
            String[] actorsArray = actors.split(", ");

            List<Actor> actorsList = new LinkedList<Actor>();

            for (String actorSplit : actorsArray)
            {
                String firstName = actorSplit.split(" ")[0];
                String lastName = actorSplit.split(" ")[1];
                Actor actor = new Actor(firstName, lastName);
                actorsList.add(actor);
            }

            Film film = new Film(title, description, categoryFromRes, length, actorsList);
            films.add(film);

        }
        return films;
    }

    public List<Film> getFilmContainingFragment(String fragment) throws SQLException
    {
        fragment = fragment.toUpperCase();
        List<Film> films = new LinkedList<Film>();

        if (filmsContainingFragmentStatement == null)
        {
            filmsContainingFragmentStatement = connection.prepareStatement(filmsContainingFragment, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        }

        filmsContainingFragmentStatement.setString(1, "%" + fragment + "%");

        ResultSet resultSet = filmsContainingFragmentStatement.executeQuery();

        while (resultSet.next())
        {
            String title = resultSet.getString("title");
            String description = resultSet.getString("description");
            String categoryFromRes = resultSet.getString("category");
            int length = resultSet.getInt("length");
            String actors = resultSet.getString("actors");
            String[] actorsArray = actors.split(", ");

            List<Actor> actorsList = new LinkedList<Actor>();

            for (String actorSplit : actorsArray)
            {
                String firstName = actorSplit.split(" ")[0];
                String lastName = actorSplit.split(" ")[1];
                Actor actor = new Actor(firstName, lastName);
                actorsList.add(actor);
            }

            Film film = new Film(title, description, categoryFromRes, length, actorsList);
            films.add(film);
        }
        return films;
    }

    public List<Actor> getActorContainingFragment(String fragment) throws SQLException
    {
        fragment = fragment.toUpperCase();
        List<Actor> actors = new LinkedList<Actor>();

        if (actorsContainingFragmentStatement == null)
        {
            actorsContainingFragmentStatement = connection.prepareStatement(actorsContainingFragment, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        }

        actorsContainingFragmentStatement.setString(1, "%" + fragment + "%");


        ResultSet resultSet = actorsContainingFragmentStatement.executeQuery();

        while (resultSet.next())
        {
            String first_name = resultSet.getString("first_name");
            String last_name = resultSet.getString("last_name");
            Actor actor = new Actor(first_name, last_name);
            actors.add(actor);
        }
        return actors;
    }
    

}
