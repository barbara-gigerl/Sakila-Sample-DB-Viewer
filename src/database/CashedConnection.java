package database;

import java.sql.Connection;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Queue;

public class CashedConnection
{

    private Connection connection;
    private Queue<Statement> statementQueue;

    public CashedConnection(Connection connection)
    {
        this.connection = connection;
        statementQueue = new LinkedList<Statement>();
    }

    public Statement getStatement() throws Exception
    {
        if (connection == null)
        {
            throw new Exception("Not connected to database (connection == null)");
        }

        if (statementQueue.size() > 0)
        {
            return statementQueue.poll();
        }

        return connection.createStatement();
    }

    public void releaseStatement(Statement statement) throws Exception
    {
        if (connection == null)
        {
            throw new Exception("Not connected to database (connection == null");
        }

        statementQueue.offer(statement);
    }
}
