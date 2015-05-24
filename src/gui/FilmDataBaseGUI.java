package gui;

import beans.Actor;
import beans.Film;
import database.DataBaseAccess;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class FilmDataBaseGUI extends JFrame
{
    private JComboBox cbCategories;
    private DataBaseAccess dataBaseAccess;
    private JList lsFilms;
    private DefaultListModel dlm;
    private JEditorPane epDetails;
    private List<Film> films;
    private JRadioButton rbActor;
    private JRadioButton rbTitle;
    private JLabel labSearchResults;

    public FilmDataBaseGUI()
    {
        try
        {
            dataBaseAccess = new DataBaseAccess();
            this.setLocationRelativeTo(null);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setSize(700, 400);
            initComponents();
            this.setTitle("FilmDataBase");
        }
        catch (Exception ex)
        {
            Logger.getLogger(FilmDataBaseGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initComponents() throws Exception
    {
        JScrollPane spFilms = new JScrollPane();
        cbCategories = new JComboBox();

        JPanel panContent = new JPanel();
        lsFilms = new JList();
        epDetails = new JEditorPane();
        final JTextField tfSearch = new JTextField();
        labSearchResults = new JLabel();
        JPanel panSearchCategories = new JPanel();
        JPanel panActorTitle = new JPanel();
        panActorTitle.setLayout(new GridLayout(1, 2));
        rbActor = new JRadioButton("Actor");
        rbTitle = new JRadioButton("Title");
        ButtonGroup bgActorTitle = new ButtonGroup();
        bgActorTitle.add(rbActor);
        bgActorTitle.add(rbTitle);
        rbTitle.setSelected(true);

        lsFilms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        epDetails.setBackground(new Color(154, 205, 50));
        tfSearch.addCaretListener(new CaretListener()
        {
            @Override
            public void caretUpdate(CaretEvent ce)
            {
                onSearch(tfSearch.getText());
            }
        });

        loadCategories();


        cbCategories.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                click();
                onCategoryChosen();
            }
        });

        lsFilms.addListSelectionListener(new ListSelectionListener()
        {

            @Override
            public void valueChanged(ListSelectionEvent arg0)
            {
                if (arg0.getValueIsAdjusting())
                {
                    epDetails.setText("");
                    if (rbTitle.isSelected())
                    {
                        click();
                        onGetInformation();
                    }

                }

            }
        });
        panActorTitle.add(rbActor);
        panActorTitle.add(rbTitle);

        rbTitle.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                click();
                onSearch(tfSearch.getText());
            }
        });

        rbActor.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                click();
                epDetails.setText("");
                onSearch(tfSearch.getText());
            }
        });

        spFilms.setViewportView(lsFilms);
        panContent.setLayout(new BorderLayout());

        panContent.add(panSearchCategories, BorderLayout.NORTH);
        panContent.add(spFilms, BorderLayout.CENTER);
        panSearchCategories.setLayout(new GridLayout(4, 1));
        panSearchCategories.add(cbCategories);
        panSearchCategories.add(panActorTitle);
        panSearchCategories.add(tfSearch);
        panSearchCategories.add(labSearchResults);

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(panContent, BorderLayout.WEST);
        this.getContentPane().add(epDetails, BorderLayout.CENTER);
    }

    private void loadCategories() throws Exception
    {
        List<String> categories = dataBaseAccess.getCategories();

        cbCategories.addItem("all movies");

        for (String category : categories)
        {
            cbCategories.addItem(category);
        }


        dlm = new DefaultListModel();

        films = dataBaseAccess.getFilmsFromCategory("*");

        for (Film film : films)
        {
            dlm.addElement(film.getTitle());
        }

        lsFilms.setModel(dlm);
        labSearchResults.setText(dlm.getSize() + " results found");
    }

    private void onCategoryChosen()
    {
        try
        {
            dlm.clear();
            rbTitle.setSelected(true);
            String category = cbCategories.getSelectedItem().toString();

            if (category.equals("all movies"))
            {
                films = dataBaseAccess.getFilmsFromCategory("*");
            }
            
            else
            {
                films = dataBaseAccess.getFilmsFromCategory(category);
            }

            for (Film film : films)
            {
                dlm.addElement(film);
            }
            
            labSearchResults.setText(dlm.getSize() + " results found");
            lsFilms.setModel(dlm);
            lsFilms.setSelectedIndex(0);
            onGetInformation();
        }
        catch (SQLException ex)
        {
            Logger.getLogger(FilmDataBaseGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void onGetInformation()
    {
        String title = lsFilms.getSelectedValue().toString();
        Film thisIsIt = null;

        for (Film film : films)
        {
            if (film.getTitle().equals(title))
            {
                thisIsIt = film;
            }
        }
        epDetails.setContentType("text/html");
        epDetails.setText(thisIsIt.getHtmlStringFromFilm());
    }

    private void onSearch(String fragment) 
    {
        dlm.clear();

        if (rbActor.isSelected()) 
        {
            try 
            {
                List<Actor> actors = dataBaseAccess.getActorContainingFragment(fragment);

                for (Actor actor : actors) 
                {
                    dlm.addElement(actor);
                }

            } 
            catch (SQLException ex)
            {
                Logger.getLogger(FilmDataBaseGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (rbTitle.isSelected()) 
        {
            try 
            {
                films = dataBaseAccess.getFilmContainingFragment(fragment);
                for (Film film : films) 
                {
                    dlm.addElement(film);
                }
            } 
            catch (SQLException ex) 
            {
                Logger.getLogger(FilmDataBaseGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        labSearchResults.setText(dlm.getSize() + " results found");
        lsFilms.setModel(dlm);
    }

    public static void colorItems(FilmDataBaseGUI filmDataBaseGUI)
    {
        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
        {
            if ("Nimbus".equals(info.getName()))
            {
                try
                {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
                catch (ClassNotFoundException ex)
                {
                    Logger.getLogger(FilmDataBaseGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (InstantiationException ex)
                {
                    Logger.getLogger(FilmDataBaseGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (IllegalAccessException ex)
                {
                    Logger.getLogger(FilmDataBaseGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (UnsupportedLookAndFeelException ex)
                {
                    Logger.getLogger(FilmDataBaseGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        UIManager.put("nimbusBase", new Color(165, 42, 42));
        UIManager.put("nimbusBlueGrey", new Color(178, 34, 34));
        UIManager.put("control", new Color(178, 34, 34));
        UIManager.put("defaultFont", new Font("Arial", Font.PLAIN, 13));
        UIManager.put("comboBox.selectionBackground", Color.yellow);

        SwingUtilities.updateComponentTreeUI(filmDataBaseGUI);
        filmDataBaseGUI.setVisible(true);
    }

    public void click()
    {
        InputStream in = null;
        AudioStream as = null;
        try
        {
            String fileName = System.getProperty("user.dir")
                    + File.separator + "src"
                    + File.separator + "data" + File.separator + "kaetzchen.wav";

            in = new FileInputStream(fileName);
            as = new AudioStream(in);
            AudioPlayer.player.start(as);
        }
        catch (IOException ex)
        {
            JOptionPane.showMessageDialog(null, ex.toString());
        }
    }
}
