/*
-------------------------------------------------
Assignment No.: 3 - Web browser application 
Name: Unmesh Suryawanshi
Net id: qd6395
Course: CS 4590 - Computer Networks
-------------------------------------------------
*/

// Declaration of predefined classes
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.util.Scanner;
import java.net.*;

public class web
{
	public static String ip_add;
    public static String h_name;
    public static String p_name;
    public static String f_name;
    public static int port_no;
   
    public static void main(String[] argv) throws IOException 
    {
    	// Declaration of the variables
    	int link_no = 0;
    	int temp2 = 0, first_temp = 0, last_temp = 0;
    	
        System.out.println(" Please Enter your URL: ");
        
        Scanner input_url = new Scanner(System.in);
        String input_read_url = input_url.next(); // Reading the console input - URL provided by the user
        URL link = new URL(input_read_url); // Associating string with URL

        h_name = link.getHost(); // Getting host name in h_name                  
        f_name = link.getFile(); // Getting file name in f_name                     
        port_no = link.getPort(); // Getting port number in port_no 
        
        // Declaring port number as 80 if it gets negative value
        if (port_no < 0)
        {
            port_no = 80;
        }
        
        ip_add = InetAddress.getByName(h_name).getHostAddress(); // Getting IP address for DNS server
      
        // Logic for splitting the URL (Separate path and file name) 
        p_name = f_name.substring(0,f_name.lastIndexOf("/")+1);
        f_name = f_name.substring(f_name.lastIndexOf("/")+1);
        
        // Display the URL parsing
        System.out.println("-------------------------------- ");
        System.out.println(" Parsing of the URL: ");
        System.out.println("-------------------------------- ");
        System.out.println(" IP Address : " + ip_add);
        System.out.println(" Host       : " + h_name);
        System.out.println(" Path       : " + p_name);
        System.out.println(" File       : " + f_name);
        System.out.println(" Port       : " + port_no);
        System.out.println("--------------------------------");

        Socket socket = null;
        PrintWriter p_write = null;
        BufferedReader b_read = null;

        soccket(socket,p_write,b_read,link_no,temp2,first_temp,last_temp,input_url);
        }

	private static void soccket(Socket socket, PrintWriter p_write ,BufferedReader b_read, int link_no, int temp2, int first_temp, int last_temp, Scanner input_url)
	{
		do
        {
            try 
            {
            	socket = new Socket(ip_add, port_no); // Creating socket at client side and connecting to port of remote machine (port no = 80)
                p_write = new PrintWriter(socket.getOutputStream(), true);
                b_read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                link_no = 1;
                System.out.println();
               
                // Creating http request 
                String http_req = "GET " + p_name + f_name + " HTTP/1.0\r\n";
                System.out.println(http_req);

                // Sending http request
                p_write.println(http_req);

                boolean input_start = true;
                boolean body_start = false;
                String socket_data;
                ArrayList<String> array_list = new ArrayList<String>();
                String web_data = "";
            
                socket_data = b_read.readLine();
                do
                {         
                	// Receiving web page data from server
                    if (!socket_data.contains("200 OK") && input_start) 
                    {
                        System.out.println(socket_data);
                        break;
                    }
                    
                    input_start = false;
                    if (socket_data.contains("<body>")) 
                    {
                    	body_start = true;
                    }
                    
                    //Logic for replacing html tags 
                    if (body_start) 
                    {
                    	 socket_data = socket_data.replace("<br>", "\n"); // Replacing html tag <br> with new line
                         socket_data = socket_data.replace("<p>", "\n"); // Replacing html tag <p> with new line
                         socket_data = socket_data.replace("<li>", "\n"); // Replacing html tag <li> with new line
                         
                         // Extracting href link and saving to a list
                         if (socket_data.toLowerCase().contains("<a href=")) 
                         {
                             for (temp2 = socket_data.indexOf("href="); temp2 >= 0; temp2 = socket_data.indexOf("href=", temp2 + 1))
                             {
                            	 first_temp = socket_data.indexOf("\"");
                                 first_temp++;
                                 last_temp = socket_data.indexOf("\"", first_temp);
                                 array_list.add(socket_data.substring(first_temp, last_temp));
                                 temp2 = socket_data.indexOf(">",last_temp);
                                 
                                 socket_data = socket_data.substring(0, temp2 + 1) + "[" + String.valueOf(link_no)
                                         + "]" + socket_data.substring(temp2 + 1, socket_data.length());
                                 link_no = link_no + 1;
                             }
                         }
                         // Store all data in vari. web_data
                         web_data = web_data + socket_data;
                    }
                }while ((socket_data = b_read.readLine()) != null);
                
                web_data = web_data.replaceAll("<[^>]*>", ""); // Replacing characters by blank space
                       
                System.out.println("-------------------------------- ");
                System.out.println(" Content of the webpage ");
                System.out.println("-------------------------------- ");
                System.out.println(web_data);
                
                if (array_list.isEmpty()) 
                {
                    break;
                }
                try 
                {
                	int i = input_url.nextInt() - 1;
                    f_name = array_list.get(i);
                    }
                catch (Exception e) 
                {
                    f_name = "";
                    System.out.println(" You entered the wrong number. Program is terminating..!");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.out.println(e);
            }
        }while (!f_name.isEmpty());
	}
}
