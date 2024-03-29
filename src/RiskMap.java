
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;


/**
 *  The RiskMap class does the initial instantiation of territories and continents and
 *  holds the state of all the Territories and continents in two hash maps:
 *  territoryMap (String to Territory) and territoryContinentMap (Territory to Continent). An instance of this
 *  class is created in the first line of the main.
 *
 *  * @author David Sciola - 101082459, Kevin Quach - 101115704 and Kashish Saxena - 101107204
 *  * @version November 23, 2020
 *
 */

public class RiskMap implements Serializable {

    private static java.util.Map<String, Territory> territoryMap;
    private static java.util.Map<Territory, Continent> territoryContinentMap;
    private static ArrayList<Territory> territories;
    private static ArrayList<Continent> continents;
    private static String mapPath;

    //variables used for testing purposes
    private boolean mapWasValid;
    private boolean testingMapWasOverrided;

    private java.util.Map<String, Territory> territoryMap2;
    private java.util.Map<Territory, Continent> territoryContinentMap2;
    private ArrayList<Territory> territories2;
    private ArrayList<Continent> continents2;
    private String mapPath2;


    /**
     * Constructor of the RiskMap class. It initializes all the field values and invokes the createMap() method that initializes
     * the Risk Map.
     * @param testing Boolean that restricts initialization of the normal Risk map when testing, allows otherwise.
     */
    public RiskMap(boolean testing, boolean testingOverrideMapFilePath) {
        territoryMap = new HashMap<>();
        territoryContinentMap = new HashMap<>();
        continents = new ArrayList<Continent>();
        territories = new ArrayList<Territory>();
        this.testingMapWasOverrided = testingOverrideMapFilePath;
        if (!testing) {
            try{
                //first create the map
                createMap(testingOverrideMapFilePath);

                //then validate it
                validateMap();
            }
            catch(Exception e){
                //need to put createMap() in a try catch block since it can throw exceptions
                System.out.println("Exception occurred while creating map");
                e.printStackTrace();
            }
        }
    }

    /**
     * Makes user select the json file to use for the custom map using the swing file chooser.
     * Once the user selects a file its path is stored in mapPath
     */
    private void selectMapPath(){
        JOptionPane.showMessageDialog(null, "Please select a map from maps folder (included in zip submission)");
        mapPath = "";
        while(mapPath.equals("")){
            // create an object of JFileChooser class
            JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

            // invoke the showsOpenDialog function to show the save dialog
            int r = j.showOpenDialog(null);

            // if the user selects a file
            if (r == JFileChooser.APPROVE_OPTION)
            {
                System.out.println(j.getSelectedFile().getAbsolutePath());
                mapPath = j.getSelectedFile().getAbsolutePath();
            }
            // if the user cancelled the operation
            else{
                JOptionPane.showMessageDialog(null, "Please select a map from maps folder (included in zip submission)");
            }
        }
    }


    /**
     * Instantiates territories, continents and set adjacent territories connections.
     * This is done by parsing a json file which contains the required information to load
     * a custom map. Note that custom maps are located within the "maps" folder (included in zip submission)
     * and note that the JSON parsing is done using an external java library and the java library was
     * included within the "json external library" folder
     *
     * jar file was downloaded from https://mvnrepository.com/artifact/org.json/json
     */
    private void createMap(boolean testingOverrideMapFilePath) throws IOException, ParseException {
        //not the special testing case where the mapPath was set directly
        if(!testingOverrideMapFilePath){
            //make user choose a json file to load the custom map from
            selectMapPath();
        }

        //create JSONObject jo based on the file path mapPath
        Object obj = new JSONParser().parse(new FileReader(mapPath));
        JSONObject jo = (JSONObject) obj;

        //invoke steps 1, 2, 3 and 4 to create the map
        createTerritories(jo);//STEP 1
        createContinents(jo);//STEP 2
        linkContinentsAndTerritories(jo);//STEP 3
        linkAdjacentTerritories(jo);//STEP 4
    }

    /**
     * STEP 1, create territory objects
     */
    private void createTerritories(JSONObject jo){
        Iterator<Map.Entry> itr;

        //fetch territories from json file
        JSONArray territoriesArray = (JSONArray) jo.get("territories");

        //traverse territoriesArray to create all territory objects
        Iterator territoryItr = territoriesArray.iterator();
        while (territoryItr.hasNext()) {

            //temporary variables
            String tempName = "";
            int tempXPos = 0;
            int tempYPos = 0;

            //fill temporary variables
            itr = ((Map) territoryItr.next()).entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry pair = itr.next();
                if (pair.getKey().equals("name")) {
                    tempName = (String) pair.getValue();
                } else if (pair.getKey().equals("xPos")) {
                    tempXPos = (int)(long) pair.getValue();
                } else if (pair.getKey().equals("yPos")) {
                    tempYPos = (int)(long) pair.getValue();
                }
            }

            //use temporary variables to create Territory and add to territories
            Territory tempTer = new Territory(tempName,tempXPos,tempYPos);
            territories.add(tempTer);
        }

        //add all Territories to territoryMap hash map
        for(Territory t: territories){
            territoryMap.put(t.getName().toLowerCase(), t);
        }
    }

    /**
     * STEP 2, create continent objects
     */
    private void createContinents(JSONObject jo){
        Iterator<Map.Entry> itr;

        //fetch territories from json file
        JSONArray continentArray = (JSONArray) jo.get("continents");

        //traverse territoriesArray to create all territory objects
        Iterator continentItr = continentArray.iterator();
        while (continentItr.hasNext()) {

            //temporary variables
            String tempName = "";
            int tempXPos = 0;
            int tempYPos = 0;
            int tempRed = 0;
            int tempGreen = 0;
            int tempBlue = 0;
            int tempVal = 0;

            //fill temporary variables
            itr = ((Map) continentItr.next()).entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry pair = itr.next();
                if(pair.getKey().equals("name")){
                    tempName = (String)pair.getValue();
                }
                else if(pair.getKey().equals("xPos")){
                    tempXPos = (int)(long) pair.getValue();
                }
                else if(pair.getKey().equals("yPos")){
                    tempYPos = (int)(long) pair.getValue();
                }
                else if(pair.getKey().equals("red")){
                    tempRed = (int)(long) pair.getValue();
                }
                else if(pair.getKey().equals("green")){
                    tempGreen = (int)(long) pair.getValue();
                }
                else if(pair.getKey().equals("blue")){
                    tempBlue = (int)(long) pair.getValue();
                }
                else if(pair.getKey().equals("value")){
                    tempVal = (int)(long) pair.getValue();
                }
            }

            //use temporary variables to create continent and add to continents
            Continent tempContinent = new Continent(tempName, tempXPos, tempYPos, new Color(tempRed,tempGreen,tempBlue), tempVal);
            continents.add(tempContinent);
        }
    }

    /**
     * STEP 3, link the territories to their respective continents
     */
    private void linkContinentsAndTerritories(JSONObject jo){
        Iterator<Map.Entry> itr;

        //fetch territoriesInContinents from json file
        JSONArray territoriesInContinentsArray = (JSONArray) jo.get("territoriesInContinents");

        //traverse territoriesArray to create all territory objects
        Iterator territoryToContinentItr = territoriesInContinentsArray.iterator();
        while (territoryToContinentItr.hasNext()) {

            //temporary variables
            String tempContinentName = "";
            String tempTerritories = "";
            Continent tempContinent = null;
            List<Territory> territoriesInContinent = new ArrayList<Territory>();

            //fill tempContinentName and tempTerritories
            itr = ((Map) territoryToContinentItr.next()).entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry pair = itr.next();
                if (pair.getKey().equals("continent")) {
                    tempContinentName = (String) pair.getValue();
                } else if (pair.getKey().equals("territories")) {
                    tempTerritories = (String) pair.getValue();
                }
            }

            //use tempContinentName to find the continent object with matching name in continents
            for(Continent c: continents){
                if(c.toString().equals(tempContinentName)){
                    tempContinent = c;
                }
            }

            //use tempTerritories to find all territory objects with matching names in territories
            String[] parts = tempTerritories.split(",");
            for (String terrName : parts) {
                for (Territory t : territories) {
                    if (t.getName().equals(terrName)) {
                        territoriesInContinent.add(t);
                    }
                }
            }

            //finally add the territories to the continent
            tempContinent.setTerritories(territoriesInContinent);

            //also add all continent to territoryContinentMap hash map
            for(Territory t: tempContinent.getTerritories()){ territoryContinentMap.put(t, tempContinent); }
        }
    }

    /**
     * STEP 4, link the territories to themselves/ set territory adjacencies
     */
    private void linkAdjacentTerritories(JSONObject jo){
        Iterator<Map.Entry> itr;

        //fetch territoriesInContinents from json file
        JSONArray territoryAdjacencyArray = (JSONArray) jo.get("territoryAdjacencies");

        //traverse territoriesArray to create all territory objects
        Iterator territoryToTerritoryItr = territoryAdjacencyArray.iterator();
        while (territoryToTerritoryItr.hasNext()) {

            //temporary variables
            String tempTerritoryName = "";
            String tempTerritories = "";
            Territory tempTerritory = null;
            List<Territory> territoriesInTerritory = new ArrayList<Territory>();

            //fill tempTerritoryName and tempTerritories
            itr = ((Map) territoryToTerritoryItr.next()).entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry pair = itr.next();

                if (pair.getKey().equals("territory")) {
                    tempTerritoryName = (String) pair.getValue();
                } else if (pair.getKey().equals("territories")) {
                    tempTerritories = (String) pair.getValue();
                }

            }

            //use tempTerritoryName to find the territory object with matching name in territories
            for(Territory t: territories){
                if(t.getName().equals(tempTerritoryName)){
                    tempTerritory = t;
                }
            }

            //use tempTerritories to find all territory objects with matching names in territories
            String[] parts = tempTerritories.split(",");
            for (String terrName : parts) {
                for (Territory t : territories) {
                    if (t.getName().equals(terrName)) {
                        territoriesInTerritory.add(t);
                    }
                }
            }

            //finally add the territories to the continent
            tempTerritory.setAdjacentTerritories(territoriesInTerritory);
        }
    }

    /**
     * Validates the custom map that was loaded from a json file by doing a depth first search
     * starting at the territory that was first added to territories in map creation.
     */
    private void validateMap(){
        ArrayList<Territory> visitedTerritories = new ArrayList<Territory>();
        recursiveDepthFirstSearchOnTerritories(territories.get(0), visitedTerritories);
        //after calling recursiveDepthFirstSearchOnFriendlyTerritories, visitedTerritories contains all the visited Territories

        //if the visitedTerritories the same size as the original territories exit program
        if(! (territories.size() == visitedTerritories.size())){
            if(!testingMapWasOverrided){
                JOptionPane.showMessageDialog(null, "Selected custom map is not valid");
                System.exit(0);
            }else{
                mapWasValid = false;
            }
        //else it was valid
        }else{
            if(!testingMapWasOverrided){
                System.out.println("map is valid");
            }else{
                mapWasValid = true;
            }
        }
    }

    /**
     * Does a depth first search traversal starting at currentTerritory to determine all "connected" territories.
     */
    private void recursiveDepthFirstSearchOnTerritories(Territory currentTerritory, ArrayList<Territory> visitedTerritories){
        //mark currentTerritory as visited by adding it to visitedTerritories
        visitedTerritories.add(currentTerritory);

        //Traverse all the adjacent and unmarked Territories and call the recursive function with index of adjacent Territory.
        for(Territory t: currentTerritory.getAdjacentFriendlyTerritories()){
            if(!visitedTerritories.contains(t)){//if not visited
                recursiveDepthFirstSearchOnTerritories(t, visitedTerritories);
            }
        }
    }

    /**
     * Returns a territory from an input string representation of a territory.
     * @param input A string representation of a territory.
     * @return A territory associated with the input territory.
     */
    public static Territory getTerritoryFromString(String input) {
        return territoryMap.get(input.toLowerCase());
    }

    /**
     *  Returns a territory from the territory map given an index.
     * @param index The input index required to get a territory from the territory map.
     * @return A territory from the territory map.
     */
    public static Territory getTerritoryFromIndex(int index) {
        ArrayList<String> keys = new ArrayList<>();
        keys.addAll(territoryMap.keySet());
        return territoryMap.get(keys.get(index));
    }

    /**
     * Returns a continent from the territory-continent map given an input territory.
     * @param childTerritory The input territory to get the associated continent.
     * @return The continent associated with the input territory.
     */
    public static Continent getContinentFromTerritory(Territory childTerritory) {
        return territoryContinentMap.get(childTerritory);
    }

    /**
     * Returns the number of territories in the territory map.
     * @return The size of the territory map.
     */
    public static int numTerritories() {
        return territoryMap.size();
    }

    /**
     * Returns the territory Map of the game.
     * @return The Territory Map.
     */
    public static java.util.Map<String, Territory> getTerritoryMap(){
        return territoryMap;
    }

    /**
     * Returns an ArrayList of Continents.
     * @return An ArrayList of Continents.
     */
    public static ArrayList<Continent> getContinentsArrayList(){
        return continents;
    }

    /**
     * Adds the input Territory to the Territory Map.
     * @param territory The territory to be added to the Territory Map.
     */
    public static void addTerritory(Territory territory) {
        territoryMap.put(territory.getName().toLowerCase(), territory);
    }

    /**
     * Adds the input continent and territory to the territoryContinentMap and the continent to the ArrayList of Continents.
     * @param territory Territory to be associated with the continent.
     * @param continent Continent to be added to the Map and ArrayLit.
     */
    public static void addContinent(Territory territory, Continent continent) {
        territoryContinentMap.put(territory, continent);
        continents.add(continent);
    }

    // Serializing RiskGame
    public void serializeRiskMap (String filename){
        try {
            FileOutputStream fileOut = new FileOutputStream("saves/" + filename + "_rm");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Deserializing RiskGame
    public static RiskMap deserializeRiskMap(String filepath) {
        try {
            FileInputStream fileIn = new FileInputStream("saves/" + filepath + "_rm");
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            RiskMap map = (RiskMap) objectIn.readObject();
            objectIn.close();
            return map;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * saves the 5 main static fields into normal non-static fields (used when saving/serializing).
     */
    public void saveState(){
        this.territoryMap2 = territoryMap;
        this.territoryContinentMap2 = territoryContinentMap;
        this.territories2 = territories;
        this.continents2 = continents;
        this.mapPath2 = mapPath;
    }

    /**
     * loads the 5 normal non-static field back into the 5 main static fields.
     */
    public void loadState(){
        territoryMap = this.territoryMap2;
        territoryContinentMap = this.territoryContinentMap2;
        territories = this.territories2;
        continents = this.continents2;
        mapPath = this.mapPath2;
    }


    /**
     * Method user during testing that overrides the mapPath variables.
     * @param newMapPath The new map path.
     */
    public static void overrideMapPath(String newMapPath){
        mapPath = newMapPath;
    }

    /**
     * Method user during testing that returns whether or not the map
     * generated was valid.
     * @return  whether or not map was valid.
     */
    public boolean wasMapValid(){
        return this.mapWasValid;
    }
}

