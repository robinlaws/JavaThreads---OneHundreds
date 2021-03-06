import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * Class Server will create players, client sockets, and allow communication
 * for game play.
 *  * @author Robin Laws
 *  * @version CP2561 Term Project
 */
public class Server {
    public static final int NUMBER_PLAYERS = 2;

    public static void main(String[] args) throws IOException {
        int portNumber = 4401; //Integer.parseInt(args[0]);

        LinkedList<Socket> clientSocketList = new LinkedList<>();
        LinkedList<Player> playerList = new LinkedList<>();
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_PLAYERS);

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (clientSocketList.size() < NUMBER_PLAYERS) {
                clientSocketList.add(serverSocket.accept());
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }

        System.out.printf("\nConnections: %d", clientSocketList.size());
        setup(playerList);

        GameProtocol gameProtocol = new GameProtocol(playerList, NUMBER_PLAYERS);
        LinkedList<GameServerThread> threadList = new LinkedList<>();
        for (int i = 0; i < NUMBER_PLAYERS; i++) {
            threadList.add (new GameServerThread(gameProtocol, playerList.get(i), clientSocketList.get(i)));
            executor.execute(threadList.get(i));
        }
    }

    /**
     * method setup is called to initialize player objects and deal the hands for game play. It will also print
     * player hands on the server side.
     * @param playerList list of players
     */
    public static void setup(LinkedList<Player> playerList){
        System.out.println("\nBeginning the Robot game \nShuffled deck:");
        CardDeck cardDeck = new CardDeck();
        cardDeck.shuffle();
        cardDeck.print(System.out);

        Player player1 = new Player("Josh");
        Player player2 = new Player("Chris");
        playerList.add(player1);
        playerList.add(player2);
        cardDeck.deal(playerList);

        for (Player p : playerList) {
            System.out.printf("\n%s hand: \n", p.getName());
            p.printHand(System.out);
        }
        System.out.println("\n");
    }
}





