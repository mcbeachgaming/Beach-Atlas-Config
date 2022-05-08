# Beach-Atlas-Config
 
 ![image](https://user-images.githubusercontent.com/105144083/167313014-95721a5f-cb7f-4dd1-b69a-0f142775ecd1.png)

 
This configurator does a couple of things.  When you select your ServerGrid.json file, it will pull the number of x grids and y grids, and begin to populate suggested ports that would need to be forwarded.  The ip address is retrieved from amazon aws.

Game, Query and RCON ports will do port number +1.  This may be removed in future releases, but keeping in place for the moment.  Seamless increments by 1 for each grid.  These ports and the external ip are saved to the json.  This program uses amazon aws to get your external ip address.  These settings are saved to the json.  

Using the check box for Fill in Discovery Zones will only add discovery names from the discozones text file located in resources.  It assigns the names from the top of the file downward, so if you decide you want some of yours assigned, put them at the top of the file.  Names will be repeated if you have more discovery zones than the number of lines in the file.

The Give Server Names From Templates option will, if server templates used when creating the servergrid, make the server name be named as server template name_increment, e.g. Polar_1, Low Desert_2.  This only writes to names that are not set.  This option is useful if you want to give your players a heads up for what type of template the grid they are going is.

The Populate Power Stones and Essences will set the quest entries to the correct islands.  The quest location points just to the island itself, not to the specific location on the island.  This option is disabled if when you specify your json it does not contain each of the PVE islands and at least 9 trenches.

The add transient nodes check box will add cursed and sulfurous ground to the islands specified in the transient.txt file.  These islands were pulled from the official json, but can add additional islands to the file.  This will alternate between the two transient types.

Selecting the overwrite option will overwrite all the changes to the json.  This is not recommended in early versions of the program.  If you don't overwrite, it will save the json next to Beach_Atlas_Config.jar inside the dist folder.

**Requirements**
Currently only windows is tested to function.
Java 8 https://www.java.com/en/download/manual.jsp

**How to use**
Download the zip from here, extract to a location, then open the dist folder and run the Beach_Atlas_Config.jar  If you already specified ports/ip addresses this CURRENTLY replaces that with the ip and ports listed on the app.  This is a feature to be added later.

**Working on**
Adding an option to not automatically update the seamless port/ip/etc.  This would allow for better ease of multi-computer clusters.
