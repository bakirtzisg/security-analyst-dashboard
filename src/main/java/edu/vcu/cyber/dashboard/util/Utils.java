package edu.vcu.cyber.dashboard.util;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Utils
{

	public static boolean openWebPage(String uri)
	{
		
		if(Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
		{
			try
			{
				Desktop.getDesktop().browse(new URI(uri));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (URISyntaxException e)
			{
				e.printStackTrace();
			}
			return true;
		}
		
		return false;
	}

}
