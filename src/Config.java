/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 *
 * @author Christopher
 */
public class Config 
{
    InputStream input;
    Properties prop;
    public Config(String s)
    {
	try {
		// load a properties file
                prop = new Properties();
                input = new FileInputStream(s);
		prop.load(input);
		// get the property value and print it out

	} catch (IOException ex) {
		ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "FILE NOT FOUND");
                System.exit(0);
	}
    }
    public String getValue(String s)
    {
        return prop.getProperty(s);
    }
}
