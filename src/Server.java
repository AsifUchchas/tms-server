import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Server extends Thread {
    public static void main(String[] args) {
        HashMap<String, User> data = new HashMap<>();

        try {
            System.out.println("Server is waiting for client.");
            ServerSocket serverSocket = new ServerSocket(6600);


            while (true) {
                Socket sc = serverSocket.accept();
                new Thread(() -> {
                    try {
                        task(data, sc);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void task(HashMap<String, User> data, Socket sc) throws IOException, ClassNotFoundException {
        OutputStreamWriter o = new OutputStreamWriter(sc.getOutputStream());
        BufferedWriter sendStr = new BufferedWriter(o);

        InputStream inputStream = sc.getInputStream();
        ObjectInputStream receiveObj = new ObjectInputStream(inputStream);

        OutputStream oo = sc.getOutputStream();
        ObjectOutputStream sendObj = new ObjectOutputStream(oo);

        InputStreamReader isr = new InputStreamReader(sc.getInputStream());
        BufferedReader receiveStr = new BufferedReader(isr);

        List<String> info = (List<String>) receiveObj.readObject();
        String command = info.get(0);
        if (command.equals("Registration")) {
            if (!data.containsKey(info.get(3))) {
                data.put(info.get(3), new User(info.get(1), info.get(2), info.get(3), info.get(4), info.get(5), info.get(6)));
                append(info);
                sendStr.write("SUCCESS!\n");
            } else
                sendStr.write("FAILED!\n");
            sendStr.flush();
        }
        else if (command.equals("Login")) {
            if (data.containsKey(info.get(1))) {
                if (data.get(info.get(1)).getPasswords().equals(info.get(2))) {
                    sendStr.write("SUCCESS!\n");
                    sendStr.flush();
                    List<String> sendInfo = new ArrayList<>();
                    User usr = data.get(info.get(1));
                    sendInfo.add(usr.getName());
                    sendInfo.add(usr.getNid());
                    sendInfo.add(usr.getPhone());
                    sendInfo.add(usr.getEmail());
                    sendInfo.add("null");   // passwords are not sending for security
                    sendInfo.add(usr.getType());
                    sendObj.writeObject(sendInfo);
                } else {
                    sendStr.write("FAILED!\n");
                    sendStr.flush();
                }
            }
        }
    }

    public static void append(List<String> list) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("database.txt", true));
            for (int i = 0; i < list.size(); i++) {
                bw.write(list.get(i));
                if (i == list.size()-1)
                    bw.write("\n");
                else
                    bw.write(", ");
            }
            bw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
