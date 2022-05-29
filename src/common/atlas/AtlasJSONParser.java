/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common.atlas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;

/**
 *
 * @author Kevin
 */
public class AtlasJSONParser {

    static boolean krakenSet = false;
    static ArrayList<String[]> locations = new ArrayList<>();
    static ArrayList<String> positionsPVE = new ArrayList<>();
    static ArrayList<String> positionsTrench = new ArrayList<>();
    static ArrayList<String> positionsFountain = new ArrayList<>();
    static String positionKraken = "";
    static String positionCave = "";
    static int gamePort = 5760;
    static int queryPort = 57560;
    static int rconPort = 47560;
    static int seamlessPort = 27000;
    static int serverCount = 1;
    static String ipAddress = "";
    static File file = new File("ServerGrid.json");
    static String myJson;
    static JSONObject obj;
    static JSONObject srv;
    static long grid;
    static long gridsX;
    static long gridsY;
    static long totalX;
    static long totalY;
    //add the kraken boss
    int krakenX = 0;
    int krakenY = 0;
    int trenchCount = 0;
    OfficialJSONLoader loader = new OfficialJSONLoader();
    static ArrayList<String> powerstones = new ArrayList<>();
    static ArrayList<String> essences = new ArrayList<>();

    public AtlasJSONParser(File f, int game, int query, int rcon, int seam, String ip) {
        file = f;
        gamePort = game;
        queryPort = query;
        rconPort = rcon;
        seamlessPort = seam;
        ipAddress = ip;
    }

    public AtlasJSONParser(File f) {
        file = f;
    }

    public int[] getBaseInfo() throws FileNotFoundException {
        int[] info = new int[3];
        myJson = new Scanner(file).useDelimiter("\\Z").next();
        obj = new JSONObject(myJson);
        info[0] = obj.getInt("totalGridsX");
        info[1] = obj.getInt("totalGridsY");
        info[2] = (int) ((info[0] * info[1]) * 2.5);
        return info;
    }

    public boolean hasPowerStoneIslands() {
        boolean missing = true;

        //get the servers
        JSONArray servers = obj.getJSONArray("servers");
        int size = servers.length();
        boolean contains = false;

        for (int k = 0; k < Constants.PVE_MAPS.length; k++) {
            contains = false;

            for (int i = 0; i < size; i++) {
                //get the islands on the server
                JSONObject server = servers.getJSONObject(i);

                JSONArray islands = server.getJSONArray("islandInstances");
                int islandSize = islands.length();
                for (int j = 0; j < islandSize; j++) {

                    //get the name of the island
                    String island = islands.getJSONObject(j).get("name").toString();
                    if (island.equalsIgnoreCase(Constants.PVE_MAPS[k])) {
                        contains = true;
                        break;
                    }
                }
            }

            if (!contains) {
                missing = false;
                gui.JSONSelection.statusArea.append(Constants.PVE_MAPS[k] + " was NOT found in the servergrid\n");
            }
        }

        return missing;
    }

    public boolean hasEnoughTrenches() {

        int count = 0;

        //get the servers
        JSONArray servers = obj.getJSONArray("servers");
        int size = servers.length();

        for (int i = 0; i < size; i++) {
            //get the islands on the server
            JSONObject server = servers.getJSONObject(i);

            JSONArray islands = server.getJSONArray("islandInstances");
            int islandSize = islands.length();
            for (int j = 0; j < islandSize; j++) {

                //get the name of the island
                String island = islands.getJSONObject(j).get("name").toString();
                if (island.equalsIgnoreCase(Constants.TRENCH)) {

                    count++;
                }
            }
        }

        return count >= 9;
    }

    public boolean hasKraken() {
        boolean exists = false;
        try {
            JSONArray servers = obj.getJSONArray("servers");
            int size = servers.length();
            for (int j = 0; j < size; j++) {
                JSONObject server = servers.getJSONObject(j);
                JSONArray sublevels = server.getJSONArray("extraSublevels");

                for (int i = 0; i < sublevels.length(); i++) {
                    String island = sublevels.get(i).toString();
                    if (island.compareToIgnoreCase("EndBossLevel") == 0) {
                        System.out.println("End Boss Level found");
                        krakenX = Integer.parseInt(server.get("gridX").toString());
                        krakenY = Integer.parseInt(server.get("gridY").toString());
                        exists = true;
                    }

                }
            }

        } catch (Exception e) {
            return false;
        }

        return exists;
    }

    private boolean hasKraken(JSONObject server) {
        boolean exists = false;
        try {
            JSONArray sublevels = server.getJSONArray("extraSublevels");

            for (int i = 0; i < sublevels.length(); i++) {
                String island = sublevels.get(i).toString();
                if (island.compareToIgnoreCase("EndBossLevel") == 0) {

                    krakenX = Integer.parseInt(server.get("gridX").toString());
                    krakenY = Integer.parseInt(server.get("gridY").toString());
                    exists = true;
                }

            }
        } catch (Exception e) {
            return false;
        }

        return exists;

    }

    public void loadJSONFile() throws FileNotFoundException {
        myJson = new Scanner(file).useDelimiter("\\Z").next();

        // build a JSON object
        obj = new JSONObject(myJson);
    }

    public void calculateQuests() {

        try {

            //get the map size and grids
            grid = obj.getLong("gridSize");
            gridsX = obj.getLong("totalGridsX");
            gridsY = obj.getLong("totalGridsY");
            totalX = grid * gridsX;
            totalY = grid * gridsY;

            //get the servers
            JSONArray servers = obj.getJSONArray("servers");
            int size = servers.length();
            for (int i = 0; i < size; i++) {
                //get the islands on the server

                JSONObject server = servers.getJSONObject(i);
                //check if this had the kraken level
                if (hasKraken(server)) {
                    positionKraken = "X=" + getCenterOfGrid(krakenX, (int) gridsX) + ",Y=" + getCenterOfGrid(krakenY, (int) gridsY);
                    System.out.println("Position kraken " + positionKraken);
                    positionsPVE.add(positionKraken);
                    positionsTrench.add(positionKraken);
                    krakenSet = true;
                }

                if (gui.JSONSelection.updateBox.isSelected()) {
                    server.put("ip", ipAddress);
                    server.put("port", gamePort);
                    server.put("seamlessDataPort", seamlessPort);
                    server.put("gamePort", queryPort);
                    String cmdLine = "RCONEnabled=True?RCONPort=" + rconPort;
                    server.put("AdditionalCmdLineParams", cmdLine);
                }

                seamlessPort++;
                gamePort += 2;
                queryPort += 2;
                rconPort += 2;
                JSONArray islands = server.getJSONArray("islandInstances");
                int islandSize = islands.length();
                for (int j = 0; j < islandSize; j++) {
                    JSONObject island = islands.getJSONObject(j);
                    //get the name of the island
                    String islandName = island.get("name").toString();

                    if (islandName.equalsIgnoreCase(Constants.TRENCH)) {
                        matchesType(Constants.TRENCH_TYPE, island);
                    } else if (islandName.equalsIgnoreCase(Constants.CAVE)) {
                        matchesType(Constants.CAVE_TYPE, island);

                    } else {
                        for (int k = 0; k < Constants.PVE_MAPS.length; k++) {
                            if (islandName.equalsIgnoreCase(Constants.PVE_MAPS[k])) {
                                matchesType(Constants.PVE_TYPE, island);
                            }
                        }
                    }

                }

                serverCount++;
            }//end server loop

            setPowerStones();
            setPowerEssences();
            String value = getQuestString();
            String key = "globalGameplaySetup";
            obj.put(key, value);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void matchesType(int type, JSONObject island) {
        //get the island x, y and rotation
        long islandX = island.getLong("worldX");
        long islandY = island.getLong("worldY");
        BigDecimal calculatedX = getCalculated(islandX, totalX);
        BigDecimal calculatedY = getCalculated(islandY, totalY);
        String position = "X=" + calculatedX + ",Y=" + calculatedY;

        switch (type) {
            case (Constants.PVE_TYPE):
                positionsPVE.add(position);
                break;
            case (Constants.TRENCH_TYPE):
                positionsTrench.add(position);
                positionsFountain.add(position);
                break;
            case (Constants.CAVE_TYPE):
                positionCave = position;
                break;
        }
    }


    /**
     * we need to track which trenches have been taken
     *
     * @param islandName the name of the island
     * @return
     */
    private JSONArray getDiscoZones(String islandName) {
        /**
         * so first, we need to load the servergrid json
         */

        ArrayList<Object[]> discos = loader.getOfficial();

        JSONArray discoArray = null;
        for (Object[] disco : discos) {
            String name = disco[0].toString();
            if (name.equalsIgnoreCase(islandName)) {
                discoArray = (JSONArray) disco[1];
            }
        }

        return discoArray;

    }

    private BigDecimal getCenterOfGrid(int selected, int total) {
        //since index starts at 0, we need to start at 1.
        //the selected grid is the grid where the kraken will be
        // we will place it in the middle of that grid.
        /**
         * 1/total = increment
         *
         */
        BigDecimal islandDec = new BigDecimal(selected);
        BigDecimal worldDec = new BigDecimal(total);
        BigDecimal baseDec = new BigDecimal(1);
        BigDecimal increment = baseDec.divide(worldDec, 9, BigDecimal.ROUND_FLOOR);
        BigDecimal half = increment.divide(new BigDecimal(2), 9, BigDecimal.ROUND_FLOOR);
        BigDecimal base = increment.multiply(islandDec);
        base = base.add(half);

        return base;

    }

    public void writeJSONFile(String step, boolean overwrite) throws IOException {
        String parent = file.getParent();
        FileWriter test = new FileWriter(parent + File.separator + "ServerGrid_modified.json");
        if (overwrite) {
            test = new FileWriter(file);
        }

        BufferedWriter writer = new BufferedWriter(test);

        writer.write(obj.toString(2));
        writer.flush();
        writer.close();

        String reason = step + " written to json";
        gui.JSONSelection.statusArea.append(reason + "\n");
    }

    public void setPowerStones() {
        int index = 0;
        JSONArray servers = obj.getJSONArray("servers");
        int size = servers.length();
        int power = 0;
        for (int k = 0; k < Constants.PVE_MAPS.length; k++) {
            for (int i = 0; i < size; i++) {
                //get the islands on the server
                JSONObject server = servers.getJSONObject(i);
                int powerStoneX = Integer.parseInt(server.get("gridX").toString());
                int powerStoneY = Integer.parseInt(server.get("gridY").toString());

                JSONArray islands = server.getJSONArray("islandInstances");
                int islandSize = islands.length();
                for (int j = 0; j < islandSize; j++) {
                    //get the name of the island
                    String island = islands.getJSONObject(j).get("name").toString();

                    if (island.equalsIgnoreCase(Constants.PVE_MAPS[k])) {
                        islands.getJSONObject(j).put("IslandInstanceCustomDatas1", "PowerStoneIndex");
                        islands.getJSONObject(j).put("IslandInstanceCustomDatas2", index);
                        long islandX = islands.getJSONObject(j).getLong("worldX");
                        long islandY = islands.getJSONObject(j).getLong("worldY");
                        BigDecimal calculatedX = getCalculated(islandX, totalX);
                        BigDecimal calculatedY = getCalculated(islandY, totalY);
                        String position = "X=" + calculatedX + ",Y=" + calculatedY;

                        powerstones.add(position);
                        /**
                         * let's get the disco zones, then modify it to change
                         * the worldx and worldy
                         */
                        JSONArray discoZone = getDiscoZones(island);
                        int discoSize = discoZone.length();
                        for (int x = 0; x < discoSize; x++) {
                            JSONObject discoInfo = discoZone.getJSONObject(x);
                            
                            long xLocation = (grid * (powerStoneX + 1)) - (grid / 2);
                            long yLocation = (grid * (powerStoneY + 1)) - (grid / 2);
                            discoInfo.put("worldX", xLocation);
                            discoInfo.put("worldY", yLocation);
                            discoInfo.put("bIsManuallyPlaced", "true");
                        }
                        /**
                         * add the discozone to the server in question
                         */
                        server.put("discoZones", discoZone);
                        power++;
                        index++;
                    }
                }
            }

        }
    }

    public void setPowerEssences() {
        int index = 0;
        JSONArray servers = obj.getJSONArray("servers");

        int size = servers.length();
    
            for (int i = 0; i < size; i++) {
                //get the islands on the server
                JSONObject server = servers.getJSONObject(i);
                JSONArray islands = server.getJSONArray("islandInstances");
                int islandSize = islands.length();
                for (int j = 0; j < islandSize; j++) {
                    //get the name of the island
                    String island = islands.getJSONObject(j).get("name").toString();
                    if (island.equalsIgnoreCase(Constants.TRENCH)) {
                        islands.getJSONObject(j).put("IslandInstanceCustomDatas1", "PowerStoneIndex");
                        islands.getJSONObject(j).put("IslandInstanceCustomDatas2", index);
                        long islandX = islands.getJSONObject(j).getLong("worldX");
                        long islandY = islands.getJSONObject(j).getLong("worldY");
                        BigDecimal calculatedX = getCalculated(islandX, totalX);
                        BigDecimal calculatedY = getCalculated(islandY, totalY);
                        String position = "X=" + calculatedX + ",Y=" + calculatedY;

                        essences.add(position);
                        index++;
                        
                    }
                }
            }

       
        setTrenchDiscos();
    }

    private void setTrenchDiscos() {
        JSONArray servers = obj.getJSONArray("servers");

        int size = servers.length();

        for (int i = 0; i < size; i++) {
            //get the islands on the server
            JSONObject server = servers.getJSONObject(i);
            int powerEssenceX = Integer.parseInt(server.get("gridX").toString());
            int powerEssenceY = Integer.parseInt(server.get("gridY").toString());
            JSONArray islands = server.getJSONArray("islandInstances");
            int islandSize = islands.length();
            for (int j = 0; j < islandSize; j++) {
                //get the name of the island
                String island = islands.getJSONObject(j).get("name").toString();
                if (island.equalsIgnoreCase(Constants.TRENCH)) {

                    /**
                     * let's get the disco zones, then modify it to change the
                     * worldx and worldy
                     */
                    JSONArray discoZone = loader.getTrenchDiscos().get(trenchCount);
                    int discoSize = discoZone.length();
                    for (int x = 0; x < discoSize; x++) {
                        JSONObject discoInfo = discoZone.getJSONObject(x);
                        //(CellSize*(GridX+1))-(Cellsize/2)
                        long xLocation = (grid * (powerEssenceX + 1)) - (grid / 2);
                        long yLocation = (grid * (powerEssenceY + 1)) - (grid / 2);
                        discoInfo.put("worldX", xLocation);
                        discoInfo.put("worldY", yLocation);
                        discoInfo.put("bIsManuallyPlaced", "true");
                    }
                    trenchCount++;
                    /**
                     * add the discozone to the island in question
                     */
                    server.put("discoZones", discoZone);
                }
            }

        }
    }

    public void setTemplateNames() {
        try {

            JSONArray servers = obj.getJSONArray("servers");
            int size = servers.length();
            int serverCount = 0;
            for (int i = 0; i < size; i++) {
                //get the discoZones on the server
                JSONObject server = servers.getJSONObject(i);
                String name = server.get("name").toString();
                int templateX = server.getInt("gridX");
                int templateY = server.getInt("gridY");

                String template = "";
                try {
                    template = server.get("serverTemplateName").toString();
                } catch (Exception e) {
                    template = "No_Template";
                }
                if (name.length() == 0) {
                    server.put("name", template + " " + getGrid(templateX, templateY));
                    serverCount++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getGrid(int x, int y) {

        String grid = letters[x] + (y + 1) + "";
        return grid;

    }

    String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"};

    private static BigDecimal getCalculated(long current, long total) {
        BigDecimal islandDec = new BigDecimal(current);
        BigDecimal worldDec = new BigDecimal(total);
        BigDecimal decimal = islandDec.divide(worldDec, 9, BigDecimal.ROUND_FLOOR);
        return decimal;
    }

    private static String getQuestString() {
        String s = common.atlas.QuestEntries.ENTRIES;
        int pos = 0;
        String[] quests = s.split("QuestID=");

        /**
         * trenches =12000 ice cave=13552 fountain of youth = powerstones ghost
         * ship = kraken
         */
        String[] ice = quests[12].split("PointOfInterestID=");
        String[] trenches = quests[11].split("PointOfInterestID=");
        String[] fountains = quests[2].split("PointOfInterestID=");
        String[] powerStones = quests[1].split("PointOfInterestID=");
        String[] ghostShip = quests[3].split("PointOfInterestID=");

        s = replaceLocations(s, powerStones, powerstones);

        s = replaceLocations(s, fountains, powerstones);
        s = replaceLocation(s, ice, positionCave);
        s = replaceLocations(s, trenches, positionsTrench);
        s= replaceLocation(s, ghostShip, getGhostShipLocation());
        return s;
    }

    private static String replaceLocations(String s, String[] lines, ArrayList<String> locations) {

        int index = 0;

        for (String line : lines) {

            if (line.contains("WorldMapPosition=")) {

                //get the piece of the string we want to replace
                String key = "WorldMapPosition=(";
                int length = key.length();
                String start = line.substring(line.indexOf(key) + length, line.indexOf(")", line.indexOf(key)));

                String end = "";
                if (index < 9) {
                    end = locations.get(index);
                } else {
                    end = positionKraken;
                }
                //get the next sorted item
                s = s.replace(start, end);
                index++;

            }
        }
        String calc = s;
        return calc;
    }

    private static String replaceLocation(String s, String[] lines, String end) {

        for (String line : lines) {

            if (line.contains("WorldMapPosition=")) {

                //get the piece of the string we want to replace
                String key = "WorldMapPosition=(";
                int length = key.length();
                String start = line.substring(line.indexOf(key) + length, line.indexOf(")", line.indexOf(key)));

                //get the next sorted item
                s = s.replace(start, end);

            }
        }
        String calc = s;
        return calc;
    }

    public void setDiscoveryZoneNames() {
        try {

            BufferedReader reader = new BufferedReader(new FileReader("resources\\discozones"));
            String line;
            ArrayList<String> discos = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                discos.add(line);
            }
            /**
             * we have to enumerate through all the servers and pull the
             * discozones array from each one to be able to set it
             *
             */
            JSONArray servers = obj.getJSONArray("servers");
            int size = servers.length();
            int iteration = 0;
            for (int i = 0; i < size; i++) {
                //get the discoZones on the server
                JSONObject server = servers.getJSONObject(i);

                JSONArray discoZones = server.getJSONArray("discoZones");
                int discoSize = discoZones.length();
                for (int j = 0; j < discoSize; j++) {
                    //put the randon name into the discozone
                    JSONObject discoZone = discoZones.getJSONObject(j);
                    discoZone.put("name", discos.get(iteration));
                    iteration++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setTransientNodes() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("resources\\transient"));
            String line;
            ArrayList<String> trans = new ArrayList<>();
            int max = trans.size();
            int min = 0;
            String landNode = "AotD";
            while ((line = reader.readLine()) != null) {
                trans.add(line.trim());
                line = reader.readLine();
            }

            JSONArray servers = obj.getJSONArray("servers");
            int size = servers.length();

            for (int i = 0; i < size; i++) {
                //get the discoZones on the server
                JSONObject server = servers.getJSONObject(i);

                JSONArray islands = server.getJSONArray("islandInstances");
                int islandSize = islands.length();

                for (int j = 0; j < islandSize; j++) {
                    //put the randon name into the discozone
                    JSONObject island = islands.getJSONObject(j);
                    String name = island.getString("name");
                    boolean exists = false;
                    for (String tran : trans) {
                        if (tran.equalsIgnoreCase(name)) {
                            exists = true;

                        }
                    }
                    if (exists) {
                        island.put("landNodeKey", landNode);
                        if (landNode.equalsIgnoreCase("AotD")) {
                            landNode = "Industrial";
                        } else {
                            landNode = "AotD";
                        }
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getGhostShipLocation() {
       String position ="";
        JSONArray paths = obj.getJSONArray("shipPaths");
        int pathSize = paths.length();
        for (int i = 0; i < pathSize; i++) {
            String pathClass = paths.getJSONObject(i).get("AutoSpawnShipClass").toString();
            if (pathClass.contains("PathFollowingGhostShip_BP")) {
                System.out.println("Ghostship path found");
                JSONArray ghostPath = paths.getJSONObject(i).getJSONArray("Nodes");
                //the first node can be the icon
                long ghostX = ghostPath.getJSONObject(0).getLong("worldX");
                long ghostY = ghostPath.getJSONObject(0).getLong("worldY");
                BigDecimal calculatedX = getCalculated(ghostX, totalX);
                BigDecimal calculatedY = getCalculated(ghostY, totalY);
                position = "X=" + calculatedX + ",Y=" + calculatedY;
            }
        }
         return position;
    }

    /**
     * "MapImageURL": "http://cdn.atlasdedicated.com/RookieCove_UpperMid.jpg",
     * "OverAllMapImageURL":
     * "http://cdn.atlasdedicated.com/Overworld_Blank.jpeg", "BaseServerArgs":
     * "%MapName%%GridLocation%?AltSaveDirectoryName=%AltSaveDir%?
     * MaxPlayers=200?ReservedPlayerSlots=50?QueryPort=%QUERYPORT%?Port=%PORT%?SeamlessIP=%MACHINEIP%?
     * MapPlayerLocation=true?ControlMaxClaimTaxRate=50?TradeRouteShipmentMaxWeight=1000?VirtualShipDelayInSeconds=600%
     * AdditionalMapArguements% -log -server -culture=en -NoCrashDialog
     * -NoBattlEye",
     */
}
