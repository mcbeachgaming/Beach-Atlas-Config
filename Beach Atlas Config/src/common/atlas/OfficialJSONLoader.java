/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package common.atlas;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Kevin
 */
public class OfficialJSONLoader {

    File file = new File("ServerGrid_official.json");
    String myJson;
    JSONObject obj;
    JSONObject srv;

    ArrayList<Object[]> discos = new ArrayList<>();
    ArrayList<JSONArray> trenches = new ArrayList<>();
    JSONArray kraken = null;

    private void loadJSONFile() throws FileNotFoundException {
        myJson = new Scanner(file).useDelimiter("\\Z").next();
        obj = new JSONObject(myJson);
        Object[] row;
        //get the servers
        JSONArray servers = obj.getJSONArray("servers");
        int size = servers.length();

        for (int i = 0; i < size; i++) {
            JSONObject server = servers.getJSONObject(i);
            JSONArray islands = server.getJSONArray("islandInstances");
            JSONArray sublevels = server.getJSONArray("extraSublevels");
            int sublevelSize = sublevels.length();
            int islandSize = islands.length();
            for (int j = 0; j < islandSize; j++) {
                JSONObject island = islands.getJSONObject(j);
                //get the name of the island
                String islandName = island.get("name").toString();

                if (islandName.equalsIgnoreCase(Constants.TRENCH)) {
                    setTrenches(server);
                } else if (islandName.equalsIgnoreCase(Constants.CAVE)) {
                    getDiscoZones(server, island);
                } else {
                    for (int k = 0; k < Constants.PVE_MAPS.length; k++) {
                        if (islandName.equalsIgnoreCase(Constants.PVE_MAPS[k])) {
                            getDiscoZones(server, island);
                        }
                    }
                }
                
                //for the other discozones, we can save to a text file
                

            }//end island loop
            for (int k = 0; k < sublevelSize; k++) {
                String sublevelName = sublevels.getString(k);
                if(sublevelName.equalsIgnoreCase(Constants.ENDBOSS)){
                    setKraken(server);
                }
            }

        }
    }

    public ArrayList<JSONArray> getTrenchDiscos() {
        return trenches;
    }

    private void setTrenches(JSONObject server) {
        JSONArray discoZones = server.getJSONArray("discoZones");
        trenches.add(discoZones);
    }

    public ArrayList<Object[]> getOfficial() {
        try {
            loadJSONFile();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(OfficialJSONLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

        return discos;
    }

    private void getDiscoZones(JSONObject server, JSONObject island) {
        String islandName = island.get("name").toString();
        JSONArray discoZones = server.getJSONArray("discoZones");
        Object[] row = new Object[2];
        row[0] = islandName;
        row[1] = discoZones;
        discos.add(row);
    }

    public JSONArray getKrakenDiscos() {
        return kraken;
    }

    private void setKraken(JSONObject server) {
        kraken = server.getJSONArray("discoZones");
       
    }

}
