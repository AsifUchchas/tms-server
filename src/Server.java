import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Server {
    public static void main(String[] args) {
        HashMap<String, User> data = new HashMap<>();

        try {
            File file = new File("database.txt");
            boolean isFileCreated = file.createNewFile();

            BufferedReader br = new BufferedReader(new FileReader("database.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] d = line.split(", ");
                data.put(d[2], new User(d[0], d[1], d[2], d[3], d[4], d[5]));
            }

            System.out.println("Server is waiting for client.");
            ServerSocket serverSocket = new ServerSocket(6600);

            while (true) {
                Socket sc = serverSocket.accept();
                new Thread(() -> {
                    try {
                        task(data, sc);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void task(HashMap<String, User> data, Socket sc) throws Exception {
        OutputStreamWriter o = new OutputStreamWriter(sc.getOutputStream());
        BufferedWriter sendStr = new BufferedWriter(o);

        InputStream inputStream = sc.getInputStream();
        ObjectInputStream receiveObj = new ObjectInputStream(inputStream);

        OutputStream oo = sc.getOutputStream();
        ObjectOutputStream sendObj = new ObjectOutputStream(oo);

        InputStreamReader isr = new InputStreamReader(sc.getInputStream());
        BufferedReader receiveStr = new BufferedReader(isr);

        List<String> info = (List<String>) receiveObj.readObject();
        System.out.println(" - Received request for " + info.get(0));
        String command = info.get(0);
        if (command.equals("Registration")) {
            System.out.println(" - Attempt to registration");
            if (!data.containsKey(info.get(3))) {
                data.put(info.get(3), new User(info.get(1), info.get(2), info.get(3), info.get(4), info.get(5), info.get(6)));
                append(info);
                sendStr.write("SUCCESS!\n");
                System.out.println(" - Registration Successful");
            } else{
                sendStr.write("FAILED!\n");
                System.out.println(" - Registration failed! Duplicate email found!");
            }
            sendStr.flush();
        }
        else if (command.equals("Login")) {
            System.out.println(" - Attempt to Login");
            if (!data.isEmpty() && data.containsKey(info.get(1))) {
                if (data.get(info.get(1)).getPasswords().equals(info.get(2))) {
                    System.out.println(" - login success");
                    sendStr.write("SUCCESS!\n");
                    sendStr.flush();
                    System.out.println(" - sending user info");
                    List<String> sendInfo = new ArrayList<>();
                    User usr = data.get(info.get(1));
                    sendInfo.add(usr.getName());
                    sendInfo.add(usr.getNid());
                    sendInfo.add(usr.getPhone());
                    sendInfo.add(usr.getEmail());
                    sendInfo.add("null");   // passwords are not sending for security
                    sendInfo.add(usr.getType());
                    sendObj.writeObject(sendInfo);
                    System.out.println(" - info send successfully!");
                    return;
                }
            }
            System.out.println(" - Invalid credentials, login failed!!");
            sendStr.write("FAILED!\n");
            sendStr.flush();
        }
    }

    public static void append(List<String> list) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("database.txt", true));
            // 0 == command
            for (int i = 1; i < list.size(); i++) {
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
