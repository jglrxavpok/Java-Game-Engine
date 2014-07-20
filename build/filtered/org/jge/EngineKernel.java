package org.jge;

import java.io.File;

import org.jge.game.Game;

public class EngineKernel
{

    private static Game game;
    private static File folder;
    private static Disposer disposer = new Disposer();

    public static Game getGame()
    {
        return game;
    }
    
    public static void setGame(Game g)
    {
        game = g;
    }
    
    public static File getEngineFolder()
    {
        if(folder == null)
        {
            String appdata = System.getenv("APPDATA");
            if(appdata != null)
                folder = new File(appdata, ".jglrEngine");
            else
                folder = new File(System.getProperty("user.home"), ".jglrEngine");
            
            if(!folder.exists())
                folder.mkdirs();
        }
        return folder;
    }
    
    public static void disposeAll()
    {
        disposer .disposeAll();
    }

    public static Disposer getDisposer()
    {
        return disposer;
    }

    public static ResourceLoader getResourceLoader()
    {
        return game.getResourceLoader();
    }
    
    public static String getEngineVersion()
    {
        return "Alpha-0.2";
    }
}
