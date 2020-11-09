import javax.swing.*;
import java.awt.*;
import java.util.*;

//RiskFrame is the JFrame that holds all the GUI, it implements RiskView and whenever a change is made
//to the model, this class handles that change with the handleRiskUpdate method which re-draws everything
public class RiskFrame extends JFrame implements RiskView {

    //RiskFrame has a reference to map so that it can fetch all the Territory names and x,y coordinates
    private RiskMap riskMap;
    private RiskGame rg;
    private RiskMapPanel mapPanel;
    private JLabel turn;
    private JLabel info;

    private ArrayList<Shape> territoryCircles; // Create an ArrayList object

    //todo, move hardcoded values into finals here
    //todo, clean up the wording of comments once done everything

    //nested MapDrawer class extends a JPanel and is the JPanel that holds the board
    //when ever the repaint() method is invoked, the paintComponent method ran which traverses all Territories
    //in the Map, fetches their X, Y coordinates and draws them out as circles
    //paintComponent also draws all the "connections" between the Territories


    public RiskFrame(RiskMap riskMap) {
        super("RISK");
        /*RiskGame*/ rg = new RiskGame();
        this.riskMap = riskMap;

        this.setLayout(new BorderLayout());

        territoryCircles = new ArrayList<Shape>();
        mapPanel = new RiskMapPanel(riskMap, rg);

        JPanel playerInputPanel = new JPanel();
        JPanel turnpanel = new JPanel();
        turn = new JLabel("Player's turn");
        info = new JLabel("Choose a territory to attack from.");
        turnpanel.add(turn);
        turnpanel.add(info);
        JPanel buttonpanel = new JPanel();
        //JButton attack = new JButton("ATTACK");
        JButton pass = new JButton("PASS");
        pass.setActionCommand("pass");

        //buttonpanel.add(attack);
        buttonpanel.add(pass);
        playerInputPanel.add(turnpanel);
        playerInputPanel.add(buttonpanel);

        RiskFrameController rfc = new RiskFrameController(rg);
        pass.addActionListener(rfc);

        //this.add(mapPanel);
        this.add(mapPanel,BorderLayout.CENTER);
        this.add(playerInputPanel, BorderLayout.SOUTH);

        this.setSize(1200, 700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
        rg.addView(this);
    }

    //whenever a change to the model is made, the model will notify all Classes that implement the RiskView Interface
    //by invoking their handleRiskUpdate method, for RiskFrame, the handleRiskUpdate method redraws the updated map
    //by triggering the paint method of mapPanel
    @Override
    public void handleRiskUpdate(RiskEvent e) {
        mapPanel.handleRiskUpdate(e);
        turn.setText(e.getCurrentPlayer().getName() + "'s turn"); //include the phase



        if (e.getPhase() == TurnPhase.ATTACK_CHOOSE_DICE) { //update this to use new phases
            //info.setText("Choose a number of dice to attack with."); //list options
            //TODO: wrap JOptionPane in the try catch, or implement a spinner
            String str = JOptionPane.showInputDialog("Choose a number of dice to attack with (1 - " + ((RiskEventBounds)e).getMaxChoice() + ")"); //list options
            int diceNum = 0;
            try
            {
                if(str != null)
                    diceNum = Integer.parseInt(str);
            }
            catch (NumberFormatException excp)
            {
                diceNum = 0;
            }
            while (((diceNum > ((RiskEventBounds)e).getMaxChoice()) || (diceNum < ((RiskEventBounds)e).getMinChoice()))) {
                System.out.println(diceNum);
                System.out.println((!(diceNum > ((RiskEventBounds)e).getMaxChoice()) || !(diceNum < ((RiskEventBounds)e).getMinChoice())));
                diceNum = Integer.parseInt(JOptionPane.showInputDialog("Invalid. Please enter number of dice between (1 - " + ((RiskEventBounds)e).getMaxChoice() + ")"));
            }
            rg.setAttackDice(diceNum); //note! some controller logic in here
        }
        else if (e.getPhase() == TurnPhase.DEFEND_CHOOSE_DICE) {
            //TODO: wrap JOptionPane in the try catch, or implement a spinner
            String str = JOptionPane.showInputDialog("Choose a number of dice to defend with (1 - " + ((RiskEventBounds)e).getMaxChoice() + ")"); //list options
            int diceNum = 0;
            try
            {
                if(str != null)
                    diceNum = Integer.parseInt(str);
            }
            catch (NumberFormatException excp)
            {
                diceNum = 0;
            }
            while (((diceNum > ((RiskEventBounds)e).getMaxChoice()) || (diceNum < ((RiskEventBounds)e).getMinChoice()))) {
                System.out.println(diceNum);
                System.out.println((!(diceNum > ((RiskEventBounds)e).getMaxChoice()) || !(diceNum < ((RiskEventBounds)e).getMinChoice())));
                diceNum = Integer.parseInt(JOptionPane.showInputDialog("Invalid. Please enter number of dice between (1 - " + ((RiskEventBounds)e).getMaxChoice() + ")"));
            }
            rg.setDefendDice(diceNum); //note! some controller logic in here
        }
        else if (e.getPhase() == TurnPhase.ATTACK_CHOOSE_MOVE) {
            //TODO: wrap JOptionPane in the try catch, or implement a spinner
            String str = JOptionPane.showInputDialog("Choose a number of dice to defend with (1 - " + ((RiskEventBounds)e).getMaxChoice() + ")"); //list options
            int diceNum = 0;
            try
            {
                if(str != null)
                    diceNum = Integer.parseInt(str);
            }
            catch (NumberFormatException excp)
            {
                diceNum = 0;
            }
            while (((diceNum > ((RiskEventBounds)e).getMaxChoice()) || (diceNum < ((RiskEventBounds)e).getMinChoice()))) {
                System.out.println(diceNum);
                System.out.println((!(diceNum > ((RiskEventBounds)e).getMaxChoice()) || !(diceNum < ((RiskEventBounds)e).getMinChoice())));
                diceNum = Integer.parseInt(JOptionPane.showInputDialog("Invalid. Please enter number of armies between (" + ((RiskEventBounds)e).getMinChoice() + " - " + ((RiskEventBounds)e).getMaxChoice() + ")"));
            }
            rg.move(diceNum); //note! some controller logic in here
        }
        else if (e.getPhase() == TurnPhase.ATTACK_RESULT) {
            RiskEventDiceResults diceResults = (RiskEventDiceResults)e;

            String message = "";
            for (Integer i : diceResults.getAttackDice()) {
                message += i + ", ";
            }
            message = "Attacker rolls: " + message.substring(0, message.length() - 2) + "\n";

            String diceRolls = "";
            for (Integer i : diceResults.getDefendDice()) {
                diceRolls += i + ", ";
            }
            diceRolls = diceRolls.substring(0, diceRolls.length() - 2);
            message += "Defender rolls: " + diceRolls + "\n"
                + "Attackers (" + diceResults.getTerritoryFrom().getName() + ") lost " + diceResults.getAttackLoss() + " armies.\n";

            if (diceResults.getDefendLoss() != -1) {
                message += "Defenders (" + diceResults.getTerritoryTo().getName() + ") lost " + diceResults.getDefendLoss() + " armies.";
            }
            else {
                message += diceResults.getCurrentPlayer().getName() + " took over " + diceResults.getDefender().getName() + "'s " + diceResults.getTerritoryTo().getName();
            }
            JOptionPane.showMessageDialog(this, message);
        }
        else if (e.getPhase() == TurnPhase.END) {
            JOptionPane.showMessageDialog(this, "game over man");
        }

    }

    //main
    public static void main(String[] args) {
        RiskMap riskMap = new RiskMap();
        RiskFrame rf = new RiskFrame(riskMap);
    }

}