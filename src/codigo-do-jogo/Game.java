import java.util.ArrayList;
import java.util.Random;

/**
 *  This class is the main class of the "World of Zuul" application. 
 *  "World of Zuul" is a very simple, text based adventure game.  Users 
 *  can walk around some scenery. That's all. It should really be extended 
 *  to make it more interesting!
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Michael Kölling and David J. Barnes
 * @version 2016.02.29
 */

public class Game 
{
    private Parser parser;
    private Room currentRoom;
    private Room exitRoom;
    private ArrayList <Room> salas = new ArrayList<Room>();
    private Random random = new Random();
    private Room salaDiploma;
    private boolean temDiploma;

    
    
        
    /**
     * Create the game and initialise its internal map.
     */
    public Game() 
    {
        createRooms();
        parser = new Parser();
        salaDiploma = salas.get(random.nextInt(5)+1);
        
    }

    

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms()
    {
        Room entrada, did1, did2, did3, did4, auditorio, saida, reitoria;

      
        // create the rooms
        entrada = new Room("Entrada da UFS");
        reitoria = new Room("Reitoria");
        did1 = new Room("Didática 1");
        did2 = new Room("Didática 2");
        did3 = new Room("Didática 3");
        did4 = new Room("Didática 4");
        auditorio = new Room("Auditório da didática 4");
        saida = new Room("Saída da UFS");

        salas.add(entrada);
        salas.add(reitoria);
        salas.add(did1);
        salas.add(did2);
        salas.add(did3);
        salas.add(did4);
        salas.add(auditorio);
        salas.add(saida);
        
        
        // initialise room exits
        entrada.setExit("south", did1);
        entrada.setExit("east", reitoria);

        reitoria.setExit("west", entrada);
        reitoria.setExit("south", did2);

        did1.setExit("east", did2);
        did1.setExit("south", did3);
        did1.setExit("north", entrada);

        did2.setExit("north", reitoria);
        did2.setExit("west", did1);
        did2.setExit("south", did4);

        did3.setExit("north", did1);
        did3.setExit("east", did4);
        did3.setExit("south", auditorio);

        did4.setExit("west", did3);
        did4.setExit("north", did2);
        did4.setExit("south", saida);

        auditorio.setExit("north", did3);
        auditorio.setExit("east", saida);

        saida.setExit("north", did4);
        saida.setExit("west", auditorio);

        exitRoom = saida;
        currentRoom = entrada;  // start game outside

        
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() 
    {            
        printWelcome();
        long startTime = 0;
        long endTime = 0;
        boolean startTimeControl = true;
        boolean hurryUp = true;
        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.
                
        boolean finished = false;
        while (!finished) {
            
            if(temDiploma == startTimeControl == true){
                startTimeControl = false;
                startTime = System.currentTimeMillis();
            }
            endTime = System.currentTimeMillis();
            long currentTime = endTime - startTime;
            if(temDiploma == hurryUp == true && currentTime >= 7500){
                hurryUp = false;
                System.out.println("O fantasma do reitor está se aproximando!");
                System.out.println("Encontre a saída já!");
            }
            else if(temDiploma == true && currentTime >= 15000){
                finished = true;
                System.out.println("O FANTASMA TE PEGOU!");
                System.out.println("Você não pegou o diploma a tempo e agora viverá");
                System.out.println("eternamente na universidade!");
                System.out.println("Boa sorte na nova vida como gato na universidade mal assombrada.");
                break;
            }
            Command command = parser.getCommand();
            finished = processCommand(command);
            if(checkWin()){
                System.out.println("Você conseguiu escapar da UFS! Parabéns!");
                finished = true;
            }
        }
        System.out.println("Obrigado por jogar, até a próxima!");
    }

    public void pegouDiploma(){
        if(currentRoom == salaDiploma){
            temDiploma = true;
            System.out.println("Você encontrou o diploma, agora vá para a saída");
            System.out.println("antes que o fantasma do reitor João Alumínio do campus te pegue!");
            System.out.println("Você tem 15 SEGUNDOS!");
        }     
    }

    public boolean checkWin(){
        if(temDiploma == false) 
            pegouDiploma();
        return temDiploma && (exitRoom == currentRoom);
        
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Bem-vindo ao jogo Escape UFS!");
        System.out.println("Escape UFS é um novo e incrível jogo de aventura.");
        System.out.println("Vá em busca do seu diploma ou algo terrível pode lhe acontecer!");
        System.out.println("Digite '" + CommandWord.HELP + "' caso precise de ajuda.");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());

        //System.out.println("O diploma está em " + salaDiploma.getShortDescription());      usado para testar
    }

    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command) 
    {
        boolean wantToQuit = false;

        CommandWord commandWord = command.getCommandWord();

        switch (commandWord) {
            case UNKNOWN:
                System.out.println("Não sei o que você quer dizer com isso...");
                break;

            case HELP:
                printHelp();
                break;

            case GO:
                goRoom(command);
                break;

            case QUIT:
                wantToQuit = quit(command);
                break;
        }
        return wantToQuit;
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() 
    {
        System.out.println("Você está perdido. Sua única companhia são os gatos e");
        System.out.println("o fantasma do reitor João Alumínio do campus. Você está vagando");
        System.out.println("ao redor da UFS.");
        System.out.println();
        System.out.println("Seus comandos são:");
        parser.showCommands();
    }

    /** 
     * Try to go in one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private void goRoom(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            System.out.println("Não há saída nesta direção!");
        }
        else {
            currentRoom = nextRoom;
            System.out.println(currentRoom.getLongDescription());
        }
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) 
    {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else {
            return true;  // signal that we want to quit
        }
    }
}
